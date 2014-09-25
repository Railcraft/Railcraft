/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.tracks;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;

/**
 * Implementation of the iron ladder blocks. Iron ladders act much like normal
 * (wooden) ladders. Climbing down iron ladders is a bit faster than climbing
 * down normal ladders. Additionally, minecarts can run down vertically on iron
 * ladders, as if they were vertical rail tracks.
 *
 * @author DizzyDragon
 */
public class BlockTrackElevator extends Block {

    /**
     * The upward velocity of an entity climbing the ladder.
     */
    public static double CLIMB_UP_VELOCITY = 0.2;
    /**
     * The downward velocity of an entity climbing the ladder
     */
    public static double CLIMB_DOWN_VELOCITY = -0.3;
    /**
     * The inverse of the downward motion an entity gets within a single update
     * of the game engine due to gravity.
     */
    public static double FALL_DOWN_CORRECTION = 0.039999999105930328D;
    /**
     * Metadata values for the direction the ladder is facing.
     */
    public static final int FACING_EAST_METADATA_VALUE = 2,
            FACING_WEST_METADATA_VALUE = 3,
            FACING_NORTH_METADATA_VALUE = 4,
            FACING_SOUTH_METADATA_VALUE = 5;
    /**
     * Velocity at which a minecart travels up on the rail when activated
     */
    public static final double RIDE_UP_VELOCITY = +0.4;
    /**
     * Velocity at which a minecart travels down on the rail when not activated
     */
    public static final double RIDE_DOWN_VELOCITY = -0.4;
    /**
     * The bits of the metadata that are used for storing the direction of the
     * ladder. Use the bitwise-and operator (&) on the metadata to discard all
     * other data from a metadata value.
     */
    public static final int BLOCK_FACING_DATA_METADATA_MASK = 0x0007;
    private final int renderType;
    private IIcon[] texture;

