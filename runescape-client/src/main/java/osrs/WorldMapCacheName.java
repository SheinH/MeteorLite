package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("gu")
@Implements("WorldMapCacheName")
public class WorldMapCacheName {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Lgu;"
   )
   public static final WorldMapCacheName field2210 = new WorldMapCacheName("details");
   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "Lgu;"
   )
   public static final WorldMapCacheName field2211 = new WorldMapCacheName("compositemap");
   @ObfuscatedName("s")
   @ObfuscatedSignature(
      descriptor = "Lgu;"
   )
   public static final WorldMapCacheName field2215 = new WorldMapCacheName("compositetexture");
   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "Lgu;"
   )
   static final WorldMapCacheName field2213 = new WorldMapCacheName("area");
   @ObfuscatedName("o")
   @ObfuscatedSignature(
      descriptor = "Lgu;"
   )
   public static final WorldMapCacheName field2214 = new WorldMapCacheName("labels");
   @ObfuscatedName("g")
   @Export("name")
   public final String name;

   WorldMapCacheName(String var1) {
      this.name = var1;
   }

   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "(I)V",
      garbageValue = "605723533"
   )
   public static void method4027() {
      class247.midiPcmStream.clear();
      class247.musicPlayerStatus = 1;
      class128.musicTrackArchive = null;
   }
}
