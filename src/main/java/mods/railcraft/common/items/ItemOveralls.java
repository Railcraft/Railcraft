/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.api.charge.IChargeProtectionItem;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemOveralls extends ItemRailcraftArmor implements IChargeProtectionItem {

    private static final ItemStack BLUE_CLOTH = new ItemStack(Blocks.WOOL, 1, 3);
    private static final String TEXTURE = RailcraftConstants.ARMOR_TEXTURE_FOLDER + "overalls.png";

    public ItemOveralls() {
        super(ItemMaterials.OVERALLS, 0, EntityEquipmentSlot.LEGS);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return TEXTURE;
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return InvTools.isItemEqual(stack, BLUE_CLOTH);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        super.addInformation(stack, world, info, adv);
        ToolTip tip = ToolTip.buildToolTip(stack.getTranslationKey() + ".tips");
        info.addAll(tip.convertToStrings());
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapedRecipe(new ItemStack(this),
                "III",
                "I I",
                "I I",
                'I', new ItemStack(Blocks.WOOL, 1, 3));
    }
}
