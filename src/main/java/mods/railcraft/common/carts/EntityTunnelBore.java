/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.carts.bore.IBoreHead;
import mods.railcraft.api.carts.bore.IMineable;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.Train.TrainState;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.collections.BlockSet;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StandardStackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.*;
import java.util.function.BiFunction;

public class EntityTunnelBore extends CartContainerBase implements ILinkableCart {
    public static final float SPEED = 0.03F;
    public static final float LENGTH = 6.2f;
    public static final float WIDTH = 2.7f;
    public static final float HEIGHT = 4f;
    public static final int MAX_FILL_DEPTH = 10;
    public static final int FAIL_DELAY = 200;
    public static final int STANDARD_DELAY = 5;
    public static final int LAYER_DELAY = 40;
    public static final int BALLAST_DELAY = 10;
    public static final int FUEL_CONSUMPTION = 12;
    public static final float HARDNESS_MULTIPLIER = 8;
    public static final BlockSet mineableBlocks = new BlockSet();
    public static final Set<Block> replaceableBlocks = new HashSet<Block>();
    protected static final int WATCHER_ID_FUEL = 16;
    protected static final int WATCHER_ID_MOVING = 25;
    protected static final int WATCHER_ID_BORE_HEAD = 26;
    protected static final int WATCHER_ID_FACING = 5;
    private static final Block[] mineable = {
            Blocks.clay,
            Blocks.snow_layer,
            Blocks.cactus,
            Blocks.carrots,
            Blocks.cobblestone,
            Blocks.mossy_cobblestone,
            Blocks.cocoa,
            Blocks.wheat,
            Blocks.deadbush,
            Blocks.dirt,
            Blocks.fire,
            Blocks.glowstone,
            Blocks.grass,
            Blocks.gravel,
            Blocks.ice,
            Blocks.leaves,
            Blocks.melon_block,
            Blocks.melon_stem,
            Blocks.brown_mushroom,
            Blocks.brown_mushroom_block,
            Blocks.red_mushroom,
            Blocks.red_mushroom_block,
            Blocks.mycelium,
            Blocks.nether_wart,
            Blocks.netherrack,
            Blocks.obsidian,
            Blocks.coal_ore,
            Blocks.diamond_ore,
            Blocks.emerald_ore,
            Blocks.gold_ore,
            Blocks.iron_ore,
            Blocks.lapis_ore,
            Blocks.redstone_ore,
            Blocks.lit_redstone_ore,
            Blocks.red_flower,
            Blocks.yellow_flower,
            Blocks.potatoes,
            Blocks.pumpkin,
            Blocks.pumpkin_stem,
            Blocks.reeds,
            Blocks.sand,
            Blocks.sandstone,
            Blocks.sapling,
            Blocks.soul_sand,
            Blocks.snow,
            Blocks.stone,
            Blocks.tallgrass,
            Blocks.farmland,
            Blocks.torch,
            Blocks.vine,
            Blocks.waterlily,
            Blocks.web,
            Blocks.end_stone,
            Blocks.log,
            Blocks.log2,};
    private static final Block[] replaceable = {
            Blocks.torch,
            Blocks.tallgrass,
            Blocks.deadbush,
            Blocks.vine,
            Blocks.brown_mushroom,
            Blocks.red_mushroom,
            Blocks.yellow_flower,
            Blocks.red_flower,
            Blocks.double_plant};

    static {
        for (Block block : mineable) {
            addMineableBlock(block);
        }
        replaceableBlocks.addAll(Arrays.asList(replaceable));
    }

    public final InventoryMapper invFuel = new InventoryMapper(this, 1, 6);
    public final InventoryMapper invBallast = new InventoryMapper(this, 7, 9);
    public final InventoryMapper invRails = new InventoryMapper(this, 16, 9);
    //    protected static final int WATCHER_ID_BURN_TIME = 22;
    protected int delay;
    protected boolean placeRail;
    protected boolean placeBallast;
    protected boolean boreLayer;
    protected int boreRotationAngle;
    private boolean active;
    private int clock = MiscTools.RANDOM.nextInt();
    private int burnTime;
    private int fuel;
    private final boolean hasInit;
    private final EntityTunnelBorePart[] partArray;

