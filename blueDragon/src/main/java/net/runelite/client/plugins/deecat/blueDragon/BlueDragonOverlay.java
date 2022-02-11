package net.runelite.client.plugins.deecat.blueDragon;


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

class BlueDragonOverlay extends OverlayPanel
{

    private final Client client;
    private final BlueDragonPlugin plugin;
    private final BlueDragonConfig config;


    @Inject
    private BlueDragonOverlay(Client client, BlueDragonPlugin plugin, BlueDragonConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;


        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Wild PVM Overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        {
            boolean atLoot = plugin.atLoot();
            boolean atSafeSpot = plugin.atSafeSpot();
            boolean moving = plugin.moving();
            //int lootSize = plugin.newLoot;



            if (atLoot)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("~Looting~")
                        .color(Color.magenta)
                        .build());
            }
            else if (atSafeSpot)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("~Safespot~")
                        .color(Color.yellow)
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

            if (plugin.looting()){
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Looting")
                        .leftColor(Color.ORANGE)
                        .build());
            }
            else {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Item stack: ")
                        .leftColor(Color.blue)
                        .build());
            }
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(directionOf(plugin.direction))
                    .leftColor(Color.LIGHT_GRAY)
                    .right(String.valueOf(plugin.nextStage.getName()))
                    .rightColor(Color.lightGray)
                    .build());
        }
        return super.render(graphics);
    }

    private String directionOf(int d){
        if(d == -1){
            return "leaving";
        }
        else if(d == 1)
        {
            return "forwards";
        }
        return "nowhere";
    }

}