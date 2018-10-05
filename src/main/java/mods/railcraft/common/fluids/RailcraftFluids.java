/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.potion.RailcraftPotions;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftFluids {

    CREOSOTE("fluid.creosote", Fluids.CREOSOTE, 800, 320, 1500, false) {
        @Override
        Block makeBlock(Fluid fluid) {
            return new BlockRailcraftFluid(fluid, Material.WATER) {
                @Override
                public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
                    super.onEntityCollision(worldIn, pos, state, entityIn);
                    if (entityIn instanceof EntityLivingBase && RailcraftPotions.CREOSOTE.isEnabled()) {
                        EntityLivingBase living = (EntityLivingBase) entityIn;
                        Potion potion = RailcraftPotions.CREOSOTE.get();
                        if (!living.isPotionActive(potion)) {
                            living.addPotionEffect(new PotionEffect(potion, 100, 0));
                        }
                    }
                }
            }.setFlammable(true).setFlammability(10).setParticleColor(0xcc / 255f, 0xa3 / 255f, 0x00 / 255f);
        }
    },
    STEAM("fluid.steam", Fluids.STEAM, -1000, 400, 500, true) {
        @Override
        Block makeBlock(Fluid fluid) {
            return new BlockRailcraftFluidFinite(fluid, new MaterialLiquid(MapColor.AIR)).setNoFlow().setParticleColor(0xf2 / 255f, 0xf2 / 255f, 0xf2 / 255f);
        }
    };
    public static final RailcraftFluids[] VALUES = values();
    private final String tag;
    private final String name;
    private final Fluids standardFluid;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final boolean isGaseous;
    final ModelResourceLocation location;
    private Fluid railcraftFluid;
    private Block railcraftBlock;
    private ItemBlock railcraftItem;

    RailcraftFluids(String tag, Fluids standardFluid, int density, int temperature, int viscosity, boolean isGaseous) {
        this.tag = tag;
        this.standardFluid = standardFluid;
        this.density = density;
        this.temperature = temperature;
        this.viscosity = viscosity;
        this.isGaseous = isGaseous;
        this.name = name().toLowerCase();
        this.location = new ModelResourceLocation(Railcraft.MOD_ID + ":fluids", this.name);
    }

    public static void preInitFluids() {
        for (RailcraftFluids fluidType : VALUES) {
            fluidType.init();
        }
    }

    public static void finalizeDefinitions() {
        for (RailcraftFluids fluidType : VALUES) {
            fluidType.postInit();
        }
    }

    private void init() {
        initFluid();
        initBlock();
        checkStandardFluidBlock();
        if (FMLCommonHandler.instance().getSidedDelegate().getSide().isClient())
            initClient();
    }

    private void postInit() {
        checkStandardFluidBlock();
        if (standardFluid.get() == null && RailcraftConfig.isFluidEnabled(standardFluid.getTag()))
            throw new MissingFluidException(standardFluid.getTag());
    }

    private void initFluid() {
        if (railcraftFluid == null && RailcraftConfig.isFluidEnabled(standardFluid.getTag())) {
            String fluidName = standardFluid.getTag();
            ResourceLocation stillTexture = new ResourceLocation("railcraft:fluids/" + fluidName + "_still");
            ResourceLocation flowTexture;
            if (isGaseous)
                flowTexture = stillTexture;
            else
                flowTexture = new ResourceLocation("railcraft:fluids/" + fluidName + "_flow");
            railcraftFluid = new Fluid(fluidName, stillTexture, flowTexture).setDensity(density).setTemperature(temperature).setViscosity(viscosity).setGaseous(isGaseous);
            FluidRegistry.registerFluid(railcraftFluid);
            if (!isGaseous)
                FluidRegistry.addBucketForFluid(railcraftFluid);
        }
    }

    @SideOnly(Side.CLIENT)
    private void initClient() {
        if (railcraftBlock != null && railcraftItem != null) {
            ModelBakery.registerItemVariants(railcraftItem);
            ModelLoader.setCustomMeshDefinition(railcraftItem, (stack) -> location);
            ModelLoader.setCustomStateMapper(railcraftBlock, new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return location;
                }
            });
        }
    }

    abstract Block makeBlock(Fluid fluid);

    private void initBlock() {
        Fluid fluid;
        if (railcraftBlock == null && RailcraftConfig.isBlockEnabled(tag) && (fluid = standardFluid.get()) != null) {
            railcraftBlock = makeBlock(fluid);
            railcraftBlock.setRegistryName(name);
            railcraftBlock.setTranslationKey("railcraft." + tag);
            railcraftItem = new ItemBlockRailcraft(railcraftBlock);
            railcraftItem.setRegistryName(name);
            RailcraftRegistry.register(railcraftBlock, railcraftItem);
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
