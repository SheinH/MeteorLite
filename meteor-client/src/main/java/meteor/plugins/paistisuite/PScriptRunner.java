package meteor.plugins.paistisuite;

import lombok.extern.slf4j.Slf4j;
import meteor.plugins.paistisuite.PScript;

import java.lang.reflect.InvocationTargetException;

@Slf4j
public class PScriptRunner implements Runnable {
    private PScript script;

    public PScriptRunner(PScript script) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        this.script = script;
    }

    private boolean stopRequested = false;
    public boolean isStopRequested(){
        synchronized (this){
            return this.stopRequested;
        }
    }

    public void requestStop (){
        synchronized (this){
            this.stopRequested = true;
        }
    };

    public void run() {
        log.info("Script runner started");
        script.onStart();
        while (true) {
            synchronized (this) {
                if (stopRequested){
                    script.onStop();
                    log.info("Script runner stopped");
                    return;
                }
            }
            script.loop();
        }
    }
}
