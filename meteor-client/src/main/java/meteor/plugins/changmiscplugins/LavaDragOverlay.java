package meteor.plugins.changmiscplugins;

import meteor.plugins.changmiscplugins.LavaDragScript;
import meteor.ui.overlay.OverlayMenuEntry;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.components.TableAlignment;
import meteor.ui.overlay.components.TableComponent;
import meteor.ui.overlay.components.TitleComponent;
import meteor.util.ColorUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static meteor.ui.overlay.OverlayManager.OPTION_CONFIGURE;

@Singleton
class LavaDragOverlay extends OverlayPanel {
    private final LavaDragScript plugin;

    @Inject
    private LavaDragOverlay(final LavaDragScript plugin) {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
    }

    private String getRuntime(){
        if(plugin.runTime.getTime(TimeUnit.HOURS) > 0)
            return DurationFormatUtils.formatDuration(plugin.runTime.getTime(),"hh:mm:ss");
        else
            return DurationFormatUtils.formatDuration(plugin.runTime.getTime(),"mm:ss");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if(!plugin.isEnabled())
            return null;

        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("State",plugin.state.name());
        tableComponent.addRow("Runtime",getRuntime());
        tableComponent.addRow("Kill Count",Integer.toString(plugin.killCount));
        if (!tableComponent.isEmpty()) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Lava Dragons")
                    .color(ColorUtil.fromHex("#f8a252"))
                    .build());
            panelComponent.getChildren().add(tableComponent);
        }
        return super.render(graphics);
    }
}
