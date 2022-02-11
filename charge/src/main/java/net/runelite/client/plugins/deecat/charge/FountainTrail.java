package net.runelite.client.plugins.deecat.charge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum FountainTrail
{
    SAFE("FEROX [safe]",new WorldPoint(3154,3634,0),"fountain",0,0),
    BANK("FEROX [bank]",new WorldPoint(3135,3628,0),"fountain",-1,0),
    FEROX("FEROX",new WorldPoint(3155,3634,0),"fountain",0,10),
    OBELISK("OBELISK",new WorldPoint(3156,3620,0),"fountain",1,9),
    OBELISK_LVL50("OBELISK [LVL50]",new WorldPoint(3307,3916,0),"fountain",1,8),
    TREE("TREE",new WorldPoint(3315,3905,0),"fountain",2,7),
    STAGE_3("STAGE 3",new WorldPoint(3325,3905,0),"fountain",3,6),
    GATE_IN("GATE IN",new WorldPoint(3336,3896,0),"fountain",4,0),
    GATE_OUT("GATE OUT",new WorldPoint(3336,3895,0),"fountain",0,5),
    BONES("BONES",new WorldPoint(3341,3882,0),"fountain",5,4),
    ENTRANCE("ENTRANCE",new WorldPoint(3353,3872,0),"fountain",6,3),
    STAGE_8("STAGE 8",new WorldPoint(3364,3879,0),"fountain",7,2),
    FOUNTAIN("FOUNTAIN",new WorldPoint(3373,3891,0),"fountain",8,1),

    VOID("VOID",new WorldPoint(0,0,0),"VOID",-1,-1),
    LUMBRIDGE("LUMBRIDGE",new WorldPoint(3220,3218,0),"safe",0,0),
    LEVER("lever",new WorldPoint(3153,3923,0),"object",0,19),
    ARDY("ardy",new WorldPoint(2562,3311,0),"safe",0,0);



    private final String name;
    private final WorldPoint worldPoint;
    private final String type;
    private final int forwardStep;
    private final int returnStep;
}
