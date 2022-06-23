/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.tracks.outfitted.kits;

import mods.railcraft.api.items.IToolCrowbar;
import mods.railcraft.api.tracks.ITrackKitPowered;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.tracks.outfitted.TrackKits;
import mods.railcraft.common.carts.CartConstants;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.*;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.function.Predicate;

import static mods.railcraft.common.util.inventory.InvTools.isEmpty;

public class TrackKitDumping extends TrackKitSuspended implements ITrackKitPowered {

    private static final int TIME_TILL_NEXT_MOUNT = 40;
    private static final int ITEM_DROP_INTERVAL = 16;
    private boolean powered;
    private final InventoryAdvanced cartFilter = new InventoryAdvanced(3).setInventoryStackLimit(1).callback(this).phantom();
    private final InventoryAdvanced itemFilter = new InventoryAdvanced(9).setInventoryStackLimit(1).callback(this).phantom();
    private final Predicate<EntityMinecart> cartMatcher = CartTools.cartFilterMatcher(cartFilter);
    private final Predicate<ItemStack> itemMatcher = StackFilters.filterMatch(itemFilter);
    private AdjacentInventoryCache cache;
    private int ticksSinceLastDrop = 0;

    public IInventory getCartFilter() {
        return cartFilter;
    }

    public IInventory getItemFilter() {
        return itemFilter;
    }

    private InventoryComposite getInvBelow() {
        if (cache == null) cache = new AdjacentInventoryCache(((TileRailcraft) getTile()).getTileCache());
        return cache.getInventoryOnSide(EnumFacing.DOWN);
    }

    @Override
    public int getRenderState() {
        return powered ? 1 : 0;
    }

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.DUMPING;
    }

    @Override
    public void update() {
        super.update();
        ticksSinceLastDrop++;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        if (isPowered())
            return;

        if (!cartMatcher.test(cart)) {
            return;
        }

        if (cart.isBeingRidden() && tryDumpRider(cart)) {
            return;
        }

        tryDumpInventory(cart);
    }

    private boolean tryDumpRider(EntityMinecart cart) {
        World world = theWorldAsserted();
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain().setPos(getTile().getPos());
        for (int i = 0; i < 2; i++) {
            pos.move(EnumFacing.DOWN);
            if (world.getBlockState(pos).getBlock().causesSuffocation(world.getBlockState(pos)))
                return false;
        }
        pos.release();
        if (cart.isBeingRidden()) {
            CartTools.removePassengers(cart, cart.getPositionVector().add(0, -2, 0));
        }
        cart.getEntityData().setInteger(CartConstants.TAG_PREVENT_MOUNT, TIME_TILL_NEXT_MOUNT);
        return true;
    }

    private void tryDumpInventory(EntityMinecart cart) {
        if (ticksSinceLastDrop < ITEM_DROP_INTERVAL) return;

        IInventoryComposite cartInv = InventoryComposite.of(cart);
        if (!cartInv.hasItems()) return;

        ticksSinceLastDrop = 0;

        IInventoryComposite invBelow = getInvBelow();

        ItemStack stack = cartInv.removeOneItem(itemMatcher);
        if (InvTools.isEmpty(stack)) return;

        stack = invBelow.addStack(stack);

        if (InvTools.isEmpty(stack)) return;

        //Dump now!
        InvTools.spewItem(stack, theWorldAsserted(), getPos().down());
    }

    @Override
    public void onNeighborBlockChange(IBlockState state, @Nullable Block neighborBlock) {
        super.onNeighborBlockChange(state, neighborBlock);
        testPower(state);
    }

    @Override
    public boolean blockActivated(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);
        if (!isEmpty(heldItem) && heldItem.getItem() instanceof IToolCrowbar) {
            IToolCrowbar crowbar = (IToolCrowbar) heldItem.getItem();
            if (crowbar.canWhack(player, hand, heldItem, getPos())) {
                GuiHandler.openGui(EnumGui.TRACK_DUMPING, player, theWorldAsserted(), getPos());
                crowbar.onWhack(player, hand, heldItem, getPos());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPowered() {
        return powered;
    }

    @Override
    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("powered", powered);
        data.setInteger("ticksSinceLastDrop", ticksSinceLastDrop);
        cartFilter.writeToNBT("cartFilter", data);
        itemFilter.writeToNBT("itemFilter", data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        powered = data.getBoolean("powered");
        ticksSinceLastDrop = data.getInteger("ticksSinceLastDrop");
        cartFilter.readFromNBT("cartFilter", data);
        itemFilter.readFromNBT("itemFilter", data);
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(powered);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        boolean p = data.readBoolean();
        if (p != powered) {
            powered = p;
            markBlockNeedsUpdate();
        }
    }
}
