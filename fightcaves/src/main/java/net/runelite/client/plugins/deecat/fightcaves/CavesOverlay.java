package net.runelite.client.plugins.deecat.fightcaves;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class CavesOverlay extends OverlayPanel
{

    private final Client client;
    private final CavesPlugin plugin;
    private final CavesConfig config;


    @Inject
    private CavesOverlay(Client client, CavesPlugin plugin, CavesConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;


        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Caves Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {
            Player local = client.getLocalPlayer();
            Widget prayer = client.getWidget(WidgetID.PRAYER_GROUP_ID, 0);


            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("!!!")
                    .color(Color.cyan)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Animation")
                    .leftColor(Color.LIGHT_GRAY)
                    .right(String.valueOf(local.getInteracting() == null ? "null" : local.getInteracting().getAnimation()))
                    .rightColor(Color.yellow)
                    .build());


        }
        return super.render(graphics);
    }



}