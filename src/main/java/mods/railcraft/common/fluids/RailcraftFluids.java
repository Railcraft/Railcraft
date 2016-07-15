/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftFluids {

    CREOSOTE("fluid.creosote", Fluids.CREOSOTE, 800, 1500) {
        @Override
        void defineContainers() {
            FluidContainers.getCreosoteOilBucket();
            FluidContainers.getCreosoteOilBottle();
            FluidContainers.getCreosoteOilCan();
            FluidContainers.getCreosoteOilCell();
            FluidContainers.getCreosoteOilWax();
            FluidContainers.getCreosoteOilRefactory();
        }

        @Override
        public Block makeBlock(Fluid fluid) {
            return new BlockRailcraftFluid(fluid, Material.WATER).setFlammable(true).setFlammability(10);
        }
    },
    STEAM("fluid.steam", Fluids.STEAM, -1000, 500) {
        @Override
        void defineContainers() {
            FluidContainers.getSteamBottle();
        }

        @Override
        Block makeBlock(Fluid fluid) {
            return new BlockRailcraftFluidFinite(fluid, new MaterialLiquid(MapColor.AIR)).setNoFlow();
        }
    };
    public static final RailcraftFluids[] VALUES = values();
    public final String tag;
    public final Fluids standardFluid;
    public final int density, viscosity;
    protected Fluid railcraftFluid;
    protected Block railcraftBlock;

    RailcraftFluids(String tag, Fluids standardFluid, int density, int viscosity) {
        this.tag = tag;
        this.standardFluid = standardFluid;
        this.density = density;
        this.viscosity = viscosity;
    }

    public static void preInitFluids() {
        for (RailcraftFluids fluidType : VALUES) {
            fluidType.init();
        }
    }

    public static void postInitFluids() {
        for (RailcraftFluids fluidType : VALUES) {
            fluidType.postInit();
        }
    }

    private void init() {
        initFluid();
        initBlock();
        checkStandardFluidBlock();
        if (standardFluid.get() != null)
            defineContainers();
    }

    private void postInit() {
        checkStandardFluidBlock();
        if (standardFluid.get() == null)
            throw new MissingFluidException(standardFluid.getTag());
    }

    private void initFluid() {
        if (railcraftFluid == null && RailcraftConfig.isFluidEnabled(standardFluid.getTag())) {
            String fluidName = standardFluid.getTag();
            ResourceLocation stillTexture = new ResourceLocation("railcraft:fluids/" + fluidName + "_still");
            ResourceLocation flowTexture = new ResourceLocation("railcraft:fluids/" + fluidName + "_flow");
            railcraftFluid = new Fluid(fluidName, stillTexture, flowTexture).setDensity(density).setViscosity(viscosity).setGaseous(density < 0);
//            if (!FluidRegistry.isFluidRegistered(standardFluid.getTag()))
            FluidRegistry.registerFluid(railcraftFluid);
//            else {
//                Game.log(Level.WARN, "Pre-existing {0} fluid detected, deferring, "
//                        + "this may cause issues if the server/client have different mod load orders, "
//                        + "recommended that you disable all but one instance of the fluid via your configs.", standardFluid.getTag());
//            }
        }
    }

    void defineContainers() {
    }

    abstract Block makeBlock(Fluid fluid);

    private void initBlock() {
        Fluid fluid;
        if (railcraftBlock == null && RailcraftConfig.isBlockEnabled(tag) && (fluid = standardFluid.get()) != null) {
            railcraftBlock = makeBlock(fluid);
            railcraftBlock.setRegistryName(tag);
            railcraftBlock.setUnlocalizedName("railcraft." + tag);
            ItemBlock itemBlock = new ItemBlockRailcraft(railcraftBlock);
            itemBlock.setRegistryName(tag);
            RailcraftRegistry.register(railcraftBlock, itemBlock);
            railcraftFluid.setBlock(railcraftBlock);
        }
    }

    private void checkStandardFluidBlock() {
        if (railcraftBlock == null)
            return;
        Fluid fluid = standardFluid.get();
        if (fluid == null)
            return;
        Block fluidBlock = fluid.getBlock();
        if (fluidBlock == null)
            fluid.setBlock(railcraftBlock);
//        } else {
//            GameRegistry.UniqueIdentifier blockID = GameRegistry.findUniqueIdentifierFor(fluidBlock);
//            Game.log(Level.WARN, "Pre-existing {0} fluid block detected, deferring to {1}:{2}, "
//                    + "this may cause issues if the server/client have different mod load orders, "
//                    + "recommended that you disable all but one instance of {0} fluid blocks via your configs.", fluid.getName(), blockID.modId, blockID.name);
//        }
    }

    public Block getBlock() {
        return railcraftBlock;
    }

    public static class MissingFluidException extends RuntimeException {
        public MissingFluidException(String tag) {
            super("Fluid '" + tag + "' was not found. Please check your configs.");
        }
    }
}
