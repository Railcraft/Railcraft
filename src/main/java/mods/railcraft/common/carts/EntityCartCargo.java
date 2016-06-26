/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.fluids.FluidItemHelper;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.DataManagerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class EntityCartCargo extends CartBaseFiltered {
    private static final DataParameter<Integer> SLOTS_FILLED = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.VARINT);

    public EntityCartCargo(World world) {
        super(world);
    }

    public EntityCartCargo(World world, double d, double d1, double d2) {
        this(world);
        setPosition(d, d1 + getYOffset(), d2);
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
        dataManager.register(SLOTS_FILLED, -1);
    }

    public int getSlotsFilled() {
        return dataManager.get(SLOTS_FILLED);
    }

    private void setSlotsFilled(int slotsFilled) {
        dataManager.set(SLOTS_FILLED, slotsFilled);
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
            items.add(new ItemStack(Items.MINECART));
            items.add(new ItemStack(Blocks.TRAPPED_CHEST));
        } else
            items.add(getCartItem());
        return items;
    }

    @Override
    public boolean doInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
        if (Game.isHost(worldObj)) {
            GuiHandler.openGui(EnumGui.CART_CARGO, player, worldObj, this);
        }
        return true;
    }

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
