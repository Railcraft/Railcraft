/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.api.carts.locomotive.LocomotiveRenderType;
import mods.railcraft.common.fluids.FluidHelper;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.fluids.Fluids;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.items.ItemTicket;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilter;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.SolidFuelProvider;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class EntityLocomotiveSteamSolid extends EntityLocomotiveSteam implements ISidedInventory, IItemCart {
    private static final int SLOT_BURN = 2;
    private static final int SLOT_FUEL_A = 3;
    private static final int SLOT_FUEL_B = 4;
    private static final int SLOT_FUEL_C = 5;
    private static final int SLOT_TICKET = 6;
    private static final int SLOT_DESTINATION = 7;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 7);
    private final IInventory invBurn = new InventoryMapper(this, SLOT_BURN, 1);
    private final IInventory invStock = new InventoryMapper(this, SLOT_FUEL_A, 3);
    private final IInventory invFuel = new InventoryMapper(this, SLOT_BURN, 4);
    private final IInventory invTicket = new InventoryMapper(this, SLOT_TICKET, 2, false);
//    private boolean outOfWater = true;

    public EntityLocomotiveSteamSolid(World world) {
        super(world);
    }

    public EntityLocomotiveSteamSolid(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.LOCO_STEAM_SOLID;
    }

    @Override
    public LocomotiveRenderType getRenderType() {
        return LocomotiveRenderType.STEAM_SOLID;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
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
//        if (Game.isHost(worldObj)) {
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

        if (Game.isHost(worldObj)) {
            InvTools.moveOneItem(invStock, invBurn);
            InvTools.moveOneItem(invBurn, invWaterOutput, FluidContainerRegistry.EMPTY_BUCKET);
            if (InvTools.isEmptySlot(invStock)) {
                ItemStack stack = CartTools.transferHelper.pullStack(this, StackFilter.FUEL);
                if (stack != null)
                    InvTools.moveItemStack(stack, invStock);
            }
            if (isSafeToFill() && tankWater.getFluidAmount() < tankWater.getCapacity() / 2) {
                FluidStack pulled = CartTools.transferHelper.pullFluid(this, Fluids.WATER.getB(1));
                tankWater.fill(pulled, true);
            }
        }
    }

    @Override
    protected void openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.LOCO_STEAM, player, worldObj, this);
    }

    @Override
    public boolean needsRefuel() {
        FluidStack water = tankWater.getFluid();
        if (water == null || water.amount < tankWater.getCapacity() / 3)
            return true;
        int numItems = InvTools.countItems(invFuel);
        if (numItems == 0)
            return true;
        int maxItems = InvTools.countMaxItemStackSize(invFuel);
        return (double) numItems / (double) maxItems < 0.25;
    }

    @Override
    protected IInventory getTicketInventory() {
        return invTicket;
    }

    @Override
    public int getSizeInventory() {
        return 8;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot < SLOT_TICKET;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case SLOT_BURN:
            case SLOT_FUEL_A:
            case SLOT_FUEL_B:
            case SLOT_FUEL_C:
                return StackFilter.FUEL.matches(stack);
            case SLOT_LIQUID_INPUT:
                FluidStack fluidStack = FluidItemHelper.getFluidStackInContainer(stack);
                if (fluidStack != null && fluidStack.amount > FluidHelper.BUCKET_VOLUME)
                    return false;
                return FluidItemHelper.containsFluid(stack, Fluids.WATER.get(1));
            case SLOT_TICKET:
                return ItemTicket.FILTER.matches(stack);
            default:
                return false;
        }
    }

    @Override
    public boolean canPassItemRequests() {
        return true;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return StackFilter.FUEL.matches(stack);
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return false;
    }
}
