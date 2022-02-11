package net.runelite.client.plugins.deecat.slayer;


import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static java.lang.Integer.parseInt;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class DCSlayerOverlay extends OverlayPanel
{

    private final Client client;
    private final DCSlayerPlugin plugin;
    private final DCSlayerConfig config;


    @Inject
    private DCSlayerOverlay(Client client, DCSlayerPlugin plugin, DCSlayerConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "DC Slayer Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {

            final Player local = client.getLocalPlayer();
            final NPC target = (NPC) local.getInteracting();
            final WorldPoint wp = local.getWorldLocation();


            if(plugin.pkers) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text((plugin.activePker == null ?
                                "Players lurking" : plugin.activePker.getName()+"!!"))
                        .color(Color.yellow)
                        .build());
            }
            else {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(wp.getX() + ", " + wp.getY() + ", " + wp.getPlane())
                        .color(Color.cyan)
                        .build());
            }
            if (client.getVar(Varbits.IN_WILDERNESS) == 1) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left(String.valueOf(target == null ? "No target" :
                                target.getName()))
                        .leftColor(Color.gray)
                        .right(String.valueOf(plugin.level))
                        .rightColor(Color.ORANGE)
                        .build());
            }
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(plugin.activePker==null?"no pkers":plugin.activePker.getName())
                    .leftColor(Color.LIGHT_GRAY)
                    .right(target == null ? "no target" : target.getAnimation() + "")
                    .rightColor(Color.lightGray)
                    .build());
        }
        return super.render(graphics);
    }
}