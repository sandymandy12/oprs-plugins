package net.runelite.client.plugins.deecat.charge;


import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class ChargeOverlay extends OverlayPanel
{

    private final Client client;
    private final ChargePlugin plugin;
    private final ChargeConfig config;


    @Inject
    private ChargeOverlay(Client client, ChargePlugin plugin, ChargeConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;


        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Charge Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {
            boolean atFountain = plugin.atFountain();
            boolean moving = plugin.moving();
            boolean pkers = plugin.pker();


            if (atFountain)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("~Fountain of Rune~")
                        .color(Color.cyan)
                        .build());
            }
            else if (moving)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Moving")
                        .color(Color.GREEN)
                        .build());
            }
            else {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(plugin.stage("last"))
                        .color(Color.yellow)
                        .build());
            }

            if (pkers){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Players lurking")
                        .leftColor(Color.ORANGE)
                        .build());
            }
            else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Coast is clear ")
                        .leftColor(Color.blue)
                        .build());
            }

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.directionOf(plugin.direction))
                    .leftColor(Color.LIGHT_GRAY)
                    .right(plugin.stage("next"))
                    .rightColor(Color.lightGray)
                    .build());
        }
        return super.render(graphics);
    }




}