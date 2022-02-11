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
package net.runelite.client.plugins.deecat.charge;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.*;
import net.runelite.client.plugins.deecat.charge.MyItems;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Extension
@PluginDescriptor(
        name = "Charge",
        description = "Charge jewelry and fountain of rune",
        tags = {"charge", "dc", "jewelry", "fountain", "deecat"}
)
@Slf4j
public class ChargePlugin extends Plugin
{


    @Inject
    private Client client;

    @Inject
    private ChargeConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ChargeOverlay chargeOverlay;

    @Inject
    private MyItems items;


    /**
     * The players location on the last game tick.
     */
    private WorldPoint lastPlayerLocation;
    private long clickThresh;
    public long lastClick;
    public long clickElapsed;
    public int direction ;
    public FountainTrail lastStage;
    public FountainTrail nextStage;
    private long invClick;


    @Provides
    ChargeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ChargeConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(chargeOverlay);
        lastStage = FountainTrail.VOID;
        clickThresh = config.clickThresh();
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();


    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(chargeOverlay);

    }

    @Subscribe
    public void onGameTick(GameTick event) throws AWTException {
        clickElapsed = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();

        getDirection();

        lastStage = getLastStage();
        nextStage = getNextStage();


        if (!inbounds())
        {
            return;
        }

        if (moving()) {
            clickElapsed = 0;
        }
        //System.out.print("charge: ...>>>[["+(lastStage) + "]}_[[" +nextStage+ "]]--_ [[" +directionOf(direction)+ "]]--\n");
        if(config.traverse())
        {
            activate();

            if (direction == -1 && inWild)
            {
                leave();
            }
            else if (atFountain() && !invFull())
            {
                charge();
            }
            else if (direction == 1 && !atFountain())
            {
                moveToFountain();
            }

        }


        energize();

    }

    public boolean enRoute() {
        if (nextStage == null || lastStage == null) {
            return false;
        }
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
    public boolean inWild() {
        return client.getVar(Varbits.IN_WILDERNESS) == 1;
    }
    public boolean moving() {
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        if (client.getLocalDestinationLocation() == null
                || local == lastPlayerLocation){

            return  false;
        }
        else if (clickElapsed >= 6245){
            return false;
        }
        lastPlayerLocation = local;
        return true;

    }
    public boolean pker() {
        List<Player> players = client.getPlayers();
        int total = 0;
        for (Player player : players)
        {
          if (player.getName().contains(client.getLocalPlayer().getName())
               || player == null || player.isFriendsChatMember())
          {
              continue;
          }
          else { total ++;}
       }
       return total > 0;
    }
    private boolean invFull(int id, int max){
        Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
        int count = 0;
        for (WidgetItem item : invItems){
            if(item.getId() == id) {
                count ++;
            }
        }
        if (count >= max) {
            return true;
        }
        return false;
    }
    public boolean invFull(){
        Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
        for (WidgetItem item : invItems){
            if(item.getId() == ItemID.RING_OF_WEALTH_5) {
                return true;
            }
        }
        return false;
    }
    public boolean inside = lastPlayerLocation.getX() <= FountainTrail.SAFE.getWorldPoint().getX();
    public boolean bankOpen = client.getWidget(WidgetInfo.BANK_TITLE_BAR) != null;
    public boolean inWild = client.getVar(Varbits.IN_WILDERNESS) == 1;
    public boolean atFountain(){
        WorldPoint fountain = FountainTrail.FOUNTAIN.getWorldPoint();
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        return (local.distanceTo(fountain) <= 5);
    }
    public boolean gateClosed(){
        return true;
    }
    public void activate(){
        String lvl = wildernessLevel();
        Obelisk ob = Obelisk.valueOfLevel(lvl);
        if(ob == null){
            return;
        }

        if ((ob.getLevel() != "50" && direction  == 1) || (invFull())){

            WorldPoint wp = client.getLocalPlayer().getWorldLocation();

            if (wp.distanceTo(ob.getCenter()) > 5 ){
                return;
            }
            LocalPoint oblp = LocalPoint.fromWorld(client,ob.getCenter());
            LocalPoint lp = client.getLocalPlayer().getLocalLocation();

            Point nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client,oblp).getBounds());

            if (clickElapsed >= clickThresh && lp.distanceTo(oblp) >= 181){
                VirtualKeyboard.click(nextPt);
                lastClick = Instant.now().toEpochMilli();
            }
        }
    }

    public String wildernessLevel() {
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
            return null;
        }
        //String text = wildernessLevelText.replace("Level: ","");

        final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+).*?(\\d+)-(\\d+).*");
        final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
        if (!m.matches()) {
            return "";
        }
        final int wildernessLevel = Integer.parseInt(m.group(1));

        //System.out.print("charge:.. wilderlvl __~~~{{ "+ wildernessLevel +" }}~~~\n");



        return String.valueOf(wildernessLevel);
    }


    public void moveToFountain(){

        if (lastStage  == FountainTrail.FOUNTAIN) {
            direction = -1;
            return;
        }

        Point nextPt;
        if (lastStage == FountainTrail.BANK)
        {
            if (bankOpen){
                return;
            }
            direction = 1;
        }
        if (lastStage == FountainTrail.SAFE)
        {
            if (inside) {
                return;
            }

            direction = 1;

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
        lastClick = Instant.now().toEpochMilli();
    }

    private void clickRing(){
        Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
        for (WidgetItem invItem : invItems)
        {
            if (invItem.getId() == ItemID.RING_OF_WEALTH)
            {
                VirtualKeyboard.doubleClick(Utility.randomPoint(invItem.getCanvasBounds()));

                break;
            }
        }
        invClick = Instant.now().toEpochMilli();
    }

    private void charge(){
        direction = -1;
        if (clickElapsed < clickThresh){
            return;
        }
        long elapsed = Instant.now().toEpochMilli() - invClick;

        if (elapsed > 2000) {
            clickRing();
        }



    }
    private void leave(){

        direction = -1;

        WorldPoint nextWp = nextStage.getWorldPoint();
        if (!nextWp.isInScene(client))
        {
            return;
        }/*
        if (lastStage == FountainTrail.GATE_IN && gateClosed()){
            return  ;
        }*/
        LocalPoint nextLp = LocalPoint.fromWorld(client,nextWp);
        Point nextPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client,nextLp).getBounds());
        if (!moving()){
            VirtualKeyboard.click(nextPt);
            lastClick = Instant.now().toEpochMilli();
        }



    }

    private void energize() throws AWTException {

        int myEnergy = client.getEnergy();
        boolean inventoryHidden = client.getWidget(149, 0).isHidden();

        if (myEnergy <= config.minEnergy())
        {
            if(inventoryHidden)
            {
                VirtualKeyboard.sendKeys("escape");
            }

            Collection<WidgetItem> invItems = client.getWidget(WidgetInfo.INVENTORY).getWidgetItems();
            for (WidgetItem invItem : invItems)
            {
                if (items.isEnergy(invItem.getId()))
                {
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }


    }

    private void getDirection(){
        if (!inWild())
        {
            direction = 1;

        }

        if (invFull())
        {
            direction = -1;
        }
        else {
            direction = 1;
        }
    }
    public String directionOf(int d){
        if(d == -1){
            return "Leaving";
        }
        else if(d == 1)
        {
            return "Forwards";
        }
        return "Nowhere";
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
    private FountainTrail getLastStage(){

        if (atFountain()){
            return FountainTrail.FOUNTAIN;
        }
        WorldPoint localWp = client.getLocalPlayer().getWorldLocation();
        for (FountainTrail stage : FountainTrail.values())
        {
            WorldPoint wp = stage.getWorldPoint();
            if (localWp.distanceTo(wp) <= 2)
            {
                return stage;
            }
        }

        for (Obelisk ob : Obelisk.values()){
            if (localWp.distanceTo(ob.getCenter()) <= 2){
                return FountainTrail.OBELISK;
            }

        }
        if (atFountain()){
            return FountainTrail.FOUNTAIN;
        }
        return lastStage;

    }
    private FountainTrail getNextStage(){

        int step;
        if (direction == -1) {
            step = lastStage.getReturnStep();
            for (FountainTrail stage : FountainTrail.values()) {
                if (stage.getReturnStep() == step + 1) {
                    return stage;
                }
            }
        }
        else if (direction == 1){
            step = lastStage.getForwardStep();
            for (FountainTrail stage : FountainTrail.values()) {
                if (stage.getForwardStep() == step + 1) {
                    return stage;
                }
            }
        }

        return nextStage;
    }

}
