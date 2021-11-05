package osrs;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import net.runelite.mapping.Export;
import net.runelite.mapping.Implements;
import net.runelite.mapping.ObfuscatedGetter;
import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("ah")
@Implements("MouseHandler")
public class MouseHandler implements MouseListener, MouseMotionListener, FocusListener {
   @ObfuscatedName("ph")
   @ObfuscatedSignature(
      descriptor = "Leg;"
   )
   @Export("mouseWheel")
   static class144 mouseWheel;
   @ObfuscatedName("a")
   @ObfuscatedSignature(
      descriptor = "Lah;"
   )
   @Export("MouseHandler_instance")
   static MouseHandler MouseHandler_instance = new MouseHandler();
   @ObfuscatedName("o")
   @ObfuscatedGetter(
      intValue = 1036288491
   )
   @Export("MouseHandler_idleCycles")
   public static volatile int MouseHandler_idleCycles = 0;
   @ObfuscatedName("g")
   @ObfuscatedGetter(
      intValue = -422885389
   )
   @Export("MouseHandler_currentButtonVolatile")
   static volatile int MouseHandler_currentButtonVolatile = 0;
   @ObfuscatedName("e")
   @ObfuscatedGetter(
      intValue = -19196403
   )
   @Export("MouseHandler_xVolatile")
   static volatile int MouseHandler_xVolatile = -1;
   @ObfuscatedName("p")
   @ObfuscatedGetter(
      intValue = 197302675
   )
   @Export("MouseHandler_yVolatile")
   static volatile int MouseHandler_yVolatile = -1;
   @ObfuscatedName("j")
   @ObfuscatedGetter(
      longValue = 8130240115469217441L
   )
   @Export("MouseHandler_lastMovedVolatile")
   static volatile long MouseHandler_lastMovedVolatile = -1L;
   @ObfuscatedName("b")
   @ObfuscatedGetter(
      intValue = -1212092085
   )
   @Export("MouseHandler_currentButton")
   public static int MouseHandler_currentButton = 0;
   @ObfuscatedName("x")
   @ObfuscatedGetter(
      intValue = -1137004173
   )
   @Export("MouseHandler_y")
   public static int MouseHandler_y = 0;
   @ObfuscatedName("y")
   @ObfuscatedGetter(
      longValue = 8667664769144098865L
   )
   @Export("MouseHandler_millis")
   public static long MouseHandler_millis = 0L;
   @ObfuscatedName("k")
   @ObfuscatedGetter(
      intValue = 1769064135
   )
   @Export("MouseHandler_x")
   public static int MouseHandler_x = 0;
   @ObfuscatedName("t")
   @ObfuscatedGetter(
      intValue = 1482494963
   )
   @Export("MouseHandler_lastButtonVolatile")
   static volatile int MouseHandler_lastButtonVolatile = 0;
   @ObfuscatedName("l")
   @ObfuscatedGetter(
      intValue = 363259853
   )
   @Export("MouseHandler_lastPressedXVolatile")
   static volatile int MouseHandler_lastPressedXVolatile = 0;
   @ObfuscatedName("u")
   @ObfuscatedGetter(
      intValue = 1639433965
   )
   @Export("MouseHandler_lastPressedYVolatile")
   static volatile int MouseHandler_lastPressedYVolatile = 0;
   @ObfuscatedName("n")
   @ObfuscatedGetter(
      longValue = -7568180939030180395L
   )
   @Export("MouseHandler_lastPressedTimeMillisVolatile")
   static volatile long MouseHandler_lastPressedTimeMillisVolatile = 0L;
   @ObfuscatedName("z")
   @ObfuscatedGetter(
      intValue = 307748861
   )
   @Export("MouseHandler_lastButton")
   public static int MouseHandler_lastButton = 0;
   @ObfuscatedName("q")
   @ObfuscatedGetter(
      intValue = -994811429
   )
   @Export("MouseHandler_lastPressedX")
   public static int MouseHandler_lastPressedX = 0;
   @ObfuscatedName("d")
   @ObfuscatedGetter(
      intValue = 1483939719
   )
   @Export("MouseHandler_lastPressedY")
   public static int MouseHandler_lastPressedY = 0;
   @ObfuscatedName("r")
   @ObfuscatedGetter(
      longValue = 5932498801769663649L
   )
   @Export("MouseHandler_lastPressedTimeMillis")
   public static long MouseHandler_lastPressedTimeMillis = 0L;
   @ObfuscatedName("bw")
   @ObfuscatedSignature(
      descriptor = "Lkz;"
   )
   @Export("clientLanguage")
   static Language clientLanguage;
   @ObfuscatedName("ix")
   @ObfuscatedGetter(
      intValue = 1129600087
   )
   @Export("selectedItemWidget")
   static int selectedItemWidget;

