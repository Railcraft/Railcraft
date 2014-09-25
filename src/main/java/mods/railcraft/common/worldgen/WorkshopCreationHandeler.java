/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import java.util.List;
import java.util.Random;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class WorkshopCreationHandeler implements IVillageCreationHandler {

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int i) {
        return new PieceWeight(ComponentWorkshop.class, 3, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentWorkshop.class;
    }

    @Override
    public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, int coordBaseMode, int p5) {
        return ComponentWorkshop.buildComponent(startPiece, pieces, random, x, y, z, coordBaseMode, p5);
    }

}
