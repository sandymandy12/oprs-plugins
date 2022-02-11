package net.runelite.client.plugins.deecat.switches;


import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.client.plugins.deecat.MyItems;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

class SwitchesOverlay extends OverlayPanel
{

    private final Client client;
    private final SwitchesPlugin plugin;
    private final SwitchesConfig config;
    private final MyItems myItems;

    @Inject
    private SwitchesOverlay(Client client, SwitchesPlugin plugin, SwitchesConfig config, MyItems myItems)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.myItems = myItems;

        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Gargoyles overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {

            int specPercent = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);
            boolean specOn = client.getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) ==1;


            if (specOn)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(("Spec Enabled"))
                        .color(Color.CYAN)
                        .build());
            }
            else {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text(("no Spec"))
                        .color(Color.CYAN)
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Spec % ")
                        .leftColor(Color.RED)
                        .right(String.valueOf(specPercent))
                        .rightColor(Color.RED)
                        .build());
            }


        }
        return super.render(graphics);
    }

}