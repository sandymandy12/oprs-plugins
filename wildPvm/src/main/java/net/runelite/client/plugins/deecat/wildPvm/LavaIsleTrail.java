package net.runelite.client.plugins.deecat.wildPvm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public
enum    LavaIsleTrail
{

    SAFE("~Safespot~",new WorldPoint(3214,3836,0),"tile",22,0, 0),
    SAFEHOUSE("Safe house",new WorldPoint(3218,3835,0),"tile",22 ,-1,2),

    LVL30_WILD("Level 30 Wild", new WorldPoint(3167,3759,0),"level",-1,9,0),
    LVL20_WILD("Level 20 Wild", new WorldPoint(3167,3678,0),"level",-1,10,0),

    LAVA_MAZE("Lava maze",new WorldPoint(3030,3840, 0),"tile",1 ,-1,3),
    STAGE2("Stage 2", new WorldPoint(3036, 3834,0),"tile",2,-1,1),
    STAGE3("Stage 3", new WorldPoint(3040, 3826,0),"tile",3,-1,1),
    STAGE4("Stage 4", new WorldPoint(3058, 3825,0),"tile",4,-1,1),
    STAGE5("Stage 5", new WorldPoint(3071, 3823,0),"tile",5,-1,1),
    STAGE6("Stage 6", new WorldPoint(3080, 3820,0),"tile",6,-1,1),
    STAGE7("Stage 7", new WorldPoint(3096, 3823,0),"tile",7,-1,2),
    ROCK_PILE("Rock pile", new WorldPoint(3110, 3824,0),"tile",8,-1,2),
    STAGE8("Stage 8", new WorldPoint(3118, 3834,0),"tile",9,-1,2),
    STAGE9("Stage 9", new WorldPoint(3124, 3841,0),"tile",10,-1,2),
    STAGE10("Stage 10", new WorldPoint(3128, 3850,0),"tile",11,-1,2),
    STAGE11("Bones (11)", new WorldPoint(3134, 3855,0),"tile",12,-1,2),
    STAGE12("Bones (12)", new WorldPoint(3148, 3864,0),"tile",13,-1,2),
    STAGE13("Lava (13)", new WorldPoint(3159, 3865,0),"tile",14,8,2),
    STAGE14("Bones (14)", new WorldPoint(3169, 3864,0),"tile",15,7,2),
    STAGE15("Stage 15", new WorldPoint(3183, 3866,0),"tile",16,6,2),
    STAGE16("Stage 16", new WorldPoint(3198, 3864,0),"tile",17,5,2),
    GATE_IN("Gate in",new WorldPoint(3202,3855,0),"tile",18,4,1),
    STAGE18("Dragon's lair", new WorldPoint(3200, 3848,0),"tile",19,2,2),
    STAGE19("Skeleton", new WorldPoint(3206, 3843,0),"tile",20,1,2),
    SAFE_TREE("Tree", new WorldPoint(3215, 3838,0),"tile",21,0,2),

    LEVER_TILE("LEVER TILE",new WorldPoint(3154,3924,0),"tile",0,0,1),
    LEVER("LEVER",new WorldPoint(3153,3923,0),"object",0,0,1),
    ARDY("ARDY",new WorldPoint(2562,3311,0),"tile",0,0,10),
    VOID("VOID",new WorldPoint(0,0,0),"void",0,0,0),
    HOME("Home",new WorldPoint(8203,2188,0),"safe",0,0,10),

    LUMBRIDGE("Lumbridge",new WorldPoint(3220,3218,0),"safe",0,0,10),
    FALADOR("Falador",new WorldPoint(2969,3339  ,0),"safe",0,0,10),
    EDGEVILLE("Edgeville",new WorldPoint(3087,3496,0),"safe",0,0,10);


    private final String name;
    private final WorldPoint worldPoint;
    private final String type;
    private final int forwardStep;
    private final int returnStep;
    private final int range;
}
