/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.common.core.IRailcraftObject;
import mods.railcraft.common.core.IVariantEnum;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemOveralls extends ItemArmor implements IRailcraftObject {

    private static final ItemStack BLUE_CLOTH = new ItemStack(Blocks.WOOL, 1, 3);
    private static final String TEXTURE = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "overalls.png";

    public ItemOveralls() {
        super(ItemMaterials.OVERALLS, 0, EntityEquipmentSlot.LEGS);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return InvTools.isItemEqual(stack, BLUE_CLOTH);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        ToolTip tip = ToolTip.buildToolTip(stack.getUnlocalizedName() + ".tip");
        if (tip != null)
            info.addAll(tip.convertToStrings());
    }

    @Override
    public Object getRecipeObject(IVariantEnum meta) {
        return this;
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addRecipe(new ItemStack(this),
                "III",
                "I I",
                "I I",
                'I', new ItemStack(Blocks.WOOL, 1, 3));
    }

    @Override
    public void finalizeDefinition() {

    }

    @Override
    public void initializeDefinintion() {
        LootPlugin.addLoot(RailcraftItems.overalls, 1, 1, LootPlugin.Type.WORKSHOP);
    }
}
