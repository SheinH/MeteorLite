package osrs;

import java.util.zip.CRC32;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("kk")
@Implements("NetCache")
public class NetCache {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Lmp;"
   )
   @Export("NetCache_socket")
   public static AbstractSocket NetCache_socket;
   @ObfuscatedName("w")
   @ObfuscatedGetter(
      intValue = -656127147
   )
   @Export("NetCache_loadTime")
   public static int NetCache_loadTime = 0;
   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "Lnq;"
   )
   @Export("NetCache_pendingPriorityWrites")
   public static NodeHashTable NetCache_pendingPriorityWrites = new NodeHashTable(4096);
   @ObfuscatedName("o")
   @ObfuscatedGetter(
      intValue = 1571433569
   )
   @Export("NetCache_pendingPriorityWritesCount")
   public static int NetCache_pendingPriorityWritesCount = 0;
   @ObfuscatedName("g")
   @ObfuscatedSignature(
      descriptor = "Lnq;"
   )
   @Export("NetCache_pendingPriorityResponses")
   public static NodeHashTable NetCache_pendingPriorityResponses = new NodeHashTable(32);
   @ObfuscatedName("e")
   @ObfuscatedGetter(
      intValue = -1196232217
   )
   @Export("NetCache_pendingPriorityResponsesCount")
   public static int NetCache_pendingPriorityResponsesCount = 0;
   @ObfuscatedName("p")
   @ObfuscatedSignature(
      descriptor = "Lka;"
   )
   @Export("NetCache_pendingWritesQueue")
   public static DualNodeDeque NetCache_pendingWritesQueue = new DualNodeDeque();
   @ObfuscatedName("j")
   @ObfuscatedSignature(
      descriptor = "Lnq;"
   )
   @Export("NetCache_pendingWrites")
   static NodeHashTable NetCache_pendingWrites = new NodeHashTable(4096);
   @ObfuscatedName("b")
   @ObfuscatedGetter(
      intValue = -1794044669
   )
   @Export("NetCache_pendingWritesCount")
   public static int NetCache_pendingWritesCount = 0;
   @ObfuscatedName("x")
   @ObfuscatedSignature(
      descriptor = "Lnq;"
   )
   @Export("NetCache_pendingResponses")
   public static NodeHashTable NetCache_pendingResponses = new NodeHashTable(4096);
   @ObfuscatedName("y")
   @ObfuscatedGetter(
      intValue = 1069606977
   )
   @Export("NetCache_pendingResponsesCount")
   public static int NetCache_pendingResponsesCount = 0;
   @ObfuscatedName("l")
   @ObfuscatedSignature(
      descriptor = "Lop;"
   )
   @Export("NetCache_responseHeaderBuffer")
   public static Buffer NetCache_responseHeaderBuffer = new Buffer(8);
   @ObfuscatedName("n")
   @ObfuscatedGetter(
      intValue = 862783157
   )
   public static int field3707 = 0;
   @ObfuscatedName("q")
   @Export("NetCache_crc")
   public static CRC32 NetCache_crc = new CRC32();
   @ObfuscatedName("r")
   @ObfuscatedSignature(
      descriptor = "[Lkd;"
   )
   @Export("NetCache_archives")
   public static Archive[] NetCache_archives = new Archive[256];
   @ObfuscatedName("m")
   public static byte field3719 = 0;
   @ObfuscatedName("c")
   @ObfuscatedGetter(
      intValue = -883980279
   )
   @Export("NetCache_crcMismatches")
   public static int NetCache_crcMismatches = 0;
   @ObfuscatedName("f")
   @ObfuscatedGetter(
      intValue = 735412947
   )
   @Export("NetCache_ioExceptions")
   public static int NetCache_ioExceptions = 0;
}
