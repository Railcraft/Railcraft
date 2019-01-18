/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.gui.containers;

import mods.railcraft.common.gui.slots.SlotRailcraft;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
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
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static mods.railcraft.common.util.inventory.InvTools.*;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ContainerAnvil extends ContainerRepair {
    public static int MAX_COST = 50;

    private final IInventory outputSlot = new InventoryCraftResult();
    private final World world;
    private final EntityPlayer thePlayer;
    private final BlockPos pos;
    private String repairedItemName;

    public ContainerAnvil(InventoryPlayer playerInv, World world, BlockPos pos, EntityPlayer player) {
        super(playerInv, world, pos, player);
        this.world = world;
        this.thePlayer = player;
        this.pos = pos;

        SlotAnvil slot = new SlotAnvil(this, outputSlot, 2, 134, 47, world, pos);
        slot.slotNumber = 2;
        inventorySlots.set(2, slot);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return WorldPlugin.getBlock(world, pos) instanceof BlockAnvil && pos.distanceSqToCenter(player.posX, player.posY, player.posZ) <= 64.0D;
    }

    @Override
    public void updateRepairOutput() {
        ItemStack input1original = inputSlots.getStackInSlot(0);
        this.maximumCost = 1;
        int enchantCost = 0;
        int baseCost = 0;
        int nameCost = 0;

        if (isEmpty(input1original)) {
            outputSlot.setInventorySlotContents(0, emptyStack());
            this.maximumCost = 0;
        } else {
            ItemStack input1 = input1original.copy();
            ItemStack input2 = inputSlots.getStackInSlot(1);
            Map<Enchantment, Integer> input1Enchantments = EnchantmentHelper.getEnchantments(input1);
            baseCost = baseCost + input1original.getRepairCost() + (InvTools.isEmpty(input2) ? 0 : input2.getRepairCost());
            this.materialCost = 0;
            boolean isEnchantedBook = false;

            if (!InvTools.isEmpty(input2)) {
                if (!net.minecraftforge.common.ForgeHooks.onAnvilChange(this, input1original, input2, outputSlot, repairedItemName, baseCost))
                    return;
                isEnchantedBook = input2.getItem() == Items.ENCHANTED_BOOK && ItemEnchantedBook.getEnchantments(input2).tagCount() > 0;

                if (input1.isItemStackDamageable() && input1.getItem().getIsRepairable(input1original, input2)) {
                    int damageToRepair = Math.min(input1.getItemDamage(), input1.getMaxDamage() / 4);

                    if (damageToRepair <= 0) {
                        outputSlot.setInventorySlotContents(0, InvTools.emptyStack());
                        this.maximumCost = 0;
                        return;
                    }

                    int projectedMaterialCost;

                    for (projectedMaterialCost = 0; damageToRepair > 0 && projectedMaterialCost < sizeOf(input2); ++projectedMaterialCost) {
                        int repairedDamage = input1.getItemDamage() - damageToRepair;
                        input1.setItemDamage(repairedDamage);
                        ++enchantCost;
                        damageToRepair = Math.min(input1.getItemDamage(), input1.getMaxDamage() / 4);
                    }

                    this.materialCost = projectedMaterialCost;
                } else {
                    if (!isEnchantedBook && (input1.getItem() != input2.getItem() || !input1.isItemStackDamageable())) {
                        outputSlot.setInventorySlotContents(0, InvTools.emptyStack());
                        this.maximumCost = 0;
                        return;
                    }

                    if (input1.isItemStackDamageable() && !isEnchantedBook) {
                        int health1 = input1original.getMaxDamage() - input1original.getItemDamage();
                        int health2 = input2.getMaxDamage() - input2.getItemDamage();
                        int repairAmount = health2 + input1.getMaxDamage() * 12 / 100;
                        int newHealth = health1 + repairAmount;
                        int damage1 = input1.getMaxDamage() - newHealth;

                        if (damage1 < 0) {
                            damage1 = 0;
                        }

                        if (damage1 < input1.getMetadata()) {
                            input1.setItemDamage(damage1);
                            enchantCost += 2;
                        }
                    }

                    Map<Enchantment, Integer> input2Enchantments = EnchantmentHelper.getEnchantments(input2);

                    for (@Nullable Enchantment input2Enchantment : input2Enchantments.keySet()) {
                        if (input2Enchantment != null) {
                            int input1EnchantLevel = input1Enchantments.getOrDefault(input2Enchantment, 0);
                            int input2EnchantLevel = input2Enchantments.get(input2Enchantment);
                            input2EnchantLevel = input1EnchantLevel == input2EnchantLevel ? input2EnchantLevel + 1 : Math.max(input2EnchantLevel, input1EnchantLevel);
                            boolean canApplyEnchants = input2Enchantment.canApply(input1original);

                            if (thePlayer.capabilities.isCreativeMode || input1original.getItem() == Items.ENCHANTED_BOOK) {
                                canApplyEnchants = true;
                            }

                            for (@Nullable Enchantment input1Enchantment : input1Enchantments.keySet()) {
                                // Null check to prevent crash
                                if (input1Enchantment != null && input1Enchantment != input2Enchantment && !(input2Enchantment.isCompatibleWith(input1Enchantment) && input1Enchantment.isCompatibleWith(input2Enchantment))) //Forge BugFix: Let Both enchantments veto being together
                                {
                                    canApplyEnchants = false;
                                    ++enchantCost;
                                }
                            }

                            if (canApplyEnchants) {
                                if (input2EnchantLevel > input2Enchantment.getMaxLevel()) {
                                    input2EnchantLevel = input2Enchantment.getMaxLevel();
                                }

                                input1Enchantments.put(input2Enchantment, input2EnchantLevel);
                                int rarityMultiplier = 0;

                                switch (input2Enchantment.getRarity()) {
                                    case COMMON:
                                        rarityMultiplier = 1;
                                        break;
                                    case UNCOMMON:
                                        rarityMultiplier = 2;
                                        break;
                                    case RARE:
                                        rarityMultiplier = 4;
                                        break;
                                    case VERY_RARE:
                                        rarityMultiplier = 8;
                                }

                                if (isEnchantedBook) {
                                    rarityMultiplier = Math.max(1, rarityMultiplier / 2);
                                }

                                enchantCost += rarityMultiplier * input2EnchantLevel;
                            }
                        }
                    }
                }
            }

            if (isEnchantedBook && !input1.getItem().isBookEnchantable(input1, input2)) input1 = InvTools.emptyStack();

            if (!InvTools.isEmpty(input1))
                if (StringUtils.isBlank(repairedItemName)) {
                    if (input1original.hasDisplayName()) {
                        nameCost = 1;
                        enchantCost += nameCost;
                        input1.clearCustomName();
                    }
                } else if (!repairedItemName.equals(input1original.getDisplayName())) {
                    nameCost = 1;
                    enchantCost += nameCost;
                    input1.setStackDisplayName(repairedItemName);
                }

            this.maximumCost = baseCost + enchantCost;

            if (enchantCost <= 0) {
                input1 = InvTools.emptyStack();
            }

            // Railcraft changes max cost from 39 to 50
            if (nameCost == enchantCost && nameCost > 0 && maximumCost > MAX_COST) {
                this.maximumCost = MAX_COST;
            }

            // Here too
            if (maximumCost > MAX_COST && !thePlayer.capabilities.isCreativeMode) {
                input1 = InvTools.emptyStack();
            }

            if (!InvTools.isEmpty(input1)) {
                int repairCost = input1.getRepairCost();

                if (!InvTools.isEmpty(input2) && repairCost < input2.getRepairCost()) {
                    repairCost = input2.getRepairCost();
                }

                if (nameCost != enchantCost || nameCost == 0) {
                    repairCost = repairCost * 2 + 1;
                }
                input1.setRepairCost(repairCost);
                EnchantmentHelper.setEnchantments(input1Enchantments, input1);
            }

            outputSlot.setInventorySlotContents(0, input1);
            detectAndSendChanges();
        }
    }

    /**
     * used by the Anvil GUI to update the Item Name being typed by the player
     *
     * We override it to point at our version of repairedItemName
     */
    @Override
    public void updateItemName(String par1Str) {
        this.repairedItemName = par1Str;

        if (getSlot(2).getHasStack()) {
            ItemStack itemstack = getSlot(2).getStack();
            assert !InvTools.isEmpty(itemstack);

            if (StringUtils.isBlank(par1Str)) {
                itemstack.clearCustomName();
            } else
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
        public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
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
        public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
            if (!playerIn.capabilities.isCreativeMode) {
                playerIn.addExperienceLevel(-repairContainer.maximumCost);
            }

            float breakChance = net.minecraftforge.common.ForgeHooks.onAnvilRepair(playerIn, stack, repairContainer.inputSlots.getStackInSlot(0), repairContainer.inputSlots.getStackInSlot(1));

            // Only Railcraft change is to divide breakChance in half
            breakChance /= 2.0;

            repairContainer.inputSlots.setInventorySlotContents(0, InvTools.emptyStack());

            if (repairContainer.materialCost > 0) {
                ItemStack itemstack = repairContainer.inputSlots.getStackInSlot(1);

                if (sizeOf(itemstack) > repairContainer.materialCost) {
                    decSize(itemstack, repairContainer.materialCost);
                    repairContainer.inputSlots.setInventorySlotContents(1, itemstack);
                } else {
                    repairContainer.inputSlots.setInventorySlotContents(1, InvTools.emptyStack());
                }
            } else {
                repairContainer.inputSlots.setInventorySlotContents(1, InvTools.emptyStack());
            }

            repairContainer.maximumCost = 0;
            IBlockState iblockstate = worldIn.getBlockState(blockPosIn);

            if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == Blocks.ANVIL && playerIn.getRNG().nextFloat() < breakChance) {
                int l = iblockstate.getValue(BlockAnvil.DAMAGE);
                ++l;

                if (l > 2) {
                    worldIn.setBlockToAir(blockPosIn);
                    worldIn.playEvent(1029, blockPosIn, 0);
                } else {
                    worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, l), 2);
                    worldIn.playEvent(1030, blockPosIn, 0);
                }
            } else if (!worldIn.isRemote) {
                worldIn.playEvent(1030, blockPosIn, 0);
            }
            return stack;
        }

    }
}