    public BlockTrackElevator(int renderId) {
        super(new MaterialElevator());
//		  setBlockName(name);
        setHardness(1.05F);
        setStepSound(soundTypeMetal);
        this.renderType = renderId;

        setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 vec3d, Vec3 vec3d1) {
        setBlockBoundsBasedOnState(world, i, j, k);
        return super.collisionRayTrace(world, i, j, k, vec3d, vec3d1);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        int meta = getLadderFacingMetadata(world, i, j, k);
        float f = 0.125F;
        if (meta == 2)
            setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        if (meta == 3)
            setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        if (meta == 4)
            setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        if (meta == 5)
            setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
        setBlockBoundsBasedOnState(world, i, j, k);
        return AxisAlignedBB.getBoundingBox((double) i + minX, (double) j + minY, (double) k + minZ, (double) i + maxX, (double) j + maxY, (double) k + maxZ);
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isACube() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        boolean powered = (meta & 8) != 0;
        if (powered)
            return texture[0];
        return texture[1];
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        texture = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:tracks/track.elevator", 2);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        if (world.isSideSolid(i - 1, j, k, ForgeDirection.EAST))
            return true;
        if (world.isSideSolid(i + 1, j, k, ForgeDirection.WEST))
            return true;
        if (world.isSideSolid(i, j, k - 1, ForgeDirection.SOUTH))
            return true;
        return world.isSideSolid(i, j, k + 1, ForgeDirection.NORTH);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float par6, float par7, float par8, int meta) {
        if ((meta == 0 || side == 2) && world.isSideSolid(x, y, z + 1, ForgeDirection.NORTH))
            meta = 2;
        if ((meta == 0 || side == 3) && world.isSideSolid(x, y, z - 1, ForgeDirection.SOUTH))
            meta = 3;
        if ((meta == 0 || side == 4) && world.isSideSolid(x + 1, y, z, ForgeDirection.WEST))
            meta = 4;
        if ((meta == 0 || side == 5) && world.isSideSolid(x - 1, y, z, ForgeDirection.EAST))
            meta = 5;
        return meta;
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        setBlockBoundsBasedOnState(world, x, y, z);

        if (TrackTools.isRailBlockAt(world, x, y - 1, z)) {
            Block block = WorldPlugin.getBlock(world, x, y - 1, z);
            BlockRailBase railBlock = (BlockRailBase) block;
            if (railBlock.canMakeSlopes(world, x, y - 1, z)) {
                int trackMeta = railBlock.getBasicRailMetadata(world, null, x, y - 1, z);
                int ladderMeta = getLadderFacingMetadata(world, x, y, z);

                int outputMeta = 0;
                if (trackMeta == 0 && ladderMeta == 2)
                    outputMeta = 5;
                else if (trackMeta == 0 && ladderMeta == 3)
                    outputMeta = 4;
                else if (trackMeta == 1 && ladderMeta == 4)
                    outputMeta = 2;
                else if (trackMeta == 1 && ladderMeta == 5)
                    outputMeta = 3;
                if (outputMeta != 0) {
                    if (railBlock.isPowered())
                        outputMeta = outputMeta | (world.getBlockMetadata(x, y - 1, z) & 8);
                    world.setBlockMetadataWithNotify(x, y - 1, z, outputMeta, 3);
                }
            }
        }

        meta = world.getBlockMetadata(x, y, z);
        boolean powered = (meta & 8) != 0;
        if (powered ^ isPowered(world, x, y, z))
            world.setBlockMetadataWithNotify(x, y, z, meta ^ 8, 3);
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f, int i1) {
//        System.out.println("dropping item");
        super.dropBlockAsItemWithChance(world, i, j, k, l, f, i1);
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
        int meta = world.getBlockMetadata(i, j, k);
        int ladderMeta = getLadderFacingMetadata(world, i, j, k);
        boolean valid = false;
        if (ladderMeta == 2 && world.isSideSolid(i, j, k + 1, ForgeDirection.NORTH))
            valid = true;
        if (ladderMeta == 3 && world.isSideSolid(i, j, k - 1, ForgeDirection.SOUTH))
            valid = true;
        if (ladderMeta == 4 && world.isSideSolid(i + 1, j, k, ForgeDirection.WEST))
            valid = true;
        if (ladderMeta == 5 && world.isSideSolid(i - 1, j, k, ForgeDirection.EAST))
            valid = true;
        if (!valid) {
            dropBlockAsItem(world, i, j, k, ladderMeta, 0);
            world.setBlockToAir(i, j, k);
        } else {
            boolean powered = (meta & 8) != 0;
            if (powered ^ isPowered(world, i, j, k)) {
//					 System.out.println("Power change");
                world.setBlockMetadataWithNotify(i, j, k, meta ^ 8, 3);
                world.markBlockForUpdate(i, j, k);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        entity.fallDistance = 0;
        if (Game.isNotHost(world) || !(entity instanceof EntityMinecart))
            return;
        minecartInteraction(world, (EntityMinecart) entity, i, j, k);
    }

    public int getLadderFacingMetadata(IBlockAccess world, int i, int j, int k) {
        return world.getBlockMetadata(i, j, k) & BLOCK_FACING_DATA_METADATA_MASK;
    }

    public boolean getPoweredBit(World world, int i, int j, int k) {
        return (world.getBlockMetadata(i, j, k) & 8) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////
    // PROTECTED                                                                //
    ////////////////////////////////////////////////////////////////////////////
    protected boolean isPowered(World world, int x, int y, int z) {
        int meta = getLadderFacingMetadata(world, x, y, z);
        if (world.getBlock(x, y - 1, z) == this && meta == getLadderFacingMetadata(world, x, y - 1, z))
            if (PowerPlugin.isBlockBeingPowered(world, x, y - 1, z))
                return true;
        if (PowerPlugin.isBlockBeingPowered(world, x, y, z))
            return true;
        return world.getBlock(x, y + 1, z) == this && meta == getLadderFacingMetadata(world, x, y + 1, z) && isPowered(world, x, y + 1, z);
    }

    /**
     * Updates the state of a single minecart that is whithin the block's area
     * of effect according to the state of the block.
     *
     * @param world the world in which the block resides
     * @param i the x-coordinate of the block
     * @param j the y-coordinate of the block
     * @param k the z-coordinate of the block
     * @param cart the minecart for which the state will be updated. It is
     * assumed that the minecart is whithin the area of effect of the block
     */
    protected void minecartInteraction(World world, EntityMinecart cart, int i, int j, int k) {
        cart.getEntityData().setByte("elevator", (byte) 20);

        boolean powered = getPoweredBit(world, i, j, k);
        if (powered)
            if (world.getBlock(i, j + 1, k) == this || isOffloadRail(world, i, j + 1, k)) {
                boolean empty = true;
                for (EntityMinecart c : CartTools.getMinecartsAt(world, i, j + 1, k, 0.2f)) {
                    if (c != cart)
                        empty = false;
                }
                if ((getPoweredBit(world, i, j + 1, k) || isOffloadRail(world, i, j + 1, k)) && empty)
                    cart.motionY = RIDE_UP_VELOCITY + FALL_DOWN_CORRECTION;
                else
                    if (pushMinecartOntoRail(world, i, j, k, cart))
                        return;
                    else {
                        cart.setPosition(cart.posX, j + 0.5f, cart.posZ);
                        cart.motionY = FALL_DOWN_CORRECTION;
                    }
            } else
                cart.setPosition(cart.posX, j + 0.5f, cart.posZ);
        else
            if (world.getBlock(i, j - 1, k) != this) {
                pushMinecartOntoRail(world, i, j, k, cart);
                return;
            } else {
                boolean empty = true;
                for (EntityMinecart c : CartTools.getMinecartsAt(world, i, j - 1, k, 0.2f)) {
                    if (c != cart)
                        empty = false;
                }
                if (empty)
                    cart.motionY = RIDE_DOWN_VELOCITY + FALL_DOWN_CORRECTION;
                else {
                    cart.setPosition(cart.posX, j + 0.5f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                }
            }

        if (powered || !TrackTools.isRailBlockAt(world, i, j - 1, k)) {
            if (TrackTools.isRailBlockAt(world, i, j - 1, k) || TrackTools.isRailBlockAt(world, i, j - 2, k))
                cart.setCanUseRail(false);
            else
                cart.setCanUseRail(true);
            keepMinecartConnected(world, i, j, k, cart);
        } else
            cart.setCanUseRail(true);

//        RailcraftUtils.resetFallDistance(cart);
        if (powered)
            pushMinecartOnSupportingBlockIfPossible(world, i, j, k, cart);
    }

    /**
     * Adjusts the motion and rotationyaw of a minecart so that it stays in
     * position and alligned to the iron ladder.
     *
     * @param world the world in which the block resides
     * @param x the x-coordinate of the block
     * @param y the y-coordinate of the block
     * @param z the z-coordinate of the block
     * @param minecart the minecart for which motion and rotation will be
     * adjusted
     */
    protected void keepMinecartConnected(World world, int x, int y, int z, EntityMinecart minecart) {
        minecart.motionX = (x + 0.5) - minecart.posX;
        minecart.motionZ = (z + 0.5) - minecart.posZ;

        allignMinecart(world, x, y, z, minecart);
    }

    /**
     * Alligns the minecart to the ladder to the ladder
     *
     * @param world the world in which the block resides
     * @param x the x-coordinate of the block
     * @param y the y-coordinate of the block
     * @param z the z-coordinate of the block
     * @param minecart the minecart for which motion and rotation will be
     * adjusted
     */
    protected void allignMinecart(World world, int x, int y, int z, EntityMinecart minecart) {
        switch (getLadderFacingMetadata(world, x, y, z)) {
            case FACING_NORTH_METADATA_VALUE:
            case FACING_SOUTH_METADATA_VALUE:
                if (minecart.rotationYaw <= 90.0f || minecart.rotationYaw > 270.0f)
                    minecart.rotationYaw = 0.0f;
                else
                    minecart.rotationYaw = 180.0f;
                return;

            case FACING_EAST_METADATA_VALUE:
            case FACING_WEST_METADATA_VALUE:
                if (minecart.rotationYaw > 180.0f)
                    minecart.rotationYaw = 270.0f;
                else
                    minecart.rotationYaw = 90.0f;
        }
    }

    private boolean isOffloadRail(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z) != this)
            switch (world.getBlockMetadata(x, y - 1, z) & BLOCK_FACING_DATA_METADATA_MASK) {
                case FACING_EAST_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, x, y, z + 1))
                        return true;

                case FACING_WEST_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, x, y, z - 1))
                        return true;

