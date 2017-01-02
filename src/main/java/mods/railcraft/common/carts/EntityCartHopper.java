/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.misc.Game;

public class EntityCartHopper extends CartBaseContainer implements IHopper {
    private boolean enabled = true;
    private int transferCooldown = -1;
    private final BlockPos lastPos = BlockPos.ORIGIN;

    public EntityCartHopper(World world) {
        super(world);
    }

    public EntityCartHopper(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.HOPPER.getDefaultState();
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 1;
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
    public boolean doInteract(EntityPlayer player, @Nullable ItemStack stack, @Nullable EnumHand hand) {
        if (Game.isHost(worldObj)) {
            player.displayGUIChest(this);
        }
        return true;
    }

    @Override
    public boolean canPassItemRequests() {
        return true;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.HOPPER;
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerHopper(playerInventory, this, playerIn);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return "minecraft:hopper";
    }

    @Nonnull
    @Override
    protected EnumGui getGuiType() {
        throw new Error("Should not be called");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(worldObj) && !isDead && enabled) {
            BlockPos blockpos = new BlockPos(this);

            if (blockpos.equals(this.lastPos)) {
                --this.transferCooldown;
            } else {
                this.transferCooldown = 0;
            }

            if (transferCooldown <= 0) {
                this.transferCooldown = 0;

                if (this.captureDroppedItems()) {
                    this.transferCooldown = 4;
                    this.markDirty();
                }
            }
        }
    }

    private boolean captureDroppedItems() {
        if (TileEntityHopper.captureDroppedItems(this)) {
            return true;
        } else {
            List<EntityItem> list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.25D, 0.0D, 0.25D), EntitySelectors.IS_ALIVE);

            if (!list.isEmpty()) {
                TileEntityHopper.putDropInInventoryAllSlots(this, list.get(0));
            }

            return false;
        }
    }

    @Override
    public World getWorld() {
        return worldObj;
    }

    @Override
    public double getXPos() {
        return posX;
    }

    @Override
    public double getYPos() {
        return posY;
    }

    @Override
    public double getZPos() {
        return posZ;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("TransferCooldown", this.transferCooldown);
        compound.setBoolean("Enabled", this.enabled);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.transferCooldown = compound.getInteger("TransferCooldown");
        this.enabled = !compound.hasKey("Enabled") || compound.getBoolean("Enabled");
    }
}
