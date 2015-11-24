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
import mods.railcraft.api.tracks.RailTools;
import mods.railcraft.common.blocks.tracks.EnumTrackMeta;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.Train.TrainState;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.FuelPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.collections.BlockKey;
import mods.railcraft.common.util.collections.BlockSet;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.BallastRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.RailcraftDamageSource;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

import java.util.*;

public class EntityTunnelBore extends CartContainerBase implements IInventory, ILinkableCart {
    public static final float SPEED = 0.03F;
    public static final float LENGTH = 6.2f;
    public static final int MAX_FILL_DEPTH = 10;
    public static final int FAIL_DELAY = 200;
    public static final int STANDARD_DELAY = 5;
    public static final int LAYER_DELAY = 40;
    public static final int BALLAST_DELAY = 10;
    public static final int FUEL_CONSUMPTION = 12;
    public static final float HARDNESS_MULTIPLER = 8;
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

    protected final IInventory invFuel = new InventoryMapper(this, 1, 6);
    protected final IInventory invBallast = new InventoryMapper(this, 7, 9);
    protected final IInventory invRails = new InventoryMapper(this, 16, 9);
    //    protected static final int WATCHER_ID_BURN_TIME = 22;
    protected boolean degreeCalc = false;
    protected int delay = 0;
    protected boolean placeRail = false;
    protected boolean placeBallast = false;
    protected boolean boreLayer = false;
    protected int boreRotationAngle = 0;
    private boolean active;
    private int clock = MiscTools.getRand().nextInt();
    private int burnTime;
    private int fuel;
    private boolean hasInit;
    private EntityTunnelBorePart[] partArray;
    private EntityTunnelBorePart partHead1;
    private EntityTunnelBorePart partHead2;
    private EntityTunnelBorePart partBody;
    private EntityTunnelBorePart partTail1;
    private EntityTunnelBorePart partTail2;

    public EntityTunnelBore(World world, double i, double j, double k) {
        this(world, i, j, k, ForgeDirection.SOUTH);
    }

    public EntityTunnelBore(World world, double i, double j, double k, ForgeDirection f) {
        super(world);
        partArray = new EntityTunnelBorePart[]{
                // ------------------------------------- width, height, forwardOffset, sideOffset
                partHead1 = new EntityTunnelBorePart(this, "head1", 1.9F, 2.6F, 2F, -0.6F),
                partHead2 = new EntityTunnelBorePart(this, "head2", 1.9F, 2.6F, 2F, 0.6F),
                partBody = new EntityTunnelBorePart(this, "body", 2.0F, 1.9F, 0.6F),
                partTail1 = new EntityTunnelBorePart(this, "tail1", 1.6F, 1.4F, -1F),
                partTail2 = new EntityTunnelBorePart(this, "tail2", 1.6F, 1.4F, -2.2F),
        };
        hasInit = true;
        setPosition(i, j + (double) yOffset, k);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = i;
        prevPosY = j;
        prevPosZ = k;
//        cargoItems = new ItemStack[25];
        setFacing(f);
        setSize(LENGTH, 4F);
    }

    public EntityTunnelBore(World world) {
        this(world, 0, 0, 0, ForgeDirection.SOUTH);
    }

    public static void addMineableBlock(Block block) {
        addMineableBlock(block, -1);
    }

    public static void addMineableBlock(Block block, int meta) {
        mineableBlocks.add(new BlockKey(block, meta));
    }

