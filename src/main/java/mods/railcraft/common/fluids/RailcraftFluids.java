/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.fluids;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class RailcraftFluids {

    public static final RailcraftFluids INSTANCE = new RailcraftFluids();
    private static Fluid railcraftCreosote;
    private static Fluid railcraftSteam;
    public static Block blockCreosote;
    public static Block blockSteam;

    public static class MissingFluidException extends RuntimeException {

        public MissingFluidException(String msg) {
            super(msg);
        }

    }

    private RailcraftFluids() {
    }

    public static void preInit() {
        initCreosote();
        initSteam();
    }

    public static void postInit() {
        initCreosoteBlock();
        initSteamBlock();

        if (Fluids.CREOSOTE.get() == null)
            throw new MissingFluidException("Fluid 'creosote' was not found. Please check your configs.");

        if (Fluids.STEAM.get() == null)
            throw new MissingFluidException("Fluid 'steam' was not found. Please check your configs.");
    }

    public static void initCreosote() {
        if (railcraftCreosote == null && RailcraftConfig.isFluidEnabled("creosote")) {
            railcraftCreosote = new Fluid("creosote").setDensity(800).setViscosity(1500);
            FluidRegistry.registerFluid(railcraftCreosote);

            initCreosoteBlock();
        }
    }

    public static void initCreosoteBlock() {
        if (blockCreosote != null)
            return;

        Fluid fluidCreosote = Fluids.CREOSOTE.get();
        if (fluidCreosote == null)
            return;

        if (fluidCreosote.getBlock() == null) {
            if (RailcraftConfig.isBlockEnabled("fluid.creosote")) {
                blockCreosote = new BlockRailcraftFluid(fluidCreosote, Material.water).setFlammable(true).setFlammability(10);
                blockCreosote.setBlockName("railcraft.fluid.creosote");
                GameRegistry.registerBlock(blockCreosote, blockCreosote.getUnlocalizedName());
                fluidCreosote.setBlock(blockCreosote);
            }
        } else
            blockCreosote = fluidCreosote.getBlock();
        FluidContainers.getCreosoteOilBucket();
        FluidContainers.getCreosoteOilBottle();
        FluidContainers.getCreosoteOilCan();
        FluidContainers.getCreosoteOilCell();
        FluidContainers.getCreosoteOilWax();
        FluidContainers.getCreosoteOilRefactory();
    }

    private static void initSteam() {
        if (railcraftSteam == null && RailcraftConfig.isFluidEnabled("steam")) {
            railcraftSteam = new Fluid("steam").setDensity(-1000).setViscosity(500);
            FluidRegistry.registerFluid(railcraftSteam);

            initSteamBlock();
        }
    }

    private static void initSteamBlock() {
        if (blockSteam != null)
            return;

        Fluid fluidSteam = Fluids.STEAM.get();
        if (fluidSteam == null)
            return;

        if (fluidSteam.getBlock() == null) {
            if (RailcraftConfig.isBlockEnabled("fluid.steam")) {
                blockSteam = new BlockRailcraftFluidFinite(fluidSteam, new MaterialLiquid(MapColor.airColor)).setNoFlow();
                blockSteam.setBlockName("railcraft.fluid.steam");
                GameRegistry.registerBlock(blockSteam, blockSteam.getUnlocalizedName());
                fluidSteam.setBlock(blockSteam);
            }
        } else
            blockSteam = fluidSteam.getBlock();
        FluidContainers.getSteamBottle();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() == 0) {
            railcraftCreosote.setIcons(blockCreosote.getBlockTextureFromSide(1), blockCreosote.getBlockTextureFromSide(2));
            railcraftSteam.setIcons(blockSteam.getBlockTextureFromSide(1), blockSteam.getBlockTextureFromSide(2));
        }
    }

}
