///*------------------------------------------------------------------------------
// Copyright (c) CovertJaguar, 2011-2017
// http://railcraft.info
//
// This code is the property of CovertJaguar
// and may only be used with explicit written
// permission unless otherwise specified on the
// license page at http://railcraft.info/wiki/info:license.
// -----------------------------------------------------------------------------*/
//package mods.railcraft.common.plugins.thaumcraft;
//
//import mods.railcraft.client.render.carts.LocomotiveRenderType;
//import mods.railcraft.common.carts.EntityLocomotiveSteam;
//import mods.railcraft.common.carts.IRailcraftCartContainer;
//import mods.railcraft.common.carts.RailcraftCarts;
//import mods.railcraft.common.fluids.FluidItemHelper;
//import mods.railcraft.common.fluids.Fluids;
//import mods.railcraft.common.gui.EnumGui;
//import mods.railcraft.common.gui.GuiHandler;
//import mods.railcraft.common.items.ItemTicket;
//import mods.railcraft.common.plugins.forge.DataManagerPlugin;
//import mods.railcraft.common.plugins.forge.FuelPlugin;
//import mods.railcraft.common.util.inventory.InvTools;
//import mods.railcraft.common.util.inventory.iterators.IInvSlot;
//import mods.railcraft.common.util.inventory.iterators.InventoryIterator;
//import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
//import mods.railcraft.common.util.misc.Game;
//import mods.railcraft.common.util.steam.EssentiaFuelProvider;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.IInventory;
//import net.minecraft.inventory.ISidedInventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.network.datasync.DataParameter;
//import net.minecraft.network.datasync.DataSerializers;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.world.World;
//import net.minecraftforge.common.ForgeModContainer;
//import net.minecraftforge.fluids.FluidStack;
//import net.minecraftforge.fml.common.Optional;
//import thaumcraft.api.aspects.Aspect;
//import thaumcraft.api.aspects.AspectList;
//import thaumcraft.api.aspects.IAspectContainer;
//
//import javax.annotation.Nonnull;
//import java.util.Map.Entry;
//
//import static mods.railcraft.common.util.inventory.InvTools.sizeOf;
//
///**
// * @author CovertJaguar <http://www.railcraft.info>
// */
//@Optional.Interface(iface = "thaumcraft.api.aspects.IAspectContainer", modid = "Thaumcraft")
//public class EntityLocomotiveSteamMagic extends EntityLocomotiveSteam implements ISidedInventory, IAspectContainer {
//
//    private static final int SLOT_BURN = 2;
//    private static final int SLOT_FUEL_A = 3;
//    private static final int SLOT_FUEL_B = 4;
//    private static final int SLOT_FUEL_C = 5;
//    private static final int SLOT_TICKET = 6;
//    private static final int SLOT_DESTINATION = 7;
//    private static final int[] SLOTS = InvTools.buildSlotArray(0, 7);
//    private static final DataParameter<Integer> FIRE_ASPECT = DataManagerPlugin.create(DataSerializers.VARINT);
//    private static final DataParameter<Integer> WATER_ASPECT = DataManagerPlugin.create(DataSerializers.VARINT);
//    private final InventoryMapper invBurn = InventoryMapper.make(this, SLOT_BURN, 1);
//    private final InventoryMapper invStock = InventoryMapper.make(this, SLOT_FUEL_A, 3);
//    private final InventoryMapper invFuel = InventoryMapper.make(this, SLOT_BURN, 4);
//    private final InventoryMapper invTicket = new InventoryMapper(this, SLOT_TICKET, 2, false);
//
//    private EssentiaTank fireAspect;
//    private EssentiaTank waterAspect;
//
//    public EntityLocomotiveSteamMagic(World world) {
//        super(world);
//    }
//
//    public EntityLocomotiveSteamMagic(World world, double x, double y, double z) {
//        super(world, x, y, z);
//    }
//
//    @Override
//    public IRailcraftCartContainer getCartType() {
//        return RailcraftCarts.LOCO_STEAM_MAGIC;
//    }
//
//    @Override
//    public LocomotiveRenderType getRenderType() {
//        return LocomotiveRenderType.STEAM_MAGIC;
//    }
//
//    @Override
//    public boolean doesContainerAccept(Aspect tag) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    protected void entityInit() {
//        super.entityInit();
//
//        fireAspect = new EssentiaTank(Aspect.FIRE, 256, dataManager, FIRE_ASPECT);
//        waterAspect = new EssentiaTank(Aspect.WATER, 256, dataManager, WATER_ASPECT);
//
//        boiler.setFuelProvider(new EssentiaFuelProvider(fireAspect) {
//            @Override
//            public double getMoreFuel() {
//                if (isShutdown())
//                    return 0;
//                return super.getMoreFuel();
//            }
//
//        });
//    }
//
//    @Override
//    public void onUpdate() {
//        super.onUpdate();
//
//        if (Game.isHost(world)) {
//            InvTools.moveOneItem(invStock, invBurn);
//            InvTools.moveOneItem(invBurn, invWaterOutput, (stack) -> stack.getItem() == ForgeModContainer.getInstance().universalBucket);
//        }
//    }
//
//    @Override
//    protected void openGui(@Nonnull EntityPlayer player) {
//        GuiHandler.openGui(EnumGui.LOCO_STEAM, player, world, this);
//    }
//
//    @Override
//    public boolean needsFuel() {
//        FluidStack water = tankWater.getFluid();
//        if (water == null || water.amount < tankWater.getCapacity() / 2)
//            return true;
//        if (InvTools.countItems(invFuel) < 16)
//            return true;
//        for (IInvSlot slot : InventoryIterator.getVanilla((IInventory) invFuel)) {
//            ItemStack stack = slot.getStack();
//            if (InvTools.isEmpty(stack) || sizeOf(stack) < stack.getMaxStackSize() / 4)
//                return true;
//        }
//        return false;
//    }
//
//    public EssentiaTank getFireAspect() {
//        return fireAspect;
//    }
//
//    public EssentiaTank getWaterAspect() {
//        return waterAspect;
//    }
//
//    @Override
//    protected IInventory getTicketInventory() {
//        return invTicket;
//    }
//
//    @Override
//    public int getSizeInventory() {
//        return 8;
//    }
//
//    @Override
//    public int[] getSlotsForFace(EnumFacing side) {
//        return SLOTS;
//    }
//
//    @Override
//    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
//        return isItemValidForSlot(slot, stack);
//    }
//
//    @Override
//    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
//        return slot < SLOT_TICKET;
//    }
//
//    @Override
//    public boolean isItemValidForSlot(int slot, ItemStack stack) {
//        switch (slot) {
//            case SLOT_BURN:
//            case SLOT_FUEL_A:
//            case SLOT_FUEL_B:
//            case SLOT_FUEL_C:
//                return FuelPlugin.getBurnTime(stack) > 0;
//            case SLOT_WATER_INPUT:
//                return FluidItemHelper.containsFluid(stack, Fluids.WATER.get(1));
//            case SLOT_TICKET:
//                return ItemTicket.FILTER.test(stack);
//            default:
//                return false;
//        }
//    }
//
//    @Override
//    public AspectList getAspects() {
//        return new AspectList().add(Aspect.FIRE, fireAspect.getAmount()).add(Aspect.WATER, waterAspect.getAmount());
//    }
//
//    @Override
//    public void setAspects(AspectList aspects) {
//    }
//
//    @Override
//    public int addToContainer(Aspect tag, int amount) {
//        if (tag == Aspect.FIRE)
//            return fireAspect.fill(amount, true);
//        if (tag == Aspect.WATER)
//            return waterAspect.fill(amount, true);
//        return amount;
//    }
//
//    @Override
//    public boolean takeFromContainer(Aspect tag, int amount) {
//        if (tag == Aspect.FIRE)
//            return fireAspect.remove(amount, true);
//        if (tag == Aspect.WATER)
//            return waterAspect.remove(amount, true);
//        return false;
//    }
//
//    @Override
//    public boolean takeFromContainer(AspectList ot) {
//        return false;
//    }
//
//    @Override
//    public boolean doesContainerContainAmount(Aspect tag, int amount) {
//        if (tag == Aspect.FIRE)
//            return fireAspect.contains(amount);
//        if (tag == Aspect.WATER)
//            return waterAspect.contains(amount);
//        return false;
//    }
//
//    @Override
//    public boolean doesContainerContain(AspectList ot) {
//        for (Entry<Aspect, Integer> entry : ot.aspects.entrySet()) {
//            if (!doesContainerContainAmount(entry.getKey(), entry.getValue()))
//                return false;
//        }
//        return true;
//    }
//
//    @Override
//    public int containerContains(Aspect tag) {
//        if (tag == Aspect.FIRE)
//            return fireAspect.getAmount();
//        if (tag == Aspect.WATER)
//            return waterAspect.getAmount();
//        return 0;
//    }
//
//    @Nonnull
//    @Override
//    protected EnumGui getGuiType() {
//        throw new Error("TODO"); //TODO not implemented
//    }
//}
