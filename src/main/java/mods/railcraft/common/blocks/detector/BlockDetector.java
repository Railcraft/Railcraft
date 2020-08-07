/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.detector;

import mods.railcraft.common.blocks.BlockContainerRailcraftSubtyped;
import mods.railcraft.common.blocks.BlockMeta;
import mods.railcraft.common.blocks.aesthetics.brick.BrickTheme;
import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.EnumFacing.NORTH;

@BlockMeta.Tile(TileDetector.class)
@BlockMeta.Variant(EnumDetector.class)
public class BlockDetector extends BlockContainerRailcraftSubtyped<TileDetector, EnumDetector> {

    public static final PropertyEnum<EnumFacing> FRONT = PropertyEnum.create("front", EnumFacing.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    @SuppressWarnings("WeakerAccess")
    public BlockDetector() {
        super(Material.ROCK);
        setResistance(4.5F);
        setHardness(2.0F);
        setSoundType(SoundType.STONE);
        setDefaultState(blockState.getBaseState().withProperty(FRONT, NORTH).withProperty(POWERED, false).withProperty(getVariantEnumProperty(), EnumDetector.ANY));

        setCreativeTab(CreativeTabs.TRANSPORTATION);

        RailcraftRegistry.register(TileDetector.class, "detector", "RCDetectorTile");
    }

    @Override
    public void initializeDefinition() {
        //            HarvestPlugin.setStateHarvestLevel(block, "pickaxe", 2);
        HarvestPlugin.setBlockHarvestLevel("crowbar", 0, this);

        for (EnumDetector d : EnumDetector.VALUES) {
            ItemStack stack = new ItemStack(this, 1, d.ordinal());
            RailcraftRegistry.register(this, d, stack);
        }
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.ITEM.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "plankWood",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.ANY.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "stone",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.EMPTY.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONEBRICK, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.MOB.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONEBRICK, 1, 1),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.MOB.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Blocks.MOSSY_COBBLESTONE,
                'P', Blocks.STONE_PRESSURE_PLATE);
//        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.POWERED.ordinal()),
//                "XXX",
//                "XPX",
//                "XXX",
//                'X', "cobblestone",
//                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.PLAYER.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.STONE_SLAB, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
//        CraftingPlugin.addRecipe(new ItemStack(this, 1, EnumDetector.EXPLOSIVE.ordinal()),
//                "XXX",
//                "XPX",
//                "XXX",
//                'X', "slabWood",
//                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.ANIMAL.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.LOG, 1, 0),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.AGE.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', new ItemStack(Blocks.LOG, 1, 1),
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.ADVANCED.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "ingotSteel",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.TANK.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', "ingotBrick",
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.SHEEP.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Blocks.WOOL,
                'P', Blocks.STONE_PRESSURE_PLATE);
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.VILLAGER.ordinal()),
                "XXX",
                "XPX",
                "XXX",
                'X', Items.LEATHER,
                'P', Blocks.STONE_PRESSURE_PLATE);

        if (BrickTheme.INFERNAL.isLoaded())
            CraftingPlugin.addShapedRecipe(new ItemStack(this, 1, EnumDetector.LOCOMOTIVE.ordinal()),
                    "XXX",
                    "XPX",
                    "XXX",
                    'X', BrickTheme.INFERNAL.getStack(1, BrickVariant.PAVER),
                    'P', Blocks.STONE_PRESSURE_PLATE);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FRONT, EnumFacing.byIndex(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FRONT).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getVariantEnumProperty(), FRONT, POWERED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState newState = super.getActualState(state, worldIn, pos);
        return getTileEntity(state, worldIn, pos)
                .map(t -> newState
                        .withProperty(getVariantEnumProperty(), t.getDetector().getType())
                        .withProperty(POWERED, t.getPowerState() > PowerPlugin.NO_POWER))
                .orElse(newState);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return getTileEntity(state, world, pos)
                .map(t -> t.getDetector().getType().getStack())
                .orElseGet(() -> super.getPickBlock(state, target, world, pos, player));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        getTileEntity(state, world, pos)
                .ifPresent(t -> drops.add(t.getDetector().getType().getStack()));
    }

    //TODO: Move drop code here? We have a reference to the TileEntity now.
    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        //noinspection ConstantConditions
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        getTileEntity(state, world, pos).ifPresent(t -> t.getDetector().onBlockRemoved());
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, pos, WorldPlugin.getBlockState(world, pos), 0);
        return world.setBlockToAir(pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        setFront(worldIn, pos, MiscTools.getSideFacingPlayer(pos, placer));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (PlayerPlugin.doesItemBlockActivation(player, hand))
            return false;
        return getTileEntity(state, worldIn, pos)
                .map(t -> t.blockActivated(player))
                .orElse(false);
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        if (byIndex(world, pos) == axis)
            setFront(world, pos, axis.getOpposite());
        else
            setFront(world, pos, axis);
        return true;
    }

    public EnumFacing byIndex(World world, BlockPos pos) {
        return WorldPlugin.getBlockState(world, pos).getValue(FRONT);
    }

    public void setFront(World world, BlockPos pos, EnumFacing front) {
        WorldPlugin.setBlockState(world, pos, getDefaultState().withProperty(FRONT, front));
    }

    @Override
    public EnumFacing[] getValidRotations(World world, BlockPos pos) {
        return EnumFacing.VALUES;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getBlockHardness(IBlockState state, World worldIn, BlockPos pos) {
        return getTileEntity(state, worldIn, pos)
                .map(t -> t.getDetector().getHardness())
                .orElseGet(() -> super.getBlockHardness(state, worldIn, pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the
     * specified side. If isBlockNormalCube returns true, standard redstone
     * propagation rules will apply instead and this will not be called. Args:
     * World, X, Y, Z, side. Note that the side is reversed - eg it is 1 (up)
     * when checking the bottom of the block.
     */
    @SuppressWarnings("deprecation")
    @Override
    public int getWeakPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        if (state.getValue(FRONT) != side.getOpposite())
            return PowerPlugin.NO_POWER;
        return getTileEntity(state, worldIn, pos)
                .map(TileDetector::getPowerState)
                .orElse(PowerPlugin.NO_POWER);
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the
     * specified side. Args: World, X, Y, Z, side. Note that the side is
     * reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @SuppressWarnings("deprecation")
    @Override
    public int getStrongPower(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return getWeakPower(state, worldIn, pos, side);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        markBlockForUpdate(state, worldIn, pos);
        if (Game.isClient(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), true);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        if (Game.isClient(worldIn))
            return;
        for (EnumFacing side : EnumFacing.VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), true);
        }
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        EnumFacing front = state.getValue(FRONT);
        return side == front.getOpposite();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumDetector detector : EnumDetector.VALUES) {
            if (detector.isEnabled())
                CreativePlugin.addToList(list, detector.getStack());
        }
    }
}