   @ObfuscatedName("o")
   @ObfuscatedSignature(
      descriptor = "(Ljava/awt/event/MouseEvent;B)I",
      garbageValue = "-42"
   )
   @Export("getButton")
   final int getButton(MouseEvent var1) {
      int var2 = var1.getButton();
      if (!var1.isAltDown() && var2 != 2) {
         return !var1.isMetaDown() && var2 != 3 ? 1 : 2;
      } else {
         return 4;
      }
   }

   public final synchronized void mouseMoved(MouseEvent var1) {
      if (MouseHandler_instance != null) {
         MouseHandler_idleCycles = 0;
         MouseHandler_xVolatile = var1.getX();
         MouseHandler_yVolatile = var1.getY();
         MouseHandler_lastMovedVolatile = var1.getWhen();
      }

   }

   public final void mouseClicked(MouseEvent var1) {
      if (var1.isPopupTrigger()) {
         var1.consume();
      }

   }

   public final synchronized void mouseEntered(MouseEvent var1) {
      this.mouseMoved(var1);
   }

   public final synchronized void mouseExited(MouseEvent var1) {
      if (MouseHandler_instance != null) {
         MouseHandler_idleCycles = 0;
         MouseHandler_xVolatile = -1;
         MouseHandler_yVolatile = -1;
         MouseHandler_lastMovedVolatile = var1.getWhen();
      }

   }

   public final synchronized void mouseDragged(MouseEvent var1) {
      this.mouseMoved(var1);
   }

   public final void focusGained(FocusEvent var1) {
   }

   public final synchronized void focusLost(FocusEvent var1) {
      if (MouseHandler_instance != null) {
         MouseHandler_currentButtonVolatile = 0;
      }

   }

   public final synchronized void mousePressed(MouseEvent var1) {
      if (MouseHandler_instance != null) {
         MouseHandler_idleCycles = 0;
         MouseHandler_lastPressedXVolatile = var1.getX();
         MouseHandler_lastPressedYVolatile = var1.getY();
         MouseHandler_lastPressedTimeMillisVolatile = class111.getServerTime();
         MouseHandler_lastButtonVolatile = this.getButton(var1);
         if (MouseHandler_lastButtonVolatile != 0) {
            MouseHandler_currentButtonVolatile = MouseHandler_lastButtonVolatile;
         }
      }

      if (var1.isPopupTrigger()) {
         var1.consume();
      }

   }

   public final synchronized void mouseReleased(MouseEvent var1) {
      if (MouseHandler_instance != null) {
         MouseHandler_idleCycles = 0;
         MouseHandler_currentButtonVolatile = 0;
      }

      if (var1.isPopupTrigger()) {
         var1.consume();
      }

   }

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(IB)Lfm;",
      garbageValue = "58"
   )
   @Export("SequenceDefinition_get")
   public static SequenceDefinition SequenceDefinition_get(int var0) {
      SequenceDefinition var1 = (SequenceDefinition)SequenceDefinition.SequenceDefinition_cached.get((long)var0);
      if (var1 != null) {
         return var1;
      } else {
         byte[] var2 = SequenceDefinition.SequenceDefinition_archive.takeFile(12, var0);
         var1 = new SequenceDefinition();
         if (var2 != null) {
            var1.decode(new Buffer(var2));
         }

         var1.postDecode();
         SequenceDefinition.SequenceDefinition_cached.put(var1, (long)var0);
         return var1;
      }
   }

   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "(Ljava/lang/Throwable;Ljava/lang/String;)Lpg;"
   )
   @Export("newRunException")
   public static RunException newRunException(Throwable var0, String var1) {
      RunException var2;
      if (var0 instanceof RunException) {
         var2 = (RunException)var0;
         var2.message = var2.message + ' ' + var1;
      } else {
         var2 = new RunException(var0, var1);
      }

      return var2;
   }

   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "(II)Ljava/lang/String;",
      garbageValue = "-1583806912"
   )
   @Export("colorStartTag")
   static String colorStartTag(int var0) {
      return "<col=" + Integer.toHexString(var0) + ">";
   }

   @ObfuscatedName("ab")
   @ObfuscatedSignature(
      descriptor = "(IS)I",
      garbageValue = "15047"
   )
   static int method626(int var0) {
      return (int)((Math.log((double)var0) / Interpreter.field801 - 7.0D) * 256.0D);
   }
}
