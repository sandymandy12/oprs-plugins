/*
 * Copyright (c) 2022, Sandra Mandell <sandman7546@gmail.com>
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
package net.runelite.client.plugins.deecat.magic;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
//import net.runelite.client.plugins.deecat.ExtUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;

@Extension
@PluginDescriptor(
	name = "Magic Plugin",
	description = "Various magic training methods",
	tags = {"magic", "dc", "skilling"},
	enabledByDefault = false
)
@Slf4j
public class MagicPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MagicConfig config;

	@Inject
	private MagicOverlay overlay;

	private int invTab;
	public String lastClicked = "Just chillin";
	public int enchants = 0;
	public int natureRunes = 0;
	public int alchs = 0;
	private Point lastPoint;


	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		alchs = 0;
		enchants = 0;
		lastClicked = "";
	}

	@Provides
	MagicConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MagicConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		int animation = client.getLocalPlayer().getAnimation();
		invTab = client.getVar(VarClientInt.INVENTORY_TAB);
		if(!inbounds()) return;

		Widget telegrab = client.getWidget(WidgetInfo.SPELL_TELEKINETIC_GRAB);
		Widget spellbook = client.getWidget((WidgetInfo.SPELLBOOK));

		if (animation == -1 && config.alt()) {
			Widget inventory = client.getWidget(WidgetInfo.INVENTORY);
//			log.info("{}", inventory.getWidgetItem(0).getQuantity());
			if (spellbook.isHidden()) {
				try {
					VirtualKeyboard.sendKeys("F6");
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			} else if (alchs < 2 && client.getSpellSelected() == false) {
				lastPoint = Utility.randomPoint(telegrab.getBounds(), 100);
				VirtualKeyboard.click(lastPoint);
			}
		}

		if(config.highAlch())
		{
			WidgetItem slot3 = client.getWidget(WidgetInfo.INVENTORY).getWidgetItem(config.alchSlot());

			Widget enchantXBow = client.getWidget(WidgetInfo.SPELL_ENCHANT_CROSSBOW_BOLT);
			Widget highAlchSpell = client.getWidget(218,39);


			if(animation == -1 && slot3.getId() != -1)
			{

				enchants = 0; // again using this to count xbow enchants
				if(invTab == 6) // oneclick plugin
				{
					lastPoint = Utility.randomPoint(highAlchSpell.getBounds(), 100);
					VirtualKeyboard.click(lastPoint);
					lastClicked = "High Alch";

				}
			}

			if (animation == 713) {

				enchants ++;
				if (config.enchant() && enchants < 3){ // using misses to count xbow enchants. compiling is really slow
					log.info("spell " + enchants);

					lastPoint = Utility.randomPoint(enchantXBow.getBounds(), 100);

					VirtualKeyboard.click(lastPoint);
					lastClicked = "Enchant";

					try {
						VirtualKeyboard.sendKeys("SPACE");
					} catch (AWTException awtException) {
						awtException.printStackTrace();
					}

				}


			}
		}
		else { lastClicked = "Just chillin"; }

//		if (config.alt()){
//			if (animation == -1)
//			{
//				Widget spell = client.getWidget(config.spell().getChildId());
//				ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//				if (inventory.count(config.spell().getItemId()) > 0)
//				{
//					VirtualKeyboard.click(Utility.randomPoint(spell.getBounds()));
//					try {
//						VirtualKeyboard.sendKeys("SPACE");
//					} catch (AWTException awtException) {
//						awtException.printStackTrace();
//					}
//				}
//			}
//		}


	}

	@Subscribe
	public void onItemQuantityChanged(ItemQuantityChanged itemQuantityChanged){
		log.info("count {}", itemQuantityChanged.getItem());
		if (alchs >= 2){
			try {
				VirtualKeyboard.sendKeys("BACK_SLASH");
			} catch (AWTException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Subscribe
	public void onGamestateChanged(GameStateChanged e) {
		if (e.getGameState() == GameState.HOPPING) {
			alchs = 0;
		}
		Widget spellbook = client.getWidget((WidgetInfo.SPELLBOOK));
		if (e.getGameState() == GameState.LOGGED_IN) {
			if (spellbook.isHidden()) {
				try {
					VirtualKeyboard.sendKeys("F6");
				} catch (AWTException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged e) {
		if (!(e.getActor() instanceof Player)) {
			return;
		}

		Widget enchantXBow = client.getWidget(WidgetInfo.SPELL_ENCHANT_CROSSBOW_BOLT);

		final Player local = (Player) e.getActor();
		if (local != client.getLocalPlayer()) return;

//		// telegrab
		if (local.getAnimation() == 723) {
			alchs ++;
		}


		if(config.highAlch()) {
			log.info(String.valueOf(local.getAnimation()));
			log.info(lastClicked);

			if (local.getAnimation() == 713) {
				alchs ++;
				// hitItem.add(lastPoint);
				try {
					// HOPS WORLD.
					VirtualKeyboard.sendKeys("BACK_SLASH");
				} catch (AWTException ex) {
					ex.printStackTrace();
				}


				if (invTab == 6 && config.enchant()) {
					log.info("xbow enchant");
					enchants = 0;
					lastPoint = Utility.randomPoint(enchantXBow.getBounds(), 100);

					VirtualKeyboard.click(lastPoint);

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

}
