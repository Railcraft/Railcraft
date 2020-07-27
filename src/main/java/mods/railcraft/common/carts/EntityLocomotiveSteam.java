/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.fluids.tanks.FilteredTank;
import mods.railcraft.common.fluids.tanks.StandardTank;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.steam.IBoilerContainer;
import mods.railcraft.common.util.steam.SteamBoiler;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.Nullable;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public abstract class EntityLocomotiveSteam extends EntityLocomotive implements IFluidCart, IBoilerContainer {
    @SuppressWarnings("WeakerAccess")
    public static final int SLOT_WATER_INPUT = 0;
    @SuppressWarnings("WeakerAccess")
    public static final int SLOT_WATER_PROCESSING = 1;
    public static final int SLOT_WATER_OUTPUT = 2;
    private static final DataParameter<Boolean> SMOKE = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> STEAM = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    private static final byte TICKS_PER_BOILER_CYCLE = 2;
    private static final int FUEL_PER_REQUEST = 3;
    public final SteamBoiler boiler;
    protected final StandardTank tankWater = new FilteredTank(FluidTools.BUCKET_VOLUME * 6) {
        @Override
        public int fillInternal(@Nullable FluidStack resource, boolean doFill) {
            return super.fillInternal(onFillWater(resource), doFill);
        }
    }.setFilterFluid(Fluids.WATER);
    @SuppressWarnings("WeakerAccess")
    protected final StandardTank tankSteam = new FilteredTank(FluidTools.BUCKET_VOLUME * 16)
            .setFilterFluid(Fluids.STEAM)
            .canDrain(false)
            .canFill(false);
    @SuppressWarnings("WeakerAccess")
    protected final InventoryMapper invWaterInput;
    @SuppressWarnings("WeakerAccess")
    protected final InventoryMapper invWaterOutput = InventoryMapper.make(this, SLOT_WATER_OUTPUT, 1);
    protected final InventoryMapper invWaterContainers = InventoryMapper.make(this, SLOT_WATER_INPUT, 3);
    private final TankManager tankManager = new TankManager();
    private int update = rand.nextInt();
    private FluidTools.ProcessState processState = FluidTools.ProcessState.RESET;

    protected EntityLocomotiveSteam(World world) {
        super(world);
    }

    protected EntityLocomotiveSteam(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setMaxReverseSpeed(LocoSpeed.SLOWEST);

        tankManager.add(tankWater);
        tankManager.add(tankSteam);

        invWaterInput = InventoryMapper.make(this, SLOT_WATER_INPUT, 1);
        invWaterInput.withStackSizeLimit(4);

        boiler = new SteamBoiler(tankWater, tankSteam);
        boiler.setEfficiencyModifier(RailcraftConfig.steamLocomotiveEfficiencyMultiplier());
        boiler.setTicksPerCycle(TICKS_PER_BOILER_CYCLE);
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

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        return FluidTools.interactWithFluidHandler(player, hand, getTankManager()) || super.doInteract(player, hand);
    }

    public TankManager getTankManager() {
        return tankManager;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T) getTankManager();
        return super.getCapability(capability, facing);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(world)) {
            update++;

            if (tankWater.isEmpty())
                setMode(LocoMode.SHUTDOWN);

            setSteaming(tankSteam.getFluidAmount() > 0);
            if (tankSteam.getRemainingSpace() >= SteamConstants.STEAM_PER_UNIT_WATER || isShutdown()) {
                boiler.tick(1);

                setSmoking(boiler.isBurning());

                if (!boiler.isBurning())
                    ventSteam();
            }

            if (update % FluidTools.BUCKET_FILL_TIME == 0)
                processState = FluidTools.processContainer(invWaterContainers, tankWater, ProcessType.DRAIN_ONLY, processState);

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
        tankSteam.drainInternal(4, true);
    }

    @Override
    public @Nullable SteamBoiler getBoiler() {
        return boiler;
    }

    @Override
    public float getTemperature() {
        return (float) boiler.getHeat();
    }

    @Override
    public int getMoreGoJuice() {
        FluidStack steam = tankSteam.getFluid();
        if (steam != null && steam.amount >= tankSteam.getCapacity() / 2) {
            tankSteam.drainInternal(SteamConstants.STEAM_PER_UNIT_WATER, true);
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
        NBTPlugin.writeEnumOrdinal(data, "processState", processState);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        tankManager.readTanksFromNBT(data);
        boiler.readFromNBT(data);
        processState = NBTPlugin.readEnumOrdinal(data, "processState", FluidTools.ProcessState.values(), FluidTools.ProcessState.RESET);
    }

    public boolean isSafeToFill() {
        return !boiler.isSuperHeated() || !tankWater.isEmpty();
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

    @Override
    public void steamExplosion(FluidStack resource) {
        explode();
    }
}

