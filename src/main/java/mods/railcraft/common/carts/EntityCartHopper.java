/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.IExtInvSlot;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryIterator;
import mods.railcraft.common.util.misc.Game;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EntityCartHopper extends CartBaseContainer implements IHopper {
    private boolean enabled = true;
    private int transferCooldown = -1;
    private @Nullable BlockPos lastPosition;
    private int pushCounter;

    public EntityCartHopper(World worldIn) {
        super(worldIn);
    }

    public EntityCartHopper(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.HOPPER;
    }

    @Override
    protected EnumGui getGuiType() {
        throw new UnsupportedOperationException("No GUI");
    }

    @Override
    protected void openRailcraftGui(EntityPlayer player) {
        player.displayGUIChest(this);
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return Blocks.HOPPER.getDefaultState();
    }

    @Override
    public int getDefaultDisplayTileOffset() {
        return 1;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return 5;
    }

    /**
     * Called every tick the minecart is on an activator rail.
     */
    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        boolean shouldEnable = !receivingPower;

        if (shouldEnable != enabled) {
            enabled = shouldEnable;
        }
    }

    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Gets the world X position for this hopper entity.
     */
    @Override
    public double getXPos() {
        return posX;
    }

    /**
     * Gets the world Y position for this hopper entity.
     */
    @Override
    public double getYPos() {
        return posY + 0.5D;
    }

    /**
     * Gets the world Z position for this hopper entity.
     */
    @Override
    public double getZPos() {
        return posZ;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        super.onUpdate();

        if (Game.isHost(world) && !isDead) {
            BlockPos nowPos = new BlockPos(this);
            if (enabled) {
                // cooldown when transferring from a same block
                if (Objects.equals(lastPosition, nowPos)) {
                    if (transferCooldown > 0)
                        transferCooldown--;
                } else {
                    transferCooldown = 0;
                }

                if (!RailcraftConfig.hopperCartTransferCooldown() || transferCooldown <= 0) {
                    if (transferAndNeedsCooldown()) {
                        transferCooldown = 4;
                    }
                }
            }

            if (pushCounter == 0) {
                tryPushItem();
            }

            pushCounter++;
            if (pushCounter >= 5)
                pushCounter = 0;
            lastPosition = nowPos;
        }
    }

    private void tryPushItem() {
        boolean emptySlot = false;
        // Push full stacks whenever possible
        for (IExtInvSlot slot : InventoryIterator.get(this)) {
            if (slot.getStack().getCount() == slot.getMaxStackSize()) {
                ItemStack left = TrainTransferHelper.INSTANCE.pushStack(this, slot.getStack());
                if (InvTools.isEmpty(left)) {
                    emptySlot = true;
                }
                slot.setStack(left);
            } else if (!slot.hasStack()) {
                emptySlot = true;
            }
        }

        if (emptySlot)
            return;

        // If all slots are occupied, try to clear one of the slots.
        for (IExtInvSlot slot : InventoryIterator.get(this)) {
            ItemStack left = TrainTransferHelper.INSTANCE.pushStack(this, slot.getStack());
            slot.setStack(left);
            if (InvTools.isEmpty(left)) {
                return;
            }
        }
    }

    public boolean transferAndNeedsCooldown() {
        if (TileEntityHopper.pullItems(this)) {
            markDirty();
            return true;
        }
        EntityItem found = EntitySearcher.findItem().around(this).growFlat(0.25D).in(world).any();

        if (found != null) {
            addStack(found.getItem());
            markDirty();
        }
        return false;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("TransferCooldown", transferCooldown);
        compound.setBoolean("Enabled", enabled);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.transferCooldown = compound.getInteger("TransferCooldown");
        this.enabled = !compound.hasKey("Enabled") || compound.getBoolean("Enabled");
    }

    @Override
    public String getGuiID() {
        return "minecraft:hopper";
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        return new ContainerHopper(playerInventory, this, playerIn);
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