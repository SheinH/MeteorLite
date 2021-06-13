package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("cm")
@Implements("GraphicsObject")
public final class GraphicsObject extends Renderable {
   @ObfuscatedName("v")
   @ObfuscatedGetter(
      intValue = -177863619
   )
   @Export("id")
   int id;
   @ObfuscatedName("n")
   @ObfuscatedGetter(
      intValue = 2035613177
   )
   @Export("cycleStart")
   int cycleStart;
   @ObfuscatedName("f")
   @ObfuscatedGetter(
      intValue = -1151722421
   )
   @Export("plane")
   int plane;
   @ObfuscatedName("y")
   @ObfuscatedSignature(
      descriptor = "Lfl;"
   )
   @Export("sequenceDefinition")
   SequenceDefinition sequenceDefinition;
   @ObfuscatedName("p")
   @ObfuscatedGetter(
      intValue = 886051665
   )
   @Export("x")
   int x;
   @ObfuscatedName("j")
   @ObfuscatedGetter(
      intValue = -1737947099
   )
   @Export("y")
   int y;
   @ObfuscatedName("r")
   @ObfuscatedGetter(
      intValue = 1509450081
   )
   @Export("height")
   int height;
   @ObfuscatedName("b")
   @ObfuscatedGetter(
      intValue = 1147002155
   )
   @Export("frame")
   int frame = 0;
   @ObfuscatedName("d")
   @ObfuscatedGetter(
      intValue = -1179440823
   )
   @Export("frameCycle")
   int frameCycle = 0;
   @ObfuscatedName("s")
   @Export("isFinished")
   boolean isFinished = false;

   GraphicsObject(int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.id = var1;
      this.plane = var2;
      this.x = var3;
      this.y = var4;
      this.height = var5;
      this.cycleStart = var7 + var6;
      int var8 = VarcInt.SpotAnimationDefinition_get(this.id).sequence;
      if (var8 != -1) {
         this.isFinished = false;
         this.sequenceDefinition = LoginScreenAnimation.SequenceDefinition_get(var8);
      } else {
         this.isFinished = true;
      }

   }

   @ObfuscatedName("v")
   @ObfuscatedSignature(
      descriptor = "(II)V",
      garbageValue = "-1622052519"
   )
   @Export("advance")
   final void advance(int var1) {
      if (!this.isFinished) {
         this.frameCycle += var1;

         while(this.frameCycle > this.sequenceDefinition.frameLengths[this.frame]) {
            this.frameCycle -= this.sequenceDefinition.frameLengths[this.frame];
            ++this.frame;
            if (this.frame >= this.sequenceDefinition.frameIds.length) {
               this.isFinished = true;
               break;
            }
         }
      }

   }

   @ObfuscatedName("n")
   @ObfuscatedSignature(
      descriptor = "(B)Lgr;",
      garbageValue = "-76"
   )
   @Export("getModel")
   protected final Model getModel() {
      SpotAnimationDefinition var1 = VarcInt.SpotAnimationDefinition_get(this.id);
      Model var2;
      if (!this.isFinished) {
         var2 = var1.getModel(this.frame);
      } else {
         var2 = var1.getModel(-1);
      }

      return var2 == null ? null : var2;
   }

