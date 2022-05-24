/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.client.render.carts.LocomotiveRenderType;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.FluidTools;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import java.util.Optional;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityLocomotiveSteamSolid extends EntityLocomotiveSteam implements ISidedInventory {
    private static final int SLOT_BURN = 3;
    private static final int SLOT_FUEL_A = 4;
    private static final int SLOT_FUEL_B = 5;
    private static final int SLOT_FUEL_C = 6;
    private static final int SLOT_TICKET = 7;
    private static final int SLOT_DESTINATION = 8;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 7);
    private final InventoryMapper firebox = InventoryMapper.make(this, SLOT_BURN, 1);
    private final InventoryMapper bunker = InventoryMapper.make(this, SLOT_FUEL_A, 3);
    private final InventoryMapper fuel = InventoryMapper.make(this, SLOT_BURN, 4);
    private final InventoryMapper invTicket = new InventoryMapper(this, SLOT_TICKET, 2).ignoreItemChecks();
//    private boolean outOfWater = true;

    public EntityLocomotiveSteamSolid(World world) {
        super(world);
    }

    public EntityLocomotiveSteamSolid(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.LOCO_STEAM_SOLID;
    }

    @Override
    public LocomotiveRenderType getRenderType() {
        return LocomotiveRenderType.STEAM_SOLID;
    }

    {
        boiler.setFuelProvider(new SolidFuelProvider(firebox, bunker, invWaterOutput) {
            @Override
            public double burnFuelUnit() {
                if (isShutdown())
                    return 0;
                return super.burnFuelUnit();
            }
        });
    }

    @Override
    public void onUpdate() {
//        if (Game.isHost(world)) {
//            if (RailcraftConfig.printSignalDebug()) {
//                if (outOfWater && !tankWater.isEmpty())
//                    outOfWater = false;
//                else if (!outOfWater && tankWater.isEmpty()) {
//                    outOfWater = true;
//                    Game.log(Level.INFO, "Solid Steam Locomotive ran out of water! [{0}, {1}, {2}] [locked:{3}] [idle:{4}] [mode:{5}]", posX, posY, posZ, Train.getTrain(this).isTrainLockedDown(), isIdle(), getMode().name());
//                }
//            }
//        }
        super.onUpdate();

        if (Game.isHost(world)) {
            firebox.moveOneItemTo(invWaterOutput, StackFilters.FUEL.negate());
            ItemStack stack = CartToolsAPI.transferHelper().pullStack(this, StackFilters.roomIn(bunker));
            if (!InvTools.isEmpty(stack))
                bunker.addStack(stack);
            if (isSafeToFill() && boiler.tankWater.getFluidAmount() < boiler.tankWater.getCapacity() / 2) {
                FluidStack pulled = CartToolsAPI.transferHelper().pullFluid(this, Fluids.WATER.getB(1));
                if (pulled != null) {
                    boiler.tankWater.fill(pulled, true);
                }
            }
        }
    }

    @Override
    protected IInventory getTicketInventory() {
        return invTicket;
    }

    @Override
    public int getSizeInventory() {
        return 9;
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
            case SLOT_BURN:
            case SLOT_FUEL_A:
            case SLOT_FUEL_B:
            case SLOT_FUEL_C:
                return StackFilters.FUEL.test(stack);
            case SLOT_WATER_INPUT:
                FluidStack fluidStack = FluidItemHelper.getFluidStackInContainer(stack);
                if (fluidStack != null && fluidStack.amount > FluidTools.BUCKET_VOLUME)
                    return false;
                return FluidItemHelper.containsFluid(stack, Fluids.WATER.get(1));
            case SLOT_TICKET:
                return ItemTicket.FILTER.test(stack);
            default:
                return false;
        }
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return StackFilters.FUEL.test(stack);
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }

    @Override
    protected Optional<EnumGui> getGuiType() {
        return EnumGui.LOCO_STEAM.op();
    }
}
