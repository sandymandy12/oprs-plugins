package net.runelite.client.plugins.deecat.gargoyles;


import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class GargoylesOverlay extends OverlayPanel
{

    private final Client client;
    private final GargoylesPlugin plugin;
    private final GargoylesConfig config;


    private final List<Integer> guthansHelm = new ArrayList<>();
    private final List<Integer> guthansWep = new ArrayList<>();
    private final List<Integer> slayerHelm = new ArrayList<>();
    private final List<Integer> wep = new ArrayList<>();


    @Inject
    private GargoylesOverlay(Client client, GargoylesPlugin plugin, GargoylesConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        getItemId();

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Gargoyles overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {


            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(String.valueOf(""))
                    .color(Color.RED)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("")
                    .leftColor(Color.ORANGE)
                    .right("")
                    .rightColor(Color.RED)
                    .build());
        }
        return super.render(graphics);
    }


    private void getItemId(){
        guthansHelm.add(ItemID.GUTHANS_HELM);
        guthansHelm.add(ItemID.GUTHANS_HELM_0);
        guthansHelm.add(ItemID.GUTHANS_HELM_25);
        guthansHelm.add(ItemID.GUTHANS_HELM_50);
        guthansHelm.add(ItemID.GUTHANS_HELM_75);
        guthansHelm.add(ItemID.GUTHANS_HELM_100);
        guthansHelm.add(ItemID.GUTHANS_HELM_23638);

        guthansWep.add(ItemID.GUTHANS_WARSPEAR);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_0);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_25);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_50);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_75);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_100);

        slayerHelm.add(ItemID.SLAYER_HELMET);
        slayerHelm.add(ItemID.SLAYER_HELMET_I);
        slayerHelm.add(ItemID.SLAYER_HELMET_I_25177);

        wep.add(ItemID.ABYSSAL_BLUDGEON);
        wep.add(ItemID.ABYSSAL_DAGGER_P_13271);
    }

}