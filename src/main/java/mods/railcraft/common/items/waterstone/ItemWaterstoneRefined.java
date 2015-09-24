/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.waterstone;

import mods.railcraft.common.items.firestone.*;
import cpw.mods.fml.common.registry.GameRegistry;

import java.util.List;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemWaterstoneRefined extends ItemRailcraft {

    public static int HEAT = 500;
    public static Item item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.waterstone.refined";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemWaterstoneRefined().setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    public static ItemStack getItemCharged() {
        return new ItemStack(item);
    }

    public static ItemStack getItemEmpty() {
        return new ItemStack(item, 1, item.getMaxDamage() - 1);
    }

    public ItemWaterstoneRefined() {
        setMaxStackSize(1);
        setMaxDamage(5000);
    }

    @Override
    public void getSubItems(Item id, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 5000));
//        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.stackSize = 1;
        newStack = InvTools.damageItem(newStack, 1);
        return newStack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        String tipTag = getUnlocalizedName() + ".tip.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getUnlocalizedName() + ".tip.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (player.canPlayerEdit(x, y, z, side, stack)) {
            Block block = WorldPlugin.getBlock(world, x, y, z);
            if (block != null && block != Blocks.stone) {
                List<ItemStack> drops = block.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                if (drops.size() == 1 && drops.get(0) != null && drops.get(0).getItem() instanceof ItemBlock) {
                    ItemStack cooked = FurnaceRecipes.smelting().getSmeltingResult(drops.get(0));
                    if (cooked != null && cooked.getItem() instanceof ItemBlock) {
                        int meta = !cooked.getItem().getHasSubtypes() ? 0 : cooked.getItem().getMetadata(cooked.getItemDamage());
                        world.setBlock(x, y, z, InvTools.getBlockFromStack(cooked), meta, 3);
                        world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                        stack.damageItem(1, player);
                        return true;
                    }
                }
            }
        }

        if (side == 0)
            --y;

        if (side == 1)
            ++y;

        if (side == 2)
            --z;

        if (side == 3)
            ++z;

        if (side == 4)
            --x;

        if (side == 5)
            ++x;

        if (player.canPlayerEdit(x, y, z, side, stack) && world.isAirBlock(x, y, z)) {
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlock(x, y, z, Blocks.fire);
            stack.damageItem(1, player);
            return true;
        }
        return false;
    }

    /**
     * Determines if this Item has a special entity for when they are in the
     * world. Is called when a EntityItem is spawned in the world, if true and
     * Item#createCustomEntity returns non null, the EntityItem will be
     * destroyed and the new Entity will be added to the world.
     *
     * @param stack The current item stack
     * @return True of the item has a custom entity, If true,
     * Item#createCustomEntity will be called
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world    The world object
     * @param location The EntityItem object, useful for getting the position of
     *                 the entity
     * @param stack    The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityItemFirestone entity = new EntityItemFirestone(world, location.posX, location.posY, location.posZ, stack);
        entity.motionX = location.motionX;
        entity.motionY = location.motionY;
        entity.motionZ = location.motionZ;
        entity.delayBeforeCanPickup = 10;
        return entity;
    }

}
