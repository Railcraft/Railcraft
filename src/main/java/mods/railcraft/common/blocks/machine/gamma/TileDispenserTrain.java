/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.gamma;

import java.util.Map;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.carts.CartUtils;
import mods.railcraft.common.carts.ItemCartAnchor;
import mods.railcraft.common.carts.ItemLocomotive;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.world.WorldServer;

public class TileDispenserTrain extends TileDispenserCart {

    public final static int PATTERN_SIZE = 9;
    public final static int BUFFER_SIZE = 18;
    private byte patternIndex;
    private boolean spawningTrain = false;
    private EntityMinecart lastCart;
    private final PhantomInventory invPattern = new PhantomInventory(PATTERN_SIZE, this);
    private final IInventory invStock;

    public TileDispenserTrain() {
        super();
        setInventorySize(BUFFER_SIZE);
        invStock = new InventoryMapper(this);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineGamma.DISPENSER_TRAIN;
    }

    public PhantomInventory getPattern() {
        return invPattern;
    }

    @Override
    public IIcon getIcon(int side) {
        if (direction.ordinal() == side)
            return getMachineType().getTexture(3);
        if (side != 0 && side != 1)
            return getMachineType().getTexture(2);
        return getMachineType().getTexture(1);
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.TRAIN_DISPENSER, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    private boolean canBuildTrain() {
        Map<ItemStack, Integer> pattern = InvTools.getManifest(getPattern());
        Map<ItemStack, Integer> buffer = InvTools.getManifest(this);

        for (Map.Entry<ItemStack, Integer> entry : pattern.entrySet()) {
            Integer count = buffer.get(entry.getKey());
            if (count == null || count < entry.getValue())
                return false;
        }

        return true;
    }

    private static class MinecartItemType implements IStackFilter {

        private final ItemStack original;

        public MinecartItemType(ItemStack cart) {
            original = cart;
        }

        @Override
        public boolean matches(ItemStack stack) {
            if (stack == null)
                return false;
            if (InvTools.isItemEqual(stack, original))
                return true;
            if (stack.getItem() instanceof ItemCartAnchor || stack.getItem() instanceof ItemLocomotive)
                return InvTools.isItemEqual(stack, original, false, false);
            return false;
        }

    }

    private boolean spawnNextCart() {
        ItemStack spawn = getPattern().getStackInSlot(patternIndex);
        if (spawn == null) {
            resetSpawnSequence();
            return false;
        }
        IStackFilter filter = new MinecartItemType(spawn);
        if (InvTools.countItems(invStock, filter) == 0) {
            resetSpawnSequence();
            return false;
        }
        int x = MiscTools.getXOnSide(xCoord, direction);
        int y = MiscTools.getYOnSide(yCoord, direction);
        int z = MiscTools.getZOnSide(zCoord, direction);
        if ((spawn.getItem() instanceof ItemMinecart || spawn.getItem() instanceof IMinecartItem)
                && CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0, direction) == null) {
            ItemStack cartItem = InvTools.removeOneItem(invStock, filter);
            if (cartItem != null) {
                EntityMinecart cartPlaced = CartUtils.placeCart(getOwner(), cartItem, (WorldServer) worldObj, x, y, z);
                if (cartPlaced != null) {
                    CartTools.getLinkageManager(worldObj).createLink(cartPlaced, lastCart);
                    lastCart = cartPlaced;
                    patternIndex++;
                    if (patternIndex >= getPattern().getSizeInventory())
                        resetSpawnSequence();
                    return true;
                } else
                    InvTools.moveItemStack(cartItem, invStock);
            }
        }
        return false;
    }

    private void resetSpawnSequence() {
        patternIndex = 0;
        spawningTrain = false;
        timeSinceLastSpawn = 0;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (spawningTrain && clock % 4 == 0)
            spawnNextCart();
    }

    @Override
    public void onPulse() {
        EntityMinecart cart = CartTools.getMinecartOnSide(worldObj, xCoord, yCoord, zCoord, 0, direction);
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("spawningTrain", spawningTrain);
        data.setByte("patternIndex", patternIndex);

        getPattern().writeToNBT("invPattern", data);
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

//    @Override
//    public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
//        if (InvTools.isInventoryEmpty(getPattern()))
//            return 0;
//        IInventory inv = invStock;
//        if (!doAdd)
//            inv = new InventoryCopy(inv);
//        ItemStack leftOver = InvTools.moveItemStack(stack, inv);
//        if (leftOver == null)
//            return stack.stackSize;
//        return stack.stackSize - leftOver.stackSize;
//    }
//
//    @Override
//    public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
//        Set<ItemStack> patternSet = new ItemStackSet();
//        Set<ItemStack> bufferSet = new ItemStackSet();
//
//        for (ItemStack stack : getPattern().getContents()) {
//            if (stack != null)
//                patternSet.add(stack);
//        }
//
//        for (ItemStack stack : getInventory().getContents()) {
//            if (stack != null)
//                bufferSet.add(stack);
//        }
//
//        bufferSet.removeAll(patternSet);
//
//        IInventory inv = invStock;
//        if (!doRemove)
//            inv = new InventoryCopy(inv);
//
//        for (ItemStack stack : bufferSet) {
//            ItemStack removed = InvTools.removeOneItem(inv, stack);
//            return removed != null ? new ItemStack[]{removed} : new ItemStack[0];
//        }
//
//        return new ItemStack[0];
//    }
}
