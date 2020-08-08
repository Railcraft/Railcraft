/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.interfaces.ITileLit;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.ItemHandlerFactory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.steam.ISteamUser;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Random;
import java.util.function.Predicate;

import static mods.railcraft.common.blocks.structures.BlockBoilerFirebox.BURNING;
import static net.minecraft.util.EnumParticleTypes.FLAME;
import static net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerFirebox extends TileBoiler implements ISidedInventory, ITileLit {

    protected static final int SLOT_LIQUID_INPUT = 0;
    protected static final int SLOT_LIQUID_OUTPUT = 1;
    public final SteamBoiler boiler;
    protected final TankManager tankManager = new TankManager();
    protected final FilteredTank tankWater = new FilteredTank(4 * FluidTools.BUCKET_VOLUME, this) {
        @Override
        public int fillInternal(@Nullable FluidStack resource, boolean doFill) {
            if (!isValidMaster()) return 0;
            return super.fillInternal(onFillWater(resource), doFill);
        }
    }.setFilterFluid(Fluids.WATER);
    protected final FilteredTank tankSteam = new FilteredTank(16 * FluidTools.BUCKET_VOLUME, this)
            .setFilterFluid(Fluids.STEAM);
    private boolean wasLit;
    protected final InventoryAdvanced inventory;
    //    protected final InventoryMapper invWaterInput = InventoryMapper.make(this, SLOT_LIQUID_INPUT, 1);
    protected final InventoryMapper invWaterOutput = InventoryMapper.make(this, SLOT_LIQUID_OUTPUT, 1).ignoreItemChecks();

    protected TileBoilerFirebox(int invSize) {
        inventory = new InventoryAdvanced(invSize).callbackInv(this);

        tankManager.add(tankWater);
        tankManager.add(tankSteam);

        tankWater.setCanDrain(false);
        tankSteam.setCanFill(false);

        boiler = new SteamBoiler(tankWater, tankSteam);
        boiler.setTile(this);
    }

    @Override
    protected void onPatternLock(StructurePattern pattern) {
        int capacity = getNumTanks() * FluidTools.BUCKET_VOLUME;
        tankManager.setCapacity(TANK_STEAM, capacity * getSteamCapacityPerTank());
        tankManager.setCapacity(TANK_WATER, capacity * 4);
        boiler.setMaxHeat(pattern.getAttachedDataOr(0, BoilerData.EMPTY).maxHeat);
        boiler.setTicksPerCycle(pattern.getAttachedDataOr(0, BoilerData.EMPTY).ticksPerCycle);
    }

    public boolean isBurning() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        return mBlock != null && mBlock.boiler.isBurning();
    }

    protected void updateLighting() {
        boolean b = isBurning();
        if (wasLit != b) {
            wasLit = b;
            world.checkLightFor(EnumSkyBlock.BLOCK, getPos());
            markBlockForUpdate();
        }
    }

    @Override
    public int getLightValue() {
        if (isStructureValid() && isBurning())
            return 13;
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(Random random) {
        if (isStructureValid()) {
            updateLighting();
            if (isBurning() && random.nextInt(100) < 20) {
                float f = (float) getX() + 0.5F;
                float f1 = getY() + 0.4375F + (random.nextFloat() * 3F / 16F);
                float f2 = (float) getZ() + 0.5F;
                float f3 = 0.52F;
                float f4 = random.nextFloat() * 0.6F - 0.3F;
                world.spawnParticle(FLAME, f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(SMOKE_NORMAL, f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(FLAME, f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(SMOKE_NORMAL, f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(FLAME, f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(SMOKE_NORMAL, f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(FLAME, f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(SMOKE_NORMAL, f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public abstract boolean needsFuel();

    @Override
    public Predicate<TileEntity> getOutputFilter() {
        return ISteamUser.FILTER;
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld()))
            return;

        if (!isMaster || getState() == MultiBlockState.INVALID) {
            boiler.reduceHeat(getNumTanks());
            return;
        }

        if (isValidMaster()) {
            process();

            boiler.tick(getNumTanks());

            if (clock % FluidTools.BUCKET_FILL_TIME == 0)
                processBuckets();
        }
    }

    protected abstract void process();

    protected void processBuckets() {
        FluidTools.drainContainers(tankManager, inventory, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
    }

    @Override
    protected void onPatternChanged() {
        super.onPatternChanged();
        reset();
    }

    @Override
    protected void onMasterChanged() {
        super.onMasterChanged();
        reset();
    }

    private void reset() {
        tankManager.get(TANK_STEAM).setFluid(null);
        boiler.reset();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        inventory.writeToNBT("inv", data);

        boiler.writeToNBT(data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
        inventory.readFromNBT("inv", data);

        boiler.readFromNBT(data);
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
//        tankManager.writePacketData(data);
        data.writeBoolean(boiler.isBurning());
        data.writeInt(tankManager.get(TANK_STEAM).getCapacity());
        data.writeInt(tankManager.get(TANK_WATER).getCapacity());
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
//        tankManager.readPacketData(data);
        boiler.setBurning(data.readBoolean());
        tankManager.get(TANK_STEAM).setCapacity(data.readInt());
        tankManager.get(TANK_WATER).setCapacity(data.readInt());
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.decrStackSize(i, j);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getStackInSlot(i);
        else
            return inventory.getStackInSlot(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            mBlock.inventory.setInventorySlotContents(i, itemstack);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return TileRailcraft.isUsableByPlayerHelper(this, player);
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.removeStackFromSlot(i);
        else
            return inventory.removeStackFromSlot(i);
    }

    @Override
    public int getField(int id) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getField(id);
        else
            return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            mBlock.inventory.setField(id, value);
        else
            inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getFieldCount();
        else
            return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            mBlock.inventory.clear();
        else
            inventory.clear();
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return base.withProperty(BURNING, wasLit);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(ItemHandlerFactory.wrap(this, facing));
        }
        return super.getCapability(capability, facing);
    }
}
