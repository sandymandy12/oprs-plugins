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
package net.runelite.client.plugins.deecat.slayer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.slayer.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.plugins.deecat.slayer.ExtUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@Extension
@PluginDescriptor(
        name = "DC Slayer",
        description = "Slayer task helper",
        tags = {"slayer", "dc", "overlay", "tags"}
)
@Slf4j
public class DCSlayerPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private DCSlayerConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DCSlayerOverlay DCSlayerOverlay;

    @Inject
    private MyItems items;

    @Inject
    private ExtUtils utils;

    @Inject
    private KeyManager keyManager;

    @Inject
    private ScheduledExecutorService executor;

    private int lastRubbed;
    private long lastHopped;
    private long clickElapsed;
    private List<NPC> interacting;

    public Player activePker;
    public Player targeted;
    public int level;
    public boolean pkers = false;
    public boolean hopFailed = false;
    public boolean teleblocked = false;
    public boolean teleportFailed = false;

    private List<String> gear1 = null;
    private List<String> gear2 = null;
    private List<String> gear3 = null;
    private List<String> activeGear = null;

    private final HotkeyListener teleKeyListener = new HotkeyListener(() -> config.teleKey())
    {
        @Override
        public void hotkeyPressed()
        {
            clientThread.invoke(() -> teleport());
        }
    };

    private final HotkeyListener gearSwapListener = new HotkeyListener(() -> config.gearSwap())
    {
        @Override
        public void hotkeyPressed()
        {
            activeGear = gear1;
            clientThread.invoke(() -> swap(gear1));
        }
    };

    private final HotkeyListener gear2SwapListener = new HotkeyListener(() -> config.gearSwap2())
    {
        @Override
        public void hotkeyPressed()
        {
            activeGear = gear2;
            clientThread.invoke(() -> swap(gear2));
        }
    };

    private final HotkeyListener gear3SwapListener = new HotkeyListener(() -> config.gearSwap3())
    {
        @Override
        public void hotkeyPressed()
        {
            activeGear = gear3;
            clientThread.invoke(() -> swap(gear3));
        }
    };

    @Provides
    DCSlayerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DCSlayerConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(DCSlayerOverlay);
        keyManager.registerKeyListener(gearSwapListener);
        keyManager.registerKeyListener(gear2SwapListener);
        keyManager.registerKeyListener(gear3SwapListener);
        keyManager.registerKeyListener(teleKeyListener);
        interacting = client.getNpcs();
        reset();
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(DCSlayerOverlay);
        keyManager.unregisterKeyListener(gearSwapListener);
        keyManager.unregisterKeyListener(gear2SwapListener);
        keyManager.unregisterKeyListener(gear3SwapListener);
        keyManager.unregisterKeyListener(teleKeyListener);
        activeGear.clear();
        interacting.clear();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        utils.appendAttackLevelRangeText();
        level = utils.wildernessLevel;
        clickElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();

        if (utils.wildernessLevel <= 1) teleblocked = false;

        if (config.blastOff()) gottaBlast();

        if (config.heal()) potUp();

        if (config.safeSpotting()) move();

        if (config.appendDefault() && level <= 30 && utils.inWild())
        {
            teleport();
        }

        if (config.traverse() && utils.inbounds()) runner();

        equip();

        heal();

        npcActions();
    }
    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("dcslayer"))
        {
            executor.execute(this::reset);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if ((e.getActor() instanceof NPC)) {
            final NPC npc = (NPC) e.getActor();
            if (npc.getInteracting() != client.getLocalPlayer()) return;

            int anim = npc.getAnimation();
            if (config.flick() && utils.inbounds() && anim == 64) flick(0);
            else if (config.flick() && utils.inbounds() && anim == -1) { flick(1); }
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage message){

        String msg = Text.removeTags(message.getMessage());
        if (message.getType() == ChatMessageType.GAMEMESSAGE &&
                msg.startsWith("Congratulations"))
        {

            try {
                VirtualKeyboard.sendKeys("space");
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        if (message.getType() == ChatMessageType.GAMEMESSAGE &&
                msg.contains("You cannot switch worlds so soon after combat"))
        {
            hopFailed = true;
        }

        if (message.getType() == ChatMessageType.GAMEMESSAGE && msg.contains("Tele Block spell"))
        {
            teleblocked = true;
        }
        if (message.getType() == ChatMessageType.GAMEMESSAGE && msg.contains("You can't use this teleport after level"));
        {
            teleportFailed = true;
        }
        if (message.getType() == ChatMessageType.GAMEMESSAGE && msg.contains("Oh dear, you are dead"));
        {
            teleportFailed = false;
            teleblocked = false;
            hopFailed = false;
        }
        if (message.getType() == ChatMessageType.GAMEMESSAGE && msg.contains("You rub the"))
        {
            log.info(msg.toUpperCase());
            if (items.isGlory(lastRubbed)) {
                try {
                    VirtualKeyboard.sendKeys("1");
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }

        }
        if (message.getType() == ChatMessageType.SPAM && msg.contains("You rub the"))
        {
            if (items.isWealth(lastRubbed)) {
                try {
                    VirtualKeyboard.sendKeys("2");
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void equip() {
        if (activeGear == null) {
            return;
        }
        swap(activeGear);
    }

    private void swap(List<String> gear) {
        List<Integer> items = new ArrayList<>();

        for (String item : gear) {
            items.add(parseInt(item));
        }

        log.info(String.valueOf(items));

        Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();

        for (WidgetItem invItem : invItems)
        {
            for (int item : items)
            {
                if (item == invItem.getId())
                {
                    log.info(invItem.getId() + " match!");

                    if(client.getWidget(149, 0).isHidden() && !client.isKeyPressed(KeyCode.KC_SHIFT))
                    {
                        try {
                            VirtualKeyboard.sendKeys("escape");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                }
            }
        }
    }

    private void flick(int s){
        Widget qp = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB);
        if (s == 1 && client.getVar(Varbits.QUICK_PRAYER) == 0)
        {
            VirtualKeyboard.click(Utility.randomPoint(qp.getBounds(),90));
        }
        if (s == 0 && client.getVar(Varbits.QUICK_PRAYER) == 1)
        {
            VirtualKeyboard.click(Utility.randomPoint(qp.getBounds(),90));
        }

        if (s == -1){
            if (client.getVar(Varbits.QUICK_PRAYER) == 1)
            {
                VirtualKeyboard.click(Utility.randomPoint(qp.getBounds(),90));
            }
            else {
                VirtualKeyboard.doubleClick(Utility.randomPoint(qp.getBounds()));
            }
        }
    }

    public WorldPoint safespot;
    private void move(){
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        if (local.distanceTo(safespot) > 0 &&
                local.distanceTo(safespot) < 2)
        {
            LocalPoint nextLp = LocalPoint.fromWorld(client, safespot);
            Point nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client,nextLp).getBounds());
            VirtualKeyboard.click(nextPt);
        }
    }

    private void reset() {

        // gets the highlighted items from the text box in the config
        List<String> safe = Text.fromCSV(config.safeSpot());
        gear1 = Text.fromCSV(config.gearSet1());
        gear2 = Text.fromCSV(config.gearSet2());
        gear3 = Text.fromCSV(config.gearSet3());


        activePker = null;
        hopFailed = false;
        hopDisabled = false;
        teleblocked = false;
        teleportFailed = false;
        safespot = new WorldPoint(parseInt(safe.get(0)),
                parseInt(safe.get(1)),
                parseInt(safe.get(2)));
    }

    private boolean hopDisabled = false;
    private void gottaBlast() {

        Player local = client.getLocalPlayer();

        if(client.getVar(Varbits.IN_WILDERNESS) != 1) return;

        if (Instant.now().toEpochMilli() - lastHopped >= 4000)
        {
            hopFailed = false;
        }

        int count = 0;
        for (Player player : client.getPlayers())
        {
            if (player == null || player == local || config.ignore().contains(player.getName()))
            {
                continue;
            }
            if (withinRange(player))
            {
                count ++;

                WorldPoint localWp = local.getWorldLocation();
                WorldPoint pWp = player.getWorldLocation();
                int range = localWp.distanceTo(pWp);

                if (player.getSkullIcon() == SkullIcon.SKULL)
                {
                    client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DING);
                    activePker = player;
                }

                if (player.getInteracting() == client.getLocalPlayer())
                {
                    targeted = player;
                    girdYourLoins();
                }

                if (hopFailed || hopDisabled)
                {
                    log.info("Hop failed: " + hopFailed + "; disabled: " + hopDisabled);
                    hopFailed = false;
                    hopDisabled = false;

                    if (teleblocked)
                    {
                        client.playSoundEffect(SoundEffectID.PRAYER_DEPLETE_TWINKLE);
                        log.info("Teleblocked -> " + player.getName());
                        girdYourLoins();
                    }
                    else {
                        if (config.teleOptions() == BlastOff.IN_RANGE && range > 12
                            || config.teleOptions() == BlastOff.TARGETED && activePker != player
                            || config.teleOptions() == BlastOff.NEVER)
                        {
                            continue;
                        }
                        teleport();
                    }
                }
                else {
                    if ((config.hopOptions() == BlastOff.IN_RANGE && range > 11)
                            || (config.hopOptions() == BlastOff.TARGETED && activePker != player)
                            || (config.hopOptions() == BlastOff.HOP_ABOVE_20 && utils.wildernessLevel < 20)
                            || (config.hopOptions() == BlastOff.HOP_ABOVE_30 && utils.wildernessLevel < 30)
                            || (config.hopOptions() == BlastOff.NEVER))
                    {
                        log.info((config.hopOptions().getName() + " -> " + player.getName() + ": " + player.getCombatLevel()));
                        hopDisabled = true;
                        continue;
                    }

                    if (Instant.now().toEpochMilli() - lastHopped > config.hopThresh())
                    {

                        try {
                            client.playSoundEffect(SoundEffectID.TELEPORT_VWOOP);
                            // THIS IS HOW TOU HOP OUT. MUST MATCH WORLD HOPPER "PREVIOUS" KEY
                            VirtualKeyboard.sendKeys("BACK_SLASH");
                            hopDisabled = false;
                            lastHopped = Instant.now().toEpochMilli();
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        pkers = count > 0;

    }
    private boolean withinRange(Player p) {
        return p.getCombatLevel() >= utils.minCombatLevel && p.getCombatLevel() <= utils.maxCombatLevel;
    }

    private void runner() {
        int x = 0;
        int y = 0;

        WorldPoint wp = client.getLocalPlayer().getWorldLocation();

        switch(config.run()) {
            case EAST:
                x = Utility.rand(wp.getX() + 3, wp.getX() + 12);
                y = Utility.rand(wp.getY() - 2, wp.getY() + 2);
                break;
            case WEST:
                x = Utility.rand(wp.getX() - 12, wp.getX() - 3);
                y = Utility.rand(wp.getY() - 2, wp.getY() + 2);
                break;
            case NORTH:
                x = Utility.rand(wp.getX() - 2, wp.getX() + 2);
                y = Utility.rand(wp.getY() + 3, wp.getY() + 12);
                break;
            case SOUTH:
                x = Utility.rand(wp.getX() - 2, wp.getX() + 2);
                y = Utility.rand(wp.getY() - 12, wp.getY() - 3);
                break;
            case NORTHEAST:
                x = Utility.rand(wp.getX() + 3, wp.getX() + 12);
                y = Utility.rand(wp.getY() + 3, wp.getY() + 12);
                break;
            case NORTHWEST:
                x = Utility.rand(wp.getX() - 12, wp.getX() - 3);
                y = Utility.rand(wp.getY() + 3, wp.getY() + 12);
                break;
            case SOUTHEAST:
                x = Utility.rand(wp.getX() + 3, wp.getX() + 12);
                y = Utility.rand(wp.getY() - 12, wp.getY() - 3);
                break;
            case SOUTHWEST:
                x = Utility.rand(wp.getX() - 12, wp.getX() - 3);
                y = Utility.rand(wp.getY() - 12, wp.getY() - 3);
                break;
            default:
                break;
        }
        if (config.run() == Direction.STAY) {
            return;
        }

        WorldPoint nextWp = new WorldPoint(x, y, client.getPlane());
        LocalPoint nextLp = LocalPoint.fromWorld(client, nextWp);
        Point nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client, nextLp).getBounds());
        if (clickElapsed >= config.clickThresh())
        {
            VirtualKeyboard.click(nextPt);
        }

    }

    private void heal() {

        final int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        final int energy = client.getEnergy();

        if (myHp <= config.minHp())
        {

            Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
            for (WidgetItem invItem : invItems)
            {
                if (items.isFood(invItem.getId()))
                {
                    if(client.getWidget(149, 0).isHidden() && !client.isKeyPressed(KeyCode.KC_SHIFT))
                    {
                        try {
                            VirtualKeyboard.sendKeys("escape");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }

                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }

        if (energy <= 40) {
            Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
            for (WidgetItem invItem : invItems)
            {
                if (items.isEnergy(invItem.getId()))
                {
                    if(client.getWidget(149, 0).isHidden() && !client.isKeyPressed(KeyCode.KC_SHIFT))
                    {
                        try {
                            VirtualKeyboard.sendKeys("escape");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }

                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }

    }
    private void potUp() {

        int prayer = client.getBoostedSkillLevel(Skill.PRAYER) + 1; // config sets to zero and will keep opening without setting to atlast 1
        boolean inventoryHidden = client.getWidget(149, 0).isHidden();

        if (prayer <= config.minPrayer())
        {


            Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
            for (WidgetItem invItem : invItems)
            {
                if (items.isPrayer(invItem.getId()))
                {

                    if(inventoryHidden && !client.isKeyPressed(KeyCode.KC_SHIFT))
                    {
                        try {
                            log.info("Inventory opened [potUp()]");
                            VirtualKeyboard.sendKeys("escape");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }

                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }


    }
    private void teleport() {
        Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
        for (WidgetItem invItem : invItems)
        {
            if (utils.inWild())
            {
                if ((items.isGlory(invItem.getId()) || items.isWealth(invItem.getId())) &&
                        (utils.wildernessLevel > 20 && utils.wildernessLevel <= 30))
                {
                    log.info("Teleporting ABOVE lvl 20 -> "+ invItem.getId());
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    lastRubbed = invItem.getId();
                    break;
                }
                else if (items.isTeleport(invItem.getId()) && utils.wildernessLevel <= 20)
                {
                    log.info("Teleporting BELOW lvl 20 -> " + invItem.getId());
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    lastRubbed = invItem.getId();
                    break;
                }
            }
            else if (items.isTeleport(invItem.getId()))
            {
                log.info("Teleporting -> " + invItem.getId());
                teleblocked = false;
                VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                lastRubbed = invItem.getId();
                break;
            }
        }
    }
    private void girdYourLoins(){
        Rectangle b;

        if (client.getVar(Varbits.QUICK_PRAYER) == 0) {
            b = client.getWidget(WidgetInfo.MINIMAP_PRAYER_ORB).getBounds();
            VirtualKeyboard.click(Utility.randomPoint(b));
        }

    }

    private void npcActions() {
        interacting = client.getNpcs();
        interacting.removeIf(npc -> npc.getInteracting() != client.getLocalPlayer());
        interacting.removeIf(Actor::isDead);

        if (interacting.isEmpty())
        {
            return;
        }
        for (NPC npc : interacting) {
            if(config.npcs().contains(String.valueOf(npc.getId())))
            {
                interacting.remove(npc);
                takeAction(npc);
            }
        }

        interacting.clear();
    }

    private void takeAction(NPC npc) {
        final Player local = client.getLocalPlayer();

        Widget protMelee = client.getWidget(WidgetInfo.PRAYER_PROTECT_FROM_MELEE);

        switch (npc.getId())
        {

            case 6605:
                if (!interacting.contains(npc))
                {
                    if (client.getVar(Varbits.PRAYER_PROTECT_FROM_MELEE) == 1)
                    {
                        if (protMelee.isHidden()) {
                            try {
                                VirtualKeyboard.sendKeys("F5");
                            } catch (AWTException e) {
                                e.printStackTrace();
                            }
                        } else {
                            VirtualKeyboard.click(Utility.randomPoint(protMelee.getBounds(),30));
                        }

                    }
                }
                if (npc.getWorldLocation().distanceTo(local.getWorldLocation()) <= 2) {
                    if (client.getVar(Varbits.PRAYER_PROTECT_FROM_MELEE) == 0)
                    {
                        if (protMelee.isHidden()) {
                            try {
                                VirtualKeyboard.sendKeys("F5");
                            } catch (AWTException e) {
                                e.printStackTrace();
                            }
                        } else {
                            VirtualKeyboard.click(Utility.randomPoint(protMelee.getBounds(),50));
                        }

                    }
                }
                break;
        }

    }
}
