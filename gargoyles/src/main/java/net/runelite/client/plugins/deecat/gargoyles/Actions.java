package net.runelite.client.plugins.deecat;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Actions {

    @Inject
    private Client client;

    @Inject
    private Actions(Client client)
    {
        this.client = client;

    }

    public boolean inbounds() {

        Point m = client.getMouseCanvasPosition();
        if (m.getX() == -1 || m.getY() == -1)
        {
            return false;
        }
        return true;
    }
    public boolean withinRange(Player p) {
        appendAttackLevelRangeText();
        return p.getCombatLevel() >= minCombatLevel && p.getCombatLevel() <= maxCombatLevel;
    }
    public boolean bankOpen = client.getWidget(WidgetInfo.BANK_TITLE_BAR) != null;
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
    public int minCombatLevel = 0;
    public int maxCombatLevel = 0;

    public void combatAttackRange(final int combatLevel, final int wildernessLevel)
    {
        minCombatLevel = Math.max(3, combatLevel - wildernessLevel);
        maxCombatLevel = Math.min(Experience.MAX_COMBAT_LEVEL, combatLevel + wildernessLevel);
    }

    private final Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");
    public void appendAttackLevelRangeText() {
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
            minCombatLevel = 0;
            maxCombatLevel = 0;
            return;
        }

        final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
        if (!m.matches()) {
            return;
        }
        final int wildernessLevel = Integer.parseInt(m.group(1));
        final int combatLevel = Objects.requireNonNull(client.getLocalPlayer()).getCombatLevel();
        combatAttackRange(combatLevel, wildernessLevel);
        minCombatLevel = Integer.parseInt(m.group(1));
        maxCombatLevel = Integer.parseInt(m.group(2));
    }




}
