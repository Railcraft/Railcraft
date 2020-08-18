/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.fluids;

import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.IRailcraftBlockContainer;
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
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum RailcraftFluids implements IRailcraftBlockContainer {

    CREOSOTE("creosote", Fluids.CREOSOTE, 800, 320, 1500, false, f -> {
        return new BlockRailcraftFluid(f, Material.WATER) {
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
    }),
    STEAM("steam", Fluids.STEAM, -1000, 400, 500, true,
            f -> new BlockRailcraftFluidFinite(f, new MaterialLiquid(MapColor.AIR)).setParticleColor(0xf2 / 255f, 0xf2 / 255f, 0xf2 / 255f)
    );
    public static final RailcraftFluids[] VALUES = values();
    private final Fluids standardFluid;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final boolean isGaseous;
    private Fluid railcraftFluid;
    private final Def<?> def;

    private class Def<B extends BlockFluidBase & IRailcraftBlock> extends Definition<Def<B>> {
        private final Function<Fluid, B> blockSupplier;
        private B block;
        private ItemBlockRailcraft<B> item;

        public Def(String tag, Function<Fluid, B> blockSupplier) {
            super(tag);
            this.blockSupplier = blockSupplier;
        }

        private void initBlock() {
            if (block == null && RailcraftConfig.isBlockEnabled("fluid." + tag) && railcraftFluid != null) {
                block = blockSupplier.apply(railcraftFluid);
                block.setRegistryName(registryName);
                block.setTranslationKey("railcraft.fluid." + tag);
                item = new ItemBlockRailcraft<B>(block) {
                    @Override
                    public void initializeClient() {
                        ModelBakery.registerItemVariants(this);
                        ModelLoader.setCustomMeshDefinition(this, (stack) -> getModel(tag));
                    }
                };
                item.setRegistryName(registryName);
                RailcraftRegistry.register(def.block, def.item);
                railcraftFluid.setBlock(def.block);
            }
        }
    }

    <B extends BlockFluidBase & IRailcraftBlock>
    RailcraftFluids(String tag, Fluids standardFluid, int density, int temperature, int viscosity, boolean isGaseous,
                    Function<Fluid, B> blockSupplier) {
        this.standardFluid = standardFluid;
        this.density = density;
        this.temperature = temperature;
        this.viscosity = viscosity;
        this.isGaseous = isGaseous;
        this.def = new Def<>(tag, blockSupplier);

    }

    @Override
    public Def getDef() {
        return def;
    }

    @Override
    public Optional<IRailcraftBlock> getObject() {
        return Optional.ofNullable(def.block);
    }

    @Override
    public void register() {
        initFluid();
        def.initBlock();
        checkStandardFluidBlock();
    }

    @Override
    public void finalizeDefinition() {
        checkStandardFluidBlock();
        if (!standardFluid.isPresent() && RailcraftConfig.isFluidEnabled(standardFluid.getTag()))
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

    public static StateMapperBase getStateMapper(IRailcraftBlock block) {
        return new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return getModel(block.getRegistryName().getPath());
            }
        };
    }

    public static ModelResourceLocation getModel(String tag) {
        return new ModelResourceLocation(Railcraft.MOD_ID + ":fluids", tag);
    }

    private void checkStandardFluidBlock() {
        if (def.block == null)
            return;
        Fluid fluid = standardFluid.get();
        if (fluid == null)
            return;
        Block fluidBlock = fluid.getBlock();
        if (fluidBlock == null)
            fluid.setBlock(def.block);
//        } else {
//            GameRegistry.UniqueIdentifier blockID = GameRegistry.findUniqueIdentifierFor(fluidBlock);
//            Game.log(Level.WARN, "Pre-existing {0} fluid block detected, deferring to {1}:{2}, "
//                    + "this may cause issues if the server/client have different mod load orders, "
//                    + "recommended that you disable all but one instance of {0} fluid blocks via your configs.", fluid.getName(), blockID.modId, blockID.name);
//        }
    }

    @Override
    public @Nullable Item item() {
        return def.item;
    }

    public static class MissingFluidException extends RuntimeException {
        public MissingFluidException(String tag) {
            super("Fluid '" + tag + "' was not found. Please check your configs.");
        }
    }
}
