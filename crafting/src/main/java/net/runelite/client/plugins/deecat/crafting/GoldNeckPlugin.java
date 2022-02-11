package net.runelite.client.plugins.deecat.crafting;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import com.google.inject.Provides;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;
import java.util.List;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.plugins.deecat.Utility;
import net.runelite.client.plugins.deecat.VirtualKeyboard;
import net.runelite.client.plugins.deecat.crafting.ColorTileObject;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
        name = "Gold Neck",
        description = "",
        tags = {""},
        enabledByDefault = false
)
@Slf4j
public class GoldNeckPlugin extends Plugin {
    private static final String CONFIG_GROUP = "goldneck";


    @Getter(AccessLevel.PACKAGE)
    private final List<ColorTileObject> objects = new ArrayList<>();
    @Getter
    private final Set<GameObject> object = new HashSet<>();

    private long lastAnimating;


    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GoldNeckOverlay overlay;

    @Inject
    private GoldNeckConfig config;

    @Inject
    private MyItems items;

    public int myEnergy;
    public String profit = "0";
    public long lastClicked;
    private long clickThresh;
    private WorldPoint lastPlayerLocation;
    public Point mouse;

    @Provides
    GoldNeckConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(GoldNeckConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }


    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
        objects.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();

        if (gameStateChanged.getGameState() != GameState.LOGGED_IN) {
            objects.clear();
        }

    }

    @Subscribe
    public void onGameTick(GameTick tick){
        mouse = client.getMouseCanvasPosition();
        myEnergy = client.getEnergy();
        profit = getProfit();
        clickThresh = config.clickThresh();
        lastClicked = Instant.now().toEpochMilli() - client.getMouseLastPressedMillis();
        lastPlayerLocation = client.getLocalPlayer().getWorldLocation();

        if (!inbounds())
        {
            return;
        }

        if (moving())
        {
            lastClicked = 0;
        }

        if (config.runTheMap()) {
            getBusyFool();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage message){

        String msg = Text.removeTags(message.getMessage());
        if (message.getType() == ChatMessageType.GAMEMESSAGE &&
            msg.startsWith("Congratulations"))
            {
                LocalPoint furnLp = LocalPoint.fromWorld(client,furnaceWp());
                Point furnPt = Utility.randomPoint(Perspective.getCanvasTilePoly(client,furnLp).getBounds());
                VirtualKeyboard.doubleClick(furnPt);
            }

    }
    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        Player localPlayer = client.getLocalPlayer();
        if (localPlayer != event.getActor())
        {
            return;
        }

        lastAnimating = Instant.now().toEpochMilli();

    }

    boolean crafting(){
        return Instant.now().toEpochMilli() - lastAnimating <= 4300;
    }
    public boolean moving() {
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        if (client.getLocalDestinationLocation() == null
                || local == lastPlayerLocation){

            return  false;
        }
        else if (lastClicked >= 6245){ //random .., hard coded
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

    boolean bankOpen() {
        return client.getWidget(WidgetInfo.BANK_TITLE_BAR) != null;
    }
    boolean furnaceOpen() {
        return client.getWidget(446, 46) != null;
    }
    boolean atFurnace() {
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        return local.distanceTo(furnaceWp()) < 1;
    }
    boolean atBank() {
        WorldPoint local = client.getLocalPlayer().getWorldLocation();
        return local.distanceTo(bankWp()) < 2;
    }
    boolean hasEnergy(){
        if ( count(ItemID.ENERGY_POTION4) > 0
                || count(ItemID.ENERGY_POTION3) > 0
                || count(ItemID.ENERGY_POTION2) > 0
                || count(ItemID.ENERGY_POTION1) > 0)
        {
            return true;
        }
        return false;
    }

    WorldPoint furnaceWp() { return new WorldPoint(3109, 3499, client.getPlane()); }
    WorldPoint bankWp(){ return new WorldPoint(3098,3494,client.getPlane()); }

    int count(int id) { return client.getItemContainer(InventoryID.INVENTORY).count(id);}

    String getProfit() {
        if (client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER) == null){
            return profit;
        }

        Widget [] bankItems = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER).getChildren();
        for (Widget bankItem : bankItems){
            if (bankItem.getItemId() == ItemID.GOLD_BRACELET_11069){
                double a = (bankItem.getItemQuantity() * (200.0-90.0)) / 1000000.0;
                DecimalFormat df = new DecimalFormat("###.##");
                return df.format(a);
            }
        }

        return profit;
    }

    private Widget getBankItem(int itemId){
        Widget[] bankItems =  client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER).getChildren();
        for (Widget bankItem : bankItems){
            if (bankItem.getItemId() == itemId){
                return bankItem;
            }
        }
        return null;
    }

    private void smelt(){
        Point braceletCoords = Utility.randomPoint(client.getWidget(446, 46).getBounds());
        if (lastClicked > clickThresh)
        {
            VirtualKeyboard.click(braceletCoords);
            System.out.println("making bracelet " + braceletCoords);
        }
    }
    private void deposit(){


        if (count(ItemID.GOLD_BRACELET_11069) == 27)
        {
            if (lastClicked < clickThresh) return;
            Point braceletCoords = Utility.randomPoint(client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER).getChild(5).getBounds());
            System.out.println("at bank depositing");
            VirtualKeyboard.click(braceletCoords);
        }
        else if (count(ItemID.GOLD_BRACELET_11069) == 0 && count(ItemID.GOLD_BAR) == 0)
        {

            if (myEnergy < 30)
            {
                System.out.println("low energy");
                energyPot("withdraw");
            }
            else {

                energyPot("deposit");
                if(!hasEnergy()){

                    Point barCoords = Utility.randomPoint(getBankItem(ItemID.GOLD_BAR).getBounds());
                    System.out.println("withdrawing bars");
                    VirtualKeyboard.click(barCoords);
                }
            }

        }
        // find the furnace and click
        else if (count(ItemID.GOLD_BAR) == 27)
        {
            VirtualKeyboard.click(furnPt());
            System.out.println("Moving to furnace");
        }
    }
    private void bank(){
        if (lastClicked < clickThresh){
            return;
        }
        System.out.println("Moving to bank " + bankPt());
        VirtualKeyboard.click(bankPt());

    }
    private void consume(){

        if (myEnergy < 80)
        {
            if (lastClicked < clickThresh) return;

            boolean inventoryHidden = client.getWidget(149, 0).isHidden();
            if(inventoryHidden && !client.isKeyPressed(KeyCode.KC_SHIFT))
            {
                try {
                    VirtualKeyboard.sendKeys("escape");
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
            energyPot("consume");
            return;
        }

    }

    private void energyPot(String action){

        if (action == "withdraw"){
            System.out.println("Getting energy..");

            Point item = Utility.randomPoint(getBankItem(ItemID.ENERGY_POTION4).getBounds());

            VirtualKeyboard.click(item);
            System.out.println(item);
            /*
            if (bankOpen()){
                System.out.println("closing bank");
                Point qMark = client.getWidget(WidgetInfo.BANK_TUTORIAL_BUTTON).getChild(0).getCanvasLocation();
                int x = qMark.getX() + 22;

                Rectangle close = new Rectangle(x,qMark.getY(),20,20 );
                System.out.println(close);
                VirtualKeyboard.click(Utility.randomPoint(close));
            }*/


        }
        else if (action == "deposit"){
            System.out.println("Depositing");
            Widget [] invItems = client.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER).getChildren();
            for (Widget invItem : invItems){
                if (invItem.getItemId() == ItemID.VIAL || items.isEnergy(invItem.getItemId())){
                    System.out.println(invItem.getItemId());
                    Point item = Utility.randomPoint(invItem.getBounds());
                    VirtualKeyboard.click(item);
                }
            }

        }
        else if (action == "consume"){
            Collection<WidgetItem> invItems = client.getWidget(149, 0).getWidgetItems();
            for (WidgetItem invItem : invItems)
            {
                if ( items.isEnergy(invItem.getId()) )
                {
                    VirtualKeyboard.click(Utility.randomPoint(invItem.getCanvasBounds()));
                    break;
                }
            }
        }
    }
    private void closeBank(){
        System.out.println("closing bank");
        Point qMark = client.getWidget(WidgetInfo.BANK_TUTORIAL_BUTTON).getChild(0).getCanvasLocation();
        int x = qMark.getX() + 22;

        Rectangle close = new Rectangle(x,qMark.getY(),20,20 );
        VirtualKeyboard.click(Utility.randomPoint(close));
    }


    private Point furnPt() {
        LocalPoint furnLp = LocalPoint.fromWorld(client,furnaceWp());
        return Utility.randomPoint(Perspective.getCanvasTilePoly(client,furnLp).getBounds());
    }
    private Point bankPt() {
        LocalPoint bankLp = LocalPoint.fromWorld(client,bankWp());
        return Utility.randomPoint(Perspective.getCanvasTilePoly(client,bankLp).getBounds());
    }

    void getBusyFool() {


        if (atBank() && bankOpen())
        {
            if (hasEnergy() && myEnergy < 80){
                closeBank();
            }
            else {
                deposit();
            }
        }
        else if (atBank() && !bankOpen())
        {
            if (hasEnergy() ){
                consume();
            }
            else {

            }
        }
        else if (atFurnace() && furnaceOpen() && count(ItemID.GOLD_BAR) != 0)
        {
            smelt();
        }
        else if (atFurnace() && !furnaceOpen() && count(ItemID.GOLD_BAR) == 0)
        {
            bank();
        }
    }
}
