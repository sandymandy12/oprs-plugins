/*
 * Copyright (c) 2018, Raqes <j.raqes@gmail.com>
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
package net.runelite.client.plugins.deecat.switches;

import com.google.inject.Provides;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.PvPUtil;
import net.runelite.client.ws.PartyService;
import net.runelite.client.ws.WSClient;
import org.pf4j.Extension;

import static net.runelite.api.MenuAction.SPELL_CAST_ON_NPC;
import static net.runelite.api.MenuAction.SPELL_CAST_ON_PLAYER;

@Extension
@PluginDescriptor(
		name = "Switches",
		description = "Track special attacks used on NPCs",
		tags = {"combat", "npcs", "overlay"},
		enabledByDefault = false
)
@Slf4j
public class SwitchesPlugin extends Plugin
{
	private long lastSwap;

	private int currentWorld;
	private int specialPercentage;
	private Actor lastSpecTarget;
	private int lastSpecTick;


	private SpecialWeapon specialWeapon;
	private final Set<Integer> interactedNpcIds = new HashSet<>();
	private final SpecialCounter[] specialCounter = new SpecialCounter[SpecialWeapon.values().length];
	public List<Player> players = new ArrayList<>();

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private WSClient wsClient;

	@Inject
	private PartyService party;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Notifier notifier;

	@Inject
	private SwitchesConfig config;

	@Inject
	private MyItems item;

	@Provides
	SwitchesConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SwitchesConfig.class);
	}

	@Override
	protected void startUp()
	{
		wsClient.registerMessage(SpecialCounterUpdate.class);
		currentWorld = -1;
		specialPercentage = -1;
		lastSpecTarget = null;
		lastSpecTick = -1;
		interactedNpcIds.clear();
	}

	@Override
	protected void shutDown()
	{
		removeCounters();
		wsClient.unregisterMessage(SpecialCounterUpdate.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			if (currentWorld == -1)
			{
				currentWorld = client.getWorld();
			}
			else if (currentWorld != client.getWorld())
			{
				currentWorld = client.getWorld();
				removeCounters();
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) throws AWTException {
		long lastTickUpdate = Instant.now().toEpochMilli();
		long swapTimer = config.swapTimer();
		long elapsed = lastTickUpdate - lastSwap;

		players = client.getPlayers();

		if (this.specialPercentage > config.darkBowThreshold() && elapsed >= swapTimer && config.swapping())
		{
			if(!inbounds() )//|| !targetFound())
			{
				return;
			}
			swapDarkBow();
		}
		else if (this.specialPercentage <= config.darkBowThreshold() && elapsed >= swapTimer && config.swapping())
		{
			if(!inbounds() )//|| !targetFound())
			{
				return;
			}
			//swapMagicBow();
			swapKarilsXbow();
		}

		heal();

	}
	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (event.isForceLeftClick())
		{
			return;
		}

	}

	@Subscribe
	public void onPlayerSpawned(PlayerSpawned playerSpawned){
		Player player = playerSpawned.getPlayer();

	}
/*
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
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
	}

	private void setSelectSpell(WidgetInfo info)
	{
		final Widget widget = client.getWidget(info);
		client.setSelectedSpellName("<col=00ff00>" + widget.getName() + "</col>");
		client.setSelectedSpellWidget(widget.getId());
		client.setSelectedSpellChildIndex(-1);
	}

 */

	private boolean hasTarget(){
		if (client.getLocalPlayer().getInteracting() != null)
		{
			return true;
		}
		return false;
	}
	private boolean inbounds() {

		Point m = client.getMouseCanvasPosition();
		if (m.getX() == -1 || m.getY() == -1)
		{
			return false;
		}
		return true;
	}

	private boolean targetFound(){

		boolean inWild = client.getVar(Varbits.IN_WILDERNESS) == 1;

		boolean target = false;
		for (Player player : players)
		{
			if (withinRange(player))
			{
				target = true;
				break;
			}
		}
		if (inWild && target)
		{
			return true;
		}
		return false;

	}

	private void attack(Player p){
		if (p == null)
		{
			return;
		}
		Rectangle bounds = p.getConvexHull().getBounds();
		VirtualKeyboard.doubleClick(Utility.randomPoint(bounds));
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged interactingChanged)
	{
		Actor source = interactingChanged.getSource();
		Actor target = interactingChanged.getTarget();
		if (lastSpecTick != client.getTickCount() || source != client.getLocalPlayer() || target == null)
		{
			return;
		}

		log.debug("Updating last spec target to {} (was {})", target.getName(), lastSpecTarget);
		lastSpecTarget = target;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int specialPercentage = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);

		if (this.specialPercentage == -1 || specialPercentage >= this.specialPercentage)
		{
			this.specialPercentage = specialPercentage;
			return;
		}

		this.specialPercentage = specialPercentage;
		this.specialWeapon = usedSpecialWeapon();

		log.debug("Special attack used - percent: {} weapon: {}", specialPercentage, specialWeapon);

		// spec was used; since the varbit change event fires before the interact change event,
		// this will be specing on the target of interact changed *if* it fires this tick,
		// otherwise it is what we are currently interacting with
		lastSpecTarget = client.getLocalPlayer().getInteracting();
		lastSpecTick = client.getTickCount();

	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		Actor target = hitsplatApplied.getActor();
		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		// Ignore all hitsplats other than mine
		if (!hitsplat.isMine() || target == client.getLocalPlayer())
		{
			return;
		}

		log.debug("Hitsplat target: {} spec target: {}", target, lastSpecTarget);

		// If waiting for a spec, ignore hitsplats not on the actor we specced
		if (lastSpecTarget != null && lastSpecTarget != target)
		{
			return;
		}

		boolean wasSpec = lastSpecTarget != null;
		lastSpecTarget = null;

		if (!(target instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) target;
		int interactingId = npc.getId();

		// If this is a new NPC reset the counters
		if (!interactedNpcIds.contains(interactingId))
		{
			removeCounters();
			addInteracting(interactingId);
		}

		if (wasSpec && specialWeapon != null && hitsplat.getAmount() > 0)
		{
			int hit = getHit(specialWeapon, hitsplat);

			updateCounter(specialWeapon, null, hit);

			if (!party.getMembers().isEmpty())
			{
				final SpecialCounterUpdate specialCounterUpdate = new SpecialCounterUpdate(interactingId, specialWeapon, hit);
				specialCounterUpdate.setMemberId(party.getLocalMember().getMemberId());
				wsClient.send(specialCounterUpdate);
			}


		}
	}

	private void addInteracting(int npcId)
	{
		interactedNpcIds.add(npcId);

		// Add alternate forms of bosses
		final Boss boss = Boss.getBoss(npcId);
		if (boss != null)
		{
			interactedNpcIds.addAll(boss.getIds());
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC actor = npcDespawned.getNpc();

		if (lastSpecTarget == actor)
		{
			lastSpecTarget = null;
		}

		if (actor.isDead() && interactedNpcIds.contains(actor.getId()))
		{
			removeCounters();
		}
	}

	@Subscribe
	public void onSpecialCounterUpdate(SpecialCounterUpdate event)
	{
		if (party.getLocalMember().getMemberId().equals(event.getMemberId()))
		{
			return;
		}

		String name = party.getMemberById(event.getMemberId()).getName();
		if (name == null)
		{
			return;
		}

		clientThread.invoke(() ->
		{
			// If not interacting with any npcs currently, add to interacting list
			if (interactedNpcIds.isEmpty())
			{
				addInteracting(event.getNpcId());
			}

			// Otherwise we only add the count if it is against a npc we are already tracking
			if (interactedNpcIds.contains(event.getNpcId()))
			{
				updateCounter(event.getWeapon(), name, event.getHit());
			}
		});
	}

	//boolean atkStylesHidden = client.getWidget(593, 36).getChild(4).isHidden();
	//boolean inventoryHidden = client.getWidget(149, 0).isHidden();
	private void swapDarkBow() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		boolean active = client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED ) == 1;

		if (usedWeapon().getId() != ItemID.DARK_BOW)
		{
			if(inventoryHidden){ // && targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (invItem.getId() == ItemID.DARK_BOW)
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}


		if (usedAmmo().getId() != ItemID.DRAGON_ARROW)
		{
			if(inventoryHidden){ // && targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (invItem.getId() == ItemID.DRAGON_ARROW)
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}

		else if (!active)
		{
			activateSpec();
		}

	}

	private void swapMagicBow() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (usedWeapon().getId() != ItemID.MAGIC_SHORTBOW_I)
		{
			if(inventoryHidden && targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}
			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (invItem.getId() == ItemID.MAGIC_SHORTBOW_I)
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
		if (usedAmmo().getId() != ItemID.RUNE_ARROW)
		{
			if(inventoryHidden && targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (invItem.getId() == ItemID.RUNE_ARROW)
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}

	}

	private void swapKarilsXbow() throws AWTException {

		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if (!item.isKarilsXbow(usedWeapon().getId()))
		{
			if(inventoryHidden){ //&& targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (item.isKarilsXbow(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}
		if (usedAmmo().getId() != ItemID.BOLT_RACK)
		{
			if(inventoryHidden){ // && targetFound()){
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (invItem.getId() == ItemID.BOLT_RACK)
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					return;
				}
			}
		}

	}

	private void heal() throws AWTException {

		int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
		boolean inventoryHidden = client.getWidget(149, 0).isHidden();

		if(myHp <= config.minHp() && config.teleOut())
		{
			if(inventoryHidden && targetFound())
			{
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (item.isTeleport(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					break;
				}
			}

		}
		else if (myHp <= config.maxHp())
		{

			if(inventoryHidden && targetFound())
			{
				VirtualKeyboard.sendKeys("escape");
			}

			Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
			for (WidgetItem invItem : invItems)
			{
				if (item.isFood(invItem.getId()))
				{
					VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
					break;
				}
			}
		}


	}

	private void activateSpec() throws AWTException {

		boolean active = client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED ) == 1;

		if (!active)
		{
			Widget specBar = client.getWidget(593,37);
			if (specBar.isHidden())
			{
				VirtualKeyboard.sendKeys("f1");
			}

			VirtualKeyboard.click(Utility.randomPoint(specBar.getBounds()));
		}
	}

	private SpecialWeapon usedSpecialWeapon()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null)
		{
			return null;
		}

		Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null)
		{
			return null;
		}

		for (SpecialWeapon specialWeapon : SpecialWeapon.values())
		{
			if (specialWeapon.getItemID() == weapon.getId())
			{
				return specialWeapon;
			}
		}
		return null;
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


	private void updateCounter(SpecialWeapon specialWeapon, String name, int hit)
	{
		SpecialCounter counter = specialCounter[specialWeapon.ordinal()];

		if (counter == null)
		{
			counter = new SpecialCounter(itemManager.getImage(specialWeapon.getItemID()), this, config,
					hit, specialWeapon);
			infoBoxManager.addInfoBox(counter);
			specialCounter[specialWeapon.ordinal()] = counter;
		}
		else
		{
			counter.addHits(hit);
		}

		// Display a notification if special attack thresholds are met
		sendNotification(specialWeapon, counter);

		// If in a party, add hit to partySpecs for the infobox tooltip
		Map<String, Integer> partySpecs = counter.getPartySpecs();
		if (!party.getMembers().isEmpty())
		{
			if (partySpecs.containsKey(name))
			{
				partySpecs.put(name, hit + partySpecs.get(name));
			}
			else
			{
				partySpecs.put(name, hit);
			}
		}
	}

	private void sendNotification(SpecialWeapon weapon, SpecialCounter counter)
	{
		int threshold = weapon.getThreshold().apply(config);
		if (threshold > 0 && counter.getCount() >= threshold && config.thresholdNotification())
		{
			notifier.notify(weapon.getName() + " special attack threshold reached!");
		}
	}

	private void removeCounters()
	{
		interactedNpcIds.clear();

		for (int i = 0; i < specialCounter.length; ++i)
		{
			SpecialCounter counter = specialCounter[i];

			if (counter != null)
			{
				infoBoxManager.removeInfoBox(counter);
				specialCounter[i] = null;
			}
		}
	}

	private int getHit(SpecialWeapon specialWeapon, Hitsplat hitsplat)
	{
		return specialWeapon.isDamage() ? hitsplat.getAmount() : 1;
	}

	public boolean withinRange(Player p) {
		appendAttackLevelRangeText();
		return p.getCombatLevel() >= minCombatLevel && p.getCombatLevel() <= maxCombatLevel;
	}

	public int minCombatLevel = 0;
	public int maxCombatLevel = 0;

	public void combatAttackRange(final int combatLevel, final int wildernessLevel)
	{
		minCombatLevel = Math.max(3, combatLevel - wildernessLevel);
		maxCombatLevel = Math.min(Experience.MAX_COMBAT_LEVEL, combatLevel + wildernessLevel);
	}

	private final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");
	public void appendAttackLevelRangeText() {
		final Widget wildernessLevelWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
		final Widget pvpWorldWidget = client.getWidget(90,58);

		String wildernessLevelText = "";
		if (pvpWorldWidget != null && !pvpWorldWidget.isHidden()) {
			wildernessLevelText = pvpWorldWidget.getText();
		}
		if (wildernessLevelText.isEmpty() && (wildernessLevelWidget != null && !wildernessLevelWidget.isHidden())) {
			wildernessLevelText = wildernessLevelWidget.getText();
		}
		if (wildernessLevelText.isEmpty()) {
			minCombatLevel = 0;
			maxCombatLevel = 0;
			return;
		}

		final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
		if (!m.matches()) {
			return;
		}
		final int wildernessLevel = Integer.parseInt(m.group(1));
		final int combatLevel = Objects.requireNonNull(client.getLocalPlayer()).getCombatLevel();
		combatAttackRange(combatLevel, wildernessLevel);
		minCombatLevel = Integer.parseInt(m.group(1));
		maxCombatLevel = Integer.parseInt(m.group(2));
	}

}