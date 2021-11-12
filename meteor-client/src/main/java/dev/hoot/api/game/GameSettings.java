package dev.hoot.api.game;

import dev.hoot.api.commons.Time;
import dev.hoot.api.widgets.Tab;
import dev.hoot.api.widgets.Tabs;
import dev.hoot.api.widgets.Widgets;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.util.function.Supplier;

public class GameSettings {

    public enum Display {
        FIXED(() -> Widgets.get(WidgetInfo.FIXED_VIEWPORT)),
        RESIZABLE_MODERN(() -> Widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_MAGIC_TAB)),
        RESIZABLE_CLASSIC(() -> Widgets.get(WidgetInfo.RESIZABLE_VIEWPORT_INTERFACE_CONTAINER)),
        ;

        private final Supplier<Widget> widgetSupplier;

        Display(Supplier<Widget> widgetSupplier) {
            this.widgetSupplier = widgetSupplier;
        }

        public Supplier<Widget> getWidgetSupplier() {
            return widgetSupplier;
        }

        public static void setMode(Display displayMode) {
            if (!Tabs.isOpen(Tab.OPTIONS)) {
                Tabs.open(Tab.OPTIONS);
                Time.sleepUntil(() -> Tabs.isOpen(Tab.OPTIONS), 2000);
            }

            switch (displayMode) {
                case FIXED -> GameThread.invoke(() -> Game.getClient().runScript(3998, 0));
                case RESIZABLE_MODERN -> GameThread.invoke(() -> Game.getClient().runScript(3998, 1));

                case RESIZABLE_CLASSIC -> GameThread.invoke(() -> {
                    // I have no fuckin idea but it works
                    Game.getClient().runScript(441, 7602188, 7602213, 7602207, 7602209, 7602214, 7602215, 7602176);
                    Game.getClient().interact(1, 57, 2, 7602213);
                });
            }
        }

        public static Display getCurrentMode() {
            for (Display display : values()) {
                Widget widget = display.getWidgetSupplier().get();
                if (widget != null && !GameThread.invokeLater(widget::isHidden)) {
                    return display;
                }
            }

            return null;
        }
    }

    public enum Audio {
        MUSIC(() -> Widgets.get(116, 39), VarPlayer.MUSIC_VOLUME.getId()),
        EFFECTS(() -> Widgets.get(116, 53), VarPlayer.SOUND_EFFECT_VOLUME.getId()),
        AREA(() -> Widgets.get(116, 67), VarPlayer.AREA_EFFECT_VOLUME.getId())
        ;

        private final Supplier<Widget> widgetSupplier;
        private final int levelVarp;

        Audio(Supplier<Widget> widgetSupplier, int levelVarp) {
            this.widgetSupplier = widgetSupplier;
            this.levelVarp = levelVarp;
        }

        public Supplier<Widget> getWidgetSupplier() {
            return widgetSupplier;
        }

        public int getLevel() {
            return Vars.getVarp(getLevelVarp());
        }

        public void setVolume(int level) {
            if (getLevel() != level) {
                if (!Tabs.isOpen(Tab.OPTIONS)) {
                    Tabs.open(Tab.OPTIONS);
                    Time.sleepUntil(() -> Tabs.isOpen(Tab.OPTIONS), 2000);
                }

                Widget widget = widgetSupplier.get();
                if (widget != null && widget.getChild(level) != null) {
                    widget.getChild(level).interact(0);
                }
            }
        }

        public static boolean isFullMuted() {
            return MUSIC.getLevel() == 0 && AREA.getLevel() == 0 && EFFECTS.getLevel() == 0;
        }

        public static void muteAll() {
            for (Audio audio : values()) {
                audio.setVolume(0);
            }
        }

        public int getLevelVarp() {
            return levelVarp;
        }
    }
}
