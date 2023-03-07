package kono.gtceu.processingplant.api.unification.material.flags;

import gregtech.api.unification.material.Materials;

import static gregtech.api.unification.material.info.MaterialFlags.*;

public class PPFlagAddition {
    public static void init() {
        Materials.RedSteel.addFlags(GENERATE_FRAME);
    }
}
