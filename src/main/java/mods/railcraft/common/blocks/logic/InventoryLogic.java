/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import mods.railcraft.common.util.inventory.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Iterator;

/**
 *
 */
public class InventoryLogic extends Logic implements IInventoryImplementor {

    protected final InventoryAdvanced inventory;
    protected final IInventoryComposite composite;

    protected InventoryLogic(Adapter adapter, int sizeInv) {
        super(adapter);
        this.inventory = new InventoryAdvanced(sizeInv).callback(adapter.getContainer());
        this.composite = InventoryComposite.of(inventory);
    }

    protected void dropItem(ItemStack stack) {
        InvTools.dropItem(stack, theWorldAsserted(), getX(), getY(), getZ());
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return adapter.isUsableByPlayer(player);
    }

    @Override
    public Iterator<InventoryAdaptor> adaptors() {
        return composite.adaptors();
    }

    @Override
    public IInventory getInventory() {
        return inventory;
    }

    public IItemHandlerModifiable getItemHandler(@Nullable EnumFacing side) {
        return ItemHandlerFactory.wrap(this, side);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        inventory.writeToNBT("inv", data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        inventory.readFromNBT("inv", data);
    }
}
