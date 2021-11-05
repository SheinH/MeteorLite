package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("bq")
@Implements("ItemContainer")
public class ItemContainer extends Node {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Lnq;"
   )
   @Export("itemContainers")
   static NodeHashTable itemContainers = new NodeHashTable(32);
   @ObfuscatedName("g")
   @ObfuscatedGetter(
      intValue = 1777464625
   )
   @Export("idxCount")
   public static int idxCount;
   @ObfuscatedName("k")
   @ObfuscatedSignature(
      descriptor = "Lau;"
   )
   @Export("pcmPlayerProvider")
   public static class45 pcmPlayerProvider;
   @ObfuscatedName("aw")
   @ObfuscatedSignature(
      descriptor = "Lmo;"
   )
   static Bounds field998;
   @ObfuscatedName("w")
   @Export("ids")
   int[] ids = new int[]{-1};
   @ObfuscatedName("s")
   @Export("quantities")
   int[] quantities = new int[]{0};

   @ObfuscatedName("s")
   @ObfuscatedSignature(
      descriptor = "(IIB)Ljf;",
      garbageValue = "-13"
   )
   @Export("getWidgetChild")
   public static Widget getWidgetChild(int var0, int var1) {
      Widget var2 = UserComparator9.getWidget(var0);
      if (var1 == -1) {
         return var2;
      } else {
         return var2 != null && var2.children != null && var1 < var2.children.length ? var2.children[var1] : null;
      }
   }
}
