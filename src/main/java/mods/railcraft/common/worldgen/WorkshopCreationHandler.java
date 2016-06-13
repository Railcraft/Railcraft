/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.worldgen;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WorkshopCreationHandler implements IVillageCreationHandler {

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int i) {
        return new PieceWeight(ComponentWorkshop.class, 3, 1);
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentWorkshop.class;
    }

    @Nullable
    @Override
    public StructureVillagePieces.Village buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
        return ComponentWorkshop.buildComponent(startPiece, pieces, random, x, y, z, facing, type);
    }

}
