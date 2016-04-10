/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ContainerAnvil extends ContainerRepair {

    private final IInventory outputSlot = new InventoryCraftResult();
    private final World world;
    private final EntityPlayer thePlayer;
    private final BlockPos pos;
    private int stackSizeToBeUsedInRepair;
    private String repairedItemName;
    private final IInventory inputSlots;

    public ContainerAnvil(InventoryPlayer playerInv, World world, BlockPos pos, EntityPlayer player) {
        super(playerInv, world, pos, player);
        this.world = world;
        this.thePlayer = player;
        this.pos = pos;

        SlotAnvil slot = new SlotAnvil(this, outputSlot, 2, 134, 47, world, pos);
        slot.slotNumber = 2;
        inventorySlots.set(2, slot);

        inputSlots = ObfuscationReflectionHelper.getPrivateValue(ContainerRepair.class, this, 2);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return WorldPlugin.getBlock(world, pos) instanceof BlockAnvil && pos.distanceSqToCenter(player.posX, player.posY, player.posZ) <= 64.0D;
    }

    @Override
    public void updateRepairOutput() {
        ItemStack input1original = inputSlots.getStackInSlot(0);
        this.maximumCost = 1;
        int l1 = 0;
        int baseCost = 0;
        int j2 = 0;

        if (input1original == null) {
            outputSlot.setInventorySlotContents(0, null);
            this.maximumCost = 0;
        } else {
            ItemStack input1 = input1original.copy();
            ItemStack input2 = inputSlots.getStackInSlot(1);
            Map<Integer, Integer> input1Enchantments = EnchantmentHelper.getEnchantments(input1);
            boolean isEnchantedBook = false;
            baseCost = baseCost + input1original.getRepairCost() + (input2 == null ? 0 : input2.getRepairCost());
            this.materialCost = 0;

            if (input2 != null) {
                if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, input1original, input2, outputSlot, repairedItemName, baseCost))
                    return;
                isEnchantedBook = input2.getItem() == Items.enchanted_book && Items.enchanted_book.getEnchantments(input2).tagCount() > 0;

                if (input1.isItemStackDamageable() && input1.getItem().getIsRepairable(input1original, input2)) {
                    int j4 = Math.min(input1.getItemDamage(), input1.getMaxDamage() / 4);

                    if (j4 <= 0) {
                        outputSlot.setInventorySlotContents(0, null);
                        this.maximumCost = 0;
                        return;
                    }

                    int projectedMaterialCost;

                    for (projectedMaterialCost = 0; j4 > 0 && projectedMaterialCost < input2.stackSize; ++projectedMaterialCost) {
                        int j5 = input1.getItemDamage() - j4;
                        input1.setItemDamage(j5);
                        ++l1;
                        j4 = Math.min(input1.getItemDamage(), input1.getMaxDamage() / 4);
                    }

                    this.materialCost = projectedMaterialCost;
                } else {
                    if (!isEnchantedBook && (input1.getItem() != input2.getItem() || !input1.isItemStackDamageable())) {
                        outputSlot.setInventorySlotContents(0, null);
                        this.maximumCost = 0;
                        return;
                    }

                    if (input1.isItemStackDamageable() && !isEnchantedBook) {
                        int k2 = input1original.getMaxDamage() - input1original.getItemDamage();
                        int l2 = input2.getMaxDamage() - input2.getItemDamage();
                        int i3 = l2 + input1.getMaxDamage() * 12 / 100;
                        int j3 = k2 + i3;
                        int k3 = input1.getMaxDamage() - j3;

                        if (k3 < 0) {
                            k3 = 0;
                        }

                        if (k3 < input1.getMetadata()) {
                            input1.setItemDamage(k3);
                            l1 += 2;
                        }
                    }

                    Map<Integer, Integer> input2Enchantments = EnchantmentHelper.getEnchantments(input2);

                    for (int input2EnchantId : input2Enchantments.keySet()) {
                        Enchantment enchantment = Enchantment.getEnchantmentById(input2EnchantId);

                        if (enchantment != null) {
                            int k5 = input1Enchantments.containsKey(input2EnchantId) ? input1Enchantments.get(input2EnchantId) : 0;
                            int l3 = input2Enchantments.get(input2EnchantId);
                            int i6;

                            if (k5 == l3) {
                                ++l3;
                                i6 = l3;
                            } else {
                                i6 = Math.max(l3, k5);
                            }

                            l3 = i6;
                            boolean canApplyEnchants = enchantment.canApply(input1original);

                            if (thePlayer.capabilities.isCreativeMode || input1original.getItem() == Items.enchanted_book) {
                                canApplyEnchants = true;
                            }

                            for (int input1EnchantId : input1Enchantments.keySet()) {
                                Enchantment e2 = Enchantment.getEnchantmentById(input1EnchantId);
                                if (input1EnchantId != input2EnchantId && !(enchantment.canApplyTogether(e2) && e2.canApplyTogether(enchantment))) //Forge BugFix: Let Both enchantments veto being together
                                {
                                    canApplyEnchants = false;
                                    ++l1;
                                }
                            }

                            if (canApplyEnchants) {
                                if (l3 > enchantment.getMaxLevel()) {
                                    l3 = enchantment.getMaxLevel();
                                }

                                input1Enchantments.put(input2EnchantId, l3);
                                int l5 = 0;

                                switch (enchantment.getWeight()) {
                                    case 1:
                                        l5 = 8;
                                        break;
                                    case 2:
                                        l5 = 4;
                                    case 3:
                                    case 4:
                                    case 6:
                                    case 7:
                                    case 8:
                                    case 9:
                                    default:
                                        break;
                                    case 5:
                                        l5 = 2;
                                        break;
                                    case 10:
                                        l5 = 1;
                                }

                                if (isEnchantedBook) {
                                    l5 = Math.max(1, l5 / 2);
                                }

                                l1 += l5 * l3;
                            }
                        }
                    }
                }
            }

            if (isEnchantedBook && !input1.getItem().isBookEnchantable(input1, input2)) input1 = null;

            if (input1 != null)
                if (StringUtils.isBlank(repairedItemName)) {
                    if (input1original.hasDisplayName()) {
                        j2 = 1;
                        l1 += j2;
                        input1.clearCustomName();
                    }
                } else if (!repairedItemName.equals(input1original.getDisplayName())) {
                    j2 = 1;
                    l1 += j2;
                    input1.setStackDisplayName(repairedItemName);
                }

            this.maximumCost = baseCost + l1;

            if (l1 <= 0) {
                input1 = null;
            }

            // Railcraft changes max cost from 39 to 50
            if (j2 == l1 && j2 > 0 && maximumCost > 50) {
                this.maximumCost = 50;
            }

            // Here too
            if (maximumCost > 50 && !thePlayer.capabilities.isCreativeMode) {
                input1 = null;
            }

            if (input1 != null) {
                int k4 = input1.getRepairCost();

                if (input2 != null && k4 < input2.getRepairCost()) {
                    k4 = input2.getRepairCost();
                }

                k4 = k4 * 2 + 1;
                input1.setRepairCost(k4);
                EnchantmentHelper.setEnchantments(input1Enchantments, input1);
            }

            outputSlot.setInventorySlotContents(0, input1);
            detectAndSendChanges();
        }
    }

    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     */
    @Override
    public void updateItemName(String par1Str) {
        this.repairedItemName = par1Str;

        if (getSlot(2).getHasStack()) {
            ItemStack itemstack = getSlot(2).getStack();

            if (StringUtils.isBlank(par1Str))
                itemstack.clearCustomName();
            else
                itemstack.setStackDisplayName(repairedItemName);
        }

        updateRepairOutput();
    }

    private class SlotAnvil extends SlotRailcraft {

        final World worldIn;
        final BlockPos blockPosIn;
        final ContainerAnvil repairContainer;

        SlotAnvil(ContainerAnvil container, IInventory inv, int index, int slotX, int slotY, World world, BlockPos pos) {
            super(inv, index, slotX, slotY);
            this.repairContainer = container;
            this.worldIn = world;
            this.blockPosIn = pos;
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
            return (par1EntityPlayer.capabilities.isCreativeMode || par1EntityPlayer.experienceLevel >= repairContainer.maximumCost) && repairContainer.maximumCost > 0 && getHasStack();
        }

        @Override
        public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
            if (!playerIn.capabilities.isCreativeMode) {
                playerIn.addExperienceLevel(-repairContainer.maximumCost);
            }

            float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(playerIn, stack, repairContainer.inputSlots.getStackInSlot(0), repairContainer.inputSlots.getStackInSlot(1));

            // Only Railcraft change is to divide breakChance in half
            breakChance /= 2.0;

            repairContainer.inputSlots.setInventorySlotContents(0, null);

            if (repairContainer.materialCost > 0) {
                ItemStack itemstack = repairContainer.inputSlots.getStackInSlot(1);

                if (itemstack != null && itemstack.stackSize > repairContainer.materialCost) {
                    itemstack.stackSize -= repairContainer.materialCost;
                    repairContainer.inputSlots.setInventorySlotContents(1, itemstack);
                } else {
                    repairContainer.inputSlots.setInventorySlotContents(1, null);
                }
            } else {
                repairContainer.inputSlots.setInventorySlotContents(1, null);
            }

            repairContainer.maximumCost = 0;
            IBlockState iblockstate = worldIn.getBlockState(blockPosIn);

            if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == Blocks.anvil && playerIn.getRNG().nextFloat() < breakChance) {
                int l = iblockstate.getValue(BlockAnvil.DAMAGE);
                ++l;

                if (l > 2) {
                    worldIn.setBlockToAir(blockPosIn);
                    worldIn.playAuxSFX(1020, blockPosIn, 0);
                } else {
                    worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, l), 2);
                    worldIn.playAuxSFX(1021, blockPosIn, 0);
                }
            } else if (!worldIn.isRemote) {
                worldIn.playAuxSFX(1021, blockPosIn, 0);
            }
        }

    }
}
