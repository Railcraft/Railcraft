/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.ItemBrick;
import mods.railcraft.common.blocks.aesthetics.generic.BlockGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.ItemBlockGeneric;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.glass.ItemStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.materials.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.materials.slab.ItemSlab;
import mods.railcraft.common.blocks.aesthetics.post.BlockPost;
import mods.railcraft.common.blocks.aesthetics.post.BlockPostMetal;
import mods.railcraft.common.blocks.aesthetics.post.ItemPost;
import mods.railcraft.common.blocks.aesthetics.post.ItemPostMetal;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.anvil.ItemAnvil;
import mods.railcraft.common.blocks.charge.BlockChargeFeeder;
import mods.railcraft.common.blocks.charge.BlockChargeTrap;
import mods.railcraft.common.blocks.charge.BlockFrame;
import mods.railcraft.common.blocks.charge.BlockWire;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.ItemDetector;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.blocks.machine.RailcraftBlockMetadata;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.manipulator.BlockMachineManipulator;
import mods.railcraft.common.blocks.machine.simplemachine.BlockMachineSimple;
import mods.railcraft.common.blocks.machine.wayobjects.actuators.BlockMachineActuator;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.BlockOreMagic;
import mods.railcraft.common.blocks.ore.BlockWorldLogic;
import mods.railcraft.common.blocks.ore.ItemOreMagic;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.elevator.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlex;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlexAbandoned;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlexElectric;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.outfitted.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.outfitted.ItemTrackOutfitted;
import mods.railcraft.common.blocks.wayobjects.BlockWayObjectRailcraft;
import mods.railcraft.common.blocks.wayobjects.ItemWayObject;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.firestone.BlockRitual;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftBlockContainer {
    ANVIL_STEEL("anvil", BlockRCAnvil.class, BlockRCAnvil::new, ItemAnvil::new),
    BRICK_ABYSSAL("brick_abyssal", BlockBrick.class, () -> new BlockBrick(BrickTheme.ABYSSAL), ItemBrick::new),
    BRICK_ANDESITE("brick_andesite", BlockBrick.class, () -> new BlockBrick(BrickTheme.ANDESITE), ItemBrick::new),
    BRICK_BLEACHED_BONE("brick_bleachedbone", BlockBrick.class, () -> new BlockBrick(BrickTheme.BLEACHEDBONE), ItemBrick::new),
    BRICK_BLOOD_STAINED("brick_bloodstained", BlockBrick.class, () -> new BlockBrick(BrickTheme.BLOODSTAINED), ItemBrick::new),
    BRICK_DIORITE("brick_diorite", BlockBrick.class, () -> new BlockBrick(BrickTheme.DIORITE), ItemBrick::new),
    BRICK_FROST_BOUND("brick_frostbound", BlockBrick.class, () -> new BlockBrick(BrickTheme.FROSTBOUND), ItemBrick::new),
    BRICK_GRANITE("brick_granite", BlockBrick.class, () -> new BlockBrick(BrickTheme.GRANITE), ItemBrick::new),
    BRICK_INFERNAL("brick_infernal", BlockBrick.class, () -> new BlockBrick(BrickTheme.INFERNAL), ItemBrick::new),
    BRICK_NETHER("brick_nether", BlockBrick.class, () -> new BlockBrick(BrickTheme.NETHER), ItemBrick::new),
    BRICK_QUARRIED("brick_quarried", BlockBrick.class, () -> new BlockBrick(BrickTheme.QUARRIED), ItemBrick::new),
    BRICK_REDNETHER("brick_rednether", BlockBrick.class, () -> new BlockBrick(BrickTheme.REDNETHER), ItemBrick::new),
    BRICK_REDSANDY("brick_redsandy", BlockBrick.class, () -> new BlockBrick(BrickTheme.REDSANDY), ItemBrick::new),
    BRICK_SANDY("brick_sandy", BlockBrick.class, () -> new BlockBrick(BrickTheme.SANDY), ItemBrick::new),
    CHARGE_FEEDER("charge_feeder", BlockChargeFeeder.class, BlockChargeFeeder::new, ItemBlockRailcraftSubtyped::new),
    CHARGE_TRAP("charge_trap", BlockChargeTrap.class, BlockChargeTrap::new, ItemBlockRailcraft::new),
    GENERIC("generic", BlockGeneric.class, BlockGeneric::new, ItemBlockGeneric::new),
    DETECTOR("detector", BlockDetector.class, BlockDetector::new, ItemDetector::new),
    FRAME("frame", BlockFrame.class, BlockFrame::new, ItemBlockRailcraft::new),
    GLASS("glass", BlockStrengthGlass.class, BlockStrengthGlass::new, ItemStrengthGlass::new),
    LANTERN("lantern", BlockLantern.class, BlockLantern::new, ItemMaterial::new),
    MACHINE_ALPHA("machine_alpha", BlockMachine.class, () -> new BlockMachine<EnumMachineAlpha>(true), ItemMachine::new),
    MACHINE_BETA("machine_beta", BlockMachine.class, () -> new BlockMachine<EnumMachineBeta>(false), ItemMachine::new),
    MACHINE_EPSILON("machine_epsilon", BlockMachine.class, () -> new BlockMachine<EnumMachineEpsilon>(true), ItemMachine::new),
    MACHINE_ACTUATOR("actuator", BlockMachineActuator.class, BlockMachineActuator::new, ItemMachine::new),
    MACHINE_MANIPULATOR("manipulator", BlockMachineManipulator.class, BlockMachineManipulator::new, ItemMachine::new),
    MACHINE_SIMPLE("machine_simple", BlockMachineSimple.class, BlockMachineSimple::new, ItemMachine::new),
    ORE("ore", BlockOre.class, BlockOre::new, ItemBlockRailcraftSubtyped::new),
    ORE_MAGIC("ore_magic", BlockOreMagic.class, BlockOreMagic::new, ItemOreMagic::new),
    POST("post", BlockPost.class, BlockPost::new, ItemPost::new),
    POST_METAL("post_metal", BlockPostMetal.class, () -> new BlockPostMetal(false), ItemPostMetal::new),
    POST_METAL_PLATFORM("post_metal_platform", BlockPostMetal.class, () -> new BlockPostMetal(true), ItemPostMetal::new),
    RITUAL("ritual", BlockRitual.class, BlockRitual::new, null),
    WAY_OBJECT("wayobject", BlockWayObjectRailcraft.class, BlockWayObjectRailcraft::new, ItemWayObject::new),
    SLAB("slab", BlockRailcraftSlab.class, BlockRailcraftSlab::new, ItemSlab::new),
    STAIR("stair", BlockRailcraftStairs.class, BlockRailcraftStairs::new, ItemMaterial::new),
    TRACK_ELEVATOR("track_elevator", BlockTrackElevator.class, BlockTrackElevator::new, ItemBlockRailcraft::new),
    TRACK_FLEX_ABANDONED("track_flex_abandoned", BlockTrackFlexAbandoned.class, () -> new BlockTrackFlexAbandoned(TrackTypes.ABANDONED.getTrackType()), ItemTrack::new),
    TRACK_FLEX_ELECTRIC("track_flex_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.ELECTRIC.getTrackType()), ItemTrack::new),
    TRACK_FLEX_HIGH_SPEED("track_flex_high_speed", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.HIGH_SPEED.getTrackType()), ItemTrack::new),
    TRACK_FLEX_HS_ELECTRIC("track_flex_hs_electric", BlockTrackFlexElectric.class, () -> new BlockTrackFlexElectric(TrackTypes.HIGH_SPEED_ELECTRIC.getTrackType()), ItemTrack::new),
    TRACK_FLEX_REINFORCED("track_flex_reinforced", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.REINFORCED.getTrackType()), ItemTrack::new),
    TRACK_FLEX_STRAP_IRON("track_flex_strap_iron", BlockTrackFlex.class, () -> new BlockTrackFlex(TrackTypes.STRAP_IRON.getTrackType()), ItemTrack::new),
    TRACK_FORCE("track_force", BlockTrackForce.class, BlockTrackForce::new, ItemTrack::new),
    //    TRACK_JUNCTION_ELECTRIC("track_junction_electric", () -> new BlockTrackJunction(TrackTypes.ELECTRIC.getTrackType()), ItemTrack::new),
