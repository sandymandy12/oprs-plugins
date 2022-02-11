/*
 * Copyright (c) 2019-2020, ganom <https://github.com/Ganom>
 * All rights reserved.
 * Licensed under GPL3, see LICENSE for the full scope.
 */
package net.runelite.client.plugins.deecat.leftclickcast;

import com.google.inject.Provides;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.PvPUtil;
import org.pf4j.Extension;

import static net.runelite.api.MenuAction.*;

@Extension
@PluginDescriptor(
	name = "Left Click Cast",
	description = "Casting made even easier."
)
@Slf4j
@SuppressWarnings("unused")
public class LeftClickCast extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private LeftClickConfig config;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatIconManager friendsManager;

	private final Set<Integer> whitelist = new HashSet<>();

	@Setter
	private boolean tick;

	private boolean isMage;
	private MenuEntry widgetEntry = null;
	private Spells currentSpell = Spells.TELE_GRAB;

	private final HotkeyListener spellOneSwap = new HotkeyListener(() -> config.spellOneSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellOne();
		}
	};

	private final HotkeyListener spellTwoSwap = new HotkeyListener(() -> config.spellTwoSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellTwo();
		}
	};

	private final HotkeyListener spellThreeSwap = new HotkeyListener(() -> config.spellThreeSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellThree();
		}
	};

	private final HotkeyListener spellFourSwap = new HotkeyListener(() -> config.spellFourSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellFour();
		}
	};

	private final HotkeyListener spellFiveSwap = new HotkeyListener(() -> config.spellFiveSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellFive();
		}
	};

	private final HotkeyListener spellSixSwap = new HotkeyListener(() -> config.spellSixSwap())
	{
		@Override
		public void hotkeyPressed()
		{
			currentSpell = config.spellSix();
		}
	};

	@Provides
	LeftClickConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LeftClickConfig.class);
	}

	@Override
	public void startUp()
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			keyManager.registerKeyListener(spellOneSwap);
			keyManager.registerKeyListener(spellTwoSwap);
			keyManager.registerKeyListener(spellThreeSwap);
			keyManager.registerKeyListener(spellFourSwap);
			keyManager.registerKeyListener(spellFiveSwap);
			keyManager.registerKeyListener(spellSixSwap);
		}
		updateConfig();
	}

	@Override
	public void shutDown()
	{
		keyManager.unregisterKeyListener(spellOneSwap);
		keyManager.unregisterKeyListener(spellTwoSwap);
		keyManager.unregisterKeyListener(spellThreeSwap);
		keyManager.unregisterKeyListener(spellFourSwap);
		keyManager.unregisterKeyListener(spellFiveSwap);
		keyManager.unregisterKeyListener(spellSixSwap);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			keyManager.unregisterKeyListener(spellOneSwap);
			keyManager.unregisterKeyListener(spellTwoSwap);
			keyManager.unregisterKeyListener(spellThreeSwap);
			keyManager.unregisterKeyListener(spellFourSwap);
			keyManager.unregisterKeyListener(spellFiveSwap);
			keyManager.unregisterKeyListener(spellSixSwap);
			return;
		}
		keyManager.registerKeyListener(spellOneSwap);
		keyManager.registerKeyListener(spellTwoSwap);
		keyManager.registerKeyListener(spellThreeSwap);
		keyManager.registerKeyListener(spellFourSwap);
		keyManager.registerKeyListener(spellFiveSwap);
		keyManager.registerKeyListener(spellSixSwap);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		updateConfig();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		tick = false;
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.isForceLeftClick()) //&& event.getOpcode() != GROUND_ITEM_FIRST_OPTION.getId())
		{
			return;
		}

		if (event.getOpcode() == MenuAction.PLAYER_SECOND_OPTION.getId() && isMage)
		{
			final String name = Text.standardize(event.getTarget(), true);

			if (!config.disableFriendlyRegionChecks() && (client.getVarbitValue(5314) == 0 && (client.isFriended(name, false))))
			{
				return;
			}

			if (!config.disableFriendlyRegionChecks())
			{
				try
				{
					boolean b = (!PvPUtil.isAttackable(client, client.getCachedPlayers()[event.getIdentifier()]));
				}
				catch (IndexOutOfBoundsException ex)
				{
					return;
				}
			}

			setSelectSpell(currentSpell.getSpell());
			client.createMenuEntry(-1)
					.setOption("(P) Left Click " + client.getSelectedSpellName() + " -> ")
					.setTarget(event.getTarget())
					.setType(SPELL_CAST_ON_PLAYER)
					.setIdentifier(event.getIdentifier())
					.setParam0(0)
					.setParam1(0)
					.setForceLeftClick(true);
		}
		else if (event.getOpcode() == MenuAction.NPC_SECOND_OPTION.getId() && isMage)
		{
			try
			{
				NPC npc = validateNpc(event.getIdentifier());

				if (npc == null)
				{
					return;
				}

				if (config.disableStaffChecks() && !whitelist.contains(npc.getId()))
				{
					return;
				}

				setSelectSpell(currentSpell.getSpell());
				client.createMenuEntry(-1)
						.setOption("(N) Left Click " + client.getSelectedSpellName() + " -> ")
						.setTarget(event.getTarget())
						.setType(SPELL_CAST_ON_NPC)
						.setIdentifier(event.getIdentifier())
						.setParam0(0)
						.setParam1(0)
						.setForceLeftClick(true);
			}
			catch (IndexOutOfBoundsException ignored)
			{
			}
		}
		else if (event.getOpcode() == MenuAction.GROUND_ITEM_THIRD_OPTION.getId())
		{
			try
			{

				setSelectSpell(currentSpell.getSpell());
				client.createMenuEntry(-1)
						.setOption("(G) Left Click " + client.getSelectedSpellName() + " -> ")
						.setTarget(event.getTarget())
						.setType(SPELL_CAST_ON_GROUND_ITEM)
						.setIdentifier(event.getIdentifier())
						.setParam0(0)
						.setParam1(0)
						.setForceLeftClick(true);
				setTick(true);


			}
			catch (IndexOutOfBoundsException ignored)
			{
			}
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e) {
		//
//		if (!(e.getActor() instanceof Player)) {
//			return;
//		}

		if (!(e.getActor() == client.getLocalPlayer())) {
			return;
		}

		Player actor = (Player) e.getActor();
		if (currentSpell == Spells.TELE_GRAB && actor.getAnimation() == 723) {
			try {
				cast();
			} catch (AWTException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded widgetLoaded) {
		Widget spellbook = client.getWidget(WidgetInfo.SPELLBOOK);

		log.info("widget: {}, spellbook: {}", widgetLoaded, spellbook.getId());
		if (spellbook.getId() == widgetLoaded.getGroupId()) {
			try {
				cast();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	public void cast() throws AWTException {
		Widget teleGrab = client.getWidget(WidgetInfo.SPELL_TELEKINETIC_GRAB);
		Widget spellbook = client.getWidget(WidgetInfo.SPELLBOOK);
//		spellbook.isHidden()
		if (client.getSpellSelected() == false && spellbook.isHidden()) {
			VirtualKeyboard.sendKeys("F6");

		} else {
			Point point = Utility.randomPoint(teleGrab.getBounds(), 100);
			VirtualKeyboard.click(point);
		}


	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		log.info("{}, {}, {}, {}", event.getMenuOption(), event.getId(), event.getMenuAction(), event.getMenuTarget());
		if (event.getMenuOption().contains("(P)"))
		{
			event.setMenuAction(SPELL_CAST_ON_PLAYER);
			event.setActionParam(0);
			event.setWidgetId(0);
		}
		else if (event.getMenuOption().contains("(N)"))
		{
			event.setMenuAction(SPELL_CAST_ON_NPC);
			event.setActionParam(0);
			event.setWidgetId(0);
		}
		else if (event.getMenuOption().contains("(G)"))
		{

			log.info("spell {}, param: {}", client.getSelectedSpellWidget(), client.getSelectedSpellActionName());
			event.setMenuAction(SPELL_CAST_ON_GROUND_ITEM);

		}
	}



	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		final ItemContainer ic = event.getItemContainer();

		if (client.getItemContainer(InventoryID.EQUIPMENT) != ic)
		{
			return;
		}

		isMage = false;

		for (Item item : ic.getItems())
		{
			final String name = client.getItemComposition(item.getId()).getName().toLowerCase();
			if (name.contains("staff") || name.contains("wand") || name.contains("sceptre") || name.contains("trident"))
			{
				isMage = true;
				break;
			}
		}

		if (config.disableStaffChecks())
		{
			isMage = true;
		}
	}

	private void updateConfig()
	{
		whitelist.clear();
		if (config.disableStaffChecks())
		{
			List<String> string = Text.fromCSV(config.whitelist());
			for (String s : string)
			{
				try
				{
					whitelist.add(Integer.parseInt(s));
				}
				catch (NumberFormatException ignored)
				{
				}
			}
		}
	}

	private void setSelectSpell(WidgetInfo info)
	{
		final Widget widget = client.getWidget(info);
		client.setSelectedSpellName("<col=00ff00>" + widget.getName() + "</col>");
		client.setSelectedSpellWidget(widget.getId());
		client.setSelectedSpellChildIndex(-1);
	}

	private void insert(MenuEntry e)
	{
		client.insertMenuItem(
			e.getOption(),
			e.getTarget(),
			e.getOpcode(),
			e.getIdentifier(),
			e.getParam0(),
			e.getParam1(),
			true
		);
	}

	/**
	 * This method is not ideal, as its going to create a ton of junk
	 * but its the most reliable method i've found so far for validating
	 * NPCs on menu events. Another solution would be to use string
	 * comparison, however most users are used to the id concept
	 * so this was the path of least resistance. I'm open to
	 * suggestions however if anyone wants to offer them.
	 * -Ganom
	 *
	 * @param index Menu event index.
	 * @return {@link NPC} object for comparison.
	 */
	@Nullable
	private NPC validateNpc(int index)
	{
		NPC npc = null;

		for (NPC clientNpc : client.getNpcs())
		{
			if (index == clientNpc.getIndex())
			{
				npc = clientNpc;
				break;
			}
		}

		return npc;
	}
}
