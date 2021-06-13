package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("jv")
@Implements("AbstractArchive")
public abstract class AbstractArchive {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Log;"
   )
   @Export("gzipDecompressor")
   static GZipDecompressor gzipDecompressor = new GZipDecompressor();
   @ObfuscatedName("al")
   @ObfuscatedGetter(
      intValue = 269464329
   )
   static int field3589 = 0;
   @ObfuscatedName("c")
   @ObfuscatedGetter(
      intValue = 15338207
   )
   @Export("groupCount")
   int groupCount;
   @ObfuscatedName("e")
   @Export("groupIds")
   int[] groupIds;
   @ObfuscatedName("g")
   @Export("groupNameHashes")
   int[] groupNameHashes;
   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "Lou;"
   )
   @Export("groupNameHashTable")
   IntHashTable groupNameHashTable;
   @ObfuscatedName("k")
   @Export("groupCrcs")
   int[] groupCrcs;
   @ObfuscatedName("m")
   @Export("groupVersions")
   int[] groupVersions;
   @ObfuscatedName("x")
   @Export("fileCounts")
   int[] fileCounts;
   @ObfuscatedName("z")
   @Export("fileIds")
   int[][] fileIds;
   @ObfuscatedName("w")
   @Export("fileNameHashes")
   int[][] fileNameHashes;
   @ObfuscatedName("t")
   @ObfuscatedSignature(
      descriptor = "[Lou;"
   )
   @Export("fileNameHashTables")
   IntHashTable[] fileNameHashTables;
   @ObfuscatedName("h")
   @Export("groups")
   Object[] groups;
   @ObfuscatedName("q")
   @Export("files")
   Object[][] files;
   @ObfuscatedName("ae")
   @ObfuscatedGetter(
      intValue = 2055325169
   )
   @Export("hash")
   public int hash;
   @ObfuscatedName("ap")
   @Export("releaseGroups")
   boolean releaseGroups;
   @ObfuscatedName("ab")
   @Export("shallowFiles")
   boolean shallowFiles;

   AbstractArchive(boolean var1, boolean var2) {
      this.releaseGroups = var1;
      this.shallowFiles = var2;
   }

   @ObfuscatedName("f")
   @ObfuscatedSignature(
      descriptor = "(IB)V",
      garbageValue = "108"
   )
   @Export("loadRegionFromGroup")
   void loadRegionFromGroup(int var1) {
   }

   @ObfuscatedName("y")
   @ObfuscatedSignature(
      descriptor = "(IB)V",
      garbageValue = "0"
   )
   @Export("loadGroup")
   void loadGroup(int var1) {
   }

   @ObfuscatedName("d")
   @ObfuscatedSignature(
      descriptor = "(II)I",
      garbageValue = "-1767586109"
   )
   @Export("groupLoadPercent")
   int groupLoadPercent(int var1) {
      return this.groups[var1] != null ? 100 : 0;
   }

   @ObfuscatedName("aq")
   @ObfuscatedSignature(
      descriptor = "([BI)V",
      garbageValue = "994270424"
   )
   @Export("decodeIndex")
   void decodeIndex(byte[] var1) {
      int var2 = var1.length;
      int var3 = GrandExchangeOfferOwnWorldComparator.method1176(var1, 0, var2);
      this.hash = var3;
      Buffer var4 = new Buffer(UserComparator6.decompressBytes(var1));
      int var5 = var4.readUnsignedByte();
      if (var5 >= 5 && var5 <= 7) {
         if (var5 >= 6) {
            var4.readInt();
         }

         int var6 = var4.readUnsignedByte();
         if (var5 >= 7) {
            this.groupCount = var4.method6569();
         } else {
            this.groupCount = var4.readUnsignedShort();
         }

         int var7 = 0;
         int var8 = -1;
         this.groupIds = new int[this.groupCount];
         int var9;
         if (var5 >= 7) {
            for(var9 = 0; var9 < this.groupCount; ++var9) {
               this.groupIds[var9] = var7 += var4.method6569();
               if (this.groupIds[var9] > var8) {
                  var8 = this.groupIds[var9];
               }
            }
         } else {
            for(var9 = 0; var9 < this.groupCount; ++var9) {
               this.groupIds[var9] = var7 += var4.readUnsignedShort();
               if (this.groupIds[var9] > var8) {
                  var8 = this.groupIds[var9];
               }
            }
         }

         this.groupCrcs = new int[var8 + 1];
         this.groupVersions = new int[var8 + 1];
         this.fileCounts = new int[var8 + 1];
         this.fileIds = new int[var8 + 1][];
         this.groups = new Object[var8 + 1];
         this.files = new Object[var8 + 1][];
         if (var6 != 0) {
            this.groupNameHashes = new int[var8 + 1];

            for(var9 = 0; var9 < this.groupCount; ++var9) {
               this.groupNameHashes[this.groupIds[var9]] = var4.readInt();
            }

            this.groupNameHashTable = new IntHashTable(this.groupNameHashes);
         }

         for(var9 = 0; var9 < this.groupCount; ++var9) {
            this.groupCrcs[this.groupIds[var9]] = var4.readInt();
         }

         for(var9 = 0; var9 < this.groupCount; ++var9) {
            this.groupVersions[this.groupIds[var9]] = var4.readInt();
         }

         for(var9 = 0; var9 < this.groupCount; ++var9) {
            this.fileCounts[this.groupIds[var9]] = var4.readUnsignedShort();
         }

         int var10;
         int var11;
         int var12;
         int var13;
         int var14;
         if (var5 >= 7) {
            for(var9 = 0; var9 < this.groupCount; ++var9) {
               var10 = this.groupIds[var9];
               var11 = this.fileCounts[var10];
               var7 = 0;
               var12 = -1;
               this.fileIds[var10] = new int[var11];

               for(var13 = 0; var13 < var11; ++var13) {
                  var14 = this.fileIds[var10][var13] = var7 += var4.method6569();
                  if (var14 > var12) {
                     var12 = var14;
                  }
               }

               this.files[var10] = new Object[var12 + 1];
            }
         } else {
            for(var9 = 0; var9 < this.groupCount; ++var9) {
               var10 = this.groupIds[var9];
               var11 = this.fileCounts[var10];
               var7 = 0;
               var12 = -1;
               this.fileIds[var10] = new int[var11];

               for(var13 = 0; var13 < var11; ++var13) {
                  var14 = this.fileIds[var10][var13] = var7 += var4.readUnsignedShort();
                  if (var14 > var12) {
                     var12 = var14;
                  }
               }

               this.files[var10] = new Object[var12 + 1];
            }
         }

         if (var6 != 0) {
            this.fileNameHashes = new int[var8 + 1][];
            this.fileNameHashTables = new IntHashTable[var8 + 1];

            for(var9 = 0; var9 < this.groupCount; ++var9) {
               var10 = this.groupIds[var9];
               var11 = this.fileCounts[var10];
               this.fileNameHashes[var10] = new int[this.files[var10].length];

               for(var12 = 0; var12 < var11; ++var12) {
                  this.fileNameHashes[var10][this.fileIds[var10][var12]] = var4.readInt();
               }

               this.fileNameHashTables[var10] = new IntHashTable(this.fileNameHashes[var10]);
            }
         }

      } else {
         throw new RuntimeException("");
      }
   }

   @ObfuscatedName("aw")
   @ObfuscatedSignature(
      descriptor = "(III)[B",
      garbageValue = "-26460885"
   )
   @Export("takeFile")
   public byte[] takeFile(int var1, int var2) {
      return this.takeFileEncrypted(var1, var2, (int[])null);
   }

   @ObfuscatedName("af")
   @ObfuscatedSignature(
      descriptor = "(II[II)[B",
      garbageValue = "744457350"
   )
   @Export("takeFileEncrypted")
   public byte[] takeFileEncrypted(int var1, int var2, int[] var3) {
      if (var1 >= 0 && var1 < this.files.length && this.files[var1] != null && var2 >= 0 && var2 < this.files[var1].length) {
         if (this.files[var1][var2] == null) {
            boolean var4 = this.buildFiles(var1, var3);
            if (!var4) {
               this.loadGroup(var1);
               var4 = this.buildFiles(var1, var3);
               if (!var4) {
                  return null;
               }
            }
         }

         byte[] var5 = ByteArrayPool.method5573(this.files[var1][var2], false);
         if (this.shallowFiles) {
            this.files[var1][var2] = null;
         }

         return var5;
      } else {
         return null;
      }
   }

   @ObfuscatedName("ak")
   @ObfuscatedSignature(
      descriptor = "(IIB)Z",
      garbageValue = "57"
   )
   @Export("tryLoadFile")
   public boolean tryLoadFile(int var1, int var2) {
      if (var1 >= 0 && var1 < this.files.length && this.files[var1] != null && var2 >= 0 && var2 < this.files[var1].length) {
         if (this.files[var1][var2] != null) {
            return true;
         } else if (this.groups[var1] != null) {
            return true;
         } else {
            this.loadGroup(var1);
            return this.groups[var1] != null;
         }
      } else {
         return false;
      }
   }

   @ObfuscatedName("ay")
   @ObfuscatedSignature(
      descriptor = "(IB)Z",
      garbageValue = "0"
   )
   public boolean method4963(int var1) {
      if (this.files.length == 1) {
         return this.tryLoadFile(0, var1);
      } else if (this.files[var1].length == 1) {
         return this.tryLoadFile(var1, 0);
      } else {
         throw new RuntimeException();
      }
   }

   @ObfuscatedName("aa")
   @ObfuscatedSignature(
      descriptor = "(IB)Z",
      garbageValue = "112"
   )
   @Export("tryLoadGroup")
   public boolean tryLoadGroup(int var1) {
      if (this.groups[var1] != null) {
         return true;
      } else {
         this.loadGroup(var1);
         return this.groups[var1] != null;
      }
   }

   @ObfuscatedName("au")
   @ObfuscatedSignature(
      descriptor = "(I)Z",
      garbageValue = "2118992751"
   )
   @Export("isFullyLoaded")
   public boolean isFullyLoaded() {
      boolean var1 = true;

      for(int var2 = 0; var2 < this.groupIds.length; ++var2) {
         int var3 = this.groupIds[var2];
         if (this.groups[var3] == null) {
            this.loadGroup(var3);
            if (this.groups[var3] == null) {
               var1 = false;
            }
         }
      }

      return var1;
   }

   @ObfuscatedName("an")
   @ObfuscatedSignature(
      descriptor = "(IB)[B",
      garbageValue = "-116"
   )
   @Export("takeFileFlat")
   public byte[] takeFileFlat(int var1) {
      if (this.files.length == 1) {
         return this.takeFile(0, var1);
      } else if (this.files[var1].length == 1) {
         return this.takeFile(var1, 0);
      } else {
         throw new RuntimeException();
      }
   }

   @ObfuscatedName("bd")
   @ObfuscatedSignature(
      descriptor = "(III)[B",
      garbageValue = "791709889"
   )
   @Export("getFile")
   public byte[] getFile(int var1, int var2) {
      if (var1 >= 0 && var1 < this.files.length && this.files[var1] != null && var2 >= 0 && var2 < this.files[var1].length) {
         if (this.files[var1][var2] == null) {
            boolean var3 = this.buildFiles(var1, (int[])null);
            if (!var3) {
               this.loadGroup(var1);
               var3 = this.buildFiles(var1, (int[])null);
               if (!var3) {
                  return null;
               }
            }
         }

         byte[] var4 = ByteArrayPool.method5573(this.files[var1][var2], false);
         return var4;
      } else {
         return null;
      }
   }

   @ObfuscatedName("bt")
   @ObfuscatedSignature(
      descriptor = "(II)[B",
      garbageValue = "444746714"
   )
   @Export("getFileFlat")
   public byte[] getFileFlat(int var1) {
      if (this.files.length == 1) {
         return this.getFile(0, var1);
      } else if (this.files[var1].length == 1) {
         return this.getFile(var1, 0);
      } else {
         throw new RuntimeException();
      }
   }

   @ObfuscatedName("bq")
   @ObfuscatedSignature(
      descriptor = "(IB)[I",
      garbageValue = "8"
   )
   @Export("getGroupFileIds")
   public int[] getGroupFileIds(int var1) {
      return var1 >= 0 && var1 < this.fileIds.length ? this.fileIds[var1] : null;
   }

   @ObfuscatedName("bu")
   @ObfuscatedSignature(
      descriptor = "(II)I",
      garbageValue = "997984533"
   )
   @Export("getGroupFileCount")
   public int getGroupFileCount(int var1) {
      return this.files[var1].length;
   }

   @ObfuscatedName("bl")
   @ObfuscatedSignature(
      descriptor = "(I)I",
      garbageValue = "756558186"
   )
   @Export("getGroupCount")
   public int getGroupCount() {
      return this.files.length;
   }

   @ObfuscatedName("bv")
   @ObfuscatedSignature(
      descriptor = "(B)V",
      garbageValue = "2"
   )
   @Export("clearGroups")
   public void clearGroups() {
      for(int var1 = 0; var1 < this.groups.length; ++var1) {
         this.groups[var1] = null;
      }

   }

   @ObfuscatedName("bm")
   @ObfuscatedSignature(
      descriptor = "(II)V",
      garbageValue = "-1987732503"
   )
   @Export("clearFilesGroup")
   public void clearFilesGroup(int var1) {
      for(int var2 = 0; var2 < this.files[var1].length; ++var2) {
         this.files[var1][var2] = null;
      }

   }

   @ObfuscatedName("bz")
   @ObfuscatedSignature(
      descriptor = "(B)V",
      garbageValue = "94"
   )
   @Export("clearFiles")
   public void clearFiles() {
      for(int var1 = 0; var1 < this.files.length; ++var1) {
         if (this.files[var1] != null) {
            for(int var2 = 0; var2 < this.files[var1].length; ++var2) {
               this.files[var1][var2] = null;
            }
         }
      }

   }

   @ObfuscatedName("bh")
   @ObfuscatedSignature(
      descriptor = "(I[II)Z",
      garbageValue = "1746231171"
   )
   @Export("buildFiles")
   boolean buildFiles(int var1, int[] var2) {
      if (this.groups[var1] == null) {
         return false;
      } else {
         int var3 = this.fileCounts[var1];
         int[] var4 = this.fileIds[var1];
         Object[] var5 = this.files[var1];
         boolean var6 = true;

         for(int var7 = 0; var7 < var3; ++var7) {
            if (var5[var4[var7]] == null) {
               var6 = false;
               break;
            }
         }

         if (var6) {
            return true;
         } else {
            byte[] var18;
            if (var2 != null && (var2[0] != 0 || var2[1] != 0 || var2[2] != 0 || var2[3] != 0)) {
               var18 = ByteArrayPool.method5573(this.groups[var1], true);
               Buffer var8 = new Buffer(var18);
               var8.xteaDecrypt(var2, 5, var8.array.length);
            } else {
               var18 = ByteArrayPool.method5573(this.groups[var1], false);
            }

            byte[] var19 = UserComparator6.decompressBytes(var18);
            if (this.releaseGroups) {
               this.groups[var1] = null;
            }

            if (var3 > 1) {
               int var9 = var19.length;
               --var9;
               int var10 = var19[var9] & 255;
               var9 -= var10 * var3 * 4;
               Buffer var11 = new Buffer(var19);
               int[] var12 = new int[var3];
               var11.offset = var9;

               int var13;
               int var14;
               for(int var15 = 0; var15 < var10; ++var15) {
                  var13 = 0;

                  for(var14 = 0; var14 < var3; ++var14) {
                     var13 += var11.readInt();
                     var12[var14] += var13;
                  }
               }

               byte[][] var20 = new byte[var3][];

               for(var13 = 0; var13 < var3; ++var13) {
                  var20[var13] = new byte[var12[var13]];
                  var12[var13] = 0;
               }

               var11.offset = var9;
               var13 = 0;

               for(var14 = 0; var14 < var10; ++var14) {
                  int var16 = 0;

                  for(int var17 = 0; var17 < var3; ++var17) {
                     var16 += var11.readInt();
                     System.arraycopy(var19, var13, var20[var17], var12[var17], var16);
                     var12[var17] += var16;
                     var13 += var16;
                  }
               }

               for(var14 = 0; var14 < var3; ++var14) {
                  if (!this.shallowFiles) {
                     var5[var4[var14]] = GrandExchangeOfferWorldComparator.method5079(var20[var14], false);
                  } else {
                     var5[var4[var14]] = var20[var14];
                  }
               }
            } else if (!this.shallowFiles) {
               var5[var4[0]] = GrandExchangeOfferWorldComparator.method5079(var19, false);
            } else {
               var5[var4[0]] = var19;
            }

            return true;
         }
      }
   }

   @ObfuscatedName("bs")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;I)I",
      garbageValue = "-1575580840"
   )
   @Export("getGroupId")
   public int getGroupId(String var1) {
      var1 = var1.toLowerCase();
      return this.groupNameHashTable.get(GraphicsObject.hashString(var1));
   }

   @ObfuscatedName("br")
   @ObfuscatedSignature(
      descriptor = "(ILjava/lang/String;I)I",
      garbageValue = "1997157248"
   )
   @Export("getFileId")
   public int getFileId(int var1, String var2) {
      var2 = var2.toLowerCase();
      return this.fileNameHashTables[var1].get(GraphicsObject.hashString(var2));
   }

   @ObfuscatedName("bf")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;Ljava/lang/String;B)Z",
      garbageValue = "0"
   )
   @Export("isValidFileName")
   public boolean isValidFileName(String var1, String var2) {
      var1 = var1.toLowerCase();
      var2 = var2.toLowerCase();
      int var3 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      if (var3 < 0) {
         return false;
      } else {
         int var4 = this.fileNameHashTables[var3].get(GraphicsObject.hashString(var2));
         return var4 >= 0;
      }
   }

   @ObfuscatedName("ba")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;Ljava/lang/String;I)[B",
      garbageValue = "-2113660686"
   )
   @Export("takeFileByNames")
   public byte[] takeFileByNames(String var1, String var2) {
      var1 = var1.toLowerCase();
      var2 = var2.toLowerCase();
      int var3 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      int var4 = this.fileNameHashTables[var3].get(GraphicsObject.hashString(var2));
      return this.takeFile(var3, var4);
   }

   @ObfuscatedName("be")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;Ljava/lang/String;I)Z",
      garbageValue = "-1619143397"
   )
   @Export("tryLoadFileByNames")
   public boolean tryLoadFileByNames(String var1, String var2) {
      var1 = var1.toLowerCase();
      var2 = var2.toLowerCase();
      int var3 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      int var4 = this.fileNameHashTables[var3].get(GraphicsObject.hashString(var2));
      return this.tryLoadFile(var3, var4);
   }

   @ObfuscatedName("bj")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;I)Z",
      garbageValue = "1308287337"
   )
   @Export("tryLoadGroupByName")
   public boolean tryLoadGroupByName(String var1) {
      var1 = var1.toLowerCase();
      int var2 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      return this.tryLoadGroup(var2);
   }

   @ObfuscatedName("bx")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;B)V",
      garbageValue = "-13"
   )
   @Export("loadRegionFromName")
   public void loadRegionFromName(String var1) {
      var1 = var1.toLowerCase();
      int var2 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      if (var2 >= 0) {
         this.loadRegionFromGroup(var2);
      }

   }

   @ObfuscatedName("bp")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/String;I)I",
      garbageValue = "-469747920"
   )
   @Export("groupLoadPercentByName")
   public int groupLoadPercentByName(String var1) {
      var1 = var1.toLowerCase();
      int var2 = this.groupNameHashTable.get(GraphicsObject.hashString(var1));
      return this.groupLoadPercent(var2);
   }

   @ObfuscatedName("p")
   @ObfuscatedSignature(
      descriptor = "(Ljv;Ljv;IIB)Lkt;",
      garbageValue = "7"
   )
   public static Font method5018(AbstractArchive var0, AbstractArchive var1, int var2, int var3) {
      byte[] var4 = var0.takeFile(var2, var3);
      boolean var5;
      if (var4 == null) {
         var5 = false;
      } else {
         ItemLayer.SpriteBuffer_decode(var4);
         var5 = true;
      }

      if (!var5) {
         return null;
      } else {
         byte[] var6 = var1.takeFile(var2, var3);
         Font var7;
         if (var6 == null) {
            var7 = null;
         } else {
            Font var8 = new Font(var6, WorldMapDecoration.SpriteBuffer_xOffsets, Calendar.SpriteBuffer_yOffsets, class396.SpriteBuffer_spriteWidths, class302.SpriteBuffer_spriteHeights, MilliClock.SpriteBuffer_spritePalette, class396.SpriteBuffer_pixels);
            MilliClock.method2587();
            var7 = var8;
         }

         return var7;
      }
   }
}
