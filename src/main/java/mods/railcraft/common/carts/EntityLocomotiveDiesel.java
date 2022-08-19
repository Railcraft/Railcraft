/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.api.carts.IFluidCart;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.FluidTools.ProcessType;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.fluids.TankManager;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.logic.DieselMotorLogic.DieselMotorData;
import mods.railcraft.common.util.sounds.RailcraftSoundEvents;
import mods.railcraft.common.util.steam.SteamConstants;
import net.minecraft.entity.item.EntityMinecart;
import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.common.modules.ModuleLocomotives;
import mods.railcraft.common.plugins.buildcraft.triggers.ITemperature;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import net.minecraft.nbt.NBTTagCompound;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Optional;

public class EntityLocomotiveDiesel extends EntityLocomotive implements IFluidCart, ISidedInventory {
    /*
    ISSUES:
        1. when switched from shutdown mode directly to running, the locomotive stays off
        2. DONE - make a new model + smoking animation
        3. make engine + whistle sounds
        4. implement a temperature system
        5. make a way to craft diesel
        6. fix diesel compatibility problems
        7. DONE - fix consumption system
        8. make a crafting recipe
        9. translate newly added items
    
    */

    public static final int SLOT_DIESEL_INPUT = 0;
    public static final int SLOT_DIESEL_PROCESSING = 1;
    public static final int SLOT_DIESEL_OUTPUT = 2;
    private static final int SLOT_TICKET = 3;
    private static final DataParameter<Boolean> SMOKE = DataManagerPlugin.create(DataSerializers.BOOLEAN);
    // private static final byte TICKS_PER_BOILER_CYCLE = 2;
    private static final int FUEL_PER_REQUEST = 3;
    protected static final int CAPACITY = FluidTools.BUCKET_VOLUME * 16;
    private FluidTools.ProcessState processState = FluidTools.ProcessState.RESET;
    public final DieselMotorLogic engine;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 3);
    private final IInventory invTicket = new InventoryMapper(this, SLOT_TICKET, 2).ignoreItemChecks();


    @SuppressWarnings("unused")
    public EntityLocomotiveDiesel(World world) {
        super(world);
    }

    @SuppressWarnings("unused")
    public EntityLocomotiveDiesel(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    {
        setAllowedModes(EnumSet.of(LocoMode.RUNNING, LocoMode.IDLE, LocoMode.SHUTDOWN));

        Logic.Adapter adapter = Logic.Adapter.of(this);

        engine = new DieselMotorLogic(adapter);
        logic = engine;
        engine.setMotorData(new DieselMotorData(
                DieselMotorLogic.TICKS_PER_CYCLE,
                SteamConstants.MAX_HEAT_LOW,
                6, 0));

        //engine.addLogic(new BucketProcessorLogic(adapter, SLOT_DIESEL_INPUT, ProcessType.DRAIN_ONLY));
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.LOCO_DIESEL;
    }

    @Override
    public LocomotiveRenderType getRenderType() {
        return LocomotiveRenderType.DIESEL;
    }

    @Override
    protected int getIdleFuelUse() {
        return 1;
    }

    @Override
    public float getOptimalDistance(EntityMinecart cart) {
        return 0.92f;
    }

    @Override
    protected IInventory getTicketInventory() {
        return invTicket;
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return slot < SLOT_TICKET;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_DIESEL_INPUT:
                //FluidStack fluidStack = FluidItemHelper.getFluidStackInContainer(stack);
                // if (fluidStack != null && fluidStack.amount > FluidTools.BUCKET_VOLUME)
                //     return false;
                if (FluidItemHelper.isEmptyContainer(stack)
                        || isFuelValid(FluidItemHelper.getFluidStackInContainer(stack).getFluid())
                        ) {
                    return true;
                }
                return false;
            case SLOT_TICKET:
                return ItemTicket.FILTER.test(stack);
            default:
                return false;
        }
    }

    public boolean isFuelValid(Fluid fluid) {
        return Fluids.DIESEL.is(fluid);

        // Fluids[] validFuels = {
        //     Fluids.FUEL,
        //     Fluids.BIOFUEL,
        //     Fluids.BIOETHANOL,
        //     Fluids.BIODIESEL,
        //     Fluids.DIESEL,
        //     Fluids.GASOLINE,
        //     Fluids.REFINED_OIL,
        //     Fluids.REFINED_FUEL,
        //     Fluids.REFINED_BIOFUEL,
        //     Fluids.FUEL_DENSE,
        //     Fluids.FUEL_MIXED_HEAVY,
        //     Fluids.FUEL_LIGHT,
        //     Fluids.FUEL_MIXED_LIGHT,
        //     Fluids.FUEL_GASEOUS
        // };

        // for (Fluids fuel : validFuels) {
        //     if (Fluids.areEqual(fluid, fuel.get(1))){
        //         return true;
        //     }
        // }
        // return false;
    }

    @Override
    protected Optional<EnumGui> getGuiType() {
        return EnumGui.LOCO_DIESEL.op();
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(SMOKE, false);
    }

    @Override
    public SoundEvent getWhistle() {
        return RailcraftSoundEvents.ENTITY_LOCOMOTIVE_ELECTRIC_WHISTLE.getSoundEvent();
    }

    @Override
    protected ItemStack getCartItemBase() {
        return RailcraftCarts.LOCO_DIESEL.getStack();
    }

    public TankManager getTankManager() {
        return engine.getLogic(FluidLogic.class).map(FluidLogic::getTankManager).orElse(TankManager.NIL);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(world)) {
            if (engine.tankDiesel.isEmpty()) {
                setMode(LocoMode.SHUTDOWN);
                this.engine.setConsumption(0);
            }

            //here i would like to use EntityLocomotive.getFuelUse(), but it is private
            if (isRunning()) {
                LocoSpeed speed = getSpeed();
                switch (speed) {
                    case SLOWEST:
                        this.engine.setConsumption(2);
                        break;
                    case SLOWER:
                        this.engine.setConsumption(4);
                        break;
                    case NORMAL:
                        this.engine.setConsumption(6);
                        break;
                    default:
                        this.engine.setConsumption(8);
                }
            } else if (isIdle()) {
                this.engine.setConsumption(1);
            } else {
                this.engine.setConsumption(0);
            }

            setSmoking(engine.isRunning());

            processState = FluidTools.processContainer(this, getTankManager(), ProcessType.DRAIN_ONLY, processState);

            if (engine.tankDiesel.getFluidAmount() < engine.tankDiesel.getCapacity() / 2) {
                FluidStack fuelType = engine.getTankDiesel().getFluid();
                if (fuelType != null/* && isFuelValid(fuelType.getFluid())*/) {
                    FluidStack pulled = CartToolsAPI.transferHelper().pullFluid(this, new FluidStack(fuelType.getFluid(), 1000));
                    if (pulled != null) {
                        engine.tankDiesel.fill(pulled, true);
                    }
                }
            }

        } else {
            if (isSmoking()) {
                double rads = renderYaw * Math.PI / 180D;
                float offset = 0.4f;
                float offsetZ = -0.20f;
                ClientEffects.INSTANCE.dieselSmokeEffect(world, posX - Math.cos(rads) * offset, posY + 1.2f,
                        posZ - Math.sin(rads) * offsetZ);
            }
        }
    }

    // @SuppressWarnings("WeakerAccess")
    public boolean isSmoking() {
        return dataManager.get(SMOKE);
    }

    private void setSmoking(boolean smoke) {
        dataManager.set(SMOKE, smoke);
    }

    // @Override
    // public double getTemp() {
    //     return (float) engine.getTemp();
    // }

    @Override
    public int getMoreGoJuice() {
        if(this.engine.isRunning()){
            return FUEL_PER_REQUEST;
        }
        return 0;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound data) {
        super.writeEntityToNBT(data);
        NBTPlugin.writeEnumOrdinal(data, "processState", processState);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound data) {
        super.readEntityFromNBT(data);
        processState = NBTPlugin.readEnumOrdinal(data, "processState", FluidTools.ProcessState.values(),
                FluidTools.ProcessState.RESET);
    }

    @Override
    public boolean canPassFluidRequests(FluidStack fluid) {
        //return engine.getTankDiesel().getFluid().isFluidEqual(fluid);
        return Fluids.DIESEL.is(fluid);
    }

    @Override
    public boolean canAcceptPushedFluid(EntityMinecart requester, FluidStack fluid) {
        //return engine.getTankDiesel().getFluid().isFluidEqual(fluid);
        return Fluids.DIESEL.is(fluid);
    }

    @Override
    public boolean canProvidePulledFluid(EntityMinecart requester, FluidStack fluid) {
        //return engine.getTankDiesel().getFluid().isFluidEqual(fluid);
        return Fluids.DIESEL.is(fluid);
    }

    @Override
    public void setFilling(boolean filling) {
    }
}
