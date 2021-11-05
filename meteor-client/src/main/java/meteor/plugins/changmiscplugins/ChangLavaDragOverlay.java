package meteor.plugins.changmiscplugins;

import meteor.ui.FontManager;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.components.TableAlignment;
import meteor.ui.overlay.components.TableComponent;
import meteor.ui.overlay.components.TitleComponent;
import meteor.util.ColorUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;

@Singleton
class ChangLavaDragOverlay extends OverlayPanel {
    private final ChangLavaDrags plugin;

    @Inject
    private ChangLavaDragOverlay(final ChangLavaDrags plugin) {
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

    private String goldToString(double goldAmount){
        DecimalFormat format;
        double amount;
        if(goldAmount > 1e7){
            amount = goldAmount / 1e6;
            format = new DecimalFormat("##.## M");
        }
        else if(goldAmount > 1e4){
            amount = goldAmount / 1e3;
            format = new DecimalFormat("# K");
        }
        else{
            amount = goldAmount;
            format = new DecimalFormat("# gp");
        }
        return format.format(amount);
    }

    private String getProfitPerHourString(double profitPerHour){
        return goldToString(profitPerHour) + " / hour";
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if(!plugin.isEnabled())
            return null;

        var profit = plugin.getTotalProfit();
        var timeElapsed = plugin.runTime.getTime();
        var profitPerHour =  ( ((double) profit) / (timeElapsed == 0 ? 0 : timeElapsed) * (60L * 60L * 1000L));
        var profitPerHourString = getProfitPerHourString(profitPerHour);
        TableComponent tableComponent = new TableComponent();
        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);
        tableComponent.addRow("State",plugin.state.name());
        tableComponent.addRow("Runtime",getRuntime());
        tableComponent.addRow("Kill Count",Integer.toString(plugin.killCount));
        tableComponent.addRow("Profit rate",profitPerHourString);
        tableComponent.addRow("Total Profit",goldToString(profit));
        var defaultFont = FontManager.getDefaultFont();
        if (!tableComponent.isEmpty()) {
            panelComponent.setPreferredSize(new Dimension(150,0));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Lava Dragons")
                    .color(ColorUtil.fromHex("#f8a252"))
                    .build());
            panelComponent.getChildren().add(tableComponent);
        }
        return super.render(graphics);
    }
}
