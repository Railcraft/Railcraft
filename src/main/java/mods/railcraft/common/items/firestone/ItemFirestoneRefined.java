/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items.firestone;

import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.sounds.SoundHelper;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class ItemFirestoneRefined extends ItemFirestoneBase {

    private static final int HEAT = 250;
    public static Item item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.firestone.refined";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemFirestoneRefined().setUnlocalizedName(tag);
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

    public ItemFirestoneRefined() {
        setMaxStackSize(1);
        setMaxDamage(5000);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, getMaxDamage()));
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack newStack;
        double damageLevel = (double) stack.getItemDamage() / (double) stack.getMaxDamage();
        if (MiscTools.RANDOM.nextDouble() < damageLevel * 0.0001) {
            newStack = ItemFirestoneCracked.getItemEmpty();
            if (stack.hasDisplayName())
                newStack.setStackDisplayName(stack.getDisplayName());
        } else
            newStack = stack.copy();
        newStack.stackSize = 1;
        newStack = InvTools.damageItem(newStack, 1);
        return newStack;
    }

    @Override
    public int getHeatValue(ItemStack stack) {
        if (stack.getItemDamage() < getMaxDamage())
            return HEAT;
        return 0;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        String tipTag = getUnlocalizedName() + ".tip.charged";
        if (stack.getItemDamage() >= stack.getMaxDamage() - 5)
            tipTag = getUnlocalizedName() + ".tip.empty";
        ToolTip tip = ToolTip.buildToolTip(tipTag);
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

    //TODO: test
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.canPlayerEdit(pos, side, stack)) {
            Block block = WorldPlugin.getBlock(world, pos);
            if (block != null && block != Blocks.STONE) {
                List<ItemStack> drops = block.getDrops(world, pos, WorldPlugin.getBlockState(world, pos), 0);
                if (drops.size() == 1 && drops.get(0) != null && drops.get(0).getItem() instanceof ItemBlock) {
                    ItemStack cooked = FurnaceRecipes.instance().getSmeltingResult(drops.get(0));
                    if (cooked != null && cooked.getItem() instanceof ItemBlock) {
                        IBlockState newState = InvTools.getBlockStateFromStack(cooked, (WorldServer) world, pos);
                        if (newState != null) {
                            WorldPlugin.setBlockState(world, pos, newState);
                            SoundHelper.playSound(world, pos, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
                            stack.damageItem(1, player);
                            return true;
                        }
                    }
                }
            }
        }

        pos = pos.offset(side);

        if (player.canPlayerEdit(pos, side, stack) && world.isAirBlock(pos)) {
            SoundHelper.playSound(world, pos, "fire.ignite", 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            stack.damageItem(1, player);
            return true;
        }
        return false;
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
