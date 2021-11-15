package meteor.plugins.changlavadrags.pluginstate

import com.google.inject.Inject
import dev.hoot.api.coords.Area
import dev.hoot.api.coords.RectangularArea
import meteor.plugins.changlavadrags.ChangLavaDragsPlugin
import meteor.plugins.changlavadrags.PluginState
import net.runelite.api.NPC
import net.runelite.api.coords.WorldPoint

object FightingState : PluginState {
    private var killCount : Int  = 0
    val fightingSpot = WorldPoint(3200, 3807, 0)
    val vetionSafeSpot = WorldPoint(3184, 3801, 0)
    val generalArea: Area = RectangularArea(3175, 3793, 3223, 3808)

    //static final Area lavaDragonTargetArea = new RectangularArea(3204, 3805, 3214, 3814);
    val lavaDragonTargetArea: Area = RectangularArea(3197, 3817, 3204, 3810)
    val geArea: Area = RectangularArea(3137, 3518, 3194, 3467)
    @Inject
    lateinit var plugin : ChangLavaDragsPlugin

    var currentTarget : NPC? = null
    override fun onGameTick() {
        if (currentTarget != null && currentTarget!!.isDead) {
            killCount++
            currentTarget = null
        }

    }

    override fun transition() {

    }

    override fun sanityCheck() {
        TODO("Not yet implemented")
    }
}