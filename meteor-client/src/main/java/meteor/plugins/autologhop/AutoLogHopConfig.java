package meteor.plugins.autologhop;

import meteor.config.*;

@ConfigGroup("autologhop")
public interface AutoLogHopConfig extends Config
{

    @ConfigSection(
            keyName = "title",
            name = "Soxs' AutoLogHop",
            description = "",
            position = 0
    )
    String title = "Soxs' AutoLogHop";

    @ConfigItem(
            keyName = "method",
            name = "Method",
            description = "Logout = log out (0 tick), hop = hop worlds (1 tick), log then hop = logout and hop worlds from client screen (0 tick)",
            position = 10,
            section = title
    )
    default Method method()
    {
        return Method.LOGOUT_HOP;
    }

    @ConfigItem(
            keyName = "teleMethod",
            name = "Teleport Method",
            description = "Logout = log out (0 tick), hop = hop worlds (1 tick), log then hop = logout and hop worlds from client screen (0 tick)",
            position = 11,
            section = title,
            hidden = true,
            unhide = "method",
            unhideValue = "TELEPORT"
    )
    default TeleportType teleMethod()
    {
        return TeleportType.ROYAL_SEED_POD;
    }

    @ConfigItem(
            keyName = "username",
            name = "username",
            description = "Username for login",
            position = 12,
            section = title,
            textField = true
    )
    default String user() {
        return "";
    }

    @ConfigItem(
            keyName = "password",
            name = "password",
            description = "Password for login",
            position = 13,
            section = title,
            textField = true,
            secret = true
    )
    default String password() {
        return "";
    }

    @ConfigItem(
            keyName = "disableWildyChecks",
            name = "Disable Wilderness Checks",
            description = "Disable wilderness checks. Makes plugin work everywhere.",
            position = 14,
            section = title
    )
    default boolean disableWildyChecks()
    {
        return false;
    }

    @ConfigItem(
            keyName = "whitelist",
            name = "Whitelist",
            description = "Players to ignore - separate with , and don't leave leading/trailing spaces",
            position = 15,
            section = title
    )
    default String whitelist()
    {
        return "";
    }

    @ConfigItem(
            keyName = "membersWorlds",
            name = "Members Worlds",
            description = "Hop to members worlds.",
            position = 16,
            section = title
    )
    default boolean membersWorlds()
    {
        return true;
    }

    @ConfigItem(
            keyName = "combatRange",
            name = "Within combat range",
            description = "Will only consider players within combat level bracket of wilderness level",
            position = 18,
            section = title
    )
    default boolean combatRange()
    {
        return true;
    }

    @ConfigItem(
            keyName = "skulledOnly",
            name = "Skulled Players Only",
            description = "Only triggers on skulled players.",
            position = 20,
            section = title
    )
    default boolean skulledOnly()
    {
        return false;
    }
}