    public EntityTunnelBore(World world, double i, double j, double k) {
        this(world, i, j, k, EnumFacing.SOUTH);
    }

    public EntityTunnelBore(World world, double i, double j, double k, EnumFacing f) {
        super(world);
        partArray = new EntityTunnelBorePart[]{
                // ------------------------------------- name, width, height, forwardOffset, sideOffset
                new EntityTunnelBorePart(this, "head1", 1.9F, 2.6F, 2F, -0.6F),
                new EntityTunnelBorePart(this, "head2", 1.9F, 2.6F, 2F, 0.6F),
                new EntityTunnelBorePart(this, "body", 2.0F, 1.9F, 0.6F),
                new EntityTunnelBorePart(this, "tail1", 1.6F, 1.4F, -1F),
                new EntityTunnelBorePart(this, "tail2", 1.6F, 1.4F, -2.2F),
        };
        hasInit = true;
        setPosition(i, j + getYOffset(), k);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = i;
        prevPosY = j;
        prevPosZ = k;
//        cargoItems = new ItemStack[25];
        setFacing(f);
        setSize(LENGTH, HEIGHT);
    }

    public EntityTunnelBore(World world) {
        this(world, 0, 0, 0, EnumFacing.SOUTH);
    }

    public static void addMineableBlock(Block block) {
        addMineableBlock(block.getDefaultState());
    }

    public static void addMineableBlock(IBlockState blockState) {
        mineableBlocks.add(blockState);
    }

    public static boolean canHeadHarvestBlock(ItemStack head, IBlockState targetState) {
        if (head == null)
            return false;

        if (head.getItem() instanceof IBoreHead) {
            IBoreHead boreHead = (IBoreHead) head.getItem();

            boolean mappingExists = false;

            int blockHarvestLevel = HarvestPlugin.getHarvestLevel(targetState, "pickaxe");
            if (blockHarvestLevel > -1) {
                if (boreHead.getHarvestLevel() >= blockHarvestLevel)
                    return true;
                mappingExists = true;
            }

            blockHarvestLevel = HarvestPlugin.getHarvestLevel(targetState, "axe");
            if (blockHarvestLevel > -1) {
                if (boreHead.getHarvestLevel() >= blockHarvestLevel)
                    return true;
                mappingExists = true;
            }

            blockHarvestLevel = HarvestPlugin.getHarvestLevel(targetState, "shovel");
            if (blockHarvestLevel > -1) {
                if (boreHead.getHarvestLevel() >= blockHarvestLevel)
                    return true;
                mappingExists = true;
            }

            if (mappingExists)
                return false;
        }

        return true;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.BORE;
    }

    private boolean isMineableBlock(IBlockState blockState) {
        return RailcraftConfig.boreMinesAllBlocks() || mineableBlocks.contains(blockState);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(WATCHER_ID_FUEL, (byte) 0);
        dataWatcher.addObject(WATCHER_ID_MOVING, (byte) 0);
        dataWatcher.addObjectByDataType(WATCHER_ID_BORE_HEAD, 5);
        dataWatcher.addObject(WATCHER_ID_FACING, (byte) 0);
//        dataWatcher.addObject(WATCHER_ID_BURN_TIME, Integer.valueOf(0));
    }

    public boolean isMinecartPowered() {
        return dataWatcher.getWatchableObjectByte(WATCHER_ID_FUEL) != 0;
    }

