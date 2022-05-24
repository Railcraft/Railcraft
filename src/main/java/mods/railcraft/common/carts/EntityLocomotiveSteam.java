/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.logic.BoilerLogic.BoilerData;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.modules.ModuleLocomotives;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class EntityLocomotiveSteam extends EntityLocomotive implements IFluidCart, ITemperature {
    @SuppressWarnings("WeakerAccess")
    public static final int SLOT_WATER_INPUT = 0;
    @SuppressWarnings("WeakerAccess")
    public static final int SLOT_WATER_PROCESSING = 1;
    public static final int SLOT_WATER_OUTPUT = 2;
    private static final DataParameter<Boolean> SMOKE = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STEAM = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final byte TICKS_PER_BOILER_CYCLE = 2;
    private static final int FUEL_PER_REQUEST = 3;
    public final BoilerLogic boiler;
    @SuppressWarnings("WeakerAccess")
    protected final InventoryMapper invWaterInput;
    @SuppressWarnings("WeakerAccess")
    protected final InventoryMapper invWaterOutput = InventoryMapper.make(this, SLOT_WATER_OUTPUT, 1);
    protected final InventoryMapper invWaterContainers = InventoryMapper.make(this, SLOT_WATER_INPUT, 3);
    private FluidTools.ProcessState processState = FluidTools.ProcessState.RESET;

    protected EntityLocomotiveSteam(World world) {
        super(world);
    }

    protected EntityLocomotiveSteam(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setMaxReverseSpeed(LocoSpeed.SLOWEST);

        invWaterInput = InventoryMapper.make(this, SLOT_WATER_INPUT, 1);
        invWaterInput.withStackSizeLimit(4);

        Logic.Adapter adapter = Logic.Adapter.of(this);

        boiler = new BoilerLogic(adapter);
        logic = boiler;
        boiler.setBoilerData(new BoilerData(1,
                TICKS_PER_BOILER_CYCLE,
                ModuleLocomotives.config.steamLocomotiveEfficiency,
                SteamConstants.MAX_HEAT_LOW,
                6, 16));

        boiler.addLogic(new ExploderLogic(adapter) {
            @Override
            protected void boom() {
                explode();
            }
        });

        boiler.addLogic(new BucketProcessorLogic(adapter, SLOT_WATER_INPUT, ProcessType.DRAIN_ONLY));
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(SMOKE, false);
        dataManager.register(STEAM, false);
    }

    @Override
    public SoundEvent getWhistle() {
        return RailcraftSoundEvents.ENTITY_LOCOMOTIVE_STEAM_WHISTLE.getSoundEvent();
    }

    @Override
    protected ItemStack getCartItemBase() {
        return RailcraftCarts.LOCO_STEAM_SOLID.getStack();
    }

    public TankManager getTankManager() {
        return boiler.getLogic(FluidLogic.class).map(FluidLogic::getTankManager).orElse(TankManager.NIL);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(world)) {
            if (boiler.tankWater.isEmpty())
                setMode(LocoMode.SHUTDOWN);

            setSteaming(boiler.tankSteam.getFluidAmount() > 0);
            setSmoking(boiler.isBurning());

            if (isShutdown() && !boiler.isBurning())
                ventSteam();

        } else {
            if (isSmoking()) {
                double rads = renderYaw * Math.PI / 180D;
                float offset = 0.4f;
                ClientEffects.INSTANCE.locomotiveEffect(world, posX - Math.cos(rads) * offset, posY + 1.5f, posZ - Math.sin(rads) * offset);
            }
            if (isSteaming())
                ClientEffects.INSTANCE.steamEffect(world, this, getEntityBoundingBox().minY - posY - 0.3);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSmoking() {
        return dataManager.get(SMOKE);
    }

    private void setSmoking(boolean smoke) {
        dataManager.set(SMOKE, smoke);
    }

    @SuppressWarnings("WeakerAccess")
    public boolean isSteaming() {
        return dataManager.get(STEAM);
    }

    private void setSteaming(boolean steam) {
        dataManager.set(STEAM, steam);
    }

    private void ventSteam() {
        boiler.tankSteam.drainInternal(4, true);
    }

    @Override
    public double getTemp() {
        return (float) boiler.getTemp();
    }

    @Override
    public int getMoreGoJuice() {
        FluidStack steam = boiler.tankSteam.getFluid();
        if (steam != null && steam.amount >= boiler.tankSteam.getCapacity() / 2) {
            boiler.tankSteam.drainInternal(SteamConstants.STEAM_PER_UNIT_WATER, true);
            return FUEL_PER_REQUEST;
        }
        return 0;
//        return 100;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        NBTPlugin.writeEnumOrdinal(data, "processState", processState);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        processState = NBTPlugin.readEnumOrdinal(data, "processState", FluidTools.ProcessState.values(), FluidTools.ProcessState.RESET);
    }

    public boolean isSafeToFill() {
        return !boiler.isSuperHeated() || !boiler.tankWater.isEmpty();
    }

    @Override
    public boolean canPassFluidRequests(FluidStack fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canAcceptPushedFluid(EntityMinecart requester, FluidStack fluid) {
        return Fluids.WATER.is(fluid);
    }

    @Override
    public boolean canProvidePulledFluid(EntityMinecart requester, FluidStack fluid) {
        return false;
    }

    @Override
    public void setFilling(boolean filling) {
    }
}

