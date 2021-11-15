package meteor.plugins.ccrabber;

import com.questhelper.requirements.player.SpellbookRequirement;
import javafx.scene.paint.Stop;
import meteor.eventbus.Subscribe;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import dev.hoot.api.commons.StopWatch;
import dev.hoot.api.entities.Players;
import dev.hoot.api.game.Worlds;
import dev.hoot.api.magic.Magic;
import dev.hoot.api.movement.Movement;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetID;
import org.apache.commons.math3.distribution.GammaDistribution;

import java.time.Duration;
import java.util.List;

@PluginDescriptor(
        name = "CCrabber",
        enabledByDefault = false
)
public class CCrabberPlugin extends Plugin {
    public static boolean enabled = false;
    int tickDelay = 0;
    WorldPoint crabSpot = new WorldPoint(3803,3755,0);
    WorldPoint resetSpot = new WorldPoint(3814,3800,0);
    StopWatch idleTimer;
    StopWatch totalRuntime = StopWatch.start();

    enum CrabberState{
        FIGHTING,
        RESET_GO,
        RESET_RETURN
    }
    CrabberState state = CrabberState.FIGHTING;

    public void shutDown() {
        enabled = false;
    }

    public Player checkForOtherCrabbers(){
        var list = Players.getAll(x -> x.getWorldLocation().distanceTo(crabSpot) < 3 && !x.isIdle());
        if(list.isEmpty()){
            return null;
        }
        else return list.get(0);
    }

    StopWatch hopWorldTimer = null;

    public void doCrab(){
        var player = client.getLocalPlayer();
        if(!player.getWorldLocation().equals(crabSpot)){
            if (player.distanceTo(crabSpot) <= 100) {
                state = CrabberState.RESET_RETURN;
            }
            return;
        }
        if(player.isIdle()){
            var others = checkForOtherCrabbers();
            if(others != null){
                if(!others.isIdle()){
                    var currentWorld = Worlds.getCurrentId();
                    var next = Worlds.getRandom(x -> x.isNormal() && x.isMembers() && x.getId() != currentWorld);
                    Worlds.hopTo(next,false);
                    return;
                }
                else{
                    if(hopWorldTimer!= null){
                        if(hopWorldTimer.exceeds(Duration.ofSeconds(6))){
                            var currentWorld = Worlds.getCurrentId();
                            var next = Worlds.getRandom(x -> x.isNormal() && x.getId() != currentWorld);
                            Worlds.hopTo(next,false);
                        }
                        else{
                            hopWorldTimer = StopWatch.start();
                        }
                        return;
                    }
                }
            }
            else{
                hopWorldTimer = null;
            }
            if(idleTimer == null){
                idleTimer = StopWatch.start();
            }
            else{
                if(idleTimer.exceeds(Duration.ofSeconds(6))){
                    idleTimer = null;
                    state = CrabberState.RESET_GO;
                }
            }
        }
        else{
            idleTimer = null;
        }
    }

    public void doResetGo(){
        var player = client.getLocalPlayer();
        if(resetSpot.distanceTo(player) < 3){
            state = CrabberState.RESET_RETURN;
            return;
        }
        else if(!Movement.isWalking()){
            Movement.walk(resetSpot);
        }
    }


    public void doResetReturn(){
        var player = client.getLocalPlayer();
        if(player.getWorldLocation().equals(crabSpot)){
            state = CrabberState.FIGHTING;
            return;
        }
        else if(!Movement.isWalking()){
            Movement.walk(crabSpot);
        }
    }
    @Subscribe
    public void onGameTick(GameTick event) {
        if(totalRuntime.exceeds(Duration.ofHours(3))){
            logger.info("Time limit reached. Halting.");
            return;
        }
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        switch (state){
            case FIGHTING -> doCrab();
            case RESET_GO -> doResetGo();
            case RESET_RETURN -> doResetReturn();
        }



    }
}
