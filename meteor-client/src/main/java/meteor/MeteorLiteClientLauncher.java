package meteor;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import meteor.themes.MeteorliteTheme;
import meteor.util.LoggerStream;
import net.runelite.api.Client;
import sun.misc.Unsafe;

public class MeteorLiteClientLauncher extends Application {
  public static final File METEOR_DIR
      = new File(System.getProperty("user.home"), ".meteorlite");
  public static final File CACHE_DIR = new File(METEOR_DIR, "cache");
  public static final File PLUGINS_DIR = new File(METEOR_DIR, "plugin-hub");
  public static final File PROFILES_DIR = new File(METEOR_DIR, "profiles");
  public static final File SCREENSHOT_DIR = new File(METEOR_DIR, "screenshots");
  public static final File VERBOSE_LOG = new File(new File(METEOR_DIR, "logs"), "log.txt");
  public static final File LOGS_DIR = new File(METEOR_DIR, "logs");
  public static final File ERROR_LOG = new File(LOGS_DIR, "error.txt");
  public static final File DEFAULT_SESSION_FILE = new File(METEOR_DIR, "session");
  public static File CONFIG_FILE;
  public static PrintStream consoleStream = null;
  public static LoggerStream verboseFileStream = null;
  public static LoggerStream errorFileStream = null;

  public static Client clientInstance; // This is reserved for the Sponge Mixins Agent

  public static Injector injector; //this is so bad

  @Override
  public void start(Stage primaryStage) throws IOException, InterruptedException, InvocationTargetException {
    try {
      Parameters params = getParameters();
      Map<String, String> namedPararms = params.getNamed();
      CONFIG_FILE = new File(METEOR_DIR, namedPararms.getOrDefault("settings", "settings.properties"));
      disableIllegalReflectiveAccessWarning();
      consoleStream = System.out;
      LOGS_DIR.mkdirs();
      errorFileStream = new LoggerStream(new FileOutputStream(ERROR_LOG));
      verboseFileStream = new LoggerStream(new FileOutputStream(VERBOSE_LOG));
      errorFileStream.error = true;
      System.setErr(errorFileStream);
      System.setOut(verboseFileStream);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    MeteorliteTheme.install();
    MeteorLiteClientModule module = new MeteorLiteClientModule();
    injector = Guice.createInjector(module);
    module.start();
  }

  public static void disableIllegalReflectiveAccessWarning() {
    try {
      Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
      theUnsafe.setAccessible(true);
      Unsafe u = (Unsafe) theUnsafe.get(null);

      Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
      Field logger = cls.getDeclaredField("logger");
      u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
    } catch (Exception e) {
      // ignore
    }
  }
}
