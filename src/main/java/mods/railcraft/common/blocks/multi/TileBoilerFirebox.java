/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import mods.railcraft.common.blocks.RailcraftTileEntity;
import mods.railcraft.common.blocks.machine.interfaces.ITileLit;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import mods.railcraft.common.util.steam.ISteamUser;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Random;
import java.util.function.Predicate;

import static net.minecraft.util.EnumParticleTypes.FLAME;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerFirebox<F extends TileBoilerFirebox<F>> extends TileBoiler<F> implements ISidedInventory, ITileLit {

    protected static final int SLOT_LIQUID_INPUT = 0;
    protected static final int SLOT_LIQUID_OUTPUT = 1;
    public final SteamBoiler boiler;
    private boolean wasLit;
    protected final StandaloneInventory inventory;
    protected InventoryMapper invWaterInput = InventoryMapper.make(this, SLOT_LIQUID_INPUT, 1);
    protected InventoryMapper invWaterOutput = InventoryMapper.make(this, SLOT_LIQUID_OUTPUT, 1, false);

    protected TileBoilerFirebox(int invSize) {
        inventory = new StandaloneInventory(invSize, (IInventory) this);
        boiler = new SteamBoiler(tankWater, tankSteam);
        boiler.setTile(this);
    }

    @Override
    protected void onPatternLock(MultiBlockPattern pattern) {
        int capacity = getNumTanks() * FluidTools.BUCKET_VOLUME;
        tankManager.setCapacity(TANK_STEAM, capacity * getSteamCapacityPerTank());
        tankManager.setCapacity(TANK_WATER, capacity * 4);
        boiler.setMaxHeat(((BoilerPattern) pattern).maxHeat);
        boiler.setTicksPerCycle(((BoilerPattern) pattern).ticksPerCycle);
    }

    public boolean isBurning() {
        F mBlock = getMasterBlock();
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
                world.spawnParticle(FLAME, f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(FLAME, f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                world.spawnParticle(FLAME, f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
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

        if (isMaster) {
            process();

            boiler.tick(getNumTanks());

            if (clock % FluidTools.BUCKET_FILL_TIME == 0)
                processBuckets();
        }
    }

    protected abstract void process();

    protected void processBuckets() {
        FluidTools.drainContainers(this.tankManager, inventory, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
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
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);
//        tankManager.readPacketData(data);
        boiler.setBurning(data.readBoolean());
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.decrStackSize(i, j);
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getStackInSlot(i);
        else
            return inventory.getStackInSlot(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        F mBlock = getMasterBlock();
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
        return RailcraftTileEntity.isUsableByPlayerHelper(this, player);
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.removeStackFromSlot(i);
        else
            return inventory.removeStackFromSlot(i);
    }

    @Override
    public int getField(int id) {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getField(id);
        else
            return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            mBlock.inventory.setField(id, value);
        else
            inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.getFieldCount();
        else
            return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        F mBlock = getMasterBlock();
        if (mBlock != null)
            mBlock.inventory.clear();
        else
            inventory.clear();
    }
}
