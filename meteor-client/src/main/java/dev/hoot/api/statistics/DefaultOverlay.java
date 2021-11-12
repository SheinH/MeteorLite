package dev.hoot.api.statistics;

import dev.hoot.api.commons.StopWatch;
import dev.hoot.api.game.Skills;
import meteor.ui.RenderableEntity;
import meteor.ui.overlay.Overlay;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.OverlayPriority;
import meteor.ui.overlay.components.PanelComponent;
import meteor.ui.overlay.components.TableAlignment;
import meteor.ui.overlay.components.TableComponent;
import meteor.ui.overlay.components.TitleComponent;
import net.runelite.api.Skill;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultOverlay extends Overlay {
	private final Map<String, Statistic> statistics = new HashMap<>();
	private final PanelComponent panel = new PanelComponent();
	private final List<RenderableEntity> overlays = new ArrayList<>();

	public DefaultOverlay() {
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.LOW);

		RenderableEntity tracker = graphics -> {
			TableComponent table = new TableComponent();
			table.setColumnAlignments(TableAlignment.LEFT, TableAlignment.LEFT);
			panel.getChildren().clear();
			panel.setPreferredLocation(new Point(306, 6));
			panel.setPreferredSize(new Dimension(200, 0));

			for (Map.Entry<String, Statistic> entry : statistics.entrySet()) {
				String key = entry.getKey();
				Statistic statistic = entry.getValue();

				if (statistic.isHeader()) {
					panel.getChildren().add(TitleComponent.builder().text(key).color(Color.WHITE).build());
				} else {
					String text = key + ":";
					table.addRow(text, statistic.toString());
				}
			}

			panel.getChildren().add(table);
			return panel.render(graphics);
		};

		overlays.add(tracker);
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		overlays.forEach(x -> x.render(graphics));
		return null;
	}

	public void submit(String key, Statistic statistic) {
		statistics.put(key, statistic);
	}

	public void submit(RenderableEntity renderableEntity) {
		overlays.add(renderableEntity);
	}

	public void clear() {
		overlays.clear();
	}

	public void remove(RenderableEntity renderableEntity) {
		overlays.remove(renderableEntity);
	}

	public void trackSkill(Skill skill, boolean trackLevels) {
		if (!statistics.containsKey(skill.getName() + " XP")) {
			ExperienceTracker tracker = new ExperienceTracker(skill, Skills.getExperience(skill), Skills.getLevel(skill));
			StopWatch timer = StopWatch.start();
			submit(skill.getName() + " XP", new Statistic(timer, tracker::getExperienceGained));

			if (trackLevels && !statistics.containsKey(skill.getName() + " LVLs")) {
				submit(skill.getName() + " LVLs", new Statistic(timer, tracker::getLevelsGained));
			}
		}
	}

	public void setHeader(String text) {
		statistics.put(text, new Statistic(true, null));
	}
}
