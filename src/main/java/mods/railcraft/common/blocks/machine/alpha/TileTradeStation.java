/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.alpha;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.blocks.machine.TileMachineItem;
import mods.railcraft.common.blocks.machine.alpha.ai.EntityAIMoveToBlock;
import mods.railcraft.common.blocks.machine.alpha.ai.EntityAIWatchBlock;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.PhantomInventory;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.network.IGuiReturnHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.util.ForgeDirection;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileTradeStation extends TileMachineItem implements IGuiReturnHandler, ISidedInventory {

    public static enum GuiPacketType {

        NEXT_TRADE, SET_PROFESSION
    };
    private static final int AREA = 6;
    private static final int[] SLOTS = InvTools.buildSlotArray(0, 16);
    private int profession;
    private final PhantomInventory recipeSlots = new PhantomInventory(9);
    private final InventoryMapper invInput;
    private final InventoryMapper invOutput;
    protected ForgeDirection direction = ForgeDirection.NORTH;

    public TileTradeStation() {
        setInventorySize(16);
        invInput = new InventoryMapper(this, 0, 10);
        invOutput = new InventoryMapper(this, 10, 6, false);
    }

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineAlpha.TRADE_STATION;
    }

    @Override
    public IIcon getIcon(int side) {
        if (side == direction.ordinal())
            return getMachineType().getTexture(4);
        if (side < 2)
            return getMachineType().getTexture(0);
        return getMachineType().getTexture(2);
    }

    public IInventory getRecipeSlots() {
        return recipeSlots;
    }

    public int getProfession() {
        return profession;
    }

    @Override
    public boolean openGui(EntityPlayer player) {
        GuiHandler.openGui(EnumGui.TRADE_STATION, player, worldObj, xCoord, yCoord, zCoord);
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (clock % 256 == 0)
            modifyNearbyAI();

        List<EntityVillager> villagers = MiscTools.getNearbyEntities(worldObj, EntityVillager.class, xCoord, yCoord - 1, yCoord + 3, zCoord, AREA);
        attemptTrade(villagers, 0);
        attemptTrade(villagers, 1);
        attemptTrade(villagers, 2);
    }

    private void modifyNearbyAI() {
        List<EntityVillager> villagers = MiscTools.getNearbyEntities(worldObj, EntityVillager.class, xCoord, yCoord - 1, yCoord + 3, zCoord, 20);
        for (EntityVillager villager : villagers) {
            AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, getMachineType().getBlock(), getMachineType().ordinal(), 4, 0.08F));
            AIPlugin.addAITask(villager, 9, new EntityAIMoveToBlock(villager, getMachineType().getBlock(), getMachineType().ordinal(), 16, 0.002F));
        }
    }

    private boolean attemptTrade(List<EntityVillager> villagers, int tradeSet) {
        ItemStack buy1 = recipeSlots.getStackInSlot(tradeSet * 3 + 0);
        ItemStack buy2 = recipeSlots.getStackInSlot(tradeSet * 3 + 1);
        ItemStack sell = recipeSlots.getStackInSlot(tradeSet * 3 + 2);
        for (EntityVillager villager : villagers) {
            MerchantRecipeList recipes = villager.getRecipes(null);
            for (MerchantRecipe recipe : (List<MerchantRecipe>) recipes) {
                if (recipe.isRecipeDisabled())
                    continue;
                if (recipe.getItemToBuy() != null && !InvTools.isItemLessThanOrEqualTo(recipe.getItemToBuy(), buy1))
                    continue;
                if (recipe.getSecondItemToBuy() != null && !InvTools.isItemLessThanOrEqualTo(recipe.getSecondItemToBuy(), buy2))
                    continue;
                if (!InvTools.isItemGreaterOrEqualThan(recipe.getItemToSell(), sell))
                    continue;
//                System.out.printf("Buying: %d %s Found: %d%n", recipe.getItemToBuy().stackSize, recipe.getItemToBuy().getDisplayName(), InvTools.countItems(invInput, recipe.getItemToBuy()));
                if (canDoTrade(recipe)) {
//                    System.out.println("Can do trade");
                    doTrade(villager, recipe);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canDoTrade(MerchantRecipe recipe) {
        if (recipe.getItemToBuy() != null && InvTools.countItems(invInput, recipe.getItemToBuy()) < recipe.getItemToBuy().stackSize)
            return false;
        if (recipe.getSecondItemToBuy() != null && InvTools.countItems(invInput, recipe.getSecondItemToBuy()) < recipe.getSecondItemToBuy().stackSize)
            return false;
        return InvTools.isRoomForStack(recipe.getItemToSell(), invOutput);
    }

    private void doTrade(IMerchant merchant, MerchantRecipe recipe) {
        merchant.useRecipe(recipe);
        if (recipe.getItemToBuy() != null)
            InvTools.removeItemsAbsolute(invInput, recipe.getItemToBuy().stackSize, recipe.getItemToBuy());
        if (recipe.getSecondItemToBuy() != null)
            InvTools.removeItemsAbsolute(invInput, recipe.getSecondItemToBuy().stackSize, recipe.getSecondItemToBuy());
        InvTools.moveItemStack(recipe.getItemToSell().copy(), invOutput);
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entityliving, ItemStack stack) {
        super.onBlockPlacedBy(entityliving, stack);
        direction = MiscTools.getSideClosestToPlayer(worldObj, xCoord, yCoord, zCoord, entityliving);
    }

    @Override
    public boolean rotateBlock(ForgeDirection axis) {
        if (direction == axis)
            direction = axis.getOpposite();
        else
            direction = axis;
        markBlockForUpdate();
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        recipeSlots.writeToNBT("recipe", data);

        data.setInteger("profession", profession);
        data.setByte("direction", (byte) direction.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        recipeSlots.readFromNBT("recipe", data);

        profession = data.getInteger("profession");
        direction = ForgeDirection.getOrientation(data.getByte("direction"));
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeInt(profession);
        data.writeByte(direction.ordinal());
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        profession = data.readInt();
        ForgeDirection f = ForgeDirection.getOrientation(data.readByte());
        if (direction != f) {
            direction = f;
            markBlockForUpdate();
        }
    }

    @Override
    public void writeGuiData(DataOutputStream data) throws IOException {
    }

    @Override
    public void readGuiData(DataInputStream data, EntityPlayer sender) throws IOException {
        GuiPacketType type = GuiPacketType.values()[data.readByte()];
        switch (type) {
            case NEXT_TRADE:
                nextTrade(data.readByte());
                break;
            case SET_PROFESSION:
                profession = data.readInt();
                sendUpdateToClient();
                break;
        }
    }

    public void nextTrade(int tradeSet) {
        EntityVillager villager = new EntityVillager(worldObj);
        villager.setProfession(profession);
        MerchantRecipeList recipes = villager.getRecipes(null);
        MerchantRecipe recipe = (MerchantRecipe) recipes.get(MiscTools.RANDOM.nextInt(recipes.size()));
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 0, recipe.getItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 1, recipe.getSecondItemToBuy());
        recipeSlots.setInventorySlotContents(tradeSet * 3 + 2, recipe.getItemToSell());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot < 10;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot >= 10;
    }

}
