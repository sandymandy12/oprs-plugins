package net.runelite.client.plugins.deecat;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.runelite.api.ItemID.*;

public class Food {

    private static final Map<Integer, Function<Integer,Integer>> ITEMS;
    private static final int[] ITEM_IDS;

    static {
        ITEMS = new HashMap<>();
        init();
        ITEM_IDS = ITEMS.keySet().stream().mapToInt(i->i).toArray();
    }

    public static int heals(int id) {
        if (!ITEMS.containsKey(id)) return 0;
        return ITEMS.get(id).apply(1);
    }

    public static int[] foodIds() {
        return ITEM_IDS;
    }

    public static int heals(int id, int maxHP) {
        if (!ITEMS.containsKey(id)) return 0;
        return ITEMS.get(id).apply(maxHP);
    }


    private static void addFood(int heal, int... ids) {
        for (int id : ids) {
            ITEMS.put(id,(maxHP) -> heal);
        }
    }

    private static void addFood(Function<Integer, Integer> func, int... ids) {
        for (int id : ids) {
            ITEMS.put(id,func);
        }
    }

    private static void init() {
        addFood(-5, POISON_KARAMBWAN);
        addFood(1, POTATO, ONION, CABBAGE, POT_OF_CREAM, CHOPPED_ONION, ANCHOVIES);
        addFood(2, TOMATO, CHOPPED_TOMATO, BANANA, SLICED_BANANA, ORANGE, ORANGE_SLICES, ORANGE_CHUNKS,
                PINEAPPLE_RING, PINEAPPLE_CHUNKS, SPICY_SAUCE, CHEESE, SPINACH_ROLL, LEMON, LEMON_CHUNKS, LEMON_SLICES,
                LIME, LIME_CHUNKS, LIME_SLICES, DWELLBERRIES);
        addFood(3, SHRIMPS, COOKED_MEAT, COOKED_CHICKEN, ROE, CHOCOLATE_BAR);
        addFood(4, SARDINE, CAKE, _23_CAKE, SLICE_OF_CAKE, CHOCOLATEY_MILK, BAKED_POTATO, EDIBLE_SEAWEED, MOONLIGHT_MEAD);
        addFood(5, BREAD, HERRING, CHOCOLATE_CAKE, _23_CHOCOLATE_CAKE, CHOCOLATE_SLICE, COOKED_RABBIT, CHILLI_CON_CARNE,
                FRIED_MUSHROOMS, FRIED_ONIONS, REDBERRY_PIE, HALF_A_REDBERRY_PIE, CAVIAR, PYSK_FISH_0);
        addFood(6, CHOCICE, MACKEREL, MEAT_PIE, HALF_A_MEAT_PIE, GUANIC_BAT_0, ROAST_BIRD_MEAT,
                SQUARE_SANDWICH, ROLL, BAGUETTE, TRIANGLE_SANDWICH, GIANT_CARP);
        addFood(7, TROUT, COD, PLAIN_PIZZA, _12_PLAIN_PIZZA, APPLE_PIE, HALF_AN_APPLE_PIE, ROAST_RABBIT,
                PREMADE_CH_CRUNCH, CHOCCHIP_CRUNCHIES, PREMADE_SY_CRUNCH, SPICY_CRUNCHIES);
        addFood(8, PIKE, ROAST_BEAST_MEAT, MEAT_PIZZA, _12_MEAT_PIZZA, PREMADE_WM_CRUN, WORM_CRUNCHIES, PREMADE_TD_CRUNCH,
                TOAD_CRUNCHIES, EGG_AND_TOMATO, PRAEL_BAT_1, PEACH, SUPHI_FISH_1);
        addFood(9, PREMADE_P_PUNCH, PINEAPPLE_PUNCH, PREMADE_FR_BLAST, FRUIT_BLAST, SALMON, ANCHOVY_PIZZA,
                _12_ANCHOVY_PIZZA);
        addFood(10, TUNA, COOKED_CRAB_MEAT, CHOPPED_TUNA, COOKED_CHOMPY, FIELD_RATION);
        addFood(11, JUG_OF_WINE, RAINBOW_FISH, STEW, PINEAPPLE_PIZZA, _12_PINEAPPLE_PIZZA, COOKED_FISHCAKE,
                PREMADE_VEG_BATTA, VEGETABLE_BATTA, PREMADE_WM_BATTA, WORM_BATTA, PREMADE_TD_BATTA, TOAD_BATTA, PREMADE_CT_BATTA,
                CHEESETOM_BATTA, PREMADE_FRT_BATTA, FRUIT_BATTA, MUSHROOM__ONION, GIRAL_BAT_2, LAVA_EEL, LECKISH_FISH_2);
        addFood(12, LOBSTER, PREMADE_WORM_HOLE, WORM_HOLE, PREMADE_VEG_BALL, VEG_BALL);
        addFood(13, BASS, TUNA_AND_CORN);
        addFood(14, POTATO_WITH_BUTTER, CHILLI_POTATO, SWORDFISH, PHLUXIA_BAT_3, PUMPKIN, EASTER_EGG, BRAWK_FISH_3);
        addFood(15, PREMADE_TTL, TANGLED_TOADS_LEGS, PREMADE_CHOC_BOMB, CHOCOLATE_BOMB, COOKED_JUBBLY);
        addFood(16, MONKFISH, POTATO_WITH_CHEESE, EGG_POTATO);
        addFood(17, MYCIL_FISH_4, KRYKET_BAT_4);
        addFood(18, COOKED_KARAMBWAN);
        addFood(19, CURRY, UGTHANKI_KEBAB, UGTHANKI_KEBAB_1885);
        addFood(20, MUSHROOM_POTATO, SHARK, ROQED_FISH_5, MURNG_BAT_5, STUFFED_SNAKE);
        addFood(21, SEA_TURTLE);
        addFood(22, MANTA_RAY, DARK_CRAB, TUNA_POTATO);
        addFood(maxHP -> (int) Math.ceil(maxHP * .06), STRAWBERRY);
        addFood(maxHP -> (int) Math.ceil(maxHP * .05), WATERMELON_SLICE);
        addFood(maxHP -> (int) Math.ceil(maxHP * .10), COOKED_SWEETCORN, SWEETCORN_7088 /* Bowl of cooked sweetcorn */);
        addFood(8, PAPAYA_FRUIT);
        addFood(5, THIN_SNAIL_MEAT);
        addFood(7, FAT_SNAIL_MEAT);
        addFood(23, KYREN_FISH_6, PSYKK_BAT_6);
        addFood(maxHP-> (int) Math.floor(maxHP*0.15+2),SARADOMIN_BREW1,SARADOMIN_BREW2,SARADOMIN_BREW3,SARADOMIN_BREW4);
        addFood(maxHP -> {
            int C;
            if (maxHP <= 24) {
                C = 2;
            } else if (maxHP <= 49) {
                C = 4;
            } else if (maxHP <= 74) {
                C = 6;
            } else if (maxHP <= 92) {
                C = 8;
            } else {
                C = 13;
            }
            return (maxHP / 10) + C;
        }, ANGLERFISH);
    }

    public static void main(String[] args) {
//        System.out.println(heals(ANGLERFISH,10));
//        System.out.println(heals(ANGLERFISH,50));
//        System.out.println(heals(ANGLERFISH,99));
        System.out.println(heals(COOKED_KARAMBWAN,99));

        String wildernessLevelText = "Level: 1<br>89-91";
        Pattern WILDERNESS_LEVEL_PATTERN = Pattern.compile(".*?(\\d+)-(\\d+).*");

        final Matcher m = WILDERNESS_LEVEL_PATTERN.matcher(wildernessLevelText);
        if (!m.matches()) {
            return;
        }
//		final int wildernessLevel = Integer.parseInt(m.group(1));
//		final int combatLevel = Objects.requireNonNull(client.getLocalPlayer()).getCombatLevel();
//		combatAttackRange(combatLevel, wildernessLevel);
        int minCombatLevel = Integer.parseInt(m.group(1));
        int maxCombatLevel = Integer.parseInt(m.group(2));
        System.out.println("text " + wildernessLevelText);
        System.out.println("wildy level " + minCombatLevel + " " + maxCombatLevel);
    }

}
