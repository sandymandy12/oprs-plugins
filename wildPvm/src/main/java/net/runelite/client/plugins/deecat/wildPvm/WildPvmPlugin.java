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
package net.runelite.client.plugins.deecat.wildPvm;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.WidgetMenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.plugins.deecat.wildPvm.ExtUtils;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Collection;

import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Wild PVM",
        description = "Wildness tasks",
        tags = {"highlight", "npcs", "overlay", "respawn", "tags"}
)
@Slf4j
public class WildPvmPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private WildPvmConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private WildPvmOverlay wildPvmOverlay;

    @Inject
    private MyItems items;

    @Inject
    private ExtUtils utils;


    /**
     * The players location on the last game tick.
     */
    public int direction;
    private long clickElapsed;
    private long lootingElapsed;
    public LavaIsleTrail lastStage;
    public LavaIsleTrail nextStage;
    private int lastClicked;
    private WorldPoint lastPlayerLocation;
    public WorldPoint lootWp;
    public int newLoot = 0;

    @Provides
    WildPvmConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(WildPvmConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(wildPvmOverlay);
        lastStage = LavaIsleTrail.VOID;
        lootWp = new WorldPoint(0,0,0);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(wildPvmOverlay);
        lastStage = null;
        nextStage = null;
        direction = 0;
        teleportFailed = false;
    }

    @Subscribe
    public void onGameTick(GameTick event) {

        utils.appendAttackLevelRangeText();
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();
        clickElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();


        direction = getDirection();
        lastStage = getLastStage();
        nextStage = getNextStage();

        if (!utils.inbounds() || (moving() && !inCombat()))
        {
            clickElapsed = 0;
        }

        if (config.leave())
        {
            direction = -1;
            nextStage = getNextStage();
            leave();
        }

        if (config.traverse())
        {
            if (direction == -1 || utils.underAttack())
            {
                leave();
            }

            else if (direction == 1 && (inCombat() || !looting()))
            {
                moveToSafeSpot();
            }

        }
        conserve();
        heal();
    }

    @Subscribe
    public void onItemDespawned(ItemDespawned itemDespawned)
    {
        if (itemDespawned.getTile().getWorldLocation().equals(lootWp)){
            newLoot = itemDespawned.getTile().getGroundItems() == null ? 0 :
                    itemDespawned.getTile().getGroundItems().size();

            if (newLoot == 0)
            {
                lootWp = LavaIsleTrail.VOID.getWorldPoint();
            }
        }
    }

    @Subscribe
    public void onNpcLootReceived(NpcLootReceived npcLootReceived)
    {
        lootWp = npcLootReceived.getNpc().getWorldLocation();
        newLoot = npcLootReceived.getItems().size();

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
                msg.contains("Tele Block spell")
                || msg.contains("You can't use this teleport after level"))
        {

            teleportFailed = true;
        }
    }

    public boolean inCombat() {
        Player local = client.getLocalPlayer();
        for (NPC npc : client.getNpcs())
        {
            if (npc.getInteracting() == local)
            {
                return true;
            }
        }
        for (Player player : client.getPlayers())
        {
            if (items.ignore(player.getName())) continue;
            if (player.getInteracting() == local)
            {
                return true;
            }
        }
        return local.getInteracting() != null;
    }

    public boolean moving() {
        if (client.getLocalDestinationLocation() == null)
        {
            return  false;
        }
        else if (clickElapsed >= 6245){ //random .., hard coded
            return false;
        }
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();
        return true;

    }

    public boolean looting(){
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        if(lootWp.distanceTo(local) <= 2)
        {
            lootingElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();
            if (newLoot > 0 && lootingElapsed <= config.timer())
            {
                return true;
            }
        }
        return false;
    }

    public String stage(String d)
    {

        if (d == "next") {
            if (nextStage != null)
            {
                return nextStage.getName();
            }
        } else if (d=="last"){
            if (lastStage != null){
                return lastStage.getName();
            }

        }
        return "Boonies";
    }

    private void moveToSafeSpot(){
        WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
        WorldPoint safe = LavaIsleTrail.SAFE.getWorldPoint();

        if (localWp.distanceTo(safe) == 0) return;
        if (lastStage == LavaIsleTrail.SAFEHOUSE && lastStage.getWorldPoint().distanceTo(lastPlayerLocation) < lastStage.getRange())
        {
            return;
        }

        if (!nextStage.getWorldPoint().isInScene(client))
        {
            return;
        }
        Point nextPt;

        if (lastStage == LavaIsleTrail.SAFE)
        {
            LocalPoint lp = LocalPoint.fromWorld(client, LavaIsleTrail.SAFE.getWorldPoint());
            nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client, lp).getBounds());
        }
        else {
            nextPt = randPoint(nextStage.getWorldPoint());
        }

        if (clickElapsed >= config.clickThresh())
        {
            VirtualKeyboard.click(nextPt);
        }

    }

    private Point randPoint(WorldPoint wp){
        final int x = wp.getX() + utils.getRandomIntBetweenRange(-1, 1);
        final int y = wp.getY() + utils.getRandomIntBetweenRange(-1, 1);

        WorldPoint nextWp = new WorldPoint(x,y,wp.getPlane());
        LocalPoint lp = LocalPoint.fromWorld(client, nextWp);
        return Utility.randomPoint(Perspective.getCanvasTilePoly(client, lp).getBounds());
    }

    private void conserve(){

        if (client.getItemContainer(InventoryID.INVENTORY).contains(ItemID.LAVA_SCALE)) {
            Widget inventory = client.getWidget(149, 0);
            for (WidgetItem invItem : inventory.getWidgetItems()) {


                if (invItem.getId() == ItemID.PESTLE_AND_MORTAR
                        && clickElapsed >= config.clickThresh())
                {

                    if(client.getWidget(149, 0).isHidden() && !client.isKeyPressed(KeyCode.KC_SHIFT))
                    {
                        try {
                            VirtualKeyboard.sendKeys("escape");
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }

                    lastClicked = invItem.getId();
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }

            }
        }
    }
    private void heal() {

        int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);

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
                    lastClicked = invItem.getId();
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }


    }

    private void leave(){

        if(config.stay()){
            return;
        }
        direction = -1;

        if (utils.wildernessLevel <= 30) {

        }

        WorldPoint nextWp;
        if (nextStage == LavaIsleTrail.LVL30_WILD || nextStage == LavaIsleTrail.LVL20_WILD)
        {
            WorldPoint wp = client.getLocalPlayer().getWorldLocation();

            int x = Utility.rand(wp.getX() - 2, wp.getX() + 2);
            int y = Utility.rand(wp.getY() - 10, wp.getY() - 2);

            nextWp = new WorldPoint(x, y, client.getPlane());
        }
        else {
            nextWp = nextStage.getWorldPoint();
        }
        if (!nextWp.isInScene(client)) {
            return;
        }

        LocalPoint nextLp = LocalPoint.fromWorld(client, nextWp);
        Point nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client, nextLp).getBounds());
        if (clickElapsed >= config.clickThresh())
        {
            VirtualKeyboard.click(nextPt);
        }

    }

    public boolean teleportFailed;

    private int getDirection(){

        if (!utils.inWild())
        {
            teleportFailed = false;
            return 1;

        }


        if (config.leave()) {
            Item[] equipment = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
            int equip = equipment[EquipmentInventorySlot.AMULET.getSlotIdx()].getId();

            if (equip == -1)
            {
                 return -1;
            }

        }

        if (utils.invFull(26) && config.traverse())
        {
            return -1;
        }
        else {
            return 1;
        }
    }

    private LavaIsleTrail getLastStage(){

        WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
        for (LavaIsleTrail stage : LavaIsleTrail.values())
        {
            WorldPoint wp = stage.getWorldPoint();
            if (localWp.distanceTo(wp) <= stage.getRange())
            {
                return stage;
            }
        }
        return lastStage;

    }
    private LavaIsleTrail getNextStage(){

        int step;
        if (direction == -1) {
            step = lastStage.getReturnStep();
            for (LavaIsleTrail stage : LavaIsleTrail.values()) {
                if (stage.getReturnStep() == step + 1) {
                    return stage;
                }
            }
        }
        else{
            step = lastStage.getForwardStep();
            for (LavaIsleTrail stage : LavaIsleTrail.values()) {
                if (stage.getForwardStep() == step + 1) {
                    return stage;
                }
            }
        }

        return nextStage;
    }

}
