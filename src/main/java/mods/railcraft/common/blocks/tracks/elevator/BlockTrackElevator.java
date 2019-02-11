/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.elevator;

import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of the iron ladder blocks. Iron ladders act much like normal
 * (wooden) ladders. Climbing down iron ladders is a bit faster than climbing
 * down normal ladders. Additionally, minecarts can run down vertically on iron
 * ladders, as if they were vertical rail tracks.
 *
 * @author DizzyDragon
 */
public class BlockTrackElevator extends BlockRailcraft {
    public static final byte ELEVATOR_TIMER = 20;
    public static final PropertyEnum<EnumFacing.Axis> ROTATION = PropertyEnum.create("rotation", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private static final float OFFSET = 0.125F;
    private static final AxisAlignedBB X_BOUNDS = AABBFactory.start().box().expandXAxis(-2.0 / 16.0).expandZAxis(0.5 / 16.0).build();
    private static final AxisAlignedBB Z_BOUNDS = AABBFactory.start().box().expandZAxis(-2.0 / 16.0).expandXAxis(0.5 / 16.0).build();

//    /**
//     * The upward velocity of an entity climbing the ladder.
//     */
//    public static double CLIMB_UP_VELOCITY = 0.2;
//    /**
//     * The downward velocity of an entity climbing the ladder
//     */
//    public static double CLIMB_DOWN_VELOCITY = -0.3;
    /**
     * The inverse of the downward motion an entity gets within a single update
     * of the game engine due to gravity.
     */
    public static final double FALL_DOWN_CORRECTION = 0.039999999105930328D;
    /**
     * Velocity at which a minecart travels up on the rail
     */
    public static final double RIDE_VELOCITY = 0.4;

    public BlockTrackElevator() {
        super(new MaterialElevator());
        setHardness(1.05F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("crowbar", 0);

        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setDefaultState(blockState.getBaseState().withProperty(ROTATION, EnumFacing.Axis.X).withProperty(POWERED, false));
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this, 8),
                "IRI",
                "ISI",
                "IRI",
                'I', RailcraftConfig.vanillaTrackRecipes() ? "ingotGold" : RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.ADVANCED),
                'S', RailcraftConfig.vanillaTrackRecipes() ? "ingotIron" : RailcraftItems.RAIL.getIngredient(ItemRail.EnumRail.STANDARD),
                'R', "dustRedstone");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED, ROTATION);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(ROTATION, state.getValue(ROTATION) == EnumFacing.Axis.Z ? EnumFacing.Axis.X : EnumFacing.Axis.Z);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta) {
        int axis = meta & 0x7;

        if (axis != 0 && axis != 2) {
            axis = 0;
        }

        IBlockState state = getDefaultState().withProperty(ROTATION, EnumFacing.Axis.values()[axis]);

        state = state.withProperty(POWERED, (meta & 0x8) > 0);

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(ROTATION).ordinal();
        if (state.getValue(POWERED))
            meta |= 0x8;
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public EnumFacing.Axis getAxis(IBlockAccess world, BlockPos pos) {
        return getAxis(WorldPlugin.getBlockState(world, pos));
    }

    public EnumFacing.Axis getAxis(IBlockState state) {
        return state.getValue(ROTATION);
    }

    public boolean getPowered(IBlockAccess world, BlockPos pos) {
        return getPowered(WorldPlugin.getBlockState(world, pos));
    }

    public boolean getPowered(IBlockState state) {
        return state.getValue(POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @Nullable AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing.Axis axis = getAxis(state);
        if (axis == EnumFacing.Axis.X) {
            return X_BOUNDS;
        } else {
            return Z_BOUNDS;
        }
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing.Axis axis = facing.getAxis();
        if (axis.isVertical()) {
            IBlockState state = WorldPlugin.getBlockState(worldIn, pos.offset(facing.getOpposite()));
            if (state.getBlock() == this) {
                axis = ((BlockTrackElevator) state.getBlock()).getAxis(state);
            } else {
                axis = placer.getHorizontalFacing().getAxis();
            }
        }
        return getDefaultState().withProperty(ROTATION, axis);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        boolean powered = getPowered(state);
        if (powered != isPowered(world, pos, state))
            WorldPlugin.setBlockState(world, pos, state.withProperty(POWERED, !powered));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos neighborPos) {
        super.neighborChanged(state, worldIn, pos, neighborBlock, neighborPos);
        boolean powered = getPowered(state);
        if (powered != isPowered(worldIn, pos, state))
            WorldPlugin.setBlockState(worldIn, pos, state.withProperty(POWERED, !powered));
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.fallDistance = 0;
        if (Game.isClient(worldIn) || !(entityIn instanceof EntityMinecart))
            return;
        minecartInteraction(worldIn, (EntityMinecart) entityIn, pos);
    }

    ////////////////////////////////////////////////////////////////////////////
    // PROTECTED                                                                //
    ////////////////////////////////////////////////////////////////////////////
    protected boolean isPowered(World world, BlockPos pos, IBlockState state) {
        BlockPos posUp = pos.up();
        IBlockState stateUp = WorldPlugin.getBlockState(world, posUp);
        return PowerPlugin.isBlockBeingPowered(world, pos) || stateUp.getBlock() == this && isPowered(world, posUp, stateUp);
    }

    /**
     * Updates the state of a single minecart that is within the block's area
     * of effect according to the state of the block.
     *
     * @param world the world in which the block resides
     * @param cart  the minecart for which the state will be updated. It is
     *              assumed that the minecart is within the area of effect of the block
     */
    protected void minecartInteraction(World world, EntityMinecart cart, BlockPos pos) {
        cart.getEntityData().setByte("elevator", ELEVATOR_TIMER);
        cart.setNoGravity(true);
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        keepMinecartConnected(pos, state, cart);
        boolean hasPath;
        boolean up = getPowered(state);
        if (up) {
            hasPath = moveUp(world, state, cart, pos);
        } else {
            hasPath = moveDown(world, state, cart, pos);
        }
        if (!hasPath) {
            pushMinecartOntoRail(world, pos, state, cart, up);
        }
    }

    private boolean moveUp(World world, IBlockState state, EntityMinecart cart, BlockPos pos) {
        BlockPos posUp = pos.up();
        boolean hasPath = WorldPlugin.isBlockAt(world, posUp, this) && getPowered(world, posUp);
        if (hasPath) {
            if (isPathEmpty(state, cart, posUp, true))
                cart.motionY = RIDE_VELOCITY;
            else holdPosition(state, cart, pos);
            return true;
        }
        return false;
    }

    private boolean moveDown(World world, IBlockState state, EntityMinecart cart, BlockPos pos) {
        BlockPos posDown = pos.down();
        boolean hasPath = WorldPlugin.isBlockAt(world, posDown, this) && !getPowered(world, posDown);
        if (hasPath) {
            if (isPathEmpty(state, cart, posDown, false))
                cart.motionY = -RIDE_VELOCITY;
            else holdPosition(state, cart, pos);
            return true;
        }
        return false;
    }

    private void holdPosition(IBlockState state, EntityMinecart cart, BlockPos pos) {
        cart.setLocationAndAngles(cart.posX, pos.getY() - cart.height / 2.0 + 0.5, cart.posZ, getCartRotation(state, cart), 0F);
        cart.motionY = 0;
    }

    /**
     * Adjusts the motion and rotation yaw of a minecart so that it stays in
     * position and aligned to the iron ladder.
     *
     * @param cart the minecart for which motion and rotation will be
     *             adjusted
     */
    protected void keepMinecartConnected(BlockPos pos, IBlockState state, EntityMinecart cart) {
        if (TrackTools.isRailBlockAt(cart.world, pos.down()) || TrackTools.isRailBlockAt(cart.world, pos.down(2)))
            cart.setCanUseRail(false);
        else
            cart.setCanUseRail(true);
        cart.motionX = (pos.getX() + 0.5) - cart.posX;
        cart.motionZ = (pos.getZ() + 0.5) - cart.posZ;

        alignMinecart(state, cart);
    }

    /**
     * Aligns the minecart to the ladder
     *
     * @param cart the minecart for which rotation will be adjusted
     */
    protected void alignMinecart(IBlockState state, EntityMinecart cart) {
        cart.rotationYaw = getCartRotation(state, cart);
    }

    private float getCartRotation(IBlockState state, EntityMinecart cart) {
        if (getAxis(state) == EnumFacing.Axis.X) {
            return cart.rotationYaw <= 90.0F || cart.rotationYaw > 270.0F ? 0.0F : 180.0F;
        } else {
            return cart.rotationYaw > 180.0F ? 270.0F : 90F;
        }
    }

    private boolean isPathEmpty(IBlockState state, EntityMinecart cart, BlockPos pos, boolean up) {
        if (WorldPlugin.getBlockMaterial(cart.world, pos).isSolid())
            return false;
        EnumFacing.Axis axis = getAxis(state);
        AABBFactory factory = AABBFactory.start().createBoxForTileAt(pos).expandAxis(axis, 1.0);
        if (up) {
            factory.raiseCeiling(0.5);
            factory.raiseFloor(0.2);
        } else {
            factory.raiseCeiling(-0.2);
            factory.raiseFloor(-0.5);
        }
        return EntitySearcher.findMinecarts().around(factory.build()).except(cart).in(cart.world).isEmpty();
    }

    /**
     * Pushes a Minecart onto a Railcraft block opposite the elevator if possible.
     */
    private boolean pushMinecartOntoRail(World world, BlockPos pos, IBlockState state, EntityMinecart cart, boolean up) {
        cart.setCanUseRail(true);
        EnumFacing.Axis axis = getAxis(state);
        for (BlockPos target : new BlockPos[]{pos, up ? pos.up() : pos.down()}) {
            for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values()) {
                if (TrackTools.isRailBlockAt(world, target.offset(EnumFacing.getFacingFromAxis(direction, axis)))) {
                    holdPosition(state, cart, target);
                    double vel = direction.getOffset() * RIDE_VELOCITY;
                    if (axis == EnumFacing.Axis.Z)
                        cart.motionZ = vel;
                    else
                        cart.motionX = vel;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
