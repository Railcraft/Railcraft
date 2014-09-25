/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemOveralls extends ItemArmor {

    private static final ItemStack BLUE_CLOTH = new ItemStack(Blocks.wool, 1, 3);
    private static final String TEXTURE = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "overalls.png";

    public static boolean isPlayerWearing(EntityPlayer player) {
        ItemStack pants = player.getCurrentArmor(MiscTools.ArmorSlots.LEGS.ordinal());
        return pants != null && pants.getItem() instanceof ItemOveralls;
    }

    public ItemOveralls() {
        super(ItemMaterials.OVERALLS, 0, 2);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(getUnlocalizedName()));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return InvTools.isItemEqual(stack, BLUE_CLOTH);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        ToolTip tip = ToolTip.buildToolTip(stack.getUnlocalizedName() + ".tip");
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

}