                case FACING_NORTH_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, x + 1, y, z))
                        return true;

                case FACING_SOUTH_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, x - 1, y, z))
                        return true;

                default:
                    return false;
            }
        else
            return false;
    }

    /**
     * Pushes a minecart onto the block on which the ladder is placed if it is
     * possible. It is only possible to push the minecart if there is air
     * directly above the ladder block and if the block directly above the
     * supporting block is a rail.
     *
     * @param world the world in which the block resides
     * @param i the x-coordinate of the ladder block
     * @param j the y-coordinate of the ladder block
     * @param k the z-coordinate of the ladder block
     * @param minecart the minecart that is pushed which onto the block if
     * possible
     * @return true if the minecart can be pushed onto the supporting block,
     * otherwise false
     */
    private boolean pushMinecartOnSupportingBlockIfPossible(World world, int i, int j, int k, EntityMinecart minecart) {
        if (!world.getBlock(i, j, k).getMaterial().isSolid())
            switch (world.getBlockMetadata(i, j, k) & BLOCK_FACING_DATA_METADATA_MASK) {
                case FACING_EAST_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, i, j + 1, k + 1)) {
                        minecart.motionY = RIDE_UP_VELOCITY;
                        minecart.motionZ = RIDE_UP_VELOCITY;
                    }
                    return true;

                case FACING_WEST_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, i, j + 1, k - 1)) {
                        minecart.motionY = RIDE_UP_VELOCITY;
                        minecart.motionZ = -RIDE_UP_VELOCITY;
                    }
                    return true;

                case FACING_NORTH_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, i + 1, j + 1, k)) {
                        minecart.motionY = RIDE_UP_VELOCITY;
                        minecart.motionX = RIDE_UP_VELOCITY;
                    }
                    return true;

                case FACING_SOUTH_METADATA_VALUE:
                    if (TrackTools.isRailBlockAt(world, i - 1, j + 1, k)) {
                        minecart.motionY = RIDE_UP_VELOCITY;
                        minecart.motionX = -RIDE_UP_VELOCITY;
                    }
                    return true;

                default:
                    return false;
            }
        else
            return false;
    }

    private boolean pushMinecartOntoRail(World world, int i, int j, int k, EntityMinecart cart) {
        cart.setCanUseRail(true);
        switch (world.getBlockMetadata(i, j, k) & BLOCK_FACING_DATA_METADATA_MASK) {
            case FACING_EAST_METADATA_VALUE:
                if (TrackTools.isRailBlockAt(world, i, j, k - 1)) {
                    cart.setPosition(cart.posX, j + 0.6f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                    cart.motionZ = -RIDE_UP_VELOCITY;
                    return true;
                }
                break;
            case FACING_WEST_METADATA_VALUE:
                if (TrackTools.isRailBlockAt(world, i, j, k + 1)) {
                    cart.setPosition(cart.posX, j + 0.6f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                    cart.motionZ = RIDE_UP_VELOCITY;
                    return true;
                }
                break;
            case FACING_NORTH_METADATA_VALUE:
                if (TrackTools.isRailBlockAt(world, i - 1, j, k)) {
                    cart.setPosition(cart.posX, j + 0.6f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                    cart.motionX = -RIDE_UP_VELOCITY;
                    return true;
                }
                break;
            case FACING_SOUTH_METADATA_VALUE:
                if (TrackTools.isRailBlockAt(world, i + 1, j, k)) {
                    cart.setPosition(cart.posX, j + 0.6f, cart.posZ);
                    cart.motionY = FALL_DOWN_CORRECTION;
                    cart.motionX = RIDE_UP_VELOCITY;
                    return true;
                }
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

}
