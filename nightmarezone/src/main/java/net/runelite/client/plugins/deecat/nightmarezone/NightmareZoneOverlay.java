/*
 * Copyright (c) 2018, Nickolaj <https://github.com/fire-proof>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.deecat.nightmarezone;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.QuantityFormatter;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class NightmareZoneOverlay extends OverlayPanel
{
	private final Client client;
	private final NightmareZoneConfig config;
	private final NightmareZonePlugin plugin;
	private final InfoBoxManager infoBoxManager;
	private final ItemManager itemManager;

	private AbsorptionCounter absorptionCounter;

	@Inject
	NightmareZoneOverlay(
			Client client,
			NightmareZoneConfig config,
			NightmareZonePlugin plugin,
			InfoBoxManager infoBoxManager,
			ItemManager itemManager)
	{
		super(plugin);
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(OverlayPriority.LOW);
		this.client = client;
		this.config = config;
		this.plugin = plugin;
		this.infoBoxManager = infoBoxManager;
		this.itemManager = itemManager;
		getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "NMZ overlay"));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInNightmareZone() || !config.moveOverlay())
		{
			if (absorptionCounter != null)
			{
				removeAbsorptionCounter();
				// Restore original widget
				Widget nmzWidget = client.getWidget(WidgetInfo.NIGHTMARE_ZONE);
				if (nmzWidget != null)
				{
					nmzWidget.setHidden(false);
				}
			}
			return null;
		}

		Widget nmzWidget = client.getWidget(WidgetInfo.NIGHTMARE_ZONE);

		if (nmzWidget != null)
		{
			nmzWidget.setHidden(true);
		}

		renderAbsorptionCounter();

		final int currentPoints = client.getVar(Varbits.NMZ_POINTS);
		final int absorptionPoints = client.getVar(Varbits.NMZ_ABSORPTION);
		final int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);

		WidgetItem invItem = client.getWidget(WidgetInfo.INVENTORY).getWidgetItem(config.dcatNumber());


		if (myHp > 1 && myHp < 50){
			panelComponent.getChildren().add(TitleComponent.builder()
					.text("Use Rock-cake")
					.color(Color.DARK_GRAY)
					.build());
		}
		else if (myHp > 50){
			panelComponent.getChildren().add(TitleComponent.builder()
					.text("Use Overload")
					.color(Color.RED)
					.build());
		}
		else if (absorptionPoints < config.absorptionThreshold()){
			panelComponent.getChildren().add(TitleComponent.builder()
					.text("Absorption Low")
					.color(Color.CYAN)
					.build());
		}
		else {
			panelComponent.getChildren().add(TitleComponent.builder()
					.text("All good")
					.color(Color.GREEN)
					.build());
		}
		panelComponent.getChildren().add(LineComponent.builder()
			.left("Points: ")
			.right(QuantityFormatter.formatNumber(currentPoints))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Absorption: ")
			.right(QuantityFormatter.formatNumber(absorptionPoints))
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left("overload?: ")
			.right(String.valueOf(plugin.isOverload(invItem)))
			.build());

		return super.render(graphics);

	}

	private void renderAbsorptionCounter()
	{
		int absorptionPoints = client.getVar(Varbits.NMZ_ABSORPTION);
		if (absorptionPoints == 0)
		{
			if (absorptionCounter != null)
			{
				removeAbsorptionCounter();
				absorptionCounter = null;
			}
		}
		else if (config.moveOverlay())
		{
			if (absorptionCounter == null)
			{
				addAbsorptionCounter(absorptionPoints);
			}
			else
			{
				absorptionCounter.setCount(absorptionPoints);
			}
		}
	}

	private void addAbsorptionCounter(int startValue)
	{
		absorptionCounter = new AbsorptionCounter(itemManager.getImage(ItemID.ABSORPTION_4), plugin, startValue, config.absorptionThreshold());
		absorptionCounter.setAboveThresholdColor(config.absorptionColorAboveThreshold());
		absorptionCounter.setBelowThresholdColor(config.absorptionColorBelowThreshold());
		infoBoxManager.addInfoBox(absorptionCounter);
	}

	public void removeAbsorptionCounter()
	{
		infoBoxManager.removeInfoBox(absorptionCounter);
		absorptionCounter = null;
	}

	public void updateConfig()
	{
		if (absorptionCounter != null)
		{
			absorptionCounter.setAboveThresholdColor(config.absorptionColorAboveThreshold());
			absorptionCounter.setBelowThresholdColor(config.absorptionColorBelowThreshold());
			absorptionCounter.setThreshold(config.absorptionThreshold());
		}
	}
}
