package net.runelite.client.plugins.deecat.blueDragon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BlueDragonTrail
{
    FALADOR("FALADOR",new WorldPoint(2965,3378,0),"town",2,0),
    STAGE1("STAGE 1",new WorldPoint(2950,3378,0),"deposit",1,1),
    BANK("BANK",new WorldPoint(2946,3368,0),"tile",1,2),
    STAGE3("STAGE 3",new WorldPoint(2966,3394,0),"tile",3,0),
    STAGE4("STAGE 4",new WorldPoint(2965,3410,0),"tile",4,0),
    STAGE5("STAGE 5",new WorldPoint(2947,3426,0),"tile",5,0),
    STAGE6("STAGE 6",new WorldPoint(2947,3442,0),"tile",6,0),
    GATE_IN("GATE_IN",new WorldPoint(2936,3450,0),"tile",7,0),
    STAGE8("STAGE 8",new WorldPoint(2928,3437,0),"tile",8,0),
    STAGE9("STAGE 9",new WorldPoint(2917,3425,0),"tile",9,0),
    STAGE10("STAGE 10",new WorldPoint(2906,3411,0),"tile",10,0),
    STAGE11("STAGE 11",new WorldPoint(2897,3399,0),"tile",11,0),
    LADDER_UP("LADDER [UP]",new WorldPoint(2885,3397,0),"tile",12,0),

    LADDER_DOWN("LADDER [DOWN]",new WorldPoint(2885,9797,0),"tile",13,0),
    STAGE14("STAGE 14",new WorldPoint(2885,9815,0),"tile",14,0),
    GATE_1("GATE 1",new WorldPoint(2888,9830,0),"tile",15,0),
    GATE_2("GATE 2",new WorldPoint(2892,9826,0),"tile",16,0),
    STAGE17("STAGE 17",new WorldPoint(2901,9819,0),"tile",17,0),
    STAGE18("STAGE 18",new WorldPoint(2915,9819,0),"tile",18,0),
    STAGE19("STAGE 19",new WorldPoint(2929,9822,0),"tile",19,0),
    STAGE20("STAGE 20",new WorldPoint(2937,9812,0),"tile",20,0),
    STAGE21("STAGE 21",new WorldPoint(2931,9809,0),"tile",21,0),
    STAGE22("STAGE 22",new WorldPoint(2925,9802,0),"tile",22,0),
    SAFE("SAFE",new WorldPoint(2901,9810,0),"tile",23,0);

    //STAGE23("STAGE 22",new WorldPoint(2925,9802,0),"tile",23,0),
//    STAGE24("STAGE 22",new WorldPoint(2925,9802,0),"tile",24,0);




    private final String name;
    private final WorldPoint worldPoint;
    private final String type;
    private final int forwardStep;
    private final int returnStep;
}
