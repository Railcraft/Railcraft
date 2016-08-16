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
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.generic.ItemBlockGeneric;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.glass.ItemStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.BlockLantern;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftStairs;
import mods.railcraft.common.blocks.aesthetics.materials.BlockRailcraftWall;
import mods.railcraft.common.blocks.aesthetics.materials.ItemMaterial;
import mods.railcraft.common.blocks.aesthetics.materials.slab.BlockRailcraftSlab;
import mods.railcraft.common.blocks.aesthetics.materials.slab.ItemSlab;
import mods.railcraft.common.blocks.aesthetics.post.*;
import mods.railcraft.common.blocks.anvil.BlockRCAnvil;
import mods.railcraft.common.blocks.anvil.ItemAnvil;
import mods.railcraft.common.blocks.charge.BlockChargeFeeder;
import mods.railcraft.common.blocks.charge.BlockChargeTrap;
import mods.railcraft.common.blocks.charge.BlockFrame;
import mods.railcraft.common.blocks.charge.BlockWire;
import mods.railcraft.common.blocks.detector.BlockDetector;
import mods.railcraft.common.blocks.detector.EnumDetector;
import mods.railcraft.common.blocks.detector.ItemDetector;
import mods.railcraft.common.blocks.machine.BlockMachine;
import mods.railcraft.common.blocks.machine.ItemMachine;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.blocks.machine.gamma.EnumMachineGamma;
import mods.railcraft.common.blocks.ore.BlockOre;
import mods.railcraft.common.blocks.ore.BlockWorldLogic;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.blocks.ore.ItemOre;
import mods.railcraft.common.blocks.tracks.ItemTrack;
import mods.railcraft.common.blocks.tracks.behaivor.TrackTypes;
import mods.railcraft.common.blocks.tracks.elevator.BlockTrackElevator;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackElectric;
import mods.railcraft.common.blocks.tracks.flex.BlockTrackFlex;
import mods.railcraft.common.blocks.tracks.force.BlockTrackForce;
import mods.railcraft.common.blocks.tracks.kits.BlockTrackOutfitted;
import mods.railcraft.common.blocks.tracks.kits.ItemTrackOutfitted;
import mods.railcraft.common.blocks.wayobjects.BlockWayObjectRailcraft;
import mods.railcraft.common.blocks.wayobjects.EnumWayObject;
import mods.railcraft.common.blocks.wayobjects.ItemWayObject;
import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItem;
import mods.railcraft.common.items.firestone.BlockRitual;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by CovertJaguar on 4/13/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum RailcraftBlocks implements IRailcraftBlockContainer {
    ANVIL_STEEL("anvil", BlockRCAnvil::new, ItemAnvil::new),
    BRICK_ABYSSAL("brick.abyssal", () -> new BlockBrick(BrickTheme.ABYSSAL), ItemBrick::new),
    BRICK_BLEACHED_BONE("brick.bleachedbone", () -> new BlockBrick(BrickTheme.BLEACHEDBONE), ItemBrick::new),
    BRICK_BLOOD_STAINED("brick.bloodstained", () -> new BlockBrick(BrickTheme.BLOODSTAINED), ItemBrick::new),
    BRICK_FROST_BOUND("brick.frostbound", () -> new BlockBrick(BrickTheme.FROSTBOUND), ItemBrick::new),
    BRICK_INFERNAL("brick.infernal", () -> new BlockBrick(BrickTheme.INFERNAL), ItemBrick::new),
    BRICK_NETHER("brick.nether", () -> new BlockBrick(BrickTheme.NETHER), ItemBrick::new),
    BRICK_QUARRIED("brick.quarried", () -> new BlockBrick(BrickTheme.QUARRIED), ItemBrick::new),
    BRICK_SANDY("brick.sandy", () -> new BlockBrick(BrickTheme.SANDY), ItemBrick::new),
    CHARGE_FEEDER("charge.feeder", BlockChargeFeeder::new, ItemBlockRailcraftSubtyped::new, BlockChargeFeeder.FeederVariant.class),
    CHARGE_TRAP("charge.trap", BlockChargeTrap::new, ItemBlockRailcraft::new),
    GENERIC("generic", BlockGeneric::new, ItemBlockGeneric::new, EnumGeneric.class),
    DETECTOR("detector", BlockDetector::new, ItemDetector::new, EnumDetector.class),
    FRAME("frame", BlockFrame::new, ItemBlockRailcraft::new),
    GLASS("glass", BlockStrengthGlass::new, ItemStrengthGlass::new),
    LANTERN("lantern", BlockLantern::new, ItemMaterial::new),
    MACHINE_ALPHA("machine.alpha", () -> new BlockMachine<EnumMachineAlpha>(EnumMachineAlpha.PROXY, true), ItemMachine::new, EnumMachineAlpha.class),
    MACHINE_BETA("machine.beta", () -> new BlockMachine<EnumMachineBeta>(EnumMachineBeta.PROXY, false), ItemMachine::new, EnumMachineBeta.class),
    MACHINE_GAMMA("machine.gamma", () -> new BlockMachine<EnumMachineGamma>(EnumMachineGamma.PROXY, false), ItemMachine::new, EnumMachineGamma.class),
    MACHINE_DELTA("machine.delta", () -> new BlockMachine<EnumMachineDelta>(EnumMachineDelta.PROXY, false), ItemMachine::new, EnumMachineDelta.class),
    MACHINE_EPSILON("machine.epsilon", () -> new BlockMachine<EnumMachineEpsilon>(EnumMachineEpsilon.PROXY, true), ItemMachine::new, EnumMachineEpsilon.class),
    ORE("ore", BlockOre::new, ItemOre::new, EnumOre.class),
    POST("post", BlockPost::new, ItemPost::new, EnumPost.class),
    POST_METAL("post.metal", () -> new BlockPostMetal(false), ItemPostMetal::new),
    POST_METAL_PLATFORM("post.metal.platform", () -> new BlockPostMetal(true), ItemPostMetal::new),
    RITUAL("ritual", BlockRitual::new, null),
    WAY_OBJECT("wayobject", BlockWayObjectRailcraft::new, ItemWayObject::new, EnumWayObject.class),
    SLAB("slab", BlockRailcraftSlab::new, ItemSlab::new),
    STAIR("stair", BlockRailcraftStairs::new, ItemMaterial::new),
    @Deprecated
    TRACK("track", BlockTrackOutfitted::new, ItemTrackOutfitted::new),
    TRACK_ABANDONED("track.abandoned", () -> new BlockTrackFlex(TrackTypes.ABANDONED), ItemTrack::new),
    TRACK_ELECTRIC("track.electric", () -> new BlockTrackElectric(TrackTypes.ELECTRIC), ItemTrack::new),
    TRACK_ELEVATOR("track.elevator", BlockTrackElevator::new, ItemBlockRailcraft::new),
    TRACK_FORCE("track.force", BlockTrackForce::new, ItemTrack::new),
    TRACK_HIGH_SPEED("track.high.speed", () -> new BlockTrackFlex(TrackTypes.HIGH_SPEED), ItemTrack::new),
    TRACK_HIGH_SPEED_ELECTRIC("track.high.speed.electric", () -> new BlockTrackElectric(TrackTypes.HIGH_SPEED_ELECTRIC), ItemTrack::new),
    TRACK_OUTFITTED("track.outfitted", BlockTrackOutfitted::new, ItemTrackOutfitted::new),
    TRACK_REINFORCED("track.reinforced", () -> new BlockTrackFlex(TrackTypes.REINFORCED), ItemTrack::new),
    TRACK_STRAP_IRON("track.strap.iron", () -> new BlockTrackFlex(TrackTypes.STRAP_IRON), ItemTrack::new),
    WALL("wall", BlockRailcraftWall::new, ItemMaterial::new),
    WIRE("wire", BlockWire::new, ItemBlockRailcraft::new),
    WORLD_LOGIC("worldlogic", BlockWorldLogic::new, ItemBlockRailcraft::new);
    public static final RailcraftBlocks[] VALUES = values();
    private final Supplier<Block> blockSupplier;
    private final Function<Block, ItemBlock> itemSupplier;
    private final Class<? extends IVariantEnum> variantClass;
    private final String tag;
    protected Object altRecipeObject;
    private Block block;
    private ItemBlock item;

    RailcraftBlocks(String tag, Supplier<Block> blockSupplier, @Nullable Function<Block, ItemBlock> itemSupplier) {
        this(tag, blockSupplier, itemSupplier, null);
    }

    RailcraftBlocks(String tag, Supplier<Block> blockSupplier, @Nullable Function<Block, ItemBlock> itemSupplier, @Nullable Class<? extends IVariantEnum> variantClass) {
        this.blockSupplier = blockSupplier;
        this.itemSupplier = itemSupplier;
        this.tag = tag;
        this.variantClass = variantClass;
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
            block.setRegistryName(tag);
            block.setUnlocalizedName("railcraft." + tag);

            if (itemSupplier != null) {
                item = itemSupplier.apply(block);
                item.setRegistryName(tag);
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
                if (item instanceof IRailcraftItem)
                    throw new RuntimeException("Railcraft ItemBlocks must not implement IRailcraftItem");
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
            obj = variant.getAlternate(tag);
        if (obj == null)
            obj = altRecipeObject;
        if (obj instanceof ItemStack)
            obj = ((ItemStack) obj).copy();
        return obj;
    }

    @Override
    public IRailcraftBlock getObject() {
        return ((IRailcraftBlock) block);
    }

    @Override
    public boolean isEnabled() {
        return RailcraftConfig.isBlockEnabled(tag);
    }

    @Override
    public boolean isLoaded() {
        return block != null;
    }
}
