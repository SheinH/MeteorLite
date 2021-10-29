package net.runelite.mixins;

import net.runelite.api.mixins.Copy;
import net.runelite.api.mixins.Mixin;
import net.runelite.api.mixins.Replace;
import net.runelite.api.mixins.Shadow;
import net.runelite.rs.api.RSClient;
import net.runelite.rs.api.RSNanoClock;

@Mixin(RSNanoClock.class)
public abstract class NanoClockMixin implements RSNanoClock
{
	@Shadow("client")
	private static RSClient client;

	@Copy("wait")
	@Replace("wait")
	public int copy$wait(int cycleDurationMillis, int var2)
	{
		if (!client.isUnlockedFps())
		{
			return copy$wait(cycleDurationMillis, var2);
		}
		else
		{
			long nanoTime = System.nanoTime();
			if (nanoTime < getLastTimeNano())
			{
				setLastTimeNano(nanoTime);
				return 1;
			}
			else
			{
				long cycleDuration = (long) cycleDurationMillis * 1000000L;
				long diff = nanoTime - getLastTimeNano();
				int cycles = (int) (diff / cycleDuration);

				setLastTimeNano(getLastTimeNano() + (long) cycles * cycleDuration);

				if (cycles > 10)
				{
					cycles = 10;
				}

				return cycles;
			}
		}
	}
}
