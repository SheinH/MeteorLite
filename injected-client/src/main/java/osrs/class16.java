package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("a")
public abstract class class16 extends Node {
   @ObfuscatedName("gf")
   @Export("regionMapArchiveIds")
   static int[] regionMapArchiveIds;

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(Lnd;I)V",
      garbageValue = "68071115"
   )
   abstract void vmethod266(Buffer var1);

   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "(Ly;I)V",
      garbageValue = "-1846892157"
   )
   abstract void vmethod264(ClanChannel var1);
}
