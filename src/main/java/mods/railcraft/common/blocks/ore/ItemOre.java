/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.ore;

import java.util.List;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.items.EntityItemFireproof;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemOre extends ItemBlock {

    public ItemOre(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    @Override
    public IIcon getIconFromDamage(int meta) {
        return EnumOre.values()[meta].getTexture(meta);
    }

    @Override
    public int getMetadata(int meta) {
        return meta;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage < 0 || damage >= EnumOre.values().length)
            return "";
        return EnumOre.values()[damage].getTag();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        String tipTag = EnumOre.fromMeta(stack.getItemDamage()).getTag() + ".tip";
        if (LocalizationPlugin.hasTag(tipTag)) {
            ToolTip tip = ToolTip.buildToolTip(tipTag);
            info.addAll(tip.convertToStrings());
        }
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
        if (stack != null && stack.getItemDamage() == EnumOre.FIRESTONE.ordinal())
            return true;
        return false;
    }

    /**
     * This function should return a new entity to replace the dropped item.
     * Returning null here will not kill the EntityItem and will leave it to
     * function normally. Called when the item it placed in a world.
     *
     * @param world The world object
     * @param location The EntityItem object, useful for getting the position of
     * the entity
     * @param stack The current item stack
     * @return A new Entity object to spawn or null
     */
    @Override
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        if (!hasCustomEntity(stack))
            return null;
        EntityItemFireproof entity = new EntityItemFireproof(world, location.posX, location.posY, location.posZ, stack);
        entity.motionX = location.motionX;
        entity.motionY = location.motionY;
        entity.motionZ = location.motionZ;
        entity.delayBeforeCanPickup = 10;
        return entity;
    }

}
