/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
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
    private final InventoryMapper invBurn = InventoryMapper.make(this, SLOT_BURN, 1);
    private final InventoryMapper invStock = InventoryMapper.make(this, SLOT_FUEL_A, 3);
    private final InventoryMapper invFuel = InventoryMapper.make(this, SLOT_BURN, 4);
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
        boiler.setFuelProvider(new SolidFuelProvider(this, SLOT_BURN) {
            @Override
            public double getMoreFuel() {
                if (isShutdown())
                    return 0;
                return super.getMoreFuel();
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
            invStock.moveOneItemTo(invBurn);
            invBurn.moveOneItemTo(invWaterOutput, StackFilters.FUEL.negate());
            ItemStack stack = CartToolsAPI.transferHelper().pullStack(this, StackFilters.roomIn(invStock));
            if (!InvTools.isEmpty(stack))
                invStock.addStack(stack);
            if (isSafeToFill() && tankWater.getFluidAmount() < tankWater.getCapacity() / 2) {
                FluidStack pulled = CartToolsAPI.transferHelper().pullFluid(this, Fluids.WATER.getB(1));
                if (pulled != null) {
                    tankWater.fill(pulled, true);
                }
            }
        }
    }

    @Override
    public boolean needsFuel() {
        FluidStack water = tankWater.getFluid();
        if (water == null || water.amount < tankWater.getCapacity() / 3)
            return true;
        int numItems = invFuel.countItems();
        if (numItems == 0)
            return true;
        int maxItems = invFuel.countMaxItemStackSize();
        // FIXME: This math is weird, it completely ignores empty slots
        return (double) numItems / (double) maxItems < 0.25;
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
    protected EnumGui getGuiType() {
        return EnumGui.LOCO_STEAM;
    }
}
