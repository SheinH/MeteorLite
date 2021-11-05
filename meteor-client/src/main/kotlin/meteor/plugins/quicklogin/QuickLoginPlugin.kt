package meteor.plugins.quicklogin

import com.google.inject.Inject
import meteor.MeteorLiteClientLauncher
import meteor.input.KeyListener
import meteor.input.KeyManager
import meteor.menus.MenuManager
import meteor.plugins.Plugin
import meteor.plugins.PluginDescriptor
import meteor.plugins.api.entities.Players
import meteor.plugins.api.input.Keyboard
import meteor.plugins.socket.org.json.JSONArray
import net.runelite.api.Player
import java.awt.event.KeyEvent
import java.io.File
import java.nio.file.Files

@PluginDescriptor(
    name = "Quick Login",
    description = "Logs in with hotkeys"
)
class QuickLoginPlugin : Plugin() {
    val PASSWORD_FILE = File(MeteorLiteClientLauncher.METEOR_DIR, "logdata.json")

    @Inject
    lateinit var keyManager: KeyManager
    private var passwords = ArrayList<String>(9)
    private var logins = ArrayList<String>(9)
    private var keyListeners = ArrayList<KeyListener>(9)

    init {
        val string = Files.readString(PASSWORD_FILE.toPath())
        val jsonLogins = JSONArray(string)
        for (i in 0 until jsonLogins.length()) {
            val jsonLogin = jsonLogins.getJSONObject(i)
            logins.add(jsonLogin.getString("login"))
            passwords.add(jsonLogin.getString("password"))
            keyListeners.add(makeHotkeyListener(i))
        }
        logger.info("Logins loaded!")
    }

    override fun startup() {
        keyListeners.forEach { keyManager.registerKeyListener(it, this.javaClass) }
    }

    override fun shutdown() {
        keyListeners.forEach { keyManager.unregisterKeyListener(it) }
    }

    fun makeHotkeyListener(num: Int): KeyListener {
        return object : KeyListener {
            override fun isEnabledOnLoginScreen() = true

            override fun keyTyped(e: KeyEvent?) {
            }

            var isPressed = false
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_1 + num && e.isControlDown) {
                    isPressed = true
                    e.consume()
                    enterPassword(num)
                }
            }

            override fun keyReleased(e: KeyEvent) {
                if (isPressed) {
                    e.consume()
                }
            }

        }
    }

    @Inject
    lateinit var menuManager : MenuManager
    private fun enterPassword(num: Int) {
        client.loginIndex = 2
        client.username = logins[num]
        client.setPassword(passwords[num])
        Keyboard.sendEnter()
        Keyboard.sendEnter()
        lastLogin = num
    }

    fun enterLastLogin() {
        enterPassword(lastLogin)
        logger.info("entering login: " + logins[lastLogin])
    }

    companion object {
        var lastLogin: Int = -1
    }
}