/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
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
package net.runelite.client.plugins.deecat.fightcaves;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.plugins.deecat.fightcaves.ExtUtils;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

@PluginDescriptor(
	name = "Caves Plugin",
	description = "The caves bish",
	tags = {"caves", "dc", "tick"},
	enabledByDefault = false
)
@Slf4j
public class CavesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ExtUtils utils;

	@Inject
	private CavesConfig config;

	@Inject
	private CavesOverlay overlay;

	@Inject
	private MyItems items;

	@Inject
	private ExtUtils extUtils;

	private ScheduledExecutorService executor;
	private Rectangle bounds;





	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		executor = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		executor.shutdownNow();
	}

	@Provides
	CavesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CavesConfig.class);
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event) {
		Projectile projectile = event.getProjectile();


		if (projectile.getInteracting() != client.getLocalPlayer())
		{
			return;
		}
		log.info("Projectile " + event.getProjectile().getId() + " incoming!!");

		if (config.tickEat())
		{
			heal();
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e) {
		if (!(e.getActor() instanceof NPC)) {
			return;
		}

		int animation = e.getActor().getAnimation();
		if (config.protection())
		{
			activate(animation);
		}


	}

	private void activate(int animation){

		for (JadAttackStyle attackStyle : JadAttackStyle.values())
		{
			if (attackStyle.getAnimationId() == animation)
			{
				protectFrom(attackStyle.getPrayer());
				break;
			}
		}
	}

	private void protectFrom(Prayer prayer){

		if (client.getWidget(WidgetID.PRAYER_GROUP_ID, 0).isHidden()) {
			bounds = client.getWidget(WidgetInfo.FIXED_VIEWPORT_PRAYER_TAB).getBounds();
			VirtualKeyboard.click(Utility.randomPoint(bounds));
		}

		switch (prayer){
			case PROTECT_FROM_MISSILES:
				if (client.getVar(Varbits.PRAYER_PROTECT_FROM_MISSILES) == 0){
					bounds = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES).getBounds();
					VirtualKeyboard.click(Utility.randomPoint(bounds));
				}
				break;

			case PROTECT_FROM_MAGIC:
				if (client.getVar(Varbits.PRAYER_PROTECT_FROM_MAGIC) == 0){
					bounds = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC).getBounds();
					VirtualKeyboard.click(Utility.randomPoint(bounds));
				}
				break;

			case PROTECT_FROM_MELEE:
				if (client.getVar(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0){
					bounds = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE).getBounds();
					VirtualKeyboard.click(Utility.randomPoint(bounds));
				}
				break;
			default:
				break;
		}
	}

	private void heal() {

		final int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);

		if (myHp <= config.maxHit())
		{
			if(client.getWidget(149, 0).isHidden() && !client.isKeyPressed(KeyCode.KC_SHIFT))
			{
				try {
					VirtualKeyboard.sendKeys("escape");
				} catch (AWTException e) {
					e.printStackTrace();
				}
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isFood(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					break;
				}
			}
		}
	}

	private boolean inbounds() {

		Point m = client.getMouseCanvasPosition();
		if (m.getX() == -1 || m.getY() == -1)
		{
			return false;
		}
		return true;
	}

	private void schedule(int delay)
	{
		executor.schedule(this::simLeftClick, delay, TimeUnit.MILLISECONDS);
	}

	private void simLeftClick()
	{
		extUtils.click(bounds);

	}

}
