/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.worldgen;

import mods.railcraft.common.core.RailcraftConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 6/8/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class GeneratorRailcraftOre implements IWorldGenerator, BooleanSupplier, Supplier<String>, IForgeRegistryEntry<IWorldGenerator> {
    private final IWorldGenerator generator;
    private final boolean retrogen;
    private final String retrogenMarker;
    private ResourceLocation registryName = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN, "generator");

    public GeneratorRailcraftOre(IWorldGenerator generator, boolean retrogen, String retrogenMarker) {
        this.generator = generator;
        this.retrogen = retrogen;
        this.retrogenMarker = retrogenMarker;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        generator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }

    @Override
    public IWorldGenerator setRegistryName(ResourceLocation name) {
        registryName = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public Class<? super IWorldGenerator> getRegistryType() {
        return IWorldGenerator.class;
    }

    @Override
    public boolean getAsBoolean() {
        return retrogen;
    }

    @Override
    public String get() {
        return retrogenMarker;
    }
}
