package net.runelite.client.plugins.deecat.crafting;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.api.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

class GoldNeckOverlay extends OverlayPanel
{

    private final Client client;
    private final GoldNeckPlugin plugin;
    private final GoldNeckConfig config;
    TooltipManager tooltipManager;

    @Inject
    private GoldNeckOverlay(Client client, GoldNeckPlugin plugin, GoldNeckConfig config, TooltipManager tooltipManager)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.tooltipManager = tooltipManager;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "GoldNeck overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        int bars = plugin.count(ItemID.GOLD_BAR);
        String profit = plugin.profit;

        boolean atBank = plugin.atBank();
        boolean atFurnace = plugin.atFurnace();

        boolean smelting = plugin.crafting();
        boolean bankOpen = plugin.bankOpen();
        boolean furnaceOpen = plugin.furnaceOpen();

        if (bankOpen)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Bank Open")
                    .color(Color.GREEN)
                    .build());
        }
        else if (furnaceOpen)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Furnace open")
                    .color(Color.GREEN)
                    .build());
        }
        else if (atBank)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("at bank")
                    .color(Color.GREEN)
                    .build());
        }
        else if (atFurnace)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("at furnace")
                    .color(Color.GREEN)
                    .build());
        }
        else if (smelting)
        {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("~Smelting~")
                    .color(Color.cyan)
                    .build());
        }
        else {
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("away")
                .color(Color.RED)
                .build());
        }
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Bars")
                .right(bars + " / [ " + profit+ "m ]")
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Mouse")
                .right(String.valueOf(plugin.mouse))
                .build());

        return super.render(graphics);
    }

}