/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.worldgen.OreGeneratorFactory.BiomeRules;
import mods.railcraft.common.worldgen.OreGeneratorFactory.DimensionRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Random;

public abstract class RuledGenerator extends Generator {

    protected final DimensionRules dimensionRules;
    protected final BiomeRules biomeRules;

    protected RuledGenerator(DimensionRules dimensionRules, BiomeRules biomeRules) {
        this.biomeRules = biomeRules;
        this.dimensionRules = dimensionRules;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public boolean canGen(World world, Random rand, BlockPos targetPos, Biome biome) {
        return dimensionRules.isDimensionValid(world) && biomeRules.isValidBiome(biome);
    }
}
