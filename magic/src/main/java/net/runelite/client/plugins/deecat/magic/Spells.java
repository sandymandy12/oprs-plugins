/*
 * Copyright (c) 2018, Kruithne <kruithne@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.deecat.magic;

import java.awt.image.BufferedImage;
import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.util.ImageUtil;
@AllArgsConstructor
@Getter
public enum Spells
{

    HIGH_LEVEL_ALCHEMY(WidgetInfo.SPELL_HIGH_LEVEL_ALCHEMY, "High alchemy",0),
    KOUREND_TELEPORT(WidgetInfo.SPELL_KOUREND_HOME_TELEPORT, "Kourend teleport",-1),
    CAMELOT_TELEPORT(WidgetInfo.SPELL_CAMELOT_TELEPORT, "Camelot teleport",-1),
    ENCHANT_XBOW(WidgetInfo.SPELL_ENCHANT_CROSSBOW_BOLT, "Enchant XBow bolts",-1),
    ENCHANT_LVL1(WidgetInfo.SPELL_LVL_1_ENCHANT, "Enchant XBow bolts",1637),
    VULNERABILITY(WidgetInfo.SPELL_VULNERABILITY, "Vulnerability", -1);


    private final WidgetInfo childId;
    private final String name;
    private final int itemId;


}
