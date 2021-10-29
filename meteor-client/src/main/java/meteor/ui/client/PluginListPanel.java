package meteor.ui.client;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import meteor.MeteorLiteClientLauncher;
import meteor.PluginManager;
import meteor.config.ConfigManager;
import meteor.eventbus.EventBus;
import meteor.eventbus.Subscribe;
import meteor.events.ExternalsReloaded;
import meteor.plugins.Plugin;
import meteor.util.MeteorConstants;
import org.controlsfx.control.textfield.CustomTextField;

import javax.inject.Inject;
import java.util.Comparator;

public class PluginListPanel extends BorderPane {

	private final ObservableList<PluginListCell> plugins;

	@Inject
	private ConfigManager configManager;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private EventBus eventBus;

	public PluginListPanel() {
		MeteorLiteClientLauncher.injector.injectMembers(this);
		eventBus.register(this);

		plugins = FXCollections.observableArrayList();

		setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		setMinWidth(MeteorConstants.PANEL_WIDTH);
		setMaxWidth(MeteorConstants.PANEL_WIDTH);

		FontAwesomeIconView addCategory = new FontAwesomeIconView();
		addCategory.setFill(Color.AQUA);
		addCategory.setGlyphName("PLUS_SQUARE");
		addCategory.setSize("28");

		FilteredList<PluginListCell> filteredData = new FilteredList<>(plugins, s -> true);

		CustomTextField searchBar = new CustomTextField();
		searchBar.setStyle("-fx-text-inner-color: white;");
		searchBar.setBackground(new Background(new BackgroundFill(MeteorConstants.DARK_GRAY, null, null)));

		FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
		searchIcon.setFill(Color.CYAN);
		searchIcon.setTranslateX(2);
		searchBar.setLeft(searchIcon);

		searchBar.textProperty().addListener(obs -> {
			String filter = searchBar.getText().toLowerCase();
			if (filter.length() == 0) {
				filteredData.setPredicate(s -> true);
			} else {
				filteredData.setPredicate(s -> s.getPluginName().toLowerCase().contains(filter));
			}
		});

		HBox.setHgrow(searchBar, Priority.ALWAYS);
		HBox.setMargin(searchBar, new Insets(4, 4, 4, 8));
		HBox.setMargin(addCategory, new Insets(4, 8, 4, 4));
		setTop(new HBox(searchBar, addCategory));

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.getStylesheets().add("css/plugins/jfx-scrollbar.css");
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setPadding(new Insets(0,4,0,4));
		scrollPane.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		VBox pluginListView = new VBox();
		pluginListView.setBackground(new Background(new BackgroundFill(MeteorConstants.LIGHT_GRAY, null, null)));

		filteredData.addListener((ListChangeListener.Change<? extends PluginListCell> c) -> {
			pluginListView.getChildren().clear();
			pluginListView.getChildren().addAll(filteredData);
		});

		scrollPane.setContent(pluginListView);
		setCenter(scrollPane);

		addCategory.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			refreshPlugins();
		});

		refreshPlugins();
	}

	public void refreshPlugins() {
		plugins.clear();

		for (Plugin p : pluginManager.getPlugins()) {
			if (p != null) {
				addPlugin(p);
			}
		}
		plugins.sort(Comparator.comparing(plugin -> plugin.getPluginName().toLowerCase()));
	}

	private void addPlugin(Plugin p) {
		PluginListCell panel = new PluginListCell(p, configManager);
		PluginToggleButton tb = panel.getToggleButton();
		if (tb != null) {
			eventBus.register(panel.getToggleButton());
		}
		plugins.add(panel);
	}

	@Subscribe
	public void onExternalsReloaded(ExternalsReloaded e) {
		refreshPlugins();
	}
}
