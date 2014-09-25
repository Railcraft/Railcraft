/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import java.util.Iterator;
import java.util.Map;
import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockAnvil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ContainerAnvil extends ContainerRepair {

    private final IInventory outputSlot = new InventoryCraftResult();
    private final World world;
    private final EntityPlayer player;
    private final int x, y, z;
    private int stackSizeToBeUsedInRepair;
    private String repairedItemName;
    private final IInventory inputSlots;

    public ContainerAnvil(InventoryPlayer playerInv, World world, int x, int y, int z, EntityPlayer player) {
        super(playerInv, world, x, y, z, player);
        this.world = world;
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;

        SlotAnvil slot = new SlotAnvil(this, outputSlot, 2, 134, 47, world, x, y, z);
        slot.slotNumber = 2;
        inventorySlots.set(2, slot);

        inputSlots = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, this, 2);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        if (!(WorldPlugin.getBlock(world, x, y, z) instanceof BlockAnvil))
            return false;
        return player.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D) <= 64.0D;
    }

    @Override
    public void updateRepairOutput() {
        ItemStack inputA = inputSlots.getStackInSlot(0);
        this.maximumCost = 0;
        int i = 0;
        byte b0 = 0;
        int j = 0;

        if (inputA == null) {
            this.outputSlot.setInventorySlotContents(0, (ItemStack) null);
            this.maximumCost = 0;
        } else {
            ItemStack inputACopy = inputA.copy();
            ItemStack inputB = this.inputSlots.getStackInSlot(1);
            Map enchantments = EnchantmentHelper.getEnchantments(inputACopy);
            boolean isBook = false;
            int baseCost = b0 + inputA.getRepairCost() + (inputB == null ? 0 : inputB.getRepairCost());
            stackSizeToBeUsedInRepair = 0;
            int l;
            int i1;
            int j1;
            int k1;
            int l1;
            Iterator iterator;
            Enchantment enchantment;

            if (inputB != null) {
                if (!ForgeHooks.onAnvilChange(this, inputA, inputB, outputSlot, repairedItemName, baseCost)) return;
                isBook = inputB.getItem() == Items.enchanted_book && Items.enchanted_book.func_92110_g(inputB).tagCount() > 0;

                if (inputACopy.isItemStackDamageable() && inputACopy.getItem().getIsRepairable(inputA, inputB)) {
                    l = Math.min(inputACopy.getItemDamageForDisplay(), inputACopy.getMaxDamage() / 4);

                    if (l <= 0) {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack) null);
                        this.maximumCost = 0;
                        return;
                    }

                    for (i1 = 0; l > 0 && i1 < inputB.stackSize; ++i1) {
                        j1 = inputACopy.getItemDamageForDisplay() - l;
                        inputACopy.setItemDamage(j1);
                        i += Math.max(1, l / 100) + enchantments.size();
                        l = Math.min(inputACopy.getItemDamageForDisplay(), inputACopy.getMaxDamage() / 4);
                    }

                    this.stackSizeToBeUsedInRepair = i1;
                } else {
                    if (!isBook && (inputACopy.getItem() != inputB.getItem() || !inputACopy.isItemStackDamageable())) {
                        this.outputSlot.setInventorySlotContents(0, (ItemStack) null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (inputACopy.isItemStackDamageable() && !isBook) {
                        l = inputA.getMaxDamage() - inputA.getItemDamageForDisplay();
                        i1 = inputB.getMaxDamage() - inputB.getItemDamageForDisplay();
                        j1 = i1 + inputACopy.getMaxDamage() * 12 / 100;
                        int i2 = l + j1;
                        k1 = inputACopy.getMaxDamage() - i2;

                        if (k1 < 0)
                            k1 = 0;

                        if (k1 < inputACopy.getItemDamage()) {
                            inputACopy.setItemDamage(k1);
                            i += Math.max(1, j1 / 100);
                        }
                    }

                    Map map1 = EnchantmentHelper.getEnchantments(inputB);
                    iterator = map1.keySet().iterator();

                    while (iterator.hasNext()) {
                        j1 = ((Integer) iterator.next()).intValue();
                        enchantment = Enchantment.enchantmentsList[j1];
                        k1 = enchantments.containsKey(Integer.valueOf(j1)) ? ((Integer) enchantments.get(Integer.valueOf(j1))).intValue() : 0;
                        l1 = ((Integer) map1.get(Integer.valueOf(j1))).intValue();
                        int j2;

                        if (k1 == l1) {
                            ++l1;
                            j2 = l1;
                        } else
                            j2 = Math.max(l1, k1);

                        l1 = j2;
                        int k2 = l1 - k1;
                        boolean isEnchantmentValid = enchantment.canApply(inputA);

                        if (player.capabilities.isCreativeMode || inputA.getItem() == Items.enchanted_book)
                            isEnchantmentValid = true;

                        Iterator iterator1 = enchantments.keySet().iterator();

                        while (iterator1.hasNext()) {
                            int l2 = ((Integer) iterator1.next()).intValue();

                            if (l2 != j1 && !enchantment.canApplyTogether(Enchantment.enchantmentsList[l2])) {
                                isEnchantmentValid = false;
                                i += k2;
                            }
                        }

                        if (isEnchantmentValid) {
                            if (l1 > enchantment.getMaxLevel())
                                l1 = enchantment.getMaxLevel();

                            enchantments.put(Integer.valueOf(j1), Integer.valueOf(l1));
                            int i3 = 0;

                            switch (enchantment.getWeight()) {
                                case 1:
                                    i3 = 8;
                                    break;
                                case 2:
                                    i3 = 4;
                                case 3:
                                case 4:
                                case 6:
                                case 7:
                                case 8:
                                case 9:
                                default:
                                    break;
                                case 5:
                                    i3 = 2;
                                    break;
                                case 10:
                                    i3 = 1;
                            }

                            if (isBook)
                                i3 = Math.max(1, i3 / 2);

                            i += i3 * k2;
                        }
                    }
                }
            }

            if (StringUtils.isBlank(this.repairedItemName)) {
                if (inputA.hasDisplayName()) {
                    j = inputA.isItemStackDamageable() ? 7 : inputA.stackSize * 5;
                    i += j;
                    inputACopy.func_135074_t();
                }
            } else if (!this.repairedItemName.equals(inputA.getDisplayName())) {
                j = inputA.isItemStackDamageable() ? 7 : inputA.stackSize * 5;
                i += j;

                if (inputA.hasDisplayName())
                    baseCost += j / 2;

                inputACopy.setStackDisplayName(this.repairedItemName);
            }

            l = 0;

            for (iterator = enchantments.keySet().iterator(); iterator.hasNext(); baseCost += l + k1 * l1) {
                j1 = ((Integer) iterator.next()).intValue();
                enchantment = Enchantment.enchantmentsList[j1];
                k1 = ((Integer) enchantments.get(Integer.valueOf(j1))).intValue();
                l1 = 0;
                ++l;

                switch (enchantment.getWeight()) {
                    case 1:
                        l1 = 8;
                        break;
                    case 2:
                        l1 = 4;
                    case 3:
                    case 4:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        break;
                    case 5:
                        l1 = 2;
                        break;
                    case 10:
                        l1 = 1;
                }

                if (isBook)
                    l1 = Math.max(1, l1 / 2);
            }

            if (isBook)
                baseCost = Math.max(1, baseCost / 2);

            if (isBook && inputACopy != null && !inputACopy.getItem().isBookEnchantable(inputACopy, inputB))
                inputACopy = null;

            this.maximumCost = baseCost + i;

            if (i <= 0)
                inputACopy = null;

            // Railcraft changes max cost from 39 to 50
            if (j == i && j > 0 && this.maximumCost > 50)
                this.maximumCost = 50;

            // Here too
            if (this.maximumCost > 50 && !player.capabilities.isCreativeMode)
                inputACopy = null;

            if (inputACopy != null) {
                int repairCost = inputACopy.getRepairCost();

                if (inputB != null && repairCost < inputB.getRepairCost())
                    repairCost = inputB.getRepairCost();

                if (inputACopy.hasDisplayName())
                    repairCost -= 9;

                if (repairCost < 0)
                    repairCost = 0;

                repairCost += 2;
                inputACopy.setRepairCost(repairCost);
                EnchantmentHelper.setEnchantments(enchantments, inputACopy);
            }

            this.outputSlot.setInventorySlotContents(0, inputACopy);
            this.detectAndSendChanges();
        }
    }

    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    @Override
    public void updateItemName(String par1Str) {
        this.repairedItemName = par1Str;

        if (this.getSlot(2).getHasStack()) {
            ItemStack itemstack = this.getSlot(2).getStack();

            if (StringUtils.isBlank(par1Str))
                itemstack.func_135074_t();
            else
                itemstack.setStackDisplayName(this.repairedItemName);
        }

        this.updateRepairOutput();
    }

    private class SlotAnvil extends SlotRailcraft {

        final World world;
        final int x;
        final int y;
        final int z;
        final ContainerAnvil repairContainer;

        SlotAnvil(ContainerAnvil container, IInventory inv, int index, int slotX, int slotY, World world, int x, int y, int z) {
            super(inv, index, slotX, slotY);
            this.repairContainer = container;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * Check if the stack is a valid item for this slot. Always true beside
         * for the armor slots.
         */
        @Override
        public boolean isItemValid(ItemStack par1ItemStack) {
            return false;
        }

        /**
         * Return whether this slot's stack can be taken from this slot.
         */
        @Override
        public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
            return (par1EntityPlayer.capabilities.isCreativeMode || par1EntityPlayer.experienceLevel >= this.repairContainer.maximumCost) && this.repairContainer.maximumCost > 0 && this.getHasStack();
        }

        @Override
        public void onPickupFromSlot(EntityPlayer player, ItemStack stackInSlot) {
            if (!player.capabilities.isCreativeMode)
                player.addExperienceLevel(-this.repairContainer.maximumCost);
            
            float breakChance = ForgeHooks.onAnvilRepair(player, stackInSlot, repairContainer.inputSlots.getStackInSlot(0), repairContainer.inputSlots.getStackInSlot(1));

            repairContainer.inputSlots.setInventorySlotContents(0, (ItemStack) null);

            if (repairContainer.stackSizeToBeUsedInRepair > 0) {
                ItemStack itemstack1 = repairContainer.inputSlots.getStackInSlot(1);

                if (itemstack1 != null && itemstack1.stackSize > repairContainer.stackSizeToBeUsedInRepair) {
                    itemstack1.stackSize -= repairContainer.stackSizeToBeUsedInRepair;
                    repairContainer.inputSlots.setInventorySlotContents(1, itemstack1);
                } else
                    repairContainer.inputSlots.setInventorySlotContents(1, (ItemStack) null);
            } else
                repairContainer.inputSlots.setInventorySlotContents(1, (ItemStack) null);

            this.repairContainer.maximumCost = 0;

            // Only Railcraft change is the random chance of damage
            if (!player.capabilities.isCreativeMode && !this.world.isRemote && WorldPlugin.getBlock(world, x, y, z) instanceof BlockAnvil && player.getRNG().nextFloat() < breakChance) {
                int i = this.world.getBlockMetadata(this.x, this.y, this.z);
                int j = i & 3;
                int k = i >> 2;
                ++k;

                if (k > 2) {
                    this.world.setBlockToAir(this.x, this.y, this.z);
                    this.world.playAuxSFX(1020, this.x, this.y, this.z, 0);
                } else {
                    this.world.setBlockMetadataWithNotify(this.x, this.y, this.z, j | k << 2, 2);
                    this.world.playAuxSFX(1021, this.x, this.y, this.z, 0);
                }
            } else if (!this.world.isRemote)
                this.world.playAuxSFX(1021, this.x, this.y, this.z, 0);
        }

    }
}
