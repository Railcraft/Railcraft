/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.common.util.steam.ISteamUser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import mods.railcraft.common.blocks.RailcraftTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.blocks.machine.MultiBlockPattern;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.inventory.StandaloneInventory;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.ITileFilter;
import mods.railcraft.common.util.steam.Steam;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraft.inventory.ISidedInventory;
import net.minecraftforge.fluids.FluidStack;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class TileBoilerFirebox extends TileBoiler implements IInventory, ISidedInventory, ITemperature {

    protected static final int SLOT_LIQUID_INPUT = 0;
    protected static final int SLOT_LIQUID_OUTPUT = 1;
    public final SteamBoiler boiler;
    private boolean wasLit;
    protected final StandaloneInventory inventory;
    protected IInventory invWaterInput = new InventoryMapper(this, SLOT_LIQUID_INPUT, 1);
    protected IInventory invWaterOutput = new InventoryMapper(this, SLOT_LIQUID_OUTPUT, 1, false);

    protected TileBoilerFirebox(int invSize) {
        super();
        inventory = new StandaloneInventory(invSize, (IInventory) this);
        boiler = new SteamBoiler(tankWater, tankSteam);
        boiler.setTile(this);
    }

    @Override
    public IIcon getIcon(int side) {
        if (side > 1 && isBurning())
            return getMachineType().getTexture(6);
        return getMachineType().getTexture(side);
    }

    @Override
    protected void onPatternLock(MultiBlockPattern pattern) {
        int capacity = getNumTanks() * FluidHelper.BUCKET_VOLUME;
        tankManager.setCapacity(TANK_STEAM, capacity * getSteamCapacityPerTank());
        tankManager.setCapacity(TANK_WATER, capacity * 4);
        boiler.setMaxHeat(((BoilerPattern) pattern).maxHeat);
        boiler.setTicksPerCycle(((BoilerPattern) pattern).ticksPerCycle);
    }

    public boolean isBurning() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock == null)
            return false;
        return mBlock.boiler.isBurning();
    }

    protected void updateLighting() {
        boolean b = isBurning();
        if (wasLit != b) {
            wasLit = b;
            worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
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
                float f = (float) xCoord + 0.5F;
                float f1 = (float) yCoord + 0.4375F + (random.nextFloat() * 3F / 16F);
                float f2 = (float) zCoord + 0.5F;
                float f3 = 0.52F;
                float f4 = random.nextFloat() * 0.6F - 0.3F;
                worldObj.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
                worldObj.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public ITileFilter getOutputFilter() {
        return ISteamUser.FILTER;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (Game.isNotHost(getWorld()))
            return;

        if (!isMaster || getState() == MultiBlockState.INVALID) {
            boiler.reduceHeat(getNumTanks());
            return;
        }

        if (isMaster) {
            process();

            boiler.tick(getNumTanks());

            if (clock % FluidHelper.BUCKET_FILL_TIME == 0)
                processBuckets();
        }
    }

    protected abstract void process();

    protected void processBuckets() {
        FluidHelper.drainContainers(this, inventory, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        tankManager.writeTanksToNBT(data);
        inventory.writeToNBT("inv", data);

        boiler.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        tankManager.readTanksFromNBT(data);
        inventory.readFromNBT("inv", data);

        boiler.readFromNBT(data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        tankManager.writePacketData(data);
        data.writeBoolean(boiler.isBurning());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        tankManager.readPacketData(data);
        boiler.setBurning(data.readBoolean());
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return mBlock.inventory.decrStackSize(i, j);
        return null;
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
    public String getInventoryName() {
        return getName();
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
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
    public ItemStack getStackInSlotOnClosing(int var1) {
        return null;
    }

    protected boolean handleClick(EntityPlayer player, int side) {
        return FluidHelper.handleRightClick(this, ForgeDirection.getOrientation(side), player, true, false);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(TANK_WATER, resource, doFill);
    }

    @Override
    public float getTemperature() {
        TileBoilerFirebox mBlock = (TileBoilerFirebox) getMasterBlock();
        if (mBlock != null)
            return (float) mBlock.boiler.getHeat();
        return Steam.COLD_TEMP;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return RailcraftTileEntity.isUseableByPlayerHelper(this, player);
    }

}