    public void setMinecartPowered(boolean powered) {
        dataWatcher.updateObject(WATCHER_ID_FUEL, (byte) (powered ? 1 : 0));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!worldObj.isRemote && !isDead)
            if (isEntityInvulnerable(source))
                return false;
            else {
                setRollingDirection(-getRollingDirection());
                setRollingAmplitude(10);
                setBeenAttacked();
                setDamage(getDamage() + damage * 10);
                boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

                if (flag || getDamage() > 120) {
                    if (riddenByEntity != null)
                        riddenByEntity.mountEntity(this);

                    if (flag && !hasCustomName())
                        setDead();
                    else
                        killMinecart(source);
                }

                return true;
            }
        else
            return true;
    }

    private void setYaw() {
        float yaw = 0;
        switch (getFacing()) {
            case NORTH:
                yaw = 90;
                break;
            case EAST:
                yaw = 0;
                break;
            case SOUTH:
                yaw = 270;
                break;
            case WEST:
                yaw = 180;
                break;
        }
        setRotation(yaw, rotationPitch);
    }

    @Override
    public int getSizeInventory() {
        return 25;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        if (!hasInit) {
            super.setPosition(x, y, z);
            return;
        }

        posX = x;
        posY = y;
        posZ = z;
        double halfWidth = WIDTH / 2.0;
        double height = this.height;
        double len = LENGTH / 2.0;
        double minX = x;
        double maxX = x;
        double minZ = z;
        double maxZ = z;
        if (getFacing() == EnumFacing.WEST || getFacing() == EnumFacing.EAST) {
            minX -= len;
            maxX += len;
            minZ -= halfWidth;
            maxZ += halfWidth;
        } else {
            minX -= halfWidth;
            maxX += halfWidth;
            minZ -= len;
            maxZ += len;
        }

        // TODO: Test this!
        setEntityBoundingBox(new AxisAlignedBB(minX, y, minZ, maxX, y + height, maxZ));
    }

    @Override
    public void onUpdate() {
        clock++;

        if (Game.isHost(worldObj)) {
            if (clock % 64 == 0) {
                forceUpdateBoreHead();
                setMinecartPowered(false);
                setMoving(false);
            }

            stockBallast();
            stockTracks();
        }

        super.onUpdate();

        for (Entity part : partArray) {
            part.onUpdate();
        }

        if (Game.isHost(worldObj)) {

            updateFuel();
//            if(update % 64 == 0){
//                System.out.println("bore tick");
//            }

            if (hasFuel() && getDelay() == 0) {
                setActive(true);
//            System.out.println("Yaw = " + MathHelper.floor_double(rotationYaw));

                BlockRailBase.EnumRailDirection dir = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
                if (getFacing() == EnumFacing.WEST || getFacing() == EnumFacing.EAST)
                    dir = BlockRailBase.EnumRailDirection.EAST_WEST;

                if (getDelay() == 0) {
                    float offset = 1.5f;
                    BlockPos targetPos = new BlockPos(getPositionAhead(offset)).down();

                    if (placeBallast) {
                        boolean placed = placeBallast(targetPos);
                        if (placed)
                            setDelay(STANDARD_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        placeBallast = false;
                    } else if (!worldObj.isSideSolid(targetPos, EnumFacing.UP)) {
                        placeBallast = true;
                        setDelay(BALLAST_DELAY);
                    }
                }

                if (getDelay() == 0) {
                    float offset = 0.8f;
                    BlockPos targetPos = new BlockPos(getPositionAhead(offset));
                    IBlockState existingState = WorldPlugin.getBlockState(worldObj, targetPos);

                    if (placeRail) {
                        boolean placed = placeTrack(targetPos, existingState, dir);
                        if (placed)
                            setDelay(STANDARD_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        placeRail = false;
                    } else if (TrackTools.isRailBlock(existingState)) {
                        if (dir != TrackTools.getTrackDirection(worldObj, targetPos, this)) {
                            TrackTools.setTrackDirection(worldObj, targetPos, dir);
                            setDelay(STANDARD_DELAY);
                        }
                    } else if (WorldPlugin.isBlockAir(worldObj, targetPos, existingState) || replaceableBlocks.contains(existingState.getBlock())) {
                        placeRail = true;
                        setDelay(STANDARD_DELAY);
                    } else {
                        setDelay(FAIL_DELAY);
                        setActive(false);
                    }
                }

                if (getDelay() == 0) {
                    float offset = 3.3f;
                    BlockPos targetPos = new BlockPos(getPositionAhead(offset));

                    if (boreLayer) {
                        boolean bored = boreLayer(targetPos, dir);
                        if (bored)
                            setDelay(LAYER_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        boreLayer = false;
                    } else if (checkForLava(targetPos, dir)) {
                        setDelay(FAIL_DELAY);
                        setActive(false);
                    } else {
                        setDelay((int) Math.ceil(getLayerHardness(targetPos, dir)));
                        if (getDelay() != 0)
                            boreLayer = true;
                    }
                }
            }

            if (isMinecartPowered()) {
                Vec3 headPos = getPositionAhead(3.3);
                double size = 0.8;
                AxisAlignedBB entitySearchBox = AABBFactory.start().setBoundsToPoint(headPos).expandHorizontally(size).raiseCeiling(2).build();
                List entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, entitySearchBox);
                for (Object e : entities) {
                    if (e instanceof EntityLivingBase) {
                        EntityLivingBase ent = (EntityLivingBase) e;
                        ent.attackEntityFrom(RailcraftDamageSource.BORE, 2);
                    }
                }
            }

            setMoving(hasFuel() && getDelay() == 0);

            if (getDelay() > 0)
                setDelay(getDelay() - 1);
        }

        if (isMoving()) {
            float factorX = MathHelper.cos((float) Math.toRadians(rotationYaw));
            float factorZ = -MathHelper.sin((float) Math.toRadians(rotationYaw));
            motionX = SPEED * factorX;
            motionZ = SPEED * factorZ;
        } else {
            motionX = 0.0D;
            motionZ = 0.0D;
        }

        emitParticles();

        if (isMinecartPowered())
            boreRotationAngle += 5;
    }

    @Override
    public float getMaxCartSpeedOnRail() {
        return SPEED;
    }

    private void updateFuel() {
        if (Game.isHost(worldObj)) {
            if (isMinecartPowered())
                spendFuel();
            stockFuel();
            if (outOfFuel())
                addFuel();
            setMinecartPowered(hasFuel() && isActive());
        }
    }

    protected Vec3 getPositionAhead(double offset) {
        double x = posX;
        double z = posZ;

        if (getFacing() == EnumFacing.EAST)
            x += offset;
        else if (getFacing() == EnumFacing.WEST)
            x -= offset;

        if (getFacing() == EnumFacing.NORTH)
            z -= offset;
        else if (getFacing() == EnumFacing.SOUTH)
            z += offset;

        return new Vec3(x, posY, z);
    }

    protected double getOffsetX(double x, double forwardOffset, double sideOffset) {
        switch (getFacing()) {
            case NORTH:
                return x + sideOffset;
            case SOUTH:
                return x - sideOffset;
            case EAST:
                return x + forwardOffset;
            case WEST:
                return x - forwardOffset;
        }
        return x;
    }

    protected double getOffsetZ(double z, double forwardOffset, double sideOffset) {
        switch (getFacing()) {
            case NORTH:
                return z - forwardOffset;
            case SOUTH:
                return z + forwardOffset;
            case EAST:
                return z - sideOffset;
            case WEST:
                return z + sideOffset;
        }
        return z;
    }

    protected void emitParticles() {
        if (isMinecartPowered()) {
            double randomFactor = 0.125;

            double forwardOffset = -0.35;
            double smokeYOffset = 2.4;
            double flameYOffset = 0.7;
            double smokeSideOffset = 0.92;
            double flameSideOffset = 1.14;
            double smokeX1 = posX;
            double smokeX2 = posX;
            double smokeZ1 = posZ;
            double smokeZ2 = posZ;

            double flameX1 = posX;
            double flameX2 = posX;
            double flameZ1 = posZ;
            double flameZ2 = posZ;
            if (getFacing() == EnumFacing.NORTH) {
                smokeX1 += smokeSideOffset;
                smokeX2 -= smokeSideOffset;
                smokeZ1 += forwardOffset;
                smokeZ2 += forwardOffset;

                flameX1 += flameSideOffset;
                flameX2 -= flameSideOffset;
                flameZ1 += forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ2 += forwardOffset + (rand.nextGaussian() * randomFactor);
            } else if (getFacing() == EnumFacing.EAST) {
                smokeX1 -= forwardOffset;
                smokeX2 -= forwardOffset;
                smokeZ1 += smokeSideOffset;
                smokeZ2 -= smokeSideOffset;

                flameX1 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameX2 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ1 += flameSideOffset;
                flameZ2 -= flameSideOffset;
            } else if (getFacing() == EnumFacing.SOUTH) {
                smokeX1 += smokeSideOffset;
                smokeX2 -= smokeSideOffset;
                smokeZ1 -= forwardOffset;
                smokeZ2 -= forwardOffset;

                flameX1 += flameSideOffset;
                flameX2 -= flameSideOffset;
                flameZ1 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ2 -= forwardOffset + (rand.nextGaussian() * randomFactor);
            } else if (getFacing() == EnumFacing.WEST) {
                smokeX1 += forwardOffset;
                smokeX2 += forwardOffset;
                smokeZ1 += smokeSideOffset;
                smokeZ2 -= smokeSideOffset;

                flameX1 += forwardOffset + (rand.nextGaussian() * randomFactor);
                flameX2 += forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ1 += flameSideOffset;
                flameZ2 -= flameSideOffset;
            }

            if (rand.nextInt(4) == 0) {
                worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, smokeX1, posY + smokeYOffset, smokeZ1, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle(EnumParticleTypes.FLAME, flameX1, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ1, 0.0D, 0.0D, 0.0D);
            }
            if (rand.nextInt(4) == 0) {
                worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, smokeX2, posY + smokeYOffset, smokeZ2, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle(EnumParticleTypes.FLAME, flameX2, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void stockBallast() {
        if (InvTools.isEmptySlot(invBallast)) {
            ItemStack stack = CartTools.transferHelper.pullStack(this, StandardStackFilters.BALLAST);
            if (stack != null)
                InvTools.moveItemStack(stack, invBallast);
        }
    }

    protected boolean placeBallast(BlockPos targetPos) {
        if (!worldObj.isSideSolid(targetPos, EnumFacing.UP))
            for (int inv = 0; inv < invBallast.getSizeInventory(); inv++) {
                ItemStack stack = invBallast.getStackInSlot(inv);
                if (stack != null && BallastRegistry.isItemBallast(stack)) {
                    BlockPos searchPos = targetPos;
                    for (int i = 0; i < MAX_FILL_DEPTH; i--) {
                        searchPos = searchPos.down();
                        if (worldObj.isSideSolid(searchPos, EnumFacing.UP)) {
                            invBallast.decrStackSize(inv, 1);
                            WorldPlugin.setBlockState(worldObj, targetPos, InvTools.getBlockStateFromStack(stack, (WorldServer) worldObj, targetPos));
                            return true;
                        }
                    }
                    return false;
                }
            }
        return false;
    }

    protected void stockTracks() {
        if (InvTools.isEmptySlot(invRails)) {
            ItemStack stack = CartTools.transferHelper.pullStack(this, StandardStackFilters.TRACK);
            if (stack != null)
                InvTools.moveItemStack(stack, invRails);
        }
    }

    protected boolean placeTrack(BlockPos targetPos, IBlockState oldState, BlockRailBase.EnumRailDirection shape) {
        if (replaceableBlocks.contains(oldState.getBlock()))
            worldObj.destroyBlock(targetPos, true);

        if (WorldPlugin.isBlockAir(worldObj, targetPos, oldState) && worldObj.isSideSolid(targetPos.down(), EnumFacing.UP))
            for (int inv = 0; inv < invRails.getSizeInventory(); inv++) {
                ItemStack stack = invRails.getStackInSlot(inv);
                if (stack != null) {
                    boolean placed = TrackToolsAPI.placeRailAt(stack, worldObj, targetPos, shape);
                    if (placed) {
                        invRails.decrStackSize(inv, 1);
                    }
                    return placed;
                }
            }
        return false;
    }

    protected boolean checkForLava(BlockPos targetPos, BlockRailBase.EnumRailDirection dir) {
        int xStart = targetPos.getX() - 1;
        int zStart = targetPos.getZ() - 1;
        int xEnd = targetPos.getX() + 1;
        int zEnd = targetPos.getZ() + 1;
        if (dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            xStart--;
            xEnd++;
        } else {
            zStart--;
            zEnd++;
        }

        int y = targetPos.getY();

        for (int yy = y; yy < y + 4; yy++) {
            for (int xx = xStart; xx <= xEnd; xx++) {
                for (int zz = zStart; zz <= zEnd; zz++) {
                    Block block = WorldPlugin.getBlock(worldObj, new BlockPos(xx, yy, zz));
                    if (block == Blocks.lava || block == Blocks.flowing_lava)
                        return true;
                }
            }
        }

        return false;
    }

    private <T> T layerAction(BlockPos targetPos, BlockRailBase.EnumRailDirection trackShape, T initialValue, BiFunction<BlockPos, BlockRailBase.EnumRailDirection, T> action, BiFunction<T, T, T> sum) {
        T returnValue = initialValue;

        int x = targetPos.getX();
        int y = targetPos.getY();
        int z = targetPos.getZ();
        for (int jj = y; jj < y + 3; jj++) {
            returnValue = sum.apply(returnValue, action.apply(new BlockPos(x, jj, z), trackShape));
        }

        if (trackShape == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            x--;
        else
            z--;
        for (int jj = y; jj < y + 3; jj++) {
            returnValue = sum.apply(returnValue, action.apply(new BlockPos(x, jj, z), trackShape));
        }

        x = targetPos.getX();
        z = targetPos.getZ();
        if (trackShape == BlockRailBase.EnumRailDirection.NORTH_SOUTH)
            x++;
        else
            z++;
        for (int jj = y; jj < y + 3; jj++) {
            returnValue = sum.apply(returnValue, action.apply(new BlockPos(x, jj, z), trackShape));
        }
        return returnValue;
    }

    protected boolean boreLayer(BlockPos targetPos, BlockRailBase.EnumRailDirection dir) {
        return layerAction(targetPos, dir, true, this::mineBlock, (s, r) -> s && r);
    }

    /**
     * @return true if the target block is clear
     */
    protected boolean mineBlock(BlockPos targetPos, BlockRailBase.EnumRailDirection preferredShape) {
        if (WorldPlugin.isBlockAir(worldObj, targetPos))
            return true;

        IBlockState targetState = WorldPlugin.getBlockState(worldObj, targetPos);
        if (TrackTools.isRailBlock(targetState)) {
            BlockRailBase.EnumRailDirection targetShape = TrackTools.getTrackDirection(worldObj, targetPos, targetState, this);
            if (preferredShape == targetShape)
                return true;
        } else if (targetState.getBlock() == Blocks.torch)
            return true;

        ItemStack head = getStackInSlot(0);
        if (head == null)
            return false;

        if (!canMineBlock(targetPos, targetState))
            return false;

        // Start of Event Fire
        BreakEvent breakEvent = new BreakEvent(worldObj, targetPos, targetState, RailcraftFakePlayer.get((WorldServer) worldObj, posX, posY, posZ));
        MinecraftForge.EVENT_BUS.post(breakEvent);

        if (breakEvent.isCanceled())
            return false;
        // End of Event Fire

        List<ItemStack> items = targetState.getBlock().getDrops(worldObj, targetPos, targetState, 0);

        for (ItemStack stack : items) {
            if (StandardStackFilters.FUEL.apply(stack))
                stack = InvTools.moveItemStack(stack, invFuel);

            if (stack != null && stack.stackSize > 0 && InvTools.isStackEqualToBlock(stack, Blocks.gravel))
                stack = InvTools.moveItemStack(stack, invBallast);

            if (stack != null && stack.stackSize > 0)
                stack = CartTools.transferHelper.pushStack(this, stack);

            if (stack != null && stack.stackSize > 0 && !RailcraftConfig.boreDestroysBlocks()) {
                float f = 0.7F;
                double xr = (worldObj.rand.nextFloat() - 0.5D) * f;
                double yr = (worldObj.rand.nextFloat() - 0.5D) * f;
                double zr = (worldObj.rand.nextFloat() - 0.5D) * f;
                Vec3 spewPos = getPositionAhead(-3.2);
                spewPos.addVector(xr, 0.3 + yr, zr);
                EntityItem entityitem = new EntityItem(worldObj, spewPos.xCoord, spewPos.yCoord, spewPos.zCoord, stack);
                worldObj.spawnEntityInWorld(entityitem);
            }
        }

        WorldPlugin.setBlockToAir(worldObj, targetPos);

        head.setItemDamage(head.getItemDamage() + 1);
        if (head.getItemDamage() > head.getMaxDamage())
            setInventorySlotContents(0, null);
        return true;
    }

    @SuppressWarnings({"SimplifiableIfStatement", "BooleanMethodIsAlwaysInverted"})
    private boolean canMineBlock(BlockPos targetPos, IBlockState existingState) {
        ItemStack head = getStackInSlot(0);
        if (existingState.getBlock() instanceof IMineable) {
            return head != null && ((IMineable) existingState.getBlock()).canMineBlock(worldObj, targetPos, this, head);
        }
        if (existingState.getBlock().getBlockHardness(worldObj, targetPos) < 0)
            return false;
        return isMineableBlock(existingState) && canHeadHarvestBlock(head, existingState);
    }

    protected float getLayerHardness(BlockPos targetPos, BlockRailBase.EnumRailDirection dir) {
        float hardness = layerAction(targetPos, dir, 0F, this::getBlockHardness, (s, r) -> s + r);
        hardness *= HARDNESS_MULTIPLIER;

        ItemStack boreSlot = getStackInSlot(0);
        if (boreSlot != null && boreSlot.getItem() instanceof IBoreHead) {
            IBoreHead head = (IBoreHead) boreSlot.getItem();
            float dig = 2f - head.getDigModifier();
            hardness *= dig;
        }

        hardness /= RailcraftConfig.boreMiningSpeedMultiplier();

        return hardness;
    }

    protected float getBlockHardness(BlockPos pos, BlockRailBase.EnumRailDirection dir) {
        if (WorldPlugin.isBlockAir(worldObj, pos))
            return 0;

        IBlockState blockState = WorldPlugin.getBlockState(worldObj, pos);
        if (TrackTools.isRailBlock(blockState)) {
            BlockRailBase.EnumRailDirection trackMeta = TrackTools.getTrackDirection(worldObj, pos, blockState, this);
            if (dir == trackMeta)
                return 0;
        }

        if (blockState == Blocks.torch)
            return 0;

        if (blockState == Blocks.obsidian)
            return 15;

        if (!canMineBlock(pos, blockState))
            return 0.1f;

        float hardness = blockState.getBlock().getBlockHardness(worldObj, pos);
        if (hardness <= 0)
            hardness = 0.1f;
        return hardness;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity other) {
        if (other instanceof EntityLivingBase)
            return other.getEntityBoundingBox();
        return null;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return null;
    }

    public float getBoreRotationAngle() {
        return (float) Math.toRadians(boreRotationAngle);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound data) {
//        fuel = getFuel();
        super.writeEntityToNBT(data);
        data.setByte("facing", (byte) getFacing().ordinal());
        data.setInteger("delay", getDelay());
        data.setBoolean("active", isActive());
        data.setInteger("burnTime", getBurnTime());
        data.setInteger("fuel", fuel);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        setFacing(EnumFacing.getFront(data.getByte("facing")));
        setDelay(data.getInteger("delay"));
        setActive(data.getBoolean("active"));
        setBurnTime(data.getInteger("burnTime"));
        setFuel(data.getInteger("fuel"));
    }

    protected int getDelay() {
        return delay;
//        return dataWatcher.getWatchableObjectInt(WATCHER_ID_DELAY);
    }

    protected void setDelay(int i) {
        delay = i;
//        dataWatcher.updateObject(WATCHER_ID_DELAY, Integer.valueOf(i));
    }

    protected boolean isActive() {
        return active;
//        return dataWatcher.getWatchableObjectByte(WATCHER_ID_ACTIVE) != 0;
    }

    protected void setActive(boolean active) {
        this.active = active;
        TrainState state = active ? Train.TrainState.STOPPED : Train.TrainState.NORMAL;
        Train.getTrain(this).setTrainState(state);
//        dataWatcher.updateObject(WATCHER_ID_ACTIVE, Byte.valueOf((byte)(active ? 1 : 0)));
    }

    protected boolean isMoving() {
        return dataWatcher.getWatchableObjectByte(WATCHER_ID_MOVING) != 0;
    }

    protected void setMoving(boolean move) {
        dataWatcher.updateObject(WATCHER_ID_MOVING, (byte) (move ? 1 : 0));
    }

    public int getBurnTime() {
        return burnTime;
//        return dataWatcher.getWatchableObjectInt(WATCHER_ID_BURN_TIME);
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
//        dataWatcher.updateObject(WATCHER_ID_BURN_TIME, Integer.valueOf(burnTime));
    }

    public int getFuel() {
        return fuel;
//        return dataWatcher.getWatchableObjectInt(WATCHER_ID_FUEL);
    }

    public void setFuel(int i) {
        fuel = i;
//        dataWatcher.updateObject(WATCHER_ID_FUEL, Integer.valueOf(i));
    }

    public boolean outOfFuel() {
        return getFuel() <= FUEL_CONSUMPTION;
    }

    public boolean hasFuel() {
        return getFuel() > 0;
    }

    protected void stockFuel() {
        if (InvTools.isEmptySlot(invFuel)) {
            ItemStack stack = CartTools.transferHelper.pullStack(this, StandardStackFilters.FUEL);
            if (stack != null)
                InvTools.moveItemStack(stack, invFuel);
        }
    }

    protected void addFuel() {
        int burn = 0;
        for (int slot = 0; slot < invFuel.getSizeInventory(); slot++) {
            ItemStack stack = invFuel.getStackInSlot(slot);
            if (stack != null) {
                burn = FuelPlugin.getBurnTime(stack);
                if (burn > 0) {
                    if (stack.getItem().hasContainerItem(stack))
                        invFuel.setInventorySlotContents(slot, stack.getItem().getContainerItem(stack));
                    else
                        invFuel.decrStackSize(slot, 1);
                    break;
                }
            }
        }
        if (burn > 0) {
            setBurnTime(burn + getFuel());
            setFuel(getFuel() + burn);
        }
    }

    public int getBurnProgressScaled(int i) {
        int burn = getBurnTime();
        if (burn == 0)
            return 0;

        return getFuel() * i / burn;
    }

    protected void spendFuel() {
        setFuel(getFuel() - FUEL_CONSUMPTION);
    }

    protected void forceUpdateBoreHead() {
        ItemStack boreStack = getStackInSlot(0);
        if (boreStack != null)
            boreStack = boreStack.copy();
        dataWatcher.updateObject(WATCHER_ID_BORE_HEAD, boreStack);
    }

    public IBoreHead getBoreHead() {
        ItemStack boreStack = dataWatcher.getWatchableObjectItemStack(WATCHER_ID_BORE_HEAD);
        if (boreStack != null && boreStack.getItem() instanceof IBoreHead)
            return (IBoreHead) boreStack.getItem();
        return null;
    }

    @Override
    protected void applyDrag() {
        motionX *= getDrag();
        motionY *= 0.0D;
        motionZ *= getDrag();
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(getCartItem());
        return items;
    }

    @Override
    public boolean isPoweredCart() {
        return true;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj))
            GuiHandler.openGui(EnumGui.CART_BORE, player, worldObj, this);
        return true;
    }

    @Override
    public void markDirty() {
        if (!isActive())
            setDelay(STANDARD_DELAY);
    }

    public final EnumFacing getFacing() {
        return EnumFacing.getFront(dataWatcher.getWatchableObjectByte(WATCHER_ID_FACING));
    }

    protected final void setFacing(EnumFacing facing) {
        dataWatcher.updateObject(WATCHER_ID_FACING, (byte) facing.ordinal());

        setYaw();
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLinkWithCart(EntityMinecart cart) {
        Vec3 pos = getPositionAhead(-LENGTH / 2.0);
        float dist = LinkageManager.LINKAGE_DISTANCE * 2;
        dist = dist * dist;
        return cart.getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) < dist;
    }

    @Override
    public boolean hasTwoLinks() {
        return false;
    }

    @Override
    public float getLinkageDistance(EntityMinecart cart) {
        return 4f;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 3.1f;
    }

    @Override
    public void onLinkCreated(EntityMinecart cart) {
    }

    @Override
    public void onLinkBroken(EntityMinecart cart) {
    }

    @Override
    public boolean canBeAdjusted(EntityMinecart cart) {
        return !isActive();
    }

    @Override
    public boolean shouldDoRailFunctions() {
        return false;
    }

    @Override
    public Entity[] getParts() {
        return partArray;
    }

    @SuppressWarnings("UnusedParameters")
    public boolean attackEntityFromPart(EntityTunnelBorePart part, DamageSource damageSource, float damage) {
        return attackEntityFrom(damageSource, damage);
    }
}
