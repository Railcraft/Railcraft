/*
 * Copyright (c) CovertJaguar, 2011-2017
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks.elevator;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.blocks.BlockRailcraft;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemRail;
import mods.railcraft.common.items.RailcraftItems;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Implementation of the iron ladder blocks. Iron ladders act much like normal
 * (wooden) ladders. Climbing down iron ladders is a bit faster than climbing
 * down normal ladders. Additionally, minecarts can run down vertically on iron
 * ladders, as if they were vertical rail tracks.
 *
 * @author DizzyDragon
 */
public class BlockTrackElevator extends BlockRailcraft {

    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private static final float OFFSET = 0.125F;
    private static final AxisAlignedBB[] BOUNDS = {
            new AxisAlignedBB(0.0F, 0.0F, 1.0F - OFFSET, 1.0F, 1.0F, 1.0F),

            new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, OFFSET),

            new AxisAlignedBB(1.0F - OFFSET, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F),

            new AxisAlignedBB(0.0F, 0.0F, 0.0F, OFFSET, 1.0F, 1.0F)
    };

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
     * Velocity at which a minecart travels up on the rail when activated
     */
    public static final double RIDE_UP_VELOCITY = +0.4;
    /**
     * Velocity at which a minecart travels down on the rail when not activated
     */
    public static final double RIDE_DOWN_VELOCITY = -0.4;

    public BlockTrackElevator() {
        super(new MaterialElevator());
        setHardness(1.05F);
        setSoundType(SoundType.METAL);
        setHarvestLevel("crowbar", 0);

        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this, 8),
                "IRI",
                "ISI",
                "IRI",
                'I', RailcraftConfig.vanillaTrackRecipes() ? "ingotGold" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.ADVANCED),
                'S', RailcraftConfig.vanillaTrackRecipes() ? "ingotIron" : RailcraftItems.RAIL.getRecipeObject(ItemRail.EnumRail.STANDARD),
                'R', "dustRedstone");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, POWERED, FACING);
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta & 0x7);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        IBlockState state = getDefaultState().withProperty(FACING, enumfacing);

        state.withProperty(POWERED, (meta & 0x8) > 0);

        return state;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(FACING).getIndex();
        if (state.getValue(POWERED))
            meta |= 0x8;
        return meta;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
        return getFacing(WorldPlugin.getBlockState(world, pos));
    }

    public EnumFacing getFacing(IBlockState state) {
        return state.getValue(FACING);
    }

    public boolean getPowered(IBlockAccess world, BlockPos pos) {
        return getPowered(WorldPlugin.getBlockState(world, pos));
    }

    public boolean getPowered(IBlockState state) {
        return state.getValue(POWERED);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState blockState, World world, BlockPos pos) {
        return AABBFactory.start().setBoundsFromBlock(blockState, world, pos).build();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = getFacing(source, pos);
        return BOUNDS[facing.ordinal() - 2];
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

//    @Override
//    public IIcon getIcon(int side, int meta) {
//        boolean powered = (meta & 8) != 0;
//        if (powered)
//            return texture[0];
//        return texture[1];
//    }
//
//    @Override
//    public void registerBlockIcons(IIconRegister iconRegister) {
//        texture = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:tracks/track.elevator", 2);
//    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if (isSideFacingSolid(world, pos, side))
                return true;
        }
        return false;
    }

    //TODO: Test, this is probably completely wrong
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
//        if ((meta == 0 || facing == 2) && worldIn.isSideSolid(x, y, z + 1, EnumFacing.NORTH))
//            meta = 2;
//        if ((meta == 0 || facing == 3) && worldIn.isSideSolid(x, y, z - 1, EnumFacing.SOUTH))
//            meta = 3;
//        if ((meta == 0 || facing == 4) && worldIn.isSideSolid(x + 1, y, z, EnumFacing.WEST))
//            meta = 4;
//        if ((meta == 0 || facing == 5) && worldIn.isSideSolid(x - 1, y, z, EnumFacing.EAST))
//            meta = 5;
        EnumFacing placement = null;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            if ((placement == null || facing == side) && isSideFacingSolid(worldIn, pos, side))
                placement = side;
        }
        assert placement != null;
        return getDefaultState().withProperty(FACING, placement);
    }

    private boolean isSideFacingSolid(World world, BlockPos pos, EnumFacing side) {
        return world.isSideSolid(pos.offset(side.getOpposite()), side);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        BlockPos down = pos.down();
        if (TrackTools.isRailBlockAt(world, down)) {
            Block block = WorldPlugin.getBlock(world, down);
            BlockRailBase railBlock = (BlockRailBase) block;
            if (railBlock.canMakeSlopes(world, down)) {
                BlockRailBase.EnumRailDirection trackMeta = TrackTools.getTrackDirection(world, down, (EntityMinecart) null);
                EnumFacing ladderFacing = getFacing(state);

                BlockRailBase.EnumRailDirection newTrackShape = null;
                if (trackMeta == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
                    if (ladderFacing == EnumFacing.NORTH)
                        newTrackShape = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
                    else if (ladderFacing == EnumFacing.SOUTH)
                        newTrackShape = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
                } else if (trackMeta == BlockRailBase.EnumRailDirection.EAST_WEST) {
                    if (ladderFacing == EnumFacing.EAST)
                        newTrackShape = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
                    else if (ladderFacing == EnumFacing.WEST)
                        newTrackShape = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
                }
                if (newTrackShape != null) {
                    TrackTools.setTrackDirection(world, down, newTrackShape);
                }
            }
        }

        boolean powered = getPowered(state);
        if (powered ^ isPowered(world, pos, state))
            WorldPlugin.setBlockState(world, pos, state.withProperty(POWERED, powered));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock) {
        super.neighborChanged(state, worldIn, pos, neighborBlock);
        EnumFacing facing = getFacing(state);
        boolean valid = false;

        if (isSideFacingSolid(worldIn, pos, facing))
            valid = true;

        if (!valid) {
            WorldPlugin.destroyBlock(worldIn, pos, true);
            return;
        }

        boolean powered = getPowered(state);
        if (powered != isPowered(worldIn, pos, state))
            WorldPlugin.setBlockState(worldIn, pos, state.withProperty(POWERED, !powered));
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.fallDistance = 0;
        if (Game.isClient(worldIn) || !(entityIn instanceof EntityMinecart))
            return;
        minecartInteraction(worldIn, (EntityMinecart) entityIn, pos);
    }

    ////////////////////////////////////////////////////////////////////////////
    // PROTECTED                                                                //
    ////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("SimplifiableIfStatement")
    protected boolean isPowered(World world, BlockPos pos, IBlockState state) {
        EnumFacing facing = getFacing(state);
        BlockPos posDown = pos.down();
        IBlockState stateDown = WorldPlugin.getBlockState(world, posDown);
        if (stateDown.getBlock() == this && facing == getFacing(stateDown) && PowerPlugin.isBlockBeingPowered(world, posDown))
            return true;
        if (PowerPlugin.isBlockBeingPowered(world, pos))
            return true;
        BlockPos posUp = pos.up();
        IBlockState stateUp = WorldPlugin.getBlockState(world, posUp);
        return stateUp.getBlock() == this && facing == getFacing(stateUp) && isPowered(world, posUp, stateUp);
    }

    /**
     * Updates the state of a single minecart that is within the block's area
     * of effect according to the state of the block.
     *
     * @param world the world in which the block resides
     * @param cart  the minecart for which the state will be updated. It is
     *              assumed that the minecart is whithin the area of effect of the block
     */
    protected void minecartInteraction(World world, EntityMinecart cart, BlockPos pos) {
        cart.getEntityData().setByte("elevator", (byte) 20);
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        boolean powered = getPowered(state);
        BlockPos posDown = pos.down();
        if (powered) {
            BlockPos posUp = pos.up();
            boolean nextIsOffload = isOffloadRail(world, posUp, state);
            if (nextIsOffload || WorldPlugin.isBlockAt(world, posUp, this)) {
                boolean empty = true;
                for (EntityMinecart c : CartToolsAPI.getMinecartsAt(world, posUp, 0.2f)) {
                    if (c != cart)
                        empty = false;
                }
                if ((nextIsOffload || getPowered(world, posUp)) && empty)
                    cart.motionY = RIDE_UP_VELOCITY + FALL_DOWN_CORRECTION;
                else if (pushMinecartOntoRail(world, pos, state, cart))
                    return;
                else {
                    cart.setPosition(cart.posX, pos.getY() + 0.5f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                }
            } else
                cart.setPosition(cart.posX, pos.getY() + 0.5f, cart.posZ);
        } else if (WorldPlugin.isBlockAt(world, posDown, this)) {
            pushMinecartOntoRail(world, pos, state, cart);
            return;
        } else {
            boolean empty = true;
            for (EntityMinecart c : CartToolsAPI.getMinecartsAt(world, posDown, 0.2f)) {
                if (c != cart)
                    empty = false;
            }
            if (empty)
                cart.motionY = RIDE_DOWN_VELOCITY + FALL_DOWN_CORRECTION;
            else {
                cart.setPosition(cart.posX, pos.getY() + 0.5f, cart.posZ);
                cart.motionY = FALL_DOWN_CORRECTION;
            }
        }

        if (powered || !TrackTools.isRailBlockAt(world, posDown)) {
            if (TrackTools.isRailBlockAt(world, posDown) || TrackTools.isRailBlockAt(world, pos.down(2)))
                cart.setCanUseRail(false);
            else
                cart.setCanUseRail(true);
            keepMinecartConnected(pos, state, cart);
        } else
            cart.setCanUseRail(true);

//        RailcraftUtils.resetFallDistance(cart);
        if (powered)
            pushMinecartOnSupportingBlockIfPossible(world, pos, state, cart);
    }

    /**
     * Adjusts the motion and rotation yaw of a minecart so that it stays in
     * position and aligned to the iron ladder.
     *
     * @param minecart the minecart for which motion and rotation will be
     *                 adjusted
     */
    protected void keepMinecartConnected(BlockPos pos, IBlockState state, EntityMinecart minecart) {
        minecart.motionX = (pos.getX() + 0.5) - minecart.posX;
        minecart.motionZ = (pos.getZ() + 0.5) - minecart.posZ;

        alignMinecart(state, minecart);
    }

    /**
     * Alligns the minecart to the ladder to the ladder
     *
     * @param minecart the minecart for which motion and rotation will be
     *                 adjusted
     */
    protected void alignMinecart(IBlockState state, EntityMinecart minecart) {
        if (getFacing(state).getAxis() == EnumFacing.Axis.X) {
            minecart.rotationYaw = minecart.rotationYaw <= 90.0F || minecart.rotationYaw > 270.0F ? 0.0F : 180.0F;
        } else {
            minecart.rotationYaw = minecart.rotationYaw > 180.0F ? 270.0F : 90F;
        }
    }

    private boolean isOffloadRail(World world, BlockPos nextPos, IBlockState state) {
        if (WorldPlugin.isBlockAir(world, nextPos)) {
            EnumFacing lastElevatorFacing = getFacing(state);
            return TrackTools.isRailBlockAt(world, nextPos.offset(lastElevatorFacing.getOpposite()));
        }
        return false;
    }

    /**
     * Pushes a minecart onto the block on which the ladder is placed if it is
     * possible. It is only possible to push the minecart if there is air
     * directly above the ladder block and if the block directly above the
     * supporting block is a rail.
     *
     * @param world the world in which the block resides
     * @param cart  the minecart that is pushed which onto the block if
     *              possible
     * @return true if the minecart can be pushed onto the supporting block,
     * otherwise false
     */
    //TODO: test
    private boolean pushMinecartOnSupportingBlockIfPossible(World world, BlockPos pos, IBlockState state, EntityMinecart cart) {
        if (!state.getMaterial().isSolid()) {
            EnumFacing facing = getFacing(state);
            if (TrackTools.isRailBlockAt(world, pos.up().offset(facing.getOpposite()))) {
                cart.motionY = RIDE_UP_VELOCITY;
                double vel = facing.getAxisDirection().getOffset() * RIDE_UP_VELOCITY;
                if (facing.getAxis() == EnumFacing.Axis.Z)
                    cart.motionZ = vel;
                else
                    cart.motionX = vel;
            }
            return true;
        }
        return false;
    }

    /**
     * Pushes a Minecart onto a Railcraft block opposite the elevator if possible.
     */
    //TODO: test
    private boolean pushMinecartOntoRail(World world, BlockPos pos, IBlockState state, EntityMinecart cart) {
        cart.setCanUseRail(true);
        EnumFacing facing = getFacing(state);
        if (TrackTools.isRailBlockAt(world, pos.offset(facing))) {
            cart.setPosition(cart.posX, pos.getY() + 0.6f, cart.posZ);
            cart.motionY = FALL_DOWN_CORRECTION;
            double vel = facing.getAxisDirection().getOffset() * RIDE_UP_VELOCITY;
            if (facing.getAxis() == EnumFacing.Axis.Z)
                cart.motionZ = vel;
            else
                cart.motionX = vel;
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }
}
