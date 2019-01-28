/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.logic.InventoryLogic;
import mods.railcraft.common.util.inventory.IInventoryImplementor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class CartBaseLogicChest extends CartBaseLogic implements IInventoryImplementor {

    protected CartBaseLogicChest(World world) {
        super(world);
    }

    protected CartBaseLogicChest(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IInventory getInventory() {
        return getLogic(IInventory.class).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return getLogic(InventoryLogic.class).map(logic -> logic.isUsableByPlayer(player)).orElse(false);
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
}
