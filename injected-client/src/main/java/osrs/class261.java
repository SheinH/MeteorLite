package osrs;

import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("jw")
public class class261 {
   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "Ljw;"
   )
   static final class261 field3201 = new class261(51, 27, 800, 0, 16, 16);
   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "Ljw;"
   )
   static final class261 field3200 = new class261(25, 28, 800, 656, 40, 40);
   @ObfuscatedName("f")
   @ObfuscatedGetter(
      intValue = 310151433
   )
   final int field3199;
   @ObfuscatedName("y")
   @ObfuscatedGetter(
      intValue = -715016481
   )
   final int field3202;

   class261(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field3199 = var5;
      this.field3202 = var6;
   }

   @ObfuscatedName("b")
   @ObfuscatedSignature(
      descriptor = "(IB)I",
      garbageValue = "0"
   )
   public static int method4842(int var0) {
      long var1 = ViewportMouse.ViewportMouse_entityTags[var0];
      int var3 = (int)(var1 >>> 14 & 3L);
      return var3;
   }
}
