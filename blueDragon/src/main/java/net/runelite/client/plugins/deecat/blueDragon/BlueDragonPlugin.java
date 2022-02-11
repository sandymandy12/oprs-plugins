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
package net.runelite.client.plugins.deecat.blueDragon;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.blueDragon.Utility;
import net.runelite.client.plugins.deecat.blueDragon.VirtualKeyboard;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.*;

@Extension
@PluginDescriptor(
        name = "blueDragons",
        description = "blue dragons tasks",
        tags = {"highlight", "npcs", "overlay", "respawn", "tags"}
)
@Slf4j
public class
BlueDragonPlugin extends Plugin
{


    @Inject
    private Client client;

    @Inject
    private BlueDragonConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BlueDragonOverlay blueDragonOverlay;

    @Inject
    private MyItems items;


    /**
     * The players location on the last game tick.
     */
    public int direction;
    private long lootTimer;
    private long clickThresh;
    public long clickElapsed;
    public long lootingElapsed;
    public BlueDragonTrail lastStage;
    public BlueDragonTrail nextStage;
    public WidgetItem lastClicked;
    private WorldPoint lastPlayerLocation;
    private WorldPoint lootWp;
    public Collection<ItemStack> newLoot;

    @Provides
    BlueDragonConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(BlueDragonConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(blueDragonOverlay);
        lastStage = BlueDragonTrail.SAFE;
        lootWp = new WorldPoint(3209,3836,0); //random from something else. just to init

    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(blueDragonOverlay);
    }

    @Subscribe
    public void onGameTick(GameTick event) throws AWTException {
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();
        lootTimer = config.timer();
        clickThresh = config.clickThresh();
        clickElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();

        getDirection();

        lastStage = getLastStage();
        nextStage = getNextStage();

        if (!inbounds())
        {
            return;
        }

        if (moving())
        {
            clickElapsed = 0;
        }

        if (config.activate())
        {
            if (banking())
                deposit();

            if (!looting())
                moveToSafeSpot();
        }

        heal();
        conserve();
    }


    @Subscribe
    public void onNpcLootReceived(NpcLootReceived npcLootReceived)
    {
        Collection<ItemStack> items = npcLootReceived.getItems();
        lootReceived(items);
    }



    private void lootReceived(Collection<ItemStack> items)
    {
        newLoot = items;
        for (ItemStack itemStack : items)
        {
            lootWp = WorldPoint.fromLocal(client, itemStack.getLocation());
        }
    }

    private void deposit() {

    }
    public boolean enRoute() {
        if (nextStage == null || lastStage == null) {
            return false;
        }
        return true;
    }
    public String stage(String d) {
        if (enRoute()) {
            String s ="";
            if (d == "next") {
                s = nextStage.getName();
            } else if (d=="last"){
                s = lastStage.getName();
            }
            return s;
        }
        return "Boonies";
    }

    public boolean atBank() { return lastPlayerLocation.distanceTo(BlueDragonTrail.BANK.getWorldPoint()) <= 1;}
    public boolean bankOpen() { return client.getWidget(WidgetInfo.BANK_TITLE_BAR) != null;}
    public boolean moving() {
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        if (client.getLocalDestinationLocation() == null
                || local == lastPlayerLocation){

            return  false;
        }
        else if (clickElapsed >= 6245){ //random .., hard coded
            return false;
        }
        lastPlayerLocation = local;
        return true;

    }
    public boolean inbounds() {

        Point m = client.getMouseCanvasPosition();
        if (m.getX() == -1 || m.getY() == -1)
        {
            return false;
        }
        return true;
    }
    public boolean atSafeSpot(){
        WorldPoint safe = BlueDragonTrail.SAFE.getWorldPoint();
        return (lastPlayerLocation.distanceTo(safe) == 0);
    }
    public boolean atLoot() {return(lastPlayerLocation.distanceTo(lootWp) == 0);}

    public boolean banking(){
        if ( bankOpen() || (atBank() && invFull()) ){
            return true;
        }
        return false;
    }
    public boolean inCombat(){
        return client.getLocalPlayer().getInteracting() != null;
    }

    public void moveToSafeSpot(){

        Point nextPt;
        if (banking()) {
            return;
        }


        WorldPoint nextWp = nextStage.getWorldPoint();
        if (!nextWp.isInScene(client))
        {
            return;
        }


        LocalPoint nextLp = LocalPoint.fromWorld(client,nextWp);
        nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client,nextLp).getBounds());

        if (!moving() && clickElapsed >= clickThresh){
            VirtualKeyboard.click(nextPt);
        }
    }

    public boolean looting(){

        if(atLoot())
        {
            lootingElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();
            if (lootingElapsed <= lootTimer)
            {
                return true;
            }
        }
        return false;
    }
    public boolean invFull(){

        Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
        return invItems.size() == 28;
    }

    private void conserve(){

        int myRange = client.getBoostedSkillLevel(Skill.RANGED);
        Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
        if(!invItems.contains(ItemID.LAVA_SCALE) )
        {
            return;
        }
        if(invItems.size() < 24)
        {
            return;
        }
        for (WidgetItem invItem : invItems)
        {
            if(invItem == lastClicked)
            {
                continue;
            }

            if ((invItem.getId() == ItemID.PESTLE_AND_MORTAR
                || invItem.getId() == ItemID.BONES)
                && clickElapsed >= clickThresh)
            {
                VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
            }
        }

    }
    private void heal() throws AWTException {

        int myHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        boolean inventoryHidden = client.getWidget(149, 0).isHidden();

        if (myHp <= config.minHp())
        {
            if(inventoryHidden && !client.isKeyPressed(KeyCode.KC_SHIFT))
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


    private void getDirection(){

        if (invFull() && !atBank())
        {
            direction = -1;
        }
        else {
            direction = 1;
        }
    }
    private BlueDragonTrail getLastStage(){

        WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
        for (BlueDragonTrail stage : BlueDragonTrail.values())
        {
            WorldPoint wp = stage.getWorldPoint();
            if (localWp.distanceTo(wp) <= 1)
            {
                return stage;
            }
        }
        return lastStage;

    }
    private BlueDragonTrail getNextStage(){

        int step;
        if(direction == -1){
            step = lastStage.getReturnStep();
            for (BlueDragonTrail stage : BlueDragonTrail.values()) {
                if (stage.getReturnStep() == step + 1) {
                    return stage;
                }
            }
        }
        else {
            step = lastStage.getForwardStep();
            for (BlueDragonTrail stage : BlueDragonTrail.values()) {
                if (stage.getForwardStep() == step + 1) {
                    return stage;
                }
            }
        }
        return nextStage;
    }
}
