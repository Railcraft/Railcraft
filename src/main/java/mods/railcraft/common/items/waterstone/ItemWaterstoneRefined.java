/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.waterstone;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.ItemRailcraft;
import mods.railcraft.common.items.firestone.EntityItemFirestone;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.List;

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
    public void getSubItems(Item id, CreativeTabs tab, List<ItemStack> list) {
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
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        String tipTag = getUnlocalizedName() + ".tip.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getUnlocalizedName() + ".tip.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.canPlayerEdit(pos, side, stack)) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (block != null && block != Blocks.stone) {
                List<ItemStack> drops = block.getDrops(world, pos, state, 0);
                if (drops.size() == 1 && drops.get(0) != null && drops.get(0).getItem() instanceof ItemBlock) {
                    ItemStack cooked = FurnaceRecipes.instance().getSmeltingResult(drops.get(0));
                    if (cooked != null && cooked.getItem() instanceof ItemBlock) {
                        int meta = !cooked.getItem().getHasSubtypes() ? 0 : cooked.getItem().getMetadata(cooked.getItemDamage());
                        world.setBlockState(pos, InvTools.getBlockFromStack(cooked).getStateFromMeta(meta), 3);
                        world.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                        stack.damageItem(1, player);
                        return true;
                    }
                }
            }
        }

        pos = pos.offset(side);

        if (player.canPlayerEdit(pos, side, stack) && world.isAirBlock(pos)) {
            world.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.fire.getDefaultState());
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
        entity.setDefaultPickupDelay();
        return entity;
    }

}