//    TRACK_JUNCTION_HIGH_SPEED("track_junction_high_speed", () -> new BlockTrackJunction(TrackTypes.HIGH_SPEED.getTrackType()), ItemTrack::new),
//    TRACK_JUNCTION_HS_ELECTRIC("track_junction_hs_electric", () -> new BlockTrackFlexElectric(TrackTypes.HIGH_SPEED_ELECTRIC.getTrackType()), ItemTrack::new),
//    TRACK_JUNCTION_IRON("track_junction_iron", () -> new BlockTrackJunction(TrackTypes.IRON.getTrackType()), ItemTrack::new),
//    TRACK_JUNCTION_REINFORCED("track_junction_reinforced", () -> new BlockTrackJunction(TrackTypes.REINFORCED.getTrackType()), ItemTrack::new),
//    TRACK_JUNCTION_STRAP_IRON("track_junction_strap_iron", () -> new BlockTrackJunction(TrackTypes.STRAP_IRON.getTrackType()), ItemTrack::new),
    TRACK_OUTFITTED("track_outfitted", BlockTrackOutfitted.class, BlockTrackOutfitted::new, ItemTrackOutfitted::new),
    WALL("wall", BlockRailcraftWall.class, BlockRailcraftWall::new, ItemMaterial::new),
    WIRE("wire", BlockWire.class, BlockWire::new, ItemBlockRailcraft::new),
    WORLD_LOGIC("worldlogic", BlockWorldLogic.class, BlockWorldLogic::new, ItemBlockRailcraft::new);
    public static final RailcraftBlocks[] VALUES = values();
    private final Supplier<Block> blockSupplier;
    private final Function<Block, ItemBlock> itemSupplier;
    private final Class<? extends Block> blockClass;
    private final Class<? extends IVariantEnum> variantClass;
    private final String tag;
    private final ResourceLocation registryName;
    protected Object altRecipeObject;
    private Block block;
    private ItemBlock item;

    RailcraftBlocks(String tag, Class<? extends Block> blockClass, Supplier<Block> blockSupplier, @Nullable Function<Block, ItemBlock> itemSupplier) {
        this.blockClass = blockClass;
        RailcraftBlockMetadata annotation = blockClass.getAnnotation(RailcraftBlockMetadata.class);
        this.variantClass = annotation != null ? annotation.variant() : null;
        this.blockSupplier = blockSupplier;
        this.itemSupplier = itemSupplier;
        this.tag = tag;
        this.registryName = new ResourceLocation(RailcraftConstants.RESOURCE_DOMAIN + ":" + getBaseTag());
    }

    public static void finalizeDefinitions() {
        for (RailcraftBlocks type : VALUES) {
            if (type.block != null)
                ((IRailcraftObject) type.block).finalizeDefinition();
            if (type.item != null)
                ((IRailcraftObject) type.item).finalizeDefinition();
        }
    }

    @Override
    public void register() {
        if (block != null)
            return;

        if (isEnabled()) {
            block = blockSupplier.get();
            block.setRegistryName(registryName);
            block.setUnlocalizedName("railcraft." + tag.replace("_", "."));

            if (itemSupplier != null) {
                item = itemSupplier.apply(block);
                item.setRegistryName(registryName);
            }

            RailcraftRegistry.register(block, item);

            if (!(block instanceof IRailcraftBlock))
                throw new RuntimeException("Railcraft Blocks must implement IRailcraftObject");
            IRailcraftBlock blockObject = (IRailcraftBlock) block;
            blockObject.initializeDefinintion();
            blockObject.defineRecipes();

            if (item != null) {
                if (!(item instanceof IRailcraftItemBlock))
                    throw new RuntimeException("Railcraft ItemBlocks must implement IRailcraftItemBlock");
                if (item instanceof IRailcraftItemSimple)
                    throw new RuntimeException("Railcraft ItemBlocks must not implement IRailcraftItemSimple");
                IRailcraftItemBlock itemObject = (IRailcraftItemBlock) item;
                itemObject.initializeDefinintion();
                itemObject.defineRecipes();
            }
        }
    }

    protected void setAltRecipeObject(Object obj) {
        altRecipeObject = obj;
    }

    @Override
    public boolean isEqual(@Nullable ItemStack stack) {
        return stack != null && block != null && InvTools.getBlockFromStack(stack) == block;
    }

    public boolean isEqual(IVariantEnum variant, @Nullable ItemStack stack) {
        return stack != null && block != null && InvTools.isItemEqual(getStack(variant));
    }

    public boolean isEqual(@Nullable Block block) {
        return block != null && this.block == block;
    }

    public boolean isEqual(IBlockState state) {
        return block != null && block == state.getBlock();
    }

    @Override
    @Nullable
    public Block block() {
        return block;
    }

    @Override
    @Nullable
    public IBlockState getDefaultState() {
        if (block == null)
            return null;
        return block.getDefaultState();
    }

    @Override
    @Nullable
    public IBlockState getState(@Nullable IVariantEnum variant) {
        if (block instanceof IRailcraftBlock)
            return ((IRailcraftBlock) block).getState(variant);
        return getDefaultState();
    }

    @Override
    @Nullable
    public ItemBlock item() {
        return item;
    }

    @Override
    public String getBaseTag() {
        return tag;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Nullable
    public Class<? extends IVariantEnum> getVariantClass() {
        return variantClass;
    }

    private void checkVariantObject(@Nullable IVariantEnum variant) {
        if (block != null)
            ((IRailcraftObject) block).checkVariant(variant);
    }

    @Nullable
    @Override
    public Object getRecipeObject(@Nullable IVariantEnum variant) {
        checkVariantObject(variant);
//        register(); Blocks are not created lazily like items.
        Object obj = null;
        if (block != null)
            obj = ((IRailcraftObject) block).getRecipeObject(variant);
        if (obj == null && variant != null)
            obj = variant.getAlternate(this);
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public Optional<IRailcraftBlock> getObject() {
        return Optional.ofNullable((IRailcraftBlock) block);
    }

    @Override
    public boolean isEnabled() {
        return RailcraftConfig.isBlockEnabled(tag);
    }

    @Override
    public boolean isLoaded() {
        return block != null;
    }

    @Override
    public String toString() {
        return "Block{" + tag + "}";
    }
}
