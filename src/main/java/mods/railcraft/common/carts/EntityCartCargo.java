/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.api.carts.IItemCart;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityCartCargo extends EntityCartFiltered implements IItemCart {
    private static final byte SLOTS_FILLED_DATA_ID = 25;

    public EntityCartCargo(World world) {
        super(world);
    }

    public EntityCartCargo(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + (double) yOffset, d2);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
        prevPosX = d;
        prevPosY = d1;
        prevPosZ = d2;
    }

    @Override
    public ICartType getCartType() {
        return EnumCart.CARGO;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(SLOTS_FILLED_DATA_ID, new Integer(-1));
    }

    public int getSlotsFilled() {
        return dataWatcher.getWatchableObjectInt(SLOTS_FILLED_DATA_ID);
    }

    private void setSlotsFilled(int slotsFilled) {
        dataWatcher.updateObject(SLOTS_FILLED_DATA_ID, slotsFilled);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Game.isHost(worldObj))
            setSlotsFilled(InvTools.countStacks(this));
    }

    @Override
    public List<ItemStack> getItemsDropped() {
        List<ItemStack> items = new ArrayList<ItemStack>();
        if (RailcraftConfig.doCartsBreakOnDrop()) {
            items.add(new ItemStack(Items.minecart));
            items.add(new ItemStack(Blocks.trapped_chest));
        } else
            items.add(getCartItem());
        return items;
    }

    @Override
    public boolean doInteract(EntityPlayer player) {
        if (Game.isHost(worldObj)) {
            GuiHandler.openGui(EnumGui.CART_CARGO, player, worldObj, this);
        }
        return true;
    }

    @Override
    public Block func_145820_n() {
        return null;
    }
//    public Block func_145820_n() {
//        return Blocks.trapped_chest;
//    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public int getSizeInventory() {
        return 18;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        ItemStack filter = getFilterItem();
        if (!InvTools.isItemEqual(stack, filter))
            return false;
        if (!RailcraftConfig.chestAllowLiquids())
            return getStackInSlot(slot) == null || !FluidItemHelper.isContainer(stack);
        return true;
    }

    @Override
    public boolean canPassItemRequests() {
        return true;
    }

    @Override
    public boolean canAcceptPushedItem(EntityMinecart requester, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canProvidePulledItem(EntityMinecart requester, ItemStack stack) {
        return true;
    }
}
