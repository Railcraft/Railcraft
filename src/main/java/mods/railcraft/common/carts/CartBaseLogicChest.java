/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.gui.EnumGui;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public abstract class CartBaseLogicChest extends CartBaseLogic implements IInteractionObject {

    protected CartBaseLogicChest(World world) {
        super(world);
    }

    protected CartBaseLogicChest(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public abstract IBlockState getDefaultDisplayTile();

    @Override
    public int getDefaultDisplayTileOffset() {
        return 8;
    }

    @Override
    public boolean canPassItemRequests(ItemStack stack) {
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

    @Override
    protected void openRailcraftGui(EntityPlayer player) {
        getLogic(IInventory.class).ifPresent(player::displayGUIChest);
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerChest(playerInventory, getLogic(IInventory.class).orElseThrow(NullPointerException::new), playerIn);
    }

    @Override
    protected final EnumGui getGuiType() {
        throw new UnsupportedOperationException("Should not be called");
    }

    @Override
    public String getGuiID() {
        return "minecraft:chest";
    }
}
