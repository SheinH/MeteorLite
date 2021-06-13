package osrs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("bo")
@Implements("ReflectionCheck")
public class ReflectionCheck extends Node {
   @ObfuscatedName("q")
   @ObfuscatedGetter(
      intValue = -1538307343
   )
   @Export("canvasHeight")
   public static int canvasHeight;
   @ObfuscatedName("lz")
   @ObfuscatedSignature(
      descriptor = "Lcp;"
   )
   @Export("tempMenuAction")
   static MenuAction tempMenuAction;
   @ObfuscatedName("ld")
   @ObfuscatedSignature(
      descriptor = "Lio;"
   )
   static Widget field609;
   @ObfuscatedName("v")
   @ObfuscatedGetter(
      intValue = -848582505
   )
   @Export("id")
   int id;
   @ObfuscatedName("n")
   @ObfuscatedGetter(
      intValue = -637285787
   )
   @Export("size")
   int size;
   @ObfuscatedName("f")
   @Export("intReplaceValues")
   int[] intReplaceValues;
   @ObfuscatedName("y")
   @Export("operations")
   int[] operations;
   @ObfuscatedName("p")
   @Export("creationErrors")
   int[] creationErrors;
   @ObfuscatedName("j")
   @Export("fields")
   Field[] fields;
   @ObfuscatedName("r")
   @Export("methods")
   Method[] methods;
   @ObfuscatedName("b")
   @Export("arguments")
   byte[][][] arguments;

   @ObfuscatedName("in")
   @ObfuscatedSignature(
      descriptor = "(III)V",
      garbageValue = "-372153509"
   )
   static void method1126(int var0, int var1) {
      int var2 = Widget.fontBold12.stringWidth("Choose Option");

      int var3;
      int var4;
      for(var3 = 0; var3 < Client.menuOptionsCount; ++var3) {
         var4 = Widget.fontBold12.stringWidth(MouseRecorder.method2098(var3));
         if (var4 > var2) {
            var2 = var4;
         }
      }

      var2 += 8;
      var3 = Client.menuOptionsCount * 15 + 22;
      var4 = var0 - var2 / 2;
      if (var2 + var4 > class32.canvasWidth) {
         var4 = class32.canvasWidth - var2;
      }

      if (var4 < 0) {
         var4 = 0;
      }

      int var5 = var1;
      if (var1 + var3 > canvasHeight) {
         var5 = canvasHeight - var3;
      }

      if (var5 < 0) {
         var5 = 0;
      }

      class14.menuX = var4;
      class243.menuY = var5;
      class29.menuWidth = var2;
      class24.menuHeight = Client.menuOptionsCount * 15 + 22;
   }

   @ObfuscatedName("jr")
   @ObfuscatedSignature(
      descriptor = "([Lio;II)V",
      garbageValue = "2064427541"
   )
   @Export("runComponentCloseListeners")
   static final void runComponentCloseListeners(Widget[] var0, int var1) {
      for(int var2 = 0; var2 < var0.length; ++var2) {
         Widget var3 = var0[var2];
         if (var3 != null) {
            if (var3.type == 0) {
               if (var3.children != null) {
                  runComponentCloseListeners(var3.children, var1);
               }

               InterfaceParent var4 = (InterfaceParent)Client.interfaceParents.get((long)var3.id);
               if (var4 != null) {
                  Login.runIntfCloseListeners(var4.group, var1);
               }
            }

            ScriptEvent var6;
            if (var1 == 0 && var3.onDialogAbort != null) {
               var6 = new ScriptEvent();
               var6.widget = var3;
               var6.args = var3.onDialogAbort;
               PacketWriter.runScriptEvent(var6);
            }

            if (var1 == 1 && var3.onSubChange != null) {
               if (var3.childIndex >= 0) {
                  Widget var5 = Frames.getWidget(var3.id);
                  if (var5 == null || var5.children == null || var3.childIndex >= var5.children.length || var3 != var5.children[var3.childIndex]) {
                     continue;
                  }
               }

               var6 = new ScriptEvent();
               var6.widget = var3;
               var6.args = var3.onSubChange;
               PacketWriter.runScriptEvent(var6);
            }
         }
      }

   }
}
