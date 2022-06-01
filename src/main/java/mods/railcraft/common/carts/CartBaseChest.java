/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Optional;

public abstract class CartBaseChest extends CartBase implements IInventoryImplementor {

    protected CartBaseChest(World world) {
        super(world);
    }

    protected CartBaseChest(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IInventory getInventory() {
        return getLogic(IInventory.class).orElse(this);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return super.isUsableByPlayer(player) && getLogic(InventoryLogic.class).map(logic -> logic.isUsableByPlayer(player)).orElse(true);
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
    protected Optional<EnumGui> getGuiType() {
        return EnumGui.CHEST.op();
    }
}
