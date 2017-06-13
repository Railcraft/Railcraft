/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
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
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandles;

public class EntityCartCargo extends CartBaseFiltered {
    private static final DataParameter<Integer> SLOTS_FILLED = DataManagerPlugin.create(MethodHandles.lookup().lookupClass(), DataSerializers.VARINT);

    public EntityCartCargo(World world) {
        super(world);
    }

    public EntityCartCargo(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.CARGO;
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
    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
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

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        return EnumGui.CART_CARGO;
    }
}
