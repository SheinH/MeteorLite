package osrs;

import net.runelite.mapping.ObfuscatedName;
import net.runelite.mapping.ObfuscatedSignature;

@ObfuscatedName("mu")
public class class359 {
   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "Lmu;"
   )
   static final class359 field4043 = new class359("application/json");
   @ObfuscatedName("w")
   @ObfuscatedSignature(
      descriptor = "Lmu;"
   )
   static final class359 field4042 = new class359("text/plain");
   @ObfuscatedName("s")
   String field4044;

   class359(String var1) {
      this.field4044 = var1;
   }

   @ObfuscatedName("i")
   @ObfuscatedSignature(
      descriptor = "(B)Ljava/lang/String;",
      garbageValue = "33"
   )
   public String method6418() {
      return this.field4044;
   }
}
