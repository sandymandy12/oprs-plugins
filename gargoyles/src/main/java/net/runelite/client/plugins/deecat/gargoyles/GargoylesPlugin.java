/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
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
package net.runelite.client.plugins.deecat.gargoyles;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;

@PluginDescriptor(
	name = "Gargoyles",
	description = "These boys gotta go",
	tags = {"highlight", "npcs", "overlay", "respawn", "tags"}
)
@Slf4j
public class GargoylesPlugin extends Plugin
{
	private long lastTickUpdate;
	private long lastSwap;
	private final List<Integer> wep = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private GargoylesConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GargoylesOverlay gargoylesOverlay;

	@Inject
	private MyItems items;

	/**
	 * The players location on the last game tick.
	 */
	private WorldPoint lastPlayerLocation;


	@Provides
	GargoylesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GargoylesConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(gargoylesOverlay);

	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(gargoylesOverlay);

	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN ||
			event.getGameState() == GameState.HOPPING)
		{
			wep.clear();
		}

	}

	@Subscribe
	public void onGameTick(GameTick event) throws AWTException {
		lastTickUpdate = Instant.now().toEpochMilli();
		long swapTimer = config.swapTimer();
		long elapsed = Instant.now().toEpochMilli() - lastSwap;

		final int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
		final int myPrayer = client.getBoostedSkillLevel(Skill.PRAYER);


		if (!inbounds())
		{
			return;
		}

		if (myHp <= config.minHp() && elapsed >= swapTimer)
		{
			log.info(String.valueOf(myHp));
			if (config.onTask())
			{
				swapGuthansHelm();
			}
			swapGuthansWep();
			lastSwap = Instant.now().toEpochMilli();
		}
		else if (myHp >= config.maxHp() && elapsed >= swapTimer)
		{
			if (config.onTask())
			{
				swapSlayerHelm();
			}

			swapAltWep();
			swapAltShield();
			lastSwap = Instant.now().toEpochMilli();
		}
		if (myPrayer <= config.minPray() && elapsed >= swapTimer)
		{

			potUp();
			lastSwap = Instant.now().toEpochMilli();
		}

		heal();

	}

	private boolean inbounds() {

		Point m = client.getMouseCanvasPosition();
		if (m.getX() == -1 || m.getY() == -1)
		{
			return false;
		}
		return true;
	}

	private boolean hasTarget(){
		System.out.println("say something"); //client.getLocalPlayer().getInteracting().getName());
		return client.getLocalPlayer().getInteracting().getName() != "";
	}

	private void heal() throws AWTException {

		int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (myHp <= config.minHp())
		{
			if(inventoryHidden)
			{
				VirtualKeyboard.sendKeys("escape");
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

	private void potUp() throws AWTException {

		int prayer = client.getBoostedSkillLevel(Skill.PRAYER);
		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (prayer <= config.minPray())
		{
			if(inventoryHidden && !client.isKeyPressed(KeyCode.KC_SHIFT))
			{
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isPrayer(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					break;
				}
			}
		}


	}

	private void swapGuthansHelm() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (usedHelmet() == null || !items.isGuthansHelm(usedHelmet().getId()))
		{
			if(inventoryHidden && hasTarget()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				System.out.println(invItem.getId());
				if (items.isGuthansHelm(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
	}

	private void swapGuthansWep() throws AWTException {
		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (usedWeapon() == null || !items.isGuthansWep(usedWeapon().getId()))
		{
			if(inventoryHidden && hasTarget()){
				VirtualKeyboard.sendKeys("escape");
			}
			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isGuthansWep(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}

	}

	private void swapSlayerHelm() throws AWTException {
		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (usedHelmet() == null || !items.isSlayerHelm(usedHelmet().getId()))
		{
			if(inventoryHidden && hasTarget()){
				VirtualKeyboard.sendKeys("escape");
			}
			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isSlayerHelm(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
	}

	private void swapAltWep() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (usedWeapon() == null || !items.isAltWep(usedWeapon().getId()))
		{
			if(inventoryHidden && hasTarget()){
				VirtualKeyboard.sendKeys("escape");
			}
			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isAltWep(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
	}

	private void swapAltShield() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();
		if (usedShield()==null || !items.isAltShield(usedShield().getId()))
		{
			if(inventoryHidden && hasTarget()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (items.isAltShield(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
	}


	public Item usedWeapon()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (item == null)
		{
			return null;
		}

		return item;
	}

	public Item usedCape()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.CAPE.getSlotIdx());
		if (item == null)
		{
			return null;
		}

		return item;
	}

	public Item usedHelmet()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.HEAD.getSlotIdx());
		if (item == null)
		{
			return null;
		}

		return item;
	}

	public Item usedAmulet()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.AMULET.getSlotIdx());
		if (item == null)
		{
			return null;
		}

		return item;
	}

	public Item usedShield()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.SHIELD.getSlotIdx());
		if (item == null)
		{
			return null;
		}

		return item;
	}

	public Item usedAmmo()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item item = equipment.getItem(EquipmentInventorySlot.AMMO.getSlotIdx());
		if (item == null)
		{
			return null;
		}
		return item;
	}
}