    public static boolean canHeadHarvestBlock(ItemStack head, Block block, int meta) {
        if (head == null)
            return false;

        if (head.getItem() instanceof IBoreHead) {
            IBoreHead boreHead = (IBoreHead) head.getItem();

            boolean mappingExists = false;

            int blockHarvestLevel = HarvestPlugin.getBlockHarvestLevel(block, meta, "pickaxe");
            if (blockHarvestLevel > -1) {
                if (boreHead.getHarvestLevel() >= blockHarvestLevel)
                    return true;
                mappingExists = true;
            }

            blockHarvestLevel = HarvestPlugin.getBlockHarvestLevel(block, meta, "axe");
            if (blockHarvestLevel > -1) {
                if (boreHead.getHarvestLevel() >= blockHarvestLevel)
                    return true;
                mappingExists = true;
            }

            blockHarvestLevel = HarvestPlugin.getBlockHarvestLevel(block, meta, "shovel");
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

    private boolean isMinableBlock(Block block, int meta) {
        if (RailcraftConfig.boreMinesAllBlocks())
            return true;
        return mineableBlocks.contains(block, meta);
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
        if (!this.worldObj.isRemote && !this.isDead)
            if (this.isEntityInvulnerable())
                return false;
            else {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + damage * 10);
                boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer) source.getEntity()).capabilities.isCreativeMode;

                if (flag || this.getDamage() > 120) {
                    if (this.riddenByEntity != null)
                        this.riddenByEntity.mountEntity(this);

                    if (flag && !this.hasCustomInventoryName())
                        this.setDead();
                    else
                        this.killMinecart(source);
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
    public void setPosition(double i, double j, double k) {
        if (!hasInit) {
            super.setPosition(i, j, k);
            return;
        }

        posX = i;
        posY = j;
        posZ = k;
        double w = 2.7 / 2.0;
        double h = 2.7;
        double l = LENGTH / 2.0;
        double x1 = i;
        double x2 = i;
        double z1 = k;
        double z2 = k;
        if (getFacing() == ForgeDirection.WEST || getFacing() == ForgeDirection.EAST) {
            x1 -= l;
            x2 += l;
            z1 -= w;
            z2 += w;
        } else {
            x1 -= w;
            x2 += w;
            z1 -= l;
            z2 += l;
        }

        boundingBox.setBounds(x1, (j - (double) yOffset) + (double) ySize, z1, x2, (j - (double) yOffset) + (double) ySize + h, z2);
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

                int x;
                int y = MathHelper.floor_double(posY);
                int z;
                EnumTrackMeta dir = EnumTrackMeta.NORTH_SOUTH;
                if (getFacing() == ForgeDirection.WEST || getFacing() == ForgeDirection.EAST)
                    dir = EnumTrackMeta.EAST_WEST;

                if (getDelay() == 0) {
                    float offset = 1.5f;
                    x = MathHelper.floor_double(getXAhead(posX, offset));
                    z = MathHelper.floor_double(getZAhead(posZ, offset));

                    if (placeBallast) {
                        boolean placed = placeBallast(x, y - 1, z);
                        if (placed)
                            setDelay(STANDARD_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        placeBallast = false;
                    } else if (!worldObj.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
                        placeBallast = true;
                        setDelay(BALLAST_DELAY);
                    }
                }

                if (getDelay() == 0) {
                    float offset = 0.8f;
                    x = MathHelper.floor_double(getXAhead(posX, offset));
                    z = MathHelper.floor_double(getZAhead(posZ, offset));

                    if (placeRail) {
                        boolean placed = placeTrack(x, y, z, dir);
                        if (placed)
                            setDelay(STANDARD_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        placeRail = false;
                    } else if (TrackTools.isRailBlockAt(worldObj, x, y, z)) {
                        if (!dir.isEqual(TrackTools.getTrackMeta(worldObj, this, x, y, z))) {
                            worldObj.setBlockMetadataWithNotify(x, y, z, dir.ordinal(), 3);
                            setDelay(STANDARD_DELAY);
                        }
                    } else if (worldObj.isAirBlock(x, y, z) || replaceableBlocks.contains(worldObj.getBlock(x, y, z))) {
                        placeRail = true;
                        setDelay(STANDARD_DELAY);
                    } else {
                        setDelay(FAIL_DELAY);
                        setActive(false);
                    }
                }

                if (getDelay() == 0) {
                    float offset = 3.3f;
                    x = MathHelper.floor_double(getXAhead(posX, offset));
                    z = MathHelper.floor_double(getZAhead(posZ, offset));

                    if (boreLayer) {
                        boolean bored = boreLayer(x, y, z, dir);
                        if (bored)
                            setDelay(LAYER_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        boreLayer = false;
                    } else if (checkForLava(x, y, z, dir)) {
                        setDelay(FAIL_DELAY);
                        setActive(false);
                    } else {
                        setDelay((int) Math.ceil(getLayerHardness(x, y, z, dir)));
                        if (getDelay() != 0)
                            boreLayer = true;
                    }
                }
            }

            if (isMinecartPowered()) {
                double i = getXAhead(posX, 3.3);
                double k = getZAhead(posZ, 3.3);
                double size = 0.8;
                List entities = worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(i - size, posY, k - size, i + size, posY + 2, k + size));
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

    protected double getXAhead(double x, double offset) {
        if (getFacing() == ForgeDirection.EAST)
            x += offset;
        else if (getFacing() == ForgeDirection.WEST)
            x -= offset;
        return x;
    }

    protected double getZAhead(double z, double offset) {
        if (getFacing() == ForgeDirection.NORTH)
            z -= offset;
        else if (getFacing() == ForgeDirection.SOUTH)
            z += offset;
        return z;
    }

    protected double getOffsetX(double x, double forwardOffset, double sideOffset) {
        switch(getFacing()){
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
        switch(getFacing()){
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
            if (getFacing() == ForgeDirection.NORTH) {
                smokeX1 += smokeSideOffset;
                smokeX2 -= smokeSideOffset;
                smokeZ1 += forwardOffset;
                smokeZ2 += forwardOffset;

                flameX1 += flameSideOffset;
                flameX2 -= flameSideOffset;
                flameZ1 += forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ2 += forwardOffset + (rand.nextGaussian() * randomFactor);
            } else if (getFacing() == ForgeDirection.EAST) {
                smokeX1 -= forwardOffset;
                smokeX2 -= forwardOffset;
                smokeZ1 += smokeSideOffset;
                smokeZ2 -= smokeSideOffset;

                flameX1 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameX2 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ1 += flameSideOffset;
                flameZ2 -= flameSideOffset;
            } else if (getFacing() == ForgeDirection.SOUTH) {
                smokeX1 += smokeSideOffset;
                smokeX2 -= smokeSideOffset;
                smokeZ1 -= forwardOffset;
                smokeZ2 -= forwardOffset;

                flameX1 += flameSideOffset;
                flameX2 -= flameSideOffset;
                flameZ1 -= forwardOffset + (rand.nextGaussian() * randomFactor);
                flameZ2 -= forwardOffset + (rand.nextGaussian() * randomFactor);
            } else if (getFacing() == ForgeDirection.WEST) {
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
                worldObj.spawnParticle("largesmoke", smokeX1, posY + smokeYOffset, smokeZ1, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle("flame", flameX1, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ1, 0.0D, 0.0D, 0.0D);
            }
            if (rand.nextInt(4) == 0) {
                worldObj.spawnParticle("largesmoke", smokeX2, posY + smokeYOffset, smokeZ2, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle("flame", flameX2, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void stockBallast() {
        if (InvTools.isEmptySlot(invBallast)) {
            ItemStack stack = CartTools.transferHelper.pullStack(this, StackFilter.BALLAST);
            if (stack != null)
                InvTools.moveItemStack(stack, invBallast);
        }
    }

    protected boolean placeBallast(int i, int j, int k) {
        if (!worldObj.isSideSolid(i, j, k, ForgeDirection.UP))
            for (int inv = 0; inv < invBallast.getSizeInventory(); inv++) {
                ItemStack stack = invBallast.getStackInSlot(inv);
                if (stack != null && BallastRegistry.isItemBallast(stack)) {
                    for (int y = j; y > j - MAX_FILL_DEPTH; y--) {
                        if (worldObj.isSideSolid(i, y, k, ForgeDirection.UP)) {
                            invBallast.decrStackSize(inv, 1);
                            worldObj.setBlock(i, j, k, InvTools.getBlockFromStack(stack), stack.getItemDamage(), 3);
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
            ItemStack stack = CartTools.transferHelper.pullStack(this, StackFilter.TRACK);
            if (stack != null)
                InvTools.moveItemStack(stack, invRails);
        }
    }

    protected boolean placeTrack(int x, int y, int z, EnumTrackMeta meta) {
        Block block = worldObj.getBlock(x, y, z);
        if (replaceableBlocks.contains(block))
            worldObj.func_147480_a(x, y, z, true);

        if (worldObj.isAirBlock(x, y, z) && worldObj.isSideSolid(x, y - 1, z, ForgeDirection.UP))
            for (int inv = 0; inv < invRails.getSizeInventory(); inv++) {
                ItemStack stack = invRails.getStackInSlot(inv);
                if (stack != null) {
                    boolean placed = RailTools.placeRailAt(stack, worldObj, x, y, z);
                    if (placed) {
                        worldObj.setBlockMetadataWithNotify(x, y, z, meta.ordinal(), 3);
                        invRails.decrStackSize(inv, 1);
                    }
                    return placed;
                }
            }
        return false;
    }

    protected boolean checkForLava(int i, int j, int k, EnumTrackMeta dir) {
        int xStart = i - 1;
        int zStart = k - 1;
        int xEnd = i + 1;
        int zEnd = k + 1;
        if (dir == EnumTrackMeta.NORTH_SOUTH) {
            xStart = i - 2;
            xEnd = i + 2;
        } else {
            zStart = k - 2;
            zEnd = k + 2;
        }

        for (int jj = j; jj < j + 4; jj++) {
            for (int ii = xStart; ii <= xEnd; ii++) {
                for (int kk = zStart; kk <= zEnd; kk++) {
                    Block block = worldObj.getBlock(ii, jj, kk);
                    if (block == Blocks.lava || block == Blocks.flowing_lava)
                        return true;
                }
            }
        }

        return false;
    }

    protected boolean boreLayer(int i, int j, int k, EnumTrackMeta dir) {
        boolean clear = true;
        int ii = i;
        int kk = k;
        for (int jj = j; jj < j + 3; jj++) {
            clear = clear && mineBlock(ii, jj, kk, dir);
        }

        if (dir == EnumTrackMeta.NORTH_SOUTH)
            ii--;
        else
            kk--;
        for (int jj = j; jj < j + 3; jj++) {
            clear = clear && mineBlock(ii, jj, kk, dir);
        }

        ii = i;
        kk = k;
        if (dir == EnumTrackMeta.NORTH_SOUTH)
            ii++;
        else
            kk++;
        for (int jj = j; jj < j + 3; jj++) {
            clear = clear && mineBlock(ii, jj, kk, dir);
        }
        return clear;
    }

    protected boolean mineBlock(int x, int y, int z, EnumTrackMeta dir) {
        if (worldObj.isAirBlock(x, y, z))
            return true;

        Block block = worldObj.getBlock(x, y, z);
        if (TrackTools.isRailBlock(block)) {
            int trackMeta = TrackTools.getTrackMeta(worldObj, block, this, x, y, z);
            if (dir.isEqual(trackMeta))
                return true;
        } else if (block == Blocks.torch)
            return true;

        ItemStack head = getStackInSlot(0);
        if (head == null)
            return false;

        int meta = worldObj.getBlockMetadata(x, y, z);

        if (!canMineBlock(x, y, z, block, meta))
            return false;

        // Start of Event Fire
        BreakEvent breakEvent = new BreakEvent(x, y, z, worldObj, block, meta, PlayerPlugin.getFakePlayer((WorldServer) worldObj, posX, posY, posZ));
        MinecraftForge.EVENT_BUS.post(breakEvent);

        if (breakEvent.isCanceled())
            return false;
        // End of Event Fire

        ArrayList<ItemStack> items = block.getDrops(worldObj, x, y, z, meta, 0);

        for (ItemStack stack : items) {
            if (StackFilter.FUEL.matches(stack))
                stack = InvTools.moveItemStack(stack, invFuel);

            if (stack != null && stack.stackSize > 0 && InvTools.isStackEqualToBlock(stack, Blocks.gravel))
                stack = InvTools.moveItemStack(stack, invBallast);

            if (stack != null && stack.stackSize > 0)
                stack = CartTools.transferHelper.pushStack(this, stack);

            if (stack != null && stack.stackSize > 0 && !RailcraftConfig.boreDestroysBlocks()) {
                float f = 0.7F;
                double xr = (double) (worldObj.rand.nextFloat() - 0.5D) * f;
                double yr = (double) (worldObj.rand.nextFloat() - 0.5D) * f;
                double zr = (double) (worldObj.rand.nextFloat() - 0.5D) * f;
                EntityItem entityitem = new EntityItem(worldObj, getXAhead(posX, -3.2) + xr, posY + 0.3 + yr, getZAhead(posZ, -3.2) + zr, stack);
                worldObj.spawnEntityInWorld(entityitem);
            }
        }

        worldObj.setBlockToAir(x, y, z);

        head.setItemDamage(head.getItemDamage() + 1);
        if (head.getItemDamage() > head.getMaxDamage())
            setInventorySlotContents(0, null);
        return true;
    }

    private boolean canMineBlock(int i, int j, int k, Block block, int meta) {
        ItemStack head = getStackInSlot(0);
        if (block instanceof IMineable) {
            if (head == null)
                return false;
            return ((IMineable) block).canMineBlock(worldObj, i, j, k, this, head);
        }
        if (block.getBlockHardness(worldObj, i, j, k) < 0)
            return false;
        return isMinableBlock(block, meta) && canHeadHarvestBlock(head, block, meta);
    }

    protected float getLayerHardness(int i, int j, int k, EnumTrackMeta dir) {
        float hardness = 0;
        int ii = i;
        int kk = k;
        for (int jj = j; jj < j + 3; jj++) {
            hardness += getBlockHardness(ii, jj, kk, dir);
        }

        if (dir == EnumTrackMeta.NORTH_SOUTH)
            ii--;
        else
            kk--;
        for (int jj = j; jj < j + 3; jj++) {
            hardness += getBlockHardness(ii, jj, kk, dir);
        }

        ii = i;
        kk = k;
        if (dir == EnumTrackMeta.NORTH_SOUTH)
            ii++;
        else
            kk++;
        for (int jj = j; jj < j + 3; jj++) {
            hardness += getBlockHardness(ii, jj, kk, dir);
        }

        hardness *= HARDNESS_MULTIPLER;

        ItemStack boreSlot = getStackInSlot(0);
        if (boreSlot != null && boreSlot.getItem() instanceof IBoreHead) {
            IBoreHead head = (IBoreHead) boreSlot.getItem();
            float dig = 2f - head.getDigModifier();
            hardness *= dig;
        }

        hardness /= RailcraftConfig.boreMiningSpeedMultiplier();

        return hardness;
    }

    protected float getBlockHardness(int x, int y, int z, EnumTrackMeta dir) {
        if (worldObj.isAirBlock(x, y, z))
            return 0;

        Block block = worldObj.getBlock(x, y, z);
        if (TrackTools.isRailBlock(block)) {
            int trackMeta = TrackTools.getTrackMeta(worldObj, block, this, x, y, z);
            if (dir.isEqual(trackMeta))
                return 0;
        }

        if (block == Blocks.torch)
            return 0;

        if (block == Blocks.obsidian)
            return 15;

        int meta = worldObj.getBlockMetadata(x, y, z);
        if (!canMineBlock(x, y, z, block, meta))
            return 0.1f;

        float hardness = block.getBlockHardness(worldObj, x, y, z);
        if (hardness <= 0)
            hardness = 0.1f;
        return hardness;
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity other) {
        if (other instanceof EntityLivingBase)
            return other.boundingBox;
        return null;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    public String getInventoryName() {
        return "Tunnel Bore";
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
        setFacing(ForgeDirection.getOrientation(data.getByte("facing")));
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
            ItemStack stack = CartTools.transferHelper.pullStack(this, StackFilter.FUEL);
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

    public final ForgeDirection getFacing() {
        return ForgeDirection.getOrientation(dataWatcher.getWatchableObjectByte(WATCHER_ID_FACING));
    }

    protected final void setFacing(ForgeDirection facing) {
        dataWatcher.updateObject(WATCHER_ID_FACING, (byte) facing.ordinal());

        setYaw();
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLinkWithCart(EntityMinecart cart) {
        double x = getXAhead(posX, -LENGTH / 2);
        double z = getZAhead(posZ, -LENGTH / 2);

        return cart.getDistance(x, posY, z) < LinkageManager.LINKAGE_DISTANCE * 2;
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

    public IInventory getInventoryFuel() {
        return invFuel;
    }

    public IInventory getInventoryGravel() {
        return invBallast;
    }

    public IInventory getInventoryRails() {
        return invRails;
    }

    public Entity[] getParts() {
        return this.partArray;
    }

    public boolean attackEntityFromPart(EntityTunnelBorePart part, DamageSource damageSource, float damage) {
        return attackEntityFrom(damageSource, damage);
    }
}
