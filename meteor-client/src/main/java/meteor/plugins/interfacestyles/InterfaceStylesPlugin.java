package meteor.plugins.interfacestyles;/*
 * Copyright (c) 2018, Lotto <https://github.com/devLotto>
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.questhelper.QuestHelperConfig;
import lombok.extern.slf4j.Slf4j;
import meteor.callback.ClientThread;
import meteor.config.ConfigManager;
import meteor.eventbus.Subscribe;
import meteor.eventbus.events.ConfigChanged;
import meteor.game.SpriteManager;
import meteor.plugins.Plugin;
import meteor.plugins.PluginDescriptor;
import meteor.util.ImageUtil;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "Interface Styles",
	description = "Change the interface style to the 2005/2010 interface",
	tags = {"2005", "2010", "skin", "theme", "ui"},
	enabledByDefault = false
)
public class InterfaceStylesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private InterfaceStylesConfig config;

	@Inject
	private SpriteManager spriteManager;

	private SpritePixels[] defaultCrossSprites;

	@Provides
	public InterfaceStylesConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(InterfaceStylesConfig.class);
	}

	@Override
	public void startup() {
		clientThread.invoke(this::updateAllOverrides);
	}

	@Override
	public void shutdown() {
		clientThread.invoke(() ->
		{
			restoreWidgetDimensions();
			removeGameframe();
			restoreHealthBars();
			restoreCrossSprites();
		});
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged config)
	{
		if (config.getGroup().equals("interfaceStyles"))
		{
			clientThread.invoke(this::updateAllOverrides);
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if ("forceStackStones".equals(event.getEventName()) && config.alwaysStack())
		{
			int[] intStack = client.getIntStack();
			int intStackSize = client.getIntStackSize();
			intStack[intStackSize - 1] = 1;
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		adjustWidgetDimensions();
	}

	@Subscribe
	public void onPostHealthBar(PostHealthBar postHealthBar)
	{
		if (!config.hdHealthBars())
		{
			return;
		}

		HealthBar healthBar = postHealthBar.getHealthBar();
		HealthbarOverride override = HealthbarOverride.get(healthBar.getHealthBarFrontSpriteId());

		// Check if this is the health bar we are replacing
		if (override != null)
		{
			// Increase padding to show some more green at very low hp percentages
			healthBar.setPadding(override.getPadding());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() != GameState.LOGIN_SCREEN)
		{
			return;
		}

		/*
		 * The cross sprites aren't loaded yet when the initial config change event is received.
		 * So run the overriding for cross sprites when we reach the login screen,
		 * at which point the cross sprites will have been loaded.
		 */
		overrideCrossSprites();
	}

	private void updateAllOverrides()
	{
		removeGameframe();
		overrideSprites();
		overrideWidgetSprites();
		restoreWidgetDimensions();
		adjustWidgetDimensions();
		overrideHealthBars();
		overrideCrossSprites();
	}

	@Subscribe
	public void onBeforeMenuRender(BeforeMenuRender event)
	{
		if (config.hdMenu())
		{
			client.draw2010Menu(config.menuAlpha());
			event.consume();
		}
		else if (config.menuAlpha() != 255)
		{
			client.drawOriginalMenu(config.menuAlpha());
			event.consume();
		}
	}

	private void overrideSprites()
	{
		final Skin configuredSkin = config.skin();
		for (SpriteOverride spriteOverride : SpriteOverride.values())
		{
			for (Skin skin : spriteOverride.getSkin())
			{
				if (skin == configuredSkin)
				{
					final String configSkin = skin.getExtendSkin() != null
						? skin.getExtendSkin().toString()
						: skin.toString();
					String file = configSkin + "/" + spriteOverride.getSpriteID() + ".png";
					SpritePixels spritePixels = getFileSpritePixels(file);

					if (spriteOverride.getSpriteID() == SpriteID.COMPASS_TEXTURE)
					{
						client.setCompass(spritePixels);
					}
					else
					{
						client.getSpriteOverrides().put(spriteOverride.getSpriteID(), spritePixels);
					}
				}
			}
		}
	}

	private void restoreSprites()
	{
		client.getWidgetSpriteCache().reset();

		for (SpriteOverride spriteOverride : SpriteOverride.values())
		{
			client.getSpriteOverrides().remove(spriteOverride.getSpriteID());
		}
	}

	private void overrideWidgetSprites()
	{
		final Skin configuredSkin = config.skin();
		for (WidgetOverride widgetOverride : WidgetOverride.values())
		{
			if (widgetOverride.getSkin() == configuredSkin
				|| widgetOverride.getSkin() == configuredSkin.getExtendSkin())
			{
				final String configSkin = configuredSkin.getExtendSkin() != null
					? configuredSkin.getExtendSkin().toString()
					: configuredSkin.toString();
				String file = configSkin + "/widget/" + widgetOverride.getName() + ".png";
				SpritePixels spritePixels = getFileSpritePixels(file);

				if (spritePixels != null)
				{
					for (WidgetInfo widgetInfo : widgetOverride.getWidgetInfo())
					{
						client.getWidgetSpriteOverrides().put(widgetInfo.getPackedId(), spritePixels);
					}
				}
			}
		}
	}

	private void restoreWidgetSprites()
	{
		for (WidgetOverride widgetOverride : WidgetOverride.values())
		{
			for (WidgetInfo widgetInfo : widgetOverride.getWidgetInfo())
			{
				client.getWidgetSpriteOverrides().remove(widgetInfo.getPackedId());
			}
		}
	}

	private SpritePixels getFileSpritePixels(String file)
	{
		try
		{
			log.debug("Loading: {}", file);
			BufferedImage image = ImageUtil.loadImageResource(this.getClass(), file);
			return ImageUtil.getImageSpritePixels(image, client);
		}
		catch (RuntimeException ex)
		{
			log.debug("Unable to load image: ", ex);
		}

		return null;
	}

	private void adjustWidgetDimensions()
	{
		for (WidgetOffset widgetOffset : WidgetOffset.values())
		{
			if (widgetOffset.getSkin() != config.skin())
			{
				continue;
			}

			Widget widget = client.getWidget(widgetOffset.getWidgetInfo());

			if (widget != null)
			{
				if (widgetOffset.getOffsetX() != null)
				{
					widget.setRelativeX(widgetOffset.getOffsetX());
				}

				if (widgetOffset.getOffsetY() != null)
				{
					widget.setRelativeY(widgetOffset.getOffsetY());
				}

				if (widgetOffset.getWidth() != null)
				{
					widget.setWidth(widgetOffset.getWidth());
				}

				if (widgetOffset.getHeight() != null)
				{
					widget.setHeight(widgetOffset.getHeight());
				}
			}
		}
	}

	private void overrideHealthBars()
	{
		if (config.hdHealthBars())
		{
			spriteManager.addSpriteOverrides(HealthbarOverride.values());
			// Reset health bar caches to apply the override
			clientThread.invokeLater(client::resetHealthBarCaches);
		}
		else
		{
			restoreHealthBars();
		}
	}

	private void restoreHealthBars()
	{
		spriteManager.removeSpriteOverrides(HealthbarOverride.values());
		clientThread.invokeLater(client::resetHealthBarCaches);
	}

	private void overrideCrossSprites()
	{
		if (config.rsCrossSprites())
		{
			// If we've already replaced them,
			// we don't need to replace them again
			if (defaultCrossSprites != null)
			{
				return;
			}

			SpritePixels[] crossSprites = client.getCrossSprites();

			if (crossSprites == null)
			{
				return;
			}

			defaultCrossSprites = new SpritePixels[crossSprites.length];
			System.arraycopy(crossSprites, 0, defaultCrossSprites, 0, defaultCrossSprites.length);

			for (int i = 0; i < crossSprites.length; i++)
			{
				SpritePixels newSprite = getFileSpritePixels("rs3/cross_sprites/" + i + ".png");

				if (newSprite == null)
				{
					continue;
				}

				crossSprites[i] = newSprite;
			}
		}
		else
		{
			restoreCrossSprites();
		}
	}

	private void restoreCrossSprites()
	{
		if (defaultCrossSprites == null)
		{
			return;
		}

		SpritePixels[] crossSprites = client.getCrossSprites();

		if (crossSprites != null && defaultCrossSprites.length == crossSprites.length)
		{
			System.arraycopy(defaultCrossSprites, 0, crossSprites, 0, defaultCrossSprites.length);
		}

		defaultCrossSprites = null;
	}

	private void restoreWidgetDimensions()
	{
		for (WidgetOffset widgetOffset : WidgetOffset.values())
		{
			Widget widget = client.getWidget(widgetOffset.getWidgetInfo());

			if (widget != null)
			{
				widget.revalidate();
			}
		}
	}

	private void removeGameframe()
	{
		restoreSprites();
		restoreWidgetSprites();

		BufferedImage compassImage = spriteManager.getSprite(SpriteID.COMPASS_TEXTURE, 0);

		if (compassImage != null)
		{
			SpritePixels compass = ImageUtil.getImageSpritePixels(compassImage, client);
			client.setCompass(compass);
		}
	}
}