/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.machine.manipulator;

import mods.railcraft.api.carts.CartToolsAPI;
import mods.railcraft.api.items.IMinecartItem;
import mods.railcraft.common.carts.CartTools;
import mods.railcraft.common.carts.ItemCartWorldspike;
import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.entity.EntitySearcher;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.InventoryAdvanced;
import mods.railcraft.common.util.inventory.InventoryManifest;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class TileDispenserTrain extends TileDispenserCart {

    public static final int PATTERN_SIZE = 9;
    public static final int BUFFER_SIZE = 18;
    private final InventoryAdvanced invPattern = new InventoryAdvanced(PATTERN_SIZE).callbackInv(this).phantom();
    private byte patternIndex;
    private boolean spawningTrain;
    private @Nullable EntityMinecart lastCart;

    public TileDispenserTrain() {
        setInventorySize(BUFFER_SIZE);
    }

    @Override
    public ManipulatorVariant getMachineType() {
        return ManipulatorVariant.DISPENSER_TRAIN;
    }

    public InventoryAdvanced getPattern() {
        return invPattern;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.TRAIN_DISPENSER, player, world, getPos());
        return true;
    }

    private boolean canBuildTrain() {
        InventoryManifest pattern = InventoryManifest.create(getPattern());
        InventoryManifest buffer = InventoryManifest.create(getInventory(), pattern.keySet());

        return pattern.values().stream().anyMatch(e -> buffer.count(e.key()) >= e.count());
    }

    private boolean spawnNextCart() {
        ItemStack spawn = getPattern().getStackInSlot(patternIndex);
        if (InvTools.isEmpty(spawn)) {
            resetSpawnSequence();
            return false;
        }
        Predicate<ItemStack> filter = new MinecartItemType(spawn);
        if (countItems(filter) == 0) {
            resetSpawnSequence();
            return false;
        }
        BlockPos offset = getPos().offset(facing);
        if ((spawn.getItem() instanceof ItemMinecart || spawn.getItem() instanceof IMinecartItem)
                && EntitySearcher.findMinecarts().around(getPos().offset(facing)).in(world).isEmpty()) {
            ItemStack cartItem = removeOneItem(filter);
            if (!InvTools.isEmpty(cartItem)) {
                EntityMinecart cartPlaced = CartTools.placeCart(getOwner(), cartItem, (WorldServer) world, offset);
                if (cartPlaced != null) {
                    if (lastCart != null)
                        CartToolsAPI.linkageManager().createLink(cartPlaced, lastCart);
                    lastCart = cartPlaced;
                    patternIndex++;
                    if (patternIndex >= getPattern().getSizeInventory())
                        resetSpawnSequence();
                    return true;
                } else
                    addStack(cartItem);
            }
        }
        return false;
    }

    private void resetSpawnSequence() {
        patternIndex = 0;
        spawningTrain = false;
        timeSinceLastSpawn = 0;
        lastCart = null;
    }

    @Override
    public void update() {
        super.update();

        if (spawningTrain && clock % 4 == 0)
            spawnNextCart();
    }

    @Override
    public void onPulse() {
        EntityMinecart cart = EntitySearcher.findMinecarts().around(getPos().offset(facing)).in(world).any();
        if (cart == null)
            if (!spawningTrain && canBuildTrain())
                if (timeSinceLastSpawn > RailcraftConfig.getCartDispenserMinDelay() * 20)
                    spawningTrain = true;
//            else if(!spawningTrain) {
//                ItemStack cartStack = InventoryTools.moveItemStack(cart.getCartItem(), invBuffer);
//                if(cartStack == null) {
//                    cart.setDead();
//                }
//            }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("spawningTrain", spawningTrain);
        data.setByte("patternIndex", patternIndex);

        getPattern().writeToNBT("invPattern", data);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        spawningTrain = data.getBoolean("spawningTrain");
        patternIndex = data.getByte("patternIndex");

        if (data.hasKey("pattern")) {
            NBTTagCompound pattern = data.getCompoundTag("pattern");
            getPattern().readFromNBT("Items", pattern);
        } else
            getPattern().readFromNBT("invPattern", data);
    }

    private static class MinecartItemType implements Predicate<ItemStack> {

        private final ItemStack original;

        public MinecartItemType(ItemStack cart) {
            original = cart;
        }

        @Override
        public boolean test(ItemStack stack) {
            if (InvTools.isEmpty(stack))
                return false;
            if (InvTools.isItemEqual(stack, original))
                return true;
            if (stack.getItem() instanceof ItemCartWorldspike || stack.getItem() instanceof ItemLocomotive)
                return InvTools.isItemEqual(stack, original, false, false);
            return false;
        }

    }

    /*@Override
    public int addItem(ItemStack stack, boolean doAdd, EnumFacing from) {
        if (InvTools.isInventoryEmpty(getPattern()))
            return 0;
        IInventory inv = invStock;
        if (!doAdd)
            inv = new InventoryCopy(inv);
        ItemStack leftOver = InvTools.moveItemStack(stack, inv);
        if (leftOver == null)
            return stack.stackSize;
        return stack.stackSize - leftOver.stackSize;
    }

    @Override
    public ItemStack[] extractItem(boolean doRemove, EnumFacing from, int maxItemCount) {
        Set<ItemStack> patternSet = new ItemStackSet();
        Set<ItemStack> bufferSet = new ItemStackSet();

        for (ItemStack stack : getPattern().getContents()) {
            if (stack != null)
                patternSet.add(stack);
        }

        for (ItemStack stack : get().getContents()) {
            if (stack != null)
                bufferSet.add(stack);
        }

        bufferSet.removeAll(patternSet);

        IInventory inv = invStock;
        if (!doRemove)
            inv = new InventoryCopy(inv);

        for (ItemStack stack : bufferSet) {
            ItemStack removed = InvTools.removeOneItem(inv, stack);
            return removed != null ? new ItemStack[]{removed} : new ItemStack[0];
        }

        return new ItemStack[0];
    }*/
}
