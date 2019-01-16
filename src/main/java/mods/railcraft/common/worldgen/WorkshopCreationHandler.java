/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.worldgen;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class WorkshopCreationHandler implements IVillageCreationHandler {

    @Override
    public PieceWeight getVillagePieceWeight(Random random, int size) {
        return new PieceWeight(ComponentWorkshop.class, 3, MathHelper.getInt(random, size, 1 + size));
    }

    @Override
    public Class<?> getComponentClass() {
        return ComponentWorkshop.class;
    }

    @Override
    public @Nullable StructureVillagePieces.Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
        return ComponentWorkshop.buildComponent(startPiece, pieces, random, x, y, z, facing, type);
    }

}
