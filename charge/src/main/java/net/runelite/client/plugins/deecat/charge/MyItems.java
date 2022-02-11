package net.runelite.client.plugins.deecat.charge;

import com.google.common.collect.ImmutableList;
import net.runelite.api.Item;
import net.runelite.api.ItemID;

import java.util.ArrayList;
import java.util.List;

public class MyItems {

    public List<Integer> lavaDragonLoot = ImmutableList.of(
            ItemID.LAVA_SCALE,ItemID.LAVA_DRAGON_BONES, ItemID.LOOTING_BAG,
            ItemID.BLACK_DRAGONHIDE
    );
    public boolean isGuthansHelm(int itemID){
        List<Integer> guthansHelm = new ArrayList<>();
        guthansHelm.add(ItemID.GUTHANS_HELM);
        guthansHelm.add(ItemID.GUTHANS_HELM_0);
        guthansHelm.add(ItemID.GUTHANS_HELM_25);
        guthansHelm.add(ItemID.GUTHANS_HELM_50);
        guthansHelm.add(ItemID.GUTHANS_HELM_75);
        guthansHelm.add(ItemID.GUTHANS_HELM_100);
        guthansHelm.add(ItemID.GUTHANS_HELM_23638);
        return guthansHelm.contains(itemID);
    }

    public boolean isGuthansWep(int itemID){
        List<Integer> guthansWep = new ArrayList<>();
        guthansWep.add(ItemID.GUTHANS_WARSPEAR);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_0);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_25);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_50);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_75);
        guthansWep.add(ItemID.GUTHANS_WARSPEAR_100);
        return guthansWep.contains(itemID);

    }
    public boolean ignore(String id) {
        List<String> ignore = new ArrayList<>();
        ignore.add("Wolfblast861");
        ignore.add("54throw2767");
        ignore.add("BlizzardAOE");
        return ignore.contains(id);
    }
    public boolean isSlayerHelm(int itemID) {
        List<Integer> slayerHelm = new ArrayList<>();
        slayerHelm.add(ItemID.SLAYER_HELMET);
        slayerHelm.add(ItemID.SLAYER_HELMET_I);
        slayerHelm.add(ItemID.SLAYER_HELMET_I_25177);
        return slayerHelm.contains(itemID);
    }

    public boolean isAltWep(int itemID){
        List<Integer> wep = new ArrayList<>();
        wep.add(ItemID.ABYSSAL_BLUDGEON);
        wep.add(ItemID.ABYSSAL_DAGGER_P_13271);
        wep.add(ItemID.DARK_BOW);
        wep.add(ItemID.ABYSSAL_WHIP);
        return wep.contains(itemID);
    }
    public boolean isAltShield(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.DRAGON_DEFENDER);
        items.add(ItemID.DRAGONFIRE_SHIELD);
        return items.contains(itemID);
    }

    public boolean isFood(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.MONKFISH);
        items.add(ItemID.SHARK);
        items.add(ItemID.COOKED_KARAMBWAN);
        items.add(ItemID.LOBSTER);
        items.add(ItemID.SARADOMIN_BREW1);
        items.add(ItemID.SARADOMIN_BREW2);
        items.add(ItemID.SARADOMIN_BREW3);
        items.add(ItemID.SARADOMIN_BREW4);
        items.add(ItemID.BANANA);
        items.add(ItemID.ANGLERFISH);
        items.add(ItemID.STRAWBERRIES5);
        items.add(ItemID.STRAWBERRIES4);
        items.add(ItemID.STRAWBERRIES3);
        items.add(ItemID.STRAWBERRIES2);
        items.add(ItemID.STRAWBERRIES1);
        return items.contains(itemID);
    }

    public boolean isStamina(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.STAMINA_POTION1);
        items.add(ItemID.STAMINA_POTION2);
        items.add(ItemID.STAMINA_POTION3);
        items.add(ItemID.STAMINA_POTION4);
        return items.contains(itemID);
    }

    public boolean isEnergy(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.ENERGY_POTION1);
        items.add(ItemID.ENERGY_POTION3);
        items.add(ItemID.ENERGY_POTION2);
        items.add(ItemID.ENERGY_POTION4);

        items.add(ItemID.SUPER_ENERGY1);
        items.add(ItemID.SUPER_ENERGY2);
        items.add(ItemID.SUPER_ENERGY3);
        items.add(ItemID.SUPER_ENERGY4);
        return items.contains(itemID);
    }

    public boolean isKarilsXbow(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.KARILS_CROSSBOW);
        items.add(ItemID.KARILS_CROSSBOW_0);
        items.add(ItemID.KARILS_CROSSBOW_25);
        items.add(ItemID.KARILS_CROSSBOW_50);
        items.add(ItemID.KARILS_CROSSBOW_75);
        items.add(ItemID.KARILS_CROSSBOW_100);
        return items.contains(itemID);
    }

    public boolean isPrayer(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.PRAYER_POTION1);
        items.add(ItemID.PRAYER_POTION2);
        items.add(ItemID.PRAYER_POTION3);
        items.add(ItemID.PRAYER_POTION4);

        items.add(ItemID.SUPER_RESTORE1);
        items.add(ItemID.SUPER_RESTORE2);
        items.add(ItemID.SUPER_RESTORE3);
        items.add(ItemID.SUPER_RESTORE4);
        return items.contains(itemID);
    }

    public boolean isTeleport(int itemID) {
        List<Integer> items = new ArrayList<>();
        items.add(ItemID.TELEPORT_TO_HOUSE);
        items.add(ItemID.VARROCK_TELEPORT);

        items.add(ItemID.AMULET_OF_GLORY1);
        items.add(ItemID.AMULET_OF_GLORY2);
        items.add(ItemID.AMULET_OF_GLORY3);
        items.add(ItemID.AMULET_OF_GLORY4);
        items.add(ItemID.AMULET_OF_GLORY5);
        items.add(ItemID.AMULET_OF_GLORY6);

        items.add(ItemID.RING_OF_WEALTH_1);
        items.add(ItemID.RING_OF_WEALTH_2);
        items.add(ItemID.RING_OF_WEALTH_3);
        items.add(ItemID.RING_OF_WEALTH_4);
        items.add(ItemID.RING_OF_WEALTH_5);
        return items.contains(itemID);
    }

    public boolean isGlory(int itemID) {
        List<Integer> items = new ArrayList<>();

        items.add(ItemID.AMULET_OF_GLORY1);
        items.add(ItemID.AMULET_OF_GLORY2);
        items.add(ItemID.AMULET_OF_GLORY3);
        items.add(ItemID.AMULET_OF_GLORY4);
        items.add(ItemID.AMULET_OF_GLORY5);
        items.add(ItemID.AMULET_OF_GLORY6);
        return items.contains(itemID);
    }
    public boolean isWealth(int itemID) {
        List<Integer> items = new ArrayList<>();

        items.add(ItemID.RING_OF_WEALTH_1);
        items.add(ItemID.RING_OF_WEALTH_2);
        items.add(ItemID.RING_OF_WEALTH_3);
        items.add(ItemID.RING_OF_WEALTH_4);
        items.add(ItemID.RING_OF_WEALTH_5);
        return items.contains(itemID);
    }
}
