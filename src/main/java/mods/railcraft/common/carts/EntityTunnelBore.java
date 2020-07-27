/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.carts.IBoreHead;
import mods.railcraft.api.carts.ILinkableCart;
import mods.railcraft.api.core.RailcraftFakePlayer;
import mods.railcraft.api.tracks.TrackToolsAPI;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.carts.Train.State;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.entity.RCEntitySelectors;
import mods.railcraft.common.util.entity.RailcraftDamageSource;
import mods.railcraft.common.util.inventory.IExtInvSlot;
import mods.railcraft.common.util.inventory.IInvSlot;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.AABBFactory;
import mods.railcraft.common.util.misc.BallastRegistry;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public class EntityTunnelBore extends CartBaseContainer implements ILinkableCart {
    public static final float SPEED = 0.03F;
    public static final float LENGTH = 6.2f;
    public static final float WIDTH = 3f;
    public static final float HEIGHT = 3f;
    public static final int MAX_FILL_DEPTH = 10;
    public static final int FAIL_DELAY = 200;
    public static final int STANDARD_DELAY = 5;
    public static final int LAYER_DELAY = 40;
    public static final int BALLAST_DELAY = 10;
    public static final int FUEL_CONSUMPTION = 12;
    public static final float HARDNESS_MULTIPLIER = 8;
    public static final Set<IBlockState> mineableStates = new HashSet<>();
    public static final Set<Block> mineableBlocks = new HashSet<>();
    public static final Set<String> mineableOreTags = new HashSet<>();
    public static final Set<Block> replaceableBlocks = new HashSet<>();
    private static final DataParameter<Boolean> HAS_FUEL = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> MOVING = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final DataParameter<EnumFacing> FACING = DataManagerPlugin.create(DataSerializers.FACING);
    private static final DataParameter<ItemStack> BORE_HEAD = DataManagerPlugin.create(DataSerializers.ITEM_STACK);
    private static final Block[] mineable = {
            Blocks.CLAY,
            Blocks.SNOW_LAYER,
            Blocks.CACTUS,
            Blocks.CARROTS,
            Blocks.COBBLESTONE,
            Blocks.MOSSY_COBBLESTONE,
            Blocks.COCOA,
            Blocks.WHEAT,
            Blocks.DEADBUSH,
            Blocks.DIRT,
            Blocks.FIRE,
            Blocks.GLOWSTONE,
            Blocks.GRASS,
            Blocks.GRAVEL,
            Blocks.ICE,
            Blocks.LEAVES,
            Blocks.MELON_BLOCK,
            Blocks.MELON_STEM,
            Blocks.BROWN_MUSHROOM,
            Blocks.BROWN_MUSHROOM_BLOCK,
            Blocks.RED_MUSHROOM,
            Blocks.RED_MUSHROOM_BLOCK,
            Blocks.MYCELIUM,
            Blocks.NETHER_WART,
            Blocks.NETHERRACK,
            Blocks.OBSIDIAN,
            Blocks.COAL_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LIT_REDSTONE_ORE,
            Blocks.RED_FLOWER,
            Blocks.YELLOW_FLOWER,
            Blocks.POTATOES,
            Blocks.PUMPKIN,
            Blocks.PUMPKIN_STEM,
            Blocks.REEDS,
            Blocks.SAND,
            Blocks.SANDSTONE,
            Blocks.SAPLING,
            Blocks.SOUL_SAND,
            Blocks.SNOW,
            Blocks.STONE,
            Blocks.TALLGRASS,
            Blocks.FARMLAND,
            Blocks.TORCH,
            Blocks.VINE,
            Blocks.WATERLILY,
            Blocks.WEB,
            Blocks.END_STONE,
            Blocks.LOG,
            Blocks.LOG2,};
    private static final Block[] replaceable = {
            Blocks.TORCH,
            Blocks.TALLGRASS,
            Blocks.DEADBUSH,
            Blocks.VINE,
            Blocks.BROWN_MUSHROOM,
            Blocks.RED_MUSHROOM,
            Blocks.YELLOW_FLOWER,
            Blocks.RED_FLOWER,
            Blocks.DOUBLE_PLANT};
    private static final String[] oreTags = {
            "stone",
            "cobblestone",
            "logWood",
            "treeSapling",
            "treeLeaves"};

    static {
        mineableBlocks.addAll(Arrays.asList(mineable));
        mineableOreTags.addAll(Arrays.asList(oreTags));
        replaceableBlocks.addAll(Arrays.asList(replaceable));
    }

    public final InventoryMapper invFuel = InventoryMapper.make(this, 1, 6).withFilters(StackFilters.FUEL);
    public final InventoryMapper invBallast = InventoryMapper.make(this, 7, 9).withFilters(StackFilters.BALLAST);
    public final InventoryMapper invRails = InventoryMapper.make(this, 16, 9).withFilters(StackFilters.TRACK);
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

    public EntityTunnelBore(World world) {
        this(world, 0, 0, 0, EnumFacing.SOUTH);
    }

    public EntityTunnelBore(World world, double x, double y, double z) {
        this(world, x, y, z, EnumFacing.SOUTH);
    }

    public EntityTunnelBore(World world, double x, double y, double z, EnumFacing f) {
        super(world, x, y, z);
        setFacing(f);
    }

    {
        float headW = 1.5F;
        float headH = 2.6F;
        float headSO = 0.7F;
        partArray = new EntityTunnelBorePart[]{
                // ------------------------------------- name, width, height, forwardOffset, sideOffset
                new EntityTunnelBorePart(this, "head1", headW, headH, 1.85F, -headSO),
                new EntityTunnelBorePart(this, "head2", headW, headH, 1.85F, headSO),
                new EntityTunnelBorePart(this, "head3", headW, headH, 2.3F, -headSO),
                new EntityTunnelBorePart(this, "head4", headW, headH, 2.3F, headSO),
                new EntityTunnelBorePart(this, "body", 2.0F, 1.9F, 0.6F),
                new EntityTunnelBorePart(this, "tail1", 1.6F, 1.4F, -1F),
                new EntityTunnelBorePart(this, "tail2", 1.6F, 1.4F, -2.2F),
        };
        hasInit = true;
        setSize(LENGTH, HEIGHT);
        invMappers = Arrays.asList(invFuel, invBallast, invRails);
    }

    public static void addMineableBlock(Block block) {
        addMineableBlock(block.getDefaultState());
    }

    public static void addMineableBlock(IBlockState blockState) {
        mineableStates.add(blockState);
    }

    @Contract("null, _ -> false")
    public boolean canHeadHarvestBlock(ItemStack head, IBlockState targetState) {
        if (InvTools.isEmpty(head))
            return false;

        if (head.getItem() instanceof IBoreHead) {

            /*
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
            */
            Item item = head.getItem();
            Set<String> toolClasses = item.getToolClasses(head);
            EntityPlayer fakePlayer = RailcraftFakePlayer.get((WorldServer) world, posX, posY, posZ);

            return toolClasses.stream()
                    .anyMatch(tool -> item.getHarvestLevel(head, tool, fakePlayer, targetState) >= HarvestPlugin.getHarvestLevel(targetState, tool));
        }

        return false;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.BORE;
    }

    private boolean isMineableBlock(IBlockState blockState) {
        if (RailcraftConfig.boreMinesAllBlocks())
            return true;
        if (mineableBlocks.contains(blockState.getBlock()) || mineableStates.contains(blockState))
            return true;
        Block block = blockState.getBlock();
        Item item = block.getItemDropped(blockState, MiscTools.RANDOM, 0);
        if (item != Items.AIR) {
            ItemStack blockStack = new ItemStack(item, 1, block.damageDropped(blockState));
            return mineableOreTags.stream().anyMatch(s -> OreDictPlugin.isOreType(s, blockStack));
        }
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(HAS_FUEL, false);
        dataManager.register(MOVING, false);
        dataManager.register(BORE_HEAD, ItemStack.EMPTY);
        dataManager.register(FACING, EnumFacing.NORTH);
//        dataManager.register(WATCHER_ID_BURN_TIME, Integer.valueOf(0));
    }

    public boolean isMinecartPowered() {
        return dataManager.get(HAS_FUEL);
    }

    public void setMinecartPowered(boolean powered) {
        dataManager.set(HAS_FUEL, powered);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (!world.isRemote && !isDead)
            if (isEntityInvulnerable(source))
                return false;
            else {
                setRollingDirection(-getRollingDirection());
                setRollingAmplitude(10);
                markVelocityChanged();
                setDamage(getDamage() + damage * 10);
                boolean flag = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer) source.getTrueSource()).capabilities.isCreativeMode;

                if (flag || getDamage() > 120) {
                    removePassengers();

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
                yaw = 180;
                break;
            case EAST:
                yaw = 270;
                break;
            case SOUTH:
                yaw = 0;
                break;
            case WEST:
                yaw = 90;
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

        setEntityBoundingBox(new AxisAlignedBB(minX, y, minZ, maxX, y + height, maxZ));
    }

    @Override
    public void onUpdate() {
        clock++;

        if (Game.isHost(world)) {
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

        if (Game.isHost(world)) {

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
                    } else if (!world.isSideSolid(targetPos, EnumFacing.UP)) {
                        placeBallast = true;
                        setDelay(BALLAST_DELAY);
                    }
                }

                if (getDelay() == 0) {
                    float offset = 0.8f;
                    BlockPos targetPos = new BlockPos(getPositionAhead(offset));
                    IBlockState existingState = WorldPlugin.getBlockState(world, targetPos);

                    if (placeRail) {
                        boolean placed = placeTrack(targetPos, existingState, dir);
                        if (placed)
                            setDelay(STANDARD_DELAY);
                        else {
                            setDelay(FAIL_DELAY);
                            setActive(false);
                        }
                        placeRail = false;
                    } else if (TrackTools.isRail(existingState)) {
                        if (dir != TrackTools.getTrackDirection(world, targetPos, this)) {
                            TrackTools.setTrackDirection(world, targetPos, dir);
                            setDelay(STANDARD_DELAY);
                        }
                    } else if (WorldPlugin.isBlockAir(world, targetPos, existingState) || replaceableBlocks.contains(existingState.getBlock())) {
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
                Vec3d headPos = getPositionAhead(3.3);
                double size = 0.8;
                AxisAlignedBB entitySearchBox = AABBFactory.start().setBoundsToPoint(headPos).expandHorizontally(size).raiseCeiling(2).build();
                List<EntityLivingBase> entities = EntitySearcher.findLiving()
                        .and(RCEntitySelectors.KILLABLE).around(entitySearchBox).in(world);
                entities.forEach(e -> e.attackEntityFrom(RailcraftDamageSource.BORE, 2));

                ItemStack head = getStackInSlot(0);
                if (!InvTools.isEmpty(head)) {
                    head.damageItem(entities.size(), CartTools.getFakePlayer(this));
                }
            }

            setMoving(hasFuel() && getDelay() == 0);

            if (getDelay() > 0)
                setDelay(getDelay() - 1);
        }

        if (isMoving()) {
            float factorX = -MathHelper.sin((float) Math.toRadians(rotationYaw));
            float factorZ = MathHelper.cos((float) Math.toRadians(rotationYaw));
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
        if (Game.isHost(world)) {
            if (isMinecartPowered())
                spendFuel();
            stockFuel();
            if (outOfFuel())
                addFuel();
            setMinecartPowered(hasFuel() && isActive());
        }
    }

    protected Vec3d getPositionAhead(double offset) {
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

        return new Vec3d(x, posY, z);
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
            double smokeYOffset = 2.8;
            double flameYOffset = 1.1;
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
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, smokeX1, posY + smokeYOffset, smokeZ1, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(EnumParticleTypes.FLAME, flameX1, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ1, 0.0D, 0.0D, 0.0D);
            }
            if (rand.nextInt(4) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, smokeX2, posY + smokeYOffset, smokeZ2, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(EnumParticleTypes.FLAME, flameX2, posY + flameYOffset + (rand.nextGaussian() * randomFactor), flameZ2, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void stockBallast() {
        ItemStack stack = CartToolsAPI.transferHelper().pullStack(this, StackFilters.roomIn(invBallast));
        if (!InvTools.isEmpty(stack))
            invBallast.addStack(stack);
    }

    protected boolean placeBallast(BlockPos targetPos) {
        if (!world.isSideSolid(targetPos, EnumFacing.UP))
            for (IExtInvSlot slot : InventoryIterator.get(invBallast)) {
                ItemStack stack = slot.getStack();
                if (!InvTools.isEmpty(stack) && BallastRegistry.isItemBallast(stack)) {
                    BlockPos.PooledMutableBlockPos searchPos = BlockPos.PooledMutableBlockPos.retain();
                    searchPos.setPos(targetPos);
                    for (int i = 0; i < MAX_FILL_DEPTH; i++) {
                        searchPos.move(EnumFacing.DOWN);
                        if (world.isSideSolid(searchPos, EnumFacing.UP)) {
                            // Fill ballast
                            IBlockState state = InvTools.getBlockStateFromStack(stack, world, targetPos);
                            if (state != null) {
                                slot.decreaseStack();
                                WorldPlugin.setBlockState(world, targetPos, state);
                                return true;
                            }
                        } else {
                            IBlockState state = WorldPlugin.getBlockState(world, searchPos);
                            if (!WorldPlugin.isBlockAir(world, searchPos, state) && !state.getMaterial().isLiquid()) {
                                // Break other blocks first
                                WorldPlugin.playerRemoveBlock(world, searchPos.toImmutable(), CartTools.getFakePlayer(this),
                                        world.getGameRules().getBoolean("doTileDrops") && RailcraftConfig.borePreserveStacks());
                            }
                        }
                    }
                    searchPos.release();
                    return false;
                }
            }
        return false;
    }

    protected void stockTracks() {
        ItemStack stack = CartToolsAPI.transferHelper().pullStack(this, StackFilters.roomIn(invRails));
        if (!InvTools.isEmpty(stack))
            invRails.addStack(stack);
    }

    protected boolean placeTrack(BlockPos targetPos, IBlockState oldState, BlockRailBase.EnumRailDirection shape) {
        EntityPlayer owner = CartTools.getFakePlayer(this);

        if (replaceableBlocks.contains(oldState.getBlock()))
            WorldPlugin.destroyBlockSafe(world, targetPos, owner, true);

        if (WorldPlugin.isBlockAir(world, targetPos, oldState) && world.isSideSolid(targetPos.down(), EnumFacing.UP))
            for (IInvSlot slot : InventoryIterator.get(invRails)) {
                ItemStack stack = slot.getStack();
                if (!InvTools.isEmpty(stack)) {
                    boolean placed = TrackToolsAPI.placeRailAt(stack, (WorldServer) world, targetPos, shape);
                    if (placed) {
                        slot.decreaseStack();
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
                    Block block = WorldPlugin.getBlock(world, new BlockPos(xx, yy, zz));
                    if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA)
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
        if (WorldPlugin.isBlockAir(world, targetPos))
            return true;

        IBlockState targetState = WorldPlugin.getBlockState(world, targetPos);
        if (TrackTools.isRail(targetState)) {
            BlockRailBase.EnumRailDirection targetShape = TrackTools.getTrackDirection(world, targetPos, targetState, this);
            if (preferredShape == targetShape)
                return true;
        } else if (targetState.getBlock() == Blocks.TORCH)
            return true;

        ItemStack head = getStackInSlot(0);
        if (InvTools.isEmpty(head))
            return false;

        if (!canMineBlock(targetPos, targetState))
            return false;

        EntityPlayerMP fakePlayer = CartTools.getFakePlayerWith(this, head);

        // Fires break event within; harvest handled separately
        BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(world, targetPos, targetState, fakePlayer);
        MinecraftForge.EVENT_BUS.post(breakEvent);

        if (breakEvent.isCanceled())
            return false;

        if (RailcraftConfig.borePreserveStacks() && world.getGameRules().getBoolean("doTileDrops")) {
            boolean silk = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, head) > 0;
            NonNullList<ItemStack> items = NonNullList.create();
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, head);

            if (silk) {
                ItemStack stack = HarvestPlugin.getSilkTouchDrop(targetState);
                if (!InvTools.isEmpty(stack)) {
                    items.add(stack);
                }
                // Use modifiable lists for events
            } else {
                targetState.getBlock().getDrops(items, world, targetPos, targetState, fortuneLevel);
            }
            // Start of Event Fire
            BlockEvent.HarvestDropsEvent harvestDropsEvent = new BlockEvent.HarvestDropsEvent(world, targetPos, targetState, fortuneLevel, 1F, items, fakePlayer, silk);
            MinecraftForge.EVENT_BUS.post(harvestDropsEvent);

            if (harvestDropsEvent.isCanceled())
                return false;
            // End of Event Fire

            for (ItemStack stack : items) {
                if (StackFilters.FUEL.test(stack))
                    stack = invFuel.addStack(stack);

                if (!InvTools.isEmpty(stack) && InvTools.isStackEqualToBlock(stack, Blocks.GRAVEL))
                    stack = invBallast.addStack(stack);

                if (!InvTools.isEmpty(stack))
                    stack = CartToolsAPI.transferHelper().pushStack(this, stack);

                if (!InvTools.isEmpty(stack)) {
                    float f = 0.7F;
                    double xr = (world.rand.nextFloat() - 0.5D) * f;
                    double yr = (world.rand.nextFloat() - 0.5D) * f;
                    double zr = (world.rand.nextFloat() - 0.5D) * f;
                    Vec3d spewPos = getPositionAhead(-3.2);
                    spewPos.add(xr, 0.3 + yr, zr);
                    EntityItem entityitem = new EntityItem(world, spewPos.x, spewPos.y, spewPos.z, stack);
                    world.spawnEntity(entityitem);
                }
            }
        }

        WorldPlugin.setBlockToAir(world, targetPos);

        head.damageItem(1, fakePlayer);
        if (head.getItemDamage() > head.getMaxDamage())
            setInventorySlotContents(0, ItemStack.EMPTY);
        return true;
    }

    @SuppressWarnings({"SimplifiableIfStatement", "BooleanMethodIsAlwaysInverted"})
    private boolean canMineBlock(BlockPos targetPos, IBlockState existingState) {
        ItemStack head = getStackInSlot(0);
        if (existingState.getBlockHardness(world, targetPos) < 0)
            return false;
        return isMineableBlock(existingState) && canHeadHarvestBlock(head, existingState);
    }

    protected float getLayerHardness(BlockPos targetPos, BlockRailBase.EnumRailDirection dir) {
        float hardness = layerAction(targetPos, dir, 0F, this::getBlockHardness, (s, r) -> s + r);
        hardness *= HARDNESS_MULTIPLIER;

        ItemStack boreSlot = getStackInSlot(0);
        if (!InvTools.isEmpty(boreSlot) && boreSlot.getItem() instanceof IBoreHead) {
            IBoreHead head = (IBoreHead) boreSlot.getItem();
            double dig = head.getDigModifier();
            hardness /= dig;
            int e = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, boreSlot);
            hardness /= (e * e * 0.2d + 1);
        }

        hardness /= RailcraftConfig.boreMiningSpeedMultiplier();

        return hardness;
    }

    protected float getBlockHardness(BlockPos pos, BlockRailBase.EnumRailDirection dir) {
        if (WorldPlugin.isBlockAir(world, pos))
            return 0;

        IBlockState blockState = WorldPlugin.getBlockState(world, pos);
        if (TrackTools.isRail(blockState)) {
            BlockRailBase.EnumRailDirection trackMeta = TrackTools.getTrackDirection(world, pos, blockState, this);
            if (dir == trackMeta)
                return 0;
        }

        if (blockState.getBlock() == Blocks.TORCH)
            return 0;

        if (blockState.getBlock() == Blocks.OBSIDIAN)
            return 15;

        if (!canMineBlock(pos, blockState))
            return 0.1f;

        float hardness = blockState.getBlockHardness(world, pos);
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
        setFacing(EnumFacing.byIndex(data.getByte("facing")));
        setDelay(data.getInteger("delay"));
        setActive(data.getBoolean("active"));
        setBurnTime(data.getInteger("burnTime"));
        setFuel(data.getInteger("fuel"));
    }

    protected int getDelay() {
        return delay;
//        return dataManager.get(WATCHER_ID_DELAY);
    }

    protected void setDelay(int i) {
        delay = i;
//        dataManager.set(WATCHER_ID_DELAY, Integer.valueOf(i));
    }

    protected boolean isActive() {
        return active;
//        return dataManager.get(WATCHER_ID_ACTIVE) != 0;
    }

    protected void setActive(boolean active) {
        this.active = active;
        Train.State state = active ? State.STOPPED : State.NORMAL;
        Train.get(this).ifPresent(t -> t.setTrainState(state));
//        dataManager.set(WATCHER_ID_ACTIVE, Byte.valueOf((byte)(active ? 1 : 0)));
    }

    protected boolean isMoving() {
        return dataManager.get(MOVING);
    }

    protected void setMoving(boolean move) {
        dataManager.set(MOVING, move);
    }

    public int getBurnTime() {
        return burnTime;
//        return dataManager.get(WATCHER_ID_BURN_TIME);
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
//        dataManager.set(WATCHER_ID_BURN_TIME, Integer.valueOf(burnTime));
    }

    public int getFuel() {
        return fuel;
//        return dataManager.get(WATCHER_ID_FUEL);
    }

    public void setFuel(int i) {
        fuel = i;
//        dataManager.set(WATCHER_ID_FUEL, Integer.valueOf(i));
    }

    public boolean outOfFuel() {
        return getFuel() <= FUEL_CONSUMPTION;
    }

    public boolean hasFuel() {
        return getFuel() > 0;
    }

    protected void stockFuel() {
        ItemStack stack = CartToolsAPI.transferHelper().pullStack(this, StackFilters.roomIn(invFuel));
        if (!InvTools.isEmpty(stack))
            invFuel.addStack(stack);
    }

    protected void addFuel() {
        int burn = 0;
        for (int slot = 0; slot < invFuel.getSizeInventory(); slot++) {
            ItemStack stack = invFuel.getStackInSlot(slot);
            if (!InvTools.isEmpty(stack)) {
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
        if (!InvTools.isEmpty(boreStack))
            boreStack = boreStack.copy();
        dataManager.set(BORE_HEAD, boreStack);
    }

    public @Nullable IBoreHead getBoreHead() {
        ItemStack boreStack = dataManager.get(BORE_HEAD);
        if (boreStack.getItem() instanceof IBoreHead)
            return (IBoreHead) boreStack.getItem();
        return null;
    }

    @Override
    protected void applyDrag() {
        motionX *= CartConstants.STANDARD_DRAG;
        motionY *= 0.0D;
        motionZ *= CartConstants.STANDARD_DRAG;
    }

    @Override
    public boolean isPoweredCart() {
        return true;
    }

    @Override
    public void markDirty() {
        if (!isActive())
            setDelay(STANDARD_DELAY);
    }

    public final EnumFacing getFacing() {
        return dataManager.get(FACING);
    }

    protected final void setFacing(EnumFacing facing) {
        dataManager.set(FACING, facing);

        setYaw();
    }

    @Override
    public boolean isLinkable() {
        return true;
    }

    @Override
    public boolean canLink(EntityMinecart cart) {
        Vec3d pos = getPositionAhead(-LENGTH / 2.0);
        float dist = LinkageManager.LINKAGE_DISTANCE * 2;
        dist = dist * dist;
        return cart.getDistanceSq(pos.x, pos.y, pos.z) < dist;
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

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_BORE;
    }
}
