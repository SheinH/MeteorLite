package dev.hoot.api.game;

import dev.hoot.api.widgets.Widgets;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;

public class Game {
    private static final int MEMBER_DAYS_VARP = 1780;
    private static final int CUTSCENE_VARBIT = 542;
    @Inject
    private static Client client;

    public static Client getClient() {
        return client;
    }

    public static boolean isLoggedIn() {
        return getState() == GameState.LOGGED_IN || getState() == GameState.LOADING;
    }

    public static boolean isOnLoginScreen() {
        return getState() == GameState.LOGIN_SCREEN || getState() == GameState.LOGIN_SCREEN_AUTHENTICATOR;
    }

    public static GameState getState() {
        return client.getGameState();
    }

    public static int getWildyLevel() {
        Widget wildyLevelWidget = Widgets.get(WidgetInfo.PVP_WILDERNESS_LEVEL);
        if (wildyLevelWidget == null || GameThread.invokeLater(wildyLevelWidget::isHidden)) {
            return 0;
        }

        // Dmm
        if (wildyLevelWidget.getText().contains("Guarded") || wildyLevelWidget.getText().contains("Protection")) {
            return 0;
        }

        if (wildyLevelWidget.getText().contains("Deadman")) {
            return Integer.MAX_VALUE;
        }

        return Integer.parseInt(wildyLevelWidget.getText().replace("Level: ", ""));
    }

    public static int getMembershipDays() {
        return Vars.getVarp(MEMBER_DAYS_VARP);
    }

    public static boolean isInCutscene() {
        return Vars.getBit(CUTSCENE_VARBIT) > 0;
    }
}
