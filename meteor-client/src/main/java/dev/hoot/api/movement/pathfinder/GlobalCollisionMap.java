package dev.hoot.api.movement.pathfinder;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class GlobalCollisionMap implements CollisionMap {
	private final BitSet4D[] regions = new BitSet4D[256 * 256];

	public GlobalCollisionMap(byte[] data) {
		var buffer = ByteBuffer.wrap(data);

		while (buffer.hasRemaining()) {
			var region = buffer.getShort() & 0xffff;
			regions[region] = new BitSet4D(buffer, 64, 64, 4, 2);
		}
	}

	public byte[] toBytes() {
		var regionCount = (int) Arrays.stream(regions).filter(Objects::nonNull).count();
		var buffer = ByteBuffer.allocate(regionCount * (2 + 64 * 64 * 4 * 2 / 8));

		for (var i = 0; i < regions.length; i++) {
			if (regions[i] != null) {
				buffer.putShort((short) i);
				regions[i].write(buffer);
			}
		}

		return buffer.array();
	}

	public void set(int x, int y, int z, int w, boolean value) {
		var region = regions[x / 64 * 256 + y / 64];

		if (region == null) {
			return;
		}

		region.set(x % 64, y % 64, z, w, value);
	}

	public boolean get(int x, int y, int z, int w) {
		var region = regions[x / 64 * 256 + y / 64];

		if (region == null) {
			return false;
		}

		return region.get(x % 64, y % 64, z, w);
	}

	public void createRegion(int region) {
		regions[region] = new BitSet4D(64, 64, 4, 2);
		regions[region].setAll(true);
	}

	@Override
	public boolean n(int x, int y, int z) {
		return get(x, y, z, 0);
	}

	@Override
	public boolean e(int x, int y, int z) {
		return get(x, y, z, 1);
	}
}
