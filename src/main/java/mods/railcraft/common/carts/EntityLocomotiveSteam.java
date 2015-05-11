/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.api.carts.IRefuelableCart;
import mods.railcraft.common.core.RailcraftConfig;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.util.effects.EffectManager;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.SoundHelper;
import mods.railcraft.common.util.steam.Steam;
import mods.railcraft.common.util.steam.SteamBoiler;
import net.minecraftforge.fluids.*;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class EntityLocomotiveSteam extends EntityLocomotive implements IFluidHandler, IRefuelableCart, IFluidCart {
    public static final int SLOT_LIQUID_INPUT = 0;
    public static final int SLOT_LIQUID_OUTPUT = 1;
    public static final byte SMOKE_FLAG = 6;
    public static final byte STEAM_FLAG = 7;
    private static final byte TICKS_PER_BOILER_CYCLE = 2;
    private static final int FUEL_PER_REQUEST = 3;
    private static final int TANK_WATER = 0;
    public SteamBoiler boiler;
    protected StandardTank tankWater;
    protected StandardTank tankSteam;
    protected InventoryMapper invWaterInput;
    protected IInventory invWaterOutput = new InventoryMapper(this, SLOT_LIQUID_OUTPUT, 1);
    private TankManager tankManager;
    private int update = rand.nextInt();

    public EntityLocomotiveSteam(World world) {
        super(world);
    }

    public EntityLocomotiveSteam(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        tankManager = new TankManager();

        tankWater = new FilteredTank(FluidHelper.BUCKET_VOLUME * 6, Fluids.WATER.get());
        tankSteam = new FilteredTank(FluidHelper.BUCKET_VOLUME * 16, Fluids.STEAM.get());

        tankManager.add(tankWater);
        tankManager.add(tankSteam);

        invWaterInput = new InventoryMapper(this, SLOT_LIQUID_INPUT, 1);
        invWaterInput.setStackSizeLimit(4);

        boiler = new SteamBoiler(tankWater, tankSteam);
        boiler.setEfficiencyModifier(RailcraftConfig.steamLocomotiveEfficiencyMultiplier());
        boiler.setTicksPerCycle(TICKS_PER_BOILER_CYCLE);
    }

    @Override
    public String getWhistle() {
        return SoundHelper.SOUND_LOCOMOTIVE_STEAM_WHISTLE;
    }

    @Override
    protected ItemStack getCartItemBase() {
        return EnumCart.LOCO_STEAM_SOLID.getCartItem();
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(worldObj)) {
            update++;

            if (tankWater.isEmpty())
                setMode(LocoMode.SHUTDOWN);

            setSteaming(tankSteam.getFluidAmount() > 0);
            if (tankSteam.getRemainingSpace() >= Steam.STEAM_PER_UNIT_WATER || isShutdown()) {
                boiler.tick(1);

                setSmoking(boiler.isBurning());

                if (!boiler.isBurning())
                    ventSteam();
            }

            if (update % FluidHelper.BUCKET_FILL_TIME == 0)
                FluidHelper.drainContainers(this, this, SLOT_LIQUID_INPUT, SLOT_LIQUID_OUTPUT);
        } else {
            if (isSmoking())
                if (rand.nextInt(3) == 0) {
                    double rads = renderYaw * Math.PI / 180D;
                    float offset = 0.4f;
                    worldObj.spawnParticle("largesmoke", posX - Math.cos(rads) * offset, posY + 1.2f, posZ - Math.sin(rads) * offset, 0, 0, 0);
                }
            if (isSteaming())
                EffectManager.instance.steamEffect(worldObj, this, boundingBox.minY - posY - 0.3);
        }
    }

    public boolean isSmoking() {
        return getFlag(SMOKE_FLAG);
    }

    private void setSmoking(boolean smoke) {
        if (getFlag(SMOKE_FLAG) != smoke)
            setFlag(SMOKE_FLAG, smoke);
    }

    public boolean isSteaming() {
        return getFlag(STEAM_FLAG);
    }

    private void setSteaming(boolean steam) {
        if (getFlag(STEAM_FLAG) != steam)
            setFlag(STEAM_FLAG, steam);
    }

    private void ventSteam() {
        tankSteam.drain(4, true);
    }

    @Override
    public int getMoreGoJuice() {
        FluidStack steam = tankSteam.getFluid();
        if (steam != null && steam.amount >= tankSteam.getCapacity() / 2) {
            tankSteam.drain(Steam.STEAM_PER_UNIT_WATER, true);
            return FUEL_PER_REQUEST;
        }
        return 0;
//        return 100;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        tankManager.writeTanksToNBT(data);
        boiler.writeToNBT(data);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        tankManager.readTanksFromNBT(data);
        boiler.readFromNBT(data);
    }

    public boolean isSafeToFill() {
        return !boiler.isSuperHeated() || !tankWater.isEmpty();
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (doFill && Fluids.WATER.is(resource))
            if (boiler.isSuperHeated() && Steam.BOILERS_EXPLODE) {
                FluidStack water = tankWater.getFluid();
                if (water == null || water.amount <= 0)
                    explode();
            }
        return tankWater.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection direction) {
        return tankManager.getTankInfo();
    }

    @Override
    public boolean canPassFluidRequests(Fluid fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canAcceptPushedFluid(EntityMinecart requester, Fluid fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canProvidePulledFluid(EntityMinecart requester, Fluid fluid) {
        return false;
    }

    @Override
    public void setFilling(boolean filling) {
    }
}

