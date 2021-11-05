package osrs;

import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;
import net.runelite.rs.ScriptOpcodes;

@ObfuscatedName("ci")
@Implements("MouseRecorder")
public class MouseRecorder implements Runnable {
   @ObfuscatedName("t")
   static int[] field1039;
   @ObfuscatedName("ca")
   @ObfuscatedSignature(
      descriptor = "Loe;"
   )
   @Export("worldSelectRightSprite")
   static IndexedSprite worldSelectRightSprite;
   @ObfuscatedName("i")
   @Export("isRunning")
   boolean isRunning = true;
   @ObfuscatedName("w")
   @Export("lock")
   Object lock = new Object();
   @ObfuscatedName("s")
   @ObfuscatedGetter(
      intValue = 1216994745
   )
   @Export("index")
   int index = 0;
   @ObfuscatedName("a")
   @Export("xs")
   int[] xs = new int[500];
   @ObfuscatedName("o")
   @Export("ys")
   int[] ys = new int[500];
   @ObfuscatedName("g")
   @Export("millis")
   long[] millis = new long[500];

   public void run() {
      for(; this.isRunning; FloorUnderlayDefinition.method3190(50L)) {
         Object var1 = this.lock;
         synchronized(this.lock) {
            if (this.index < 500) {
               this.xs[this.index] = MouseHandler.MouseHandler_x;
               this.ys[this.index] = MouseHandler.MouseHandler_y;
               this.millis[this.index] = MouseHandler.MouseHandler_millis;
               ++this.index;
            }
         }
      }

   }

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(Lko;B)V",
      garbageValue = "-12"
   )
   public static void method2093(AbstractArchive var0) {
      VarcInt.VarcInt_archive = var0;
   }

   @ObfuscatedName("d")
   @ObfuscatedSignature(
      descriptor = "(ILbp;ZI)I",
      garbageValue = "355383775"
   )
   static int method2089(int var0, Script var1, boolean var2) {
      Widget var5;
      if (var0 == ScriptOpcodes.IF_GETINVOBJECT) {
         var5 = UserComparator9.getWidget(Interpreter.Interpreter_intStack[--class14.Interpreter_intStackSize]);
         Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = var5.itemId;
         return 1;
      } else if (var0 == ScriptOpcodes.IF_GETINVCOUNT) {
         var5 = UserComparator9.getWidget(Interpreter.Interpreter_intStack[--class14.Interpreter_intStackSize]);
         if (var5.itemId != -1) {
            Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = var5.itemQuantity;
         } else {
            Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = 0;
         }

         return 1;
      } else if (var0 == ScriptOpcodes.IF_HASSUB) {
         int var3 = Interpreter.Interpreter_intStack[--class14.Interpreter_intStackSize];
         InterfaceParent var4 = (InterfaceParent)Client.interfaceParents.get((long)var3);
         if (var4 != null) {
            Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = 1;
         } else {
            Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = 0;
         }

         return 1;
      } else if (var0 == ScriptOpcodes.IF_GETTOP) {
         Interpreter.Interpreter_intStack[++class14.Interpreter_intStackSize - 1] = Client.rootInterface;
         return 1;
      } else {
         return 2;
      }
   }

   @ObfuscatedName("ie")
   @ObfuscatedSignature(
      descriptor = "([Ljf;Ljf;ZB)V",
      garbageValue = "0"
   )
   @Export("revalidateWidgetScroll")
   static void revalidateWidgetScroll(Widget[] var0, Widget var1, boolean var2) {
      int var3 = var1.scrollWidth != 0 ? var1.scrollWidth * 1040080419 * -992722549 : var1.width * -1492995549 * 1571065227;
      int var4 = var1.scrollHeight != 0 ? var1.scrollHeight * -1297742855 * -1412739511 : var1.height * 380936511 * 1623576255;
      WorldMapLabelSize.resizeInterface(var0, var1.id, var3, var4, var2);
      if (var1.children != null) {
         WorldMapLabelSize.resizeInterface(var1.children, var1.id, var3, var4, var2);
      }

      InterfaceParent var5 = (InterfaceParent)Client.interfaceParents.get((long)var1.id);
      if (var5 != null) {
         SecureRandomCallable.method2051(var5.group, var3, var4, var2);
      }

      if (var1.contentType == 1337) {
         ;
      }

   }
}
