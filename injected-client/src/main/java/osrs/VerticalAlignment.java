package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("em")
@Implements("VerticalAlignment")
public enum VerticalAlignment implements Enumerated {
   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "Lem;"
   )
   field1672(0, 0),
   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "Lem;"
   )
   @Export("VerticalAlignment_centered")
   VerticalAlignment_centered(1, 1),
   @ObfuscatedName("f")
   @ObfuscatedSignature(
      descriptor = "Lem;"
   )
   field1670(2, 2);

   @ObfuscatedName("y")
   @ObfuscatedGetter(
      intValue = -1918765839
   )
   @Export("value")
   public final int value;
   @ObfuscatedName("p")
   @ObfuscatedGetter(
      intValue = 76672001
   )
   @Export("id")
   final int id;

   private VerticalAlignment(int var3, int var4) {
      this.value = var3;
      this.id = var4;
   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(I)I",
      garbageValue = "-907662946"
   )
   @Export("rsOrdinal")
   public int rsOrdinal() {
      return this.id;
   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(Ljv;III)[Lop;",
      garbageValue = "-178534371"
   )
   public static IndexedSprite[] method2799(AbstractArchive var0, int var1, int var2) {
      return !class339.method6015(var0, var1, var2) ? null : class24.method262();
   }

   @ObfuscatedName("g")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;II)V",
      garbageValue = "1083454679"
   )
   static final void method2796(String var0, int var1) {
      PacketBufferNode var2 = class21.getPacketBufferNode(ClientPacket.field2621, Client.packetWriter.isaacCipher);
      var2.packetBuffer.writeByte(Tiles.stringCp1252NullTerminatedByteSize(var0) + 1);
      var2.packetBuffer.writeStringCp1252NullTerminated(var0);
      var2.packetBuffer.method6584(var1);
      Client.packetWriter.addNode(var2);
   }
}
