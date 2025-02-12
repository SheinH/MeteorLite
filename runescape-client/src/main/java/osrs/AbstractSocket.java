package osrs;

import java.io.IOException;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("mp")
@Implements("AbstractSocket")
public abstract class AbstractSocket {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(B)V",
      garbageValue = "1"
   )
   @Export("close")
   public abstract void close();

   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "(I)I",
      garbageValue = "1953698452"
   )
   @Export("readUnsignedByte")
   public abstract int readUnsignedByte() throws IOException;

   @ObfuscatedName("s")
   @ObfuscatedSignature(
      descriptor = "(I)I",
      garbageValue = "-1632705310"
   )
   @Export("available")
   public abstract int available() throws IOException;

   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "(II)Z",
      garbageValue = "-1771919747"
   )
   @Export("isAvailable")
   public abstract boolean isAvailable(int var1) throws IOException;

   @ObfuscatedName("o")
   @ObfuscatedSignature(
      descriptor = "([BIII)I",
      garbageValue = "-1246528305"
   )
   @Export("read")
   public abstract int read(byte[] var1, int var2, int var3) throws IOException;

   @ObfuscatedName("e")
   @ObfuscatedSignature(
      descriptor = "([BIII)V",
      garbageValue = "-748791607"
   )
   @Export("write")
   public abstract void write(byte[] var1, int var2, int var3) throws IOException;

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(CI)B",
      garbageValue = "1205000986"
   )
   @Export("charToByteCp1252")
   public static byte charToByteCp1252(char var0) {
      byte var1;
      if (var0 > 0 && var0 < 128 || var0 >= 160 && var0 <= 255) {
         var1 = (byte)var0;
      } else if (var0 == 8364) {
         var1 = -128;
      } else if (var0 == 8218) {
         var1 = -126;
      } else if (var0 == 402) {
         var1 = -125;
      } else if (var0 == 8222) {
         var1 = -124;
      } else if (var0 == 8230) {
         var1 = -123;
      } else if (var0 == 8224) {
         var1 = -122;
      } else if (var0 == 8225) {
         var1 = -121;
      } else if (var0 == 710) {
         var1 = -120;
      } else if (var0 == 8240) {
         var1 = -119;
      } else if (var0 == 352) {
         var1 = -118;
      } else if (var0 == 8249) {
         var1 = -117;
      } else if (var0 == 338) {
         var1 = -116;
      } else if (var0 == 381) {
         var1 = -114;
      } else if (var0 == 8216) {
         var1 = -111;
      } else if (var0 == 8217) {
         var1 = -110;
      } else if (var0 == 8220) {
         var1 = -109;
      } else if (var0 == 8221) {
         var1 = -108;
      } else if (var0 == 8226) {
         var1 = -107;
      } else if (var0 == 8211) {
         var1 = -106;
      } else if (var0 == 8212) {
         var1 = -105;
      } else if (var0 == 732) {
         var1 = -104;
      } else if (var0 == 8482) {
         var1 = -103;
      } else if (var0 == 353) {
         var1 = -102;
      } else if (var0 == 8250) {
         var1 = -101;
      } else if (var0 == 339) {
         var1 = -100;
      } else if (var0 == 382) {
         var1 = -98;
      } else if (var0 == 376) {
         var1 = -97;
      } else {
         var1 = 63;
      }

      return var1;
   }
}
