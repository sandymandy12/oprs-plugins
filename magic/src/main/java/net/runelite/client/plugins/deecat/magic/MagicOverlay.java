package net.runelite.client.plugins.deecat.magic;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class MagicOverlay extends OverlayPanel
{

    private final Client client;
    private final MagicPlugin plugin;
    private final MagicConfig config;


    @Inject
    private MagicOverlay(Client client, MagicPlugin plugin, MagicConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;


        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Magic Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {

            final Player local = client.getLocalPlayer();
            float total = (float) (plugin.alchs + plugin.enchants);
            float alchs = (float) (plugin.alchs);
            double percent = (alchs / total) * 100;

            panelComponent.getChildren().add(TitleComponent.builder()
                    .text(plugin.lastClicked)
                    .color(Color.cyan)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Animation")
                    .leftColor(Color.LIGHT_GRAY)
                    .right(String.valueOf(local.getAnimation()))
                    .rightColor(Color.yellow)
                    .build());
            if ((int)percent < 60) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left((int)percent + "%")
                        .leftColor(Color.orange)
                        .right(String.valueOf(alchs))
                        .rightColor(Color.lightGray)
                        .build());
            }
            else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left((int)percent + "%")
                        .leftColor(Color.green)
                        .right(String.valueOf(alchs))
                        .rightColor(Color.RED)
                        .build());
            }


        }
        return super.render(graphics);
    }



}