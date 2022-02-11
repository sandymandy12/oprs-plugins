package net.runelite.client.plugins.deecat.fightcaves;

import net.runelite.api.Prayer;

import java.awt.*;

public enum JadAttackStyle {

    MAGE(2656, Prayer.PROTECT_FROM_MAGIC, Color.BLUE),
    MELEE(2655, Prayer.PROTECT_FROM_MELEE, Color.RED),
    RANGE(2652, Prayer.PROTECT_FROM_MISSILES, Color.GREEN);

    private int animationId;
    private Prayer prayer;
    private Color textColor;

    private JadAttackStyle(int animationId, Prayer prayer, Color textColor) {
        this.animationId = animationId;
        this.prayer = prayer;
        this.textColor = textColor;
    }

    public int getAnimationId() {
        return animationId;
    }

    public Prayer getPrayer() {
        return prayer;
    }

    public Color getTextColor() {
        return textColor;
    }

}