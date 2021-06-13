package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("na")
public enum class386 implements Enumerated {
   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4210(0, 0),
   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4207(3, 2),
   @ObfuscatedName("f")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4208(5, 5),
   @ObfuscatedName("y")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4209(2, 6),
   @ObfuscatedName("p")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4213(1, 7),
   @ObfuscatedName("j")
   @ObfuscatedSignature(
      descriptor = "Lna;"
   )
   field4211(4, 8);

   @ObfuscatedName("r")
   @ObfuscatedGetter(
      intValue = -1819892595
   )
   final int field4212;
   @ObfuscatedName("b")
   @ObfuscatedGetter(
      intValue = -1356390723
   )
   final int field4206;

   private class386(int var3, int var4) {
      this.field4212 = var3;
      this.field4206 = var4;
   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(I)I",
      garbageValue = "-907662946"
   )
   @Export("rsOrdinal")
   public int rsOrdinal() {
      return this.field4206;
   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(IIB)I",
      garbageValue = "-6"
   )
   public static int method6818(int var0, int var1) {
      int var2;
      if (var1 > var0) {
         var2 = var0;
         var0 = var1;
         var1 = var2;
      }

      while(var1 != 0) {
         var2 = var0 % var1;
         var0 = var1;
         var1 = var2;
      }

      return var0;
   }
}