   @ObfuscatedName("b")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/CharSequence;I)I",
      garbageValue = "-1841267567"
   )
   @Export("hashString")
   public static int hashString(CharSequence var0) {
      int var1 = var0.length();
      int var2 = 0;

      for(int var3 = 0; var3 < var1; ++var3) {
         var2 = (var2 << 5) - var2 + ServerPacket.charToByteCp1252(var0.charAt(var3));
      }

      return var2;
   }

   @ObfuscatedName("b")
   @ObfuscatedSignature(
      descriptor = "(I)Lbg;",
      garbageValue = "503233534"
   )
   @Export("getNextWorldListWorld")
   static World getNextWorldListWorld() {
      return World.World_listCount < World.World_count ? Tiles.World_worlds[++World.World_listCount - 1] : null;
   }

   @ObfuscatedName("av")
   @ObfuscatedSignature(
      descriptor = "(ILcf;ZI)I",
      garbageValue = "-510374773"
   )
   static int method1892(int var0, Script var1, boolean var2) {
      if (var0 == 6500) {
         Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = SoundSystem.loadWorlds() ? 1 : 0;
         return 1;
      } else {
         World var3;
         if (var0 == 6501) {
            var3 = SpotAnimationDefinition.worldListStart();
            if (var3 != null) {
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.id;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.properties;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var3.activity;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.location;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.population;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var3.host;
            } else {
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = -1;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
            }

            return 1;
         } else if (var0 == 6502) {
            var3 = getNextWorldListWorld();
            if (var3 != null) {
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.id;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.properties;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var3.activity;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.location;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var3.population;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var3.host;
            } else {
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = -1;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
               Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
            }

            return 1;
         } else {
            int var4;
            World var5;
            int var6;
            if (var0 == 6506) {
               var4 = Interpreter.Interpreter_intStack[--class44.Interpreter_intStackSize];
               var5 = null;

               for(var6 = 0; var6 < World.World_count; ++var6) {
                  if (var4 == Tiles.World_worlds[var6].id) {
                     var5 = Tiles.World_worlds[var6];
                     break;
                  }
               }

               if (var5 != null) {
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.id;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.properties;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var5.activity;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.location;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.population;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var5.host;
               } else {
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = -1;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
               }

               return 1;
            } else if (var0 == 6507) {
               class44.Interpreter_intStackSize -= 4;
               var4 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize];
               boolean var9 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 1] == 1;
               var6 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 2];
               boolean var10 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 3] == 1;
               DirectByteArrayCopier.sortWorldList(var4, var9, var6, var10);
               return 1;
            } else if (var0 != 6511) {
               if (var0 == 6512) {
                  Client.followerOpsLowPriority = Interpreter.Interpreter_intStack[--class44.Interpreter_intStackSize] == 1;
                  return 1;
               } else {
                  int var7;
                  ParamComposition var8;
                  if (var0 == 6513) {
                     class44.Interpreter_intStackSize -= 2;
                     var4 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize];
                     var7 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 1];
                     var8 = Varcs.getParamDefinition(var7);
                     if (var8.isString()) {
                        Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = StructComposition.getNpcDefinition(var4).getStringParam(var7, var8.defaultStr);
                     } else {
                        Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = StructComposition.getNpcDefinition(var4).getIntParam(var7, var8.defaultInt);
                     }

                     return 1;
                  } else if (var0 == 6514) {
                     class44.Interpreter_intStackSize -= 2;
                     var4 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize];
                     var7 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 1];
                     var8 = Varcs.getParamDefinition(var7);
                     if (var8.isString()) {
                        Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = class23.getObjectDefinition(var4).getStringParam(var7, var8.defaultStr);
                     } else {
                        Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = class23.getObjectDefinition(var4).getIntParam(var7, var8.defaultInt);
                     }

                     return 1;
                  } else if (var0 == 6515) {
                     class44.Interpreter_intStackSize -= 2;
                     var4 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize];
                     var7 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 1];
                     var8 = Varcs.getParamDefinition(var7);
                     if (var8.isString()) {
                        Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = class260.ItemDefinition_get(var4).getStringParam(var7, var8.defaultStr);
                     } else {
                        Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = class260.ItemDefinition_get(var4).getIntParam(var7, var8.defaultInt);
                     }

                     return 1;
                  } else if (var0 == 6516) {
                     class44.Interpreter_intStackSize -= 2;
                     var4 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize];
                     var7 = Interpreter.Interpreter_intStack[class44.Interpreter_intStackSize + 1];
                     var8 = Varcs.getParamDefinition(var7);
                     if (var8.isString()) {
                        Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = ArchiveLoader.StructDefinition_getStructDefinition(var4).getStringParam(var7, var8.defaultStr);
                     } else {
                        Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = ArchiveLoader.StructDefinition_getStructDefinition(var4).getIntParam(var7, var8.defaultInt);
                     }

                     return 1;
                  } else if (var0 == 6518) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = Client.onMobile ? 1 : 0;
                     return 1;
                  } else if (var0 == 6519) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = Client.clientType & 3;
                     return 1;
                  } else if (var0 == 6520) {
                     return 1;
                  } else if (var0 == 6521) {
                     return 1;
                  } else if (var0 == 6522) {
                     --Interpreter.Interpreter_stringStackSize;
                     --class44.Interpreter_intStackSize;
                     return 1;
                  } else if (var0 == 6523) {
                     --Interpreter.Interpreter_stringStackSize;
                     --class44.Interpreter_intStackSize;
                     return 1;
                  } else if (var0 == 6524) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = -1;
                     return 1;
                  } else if (var0 == 6525) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 1;
                     return 1;
                  } else if (var0 == 6526) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 1;
                     return 1;
                  } else if (var0 == 6527) {
                     Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = Client.field643;
                     return 1;
                  } else {
                     return 2;
                  }
               }
            } else {
               var4 = Interpreter.Interpreter_intStack[--class44.Interpreter_intStackSize];
               if (var4 >= 0 && var4 < World.World_count) {
                  var5 = Tiles.World_worlds[var4];
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.id;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.properties;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var5.activity;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.location;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = var5.population;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = var5.host;
               } else {
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = -1;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_intStack[++class44.Interpreter_intStackSize - 1] = 0;
                  Interpreter.Interpreter_stringStack[++Interpreter.Interpreter_stringStackSize - 1] = "";
               }

               return 1;
            }
         }
      }
   }

   @ObfuscatedName("ht")
   @ObfuscatedSignature(
      descriptor = "(IB)V",
      garbageValue = "-27"
   )
   @Export("createMinimapSprite")
   static final void createMinimapSprite(int var0) {
      int[] var1 = GameEngine.sceneMinimapSprite.pixels;
      int var2 = var1.length;

      int var3;
      for(var3 = 0; var3 < var2; ++var3) {
         var1[var3] = 0;
      }

      int var4;
      int var5;
      for(var3 = 1; var3 < 103; ++var3) {
         var4 = (103 - var3) * 2048 + 24628;

         for(var5 = 1; var5 < 103; ++var5) {
            if ((Tiles.Tiles_renderFlags[var0][var5][var3] & 24) == 0) {
               AbstractSocket.scene.drawTileMinimap(var1, var4, 512, var0, var5, var3);
            }

            if (var0 < 3 && (Tiles.Tiles_renderFlags[var0 + 1][var5][var3] & 8) != 0) {
               AbstractSocket.scene.drawTileMinimap(var1, var4, 512, var0 + 1, var5, var3);
            }

            var4 += 4;
         }
      }

      var3 = (238 + (int)(Math.random() * 20.0D) - 10 << 16) + (238 + (int)(Math.random() * 20.0D) - 10 << 8) + (238 + (int)(Math.random() * 20.0D) - 10);
      var4 = 238 + (int)(Math.random() * 20.0D) - 10 << 16;
      GameEngine.sceneMinimapSprite.setRaster();

      int var6;
      for(var5 = 1; var5 < 103; ++var5) {
         for(var6 = 1; var6 < 103; ++var6) {
            if ((Tiles.Tiles_renderFlags[var0][var6][var5] & 24) == 0) {
               Coord.drawObject(var0, var6, var5, var3, var4);
            }

            if (var0 < 3 && (Tiles.Tiles_renderFlags[var0 + 1][var6][var5] & 8) != 0) {
               Coord.drawObject(var0 + 1, var6, var5, var3, var4);
            }
         }
      }

      Client.mapIconCount = 0;

      for(var5 = 0; var5 < 104; ++var5) {
         for(var6 = 0; var6 < 104; ++var6) {
            long var7 = AbstractSocket.scene.getFloorDecorationTag(class22.Client_plane, var5, var6);
            if (var7 != 0L) {
               int var9 = class93.Entity_unpackID(var7);
               int var10 = class23.getObjectDefinition(var9).mapIconId;
               if (var10 >= 0 && class6.WorldMapElement_get(var10).field1561) {
                  Client.mapIcons[Client.mapIconCount] = class6.WorldMapElement_get(var10).getSpriteBool(false);
                  Client.mapIconXs[Client.mapIconCount] = var5;
                  Client.mapIconYs[Client.mapIconCount] = var6;
                  ++Client.mapIconCount;
               }
            }
         }
      }

      class26.rasterProvider.apply();
   }
}
