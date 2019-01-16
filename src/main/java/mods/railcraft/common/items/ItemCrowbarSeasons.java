/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.ChatPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.misc.SeasonPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemCrowbarSeasons extends ItemCrowbar {

    public ItemCrowbarSeasons() {
        super(ItemMaterials.Material.DIAMOND, ToolMaterial.DIAMOND);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static SeasonPlugin.Season getCurrentSeason(ItemStack crowbar) {
        SeasonPlugin.Season season = SeasonPlugin.Season.DEFAULT;
        if (crowbar.getItem() instanceof ItemCrowbarSeasons) {
            NBTTagCompound data = crowbar.getTagCompound();
            if (data != null)
                season = SeasonPlugin.Season.VALUES[data.getByte("season")];
        }
        return season;
    }

    public static void incrementSeason(ItemStack crowbar) {
        if (crowbar.getItem() instanceof ItemCrowbarSeasons) {
            NBTTagCompound data = crowbar.getTagCompound();
            if (data == null) {
                data = new NBTTagCompound();
                crowbar.setTagCompound(data);
            }
            byte aura = data.getByte("season");
            aura++;
            if (aura >= SeasonPlugin.Season.VALUES.length)
                aura = 0;
            data.setByte("season", aura);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (Game.isHost(world)) {
            incrementSeason(stack);
            SeasonPlugin.Season aura = getCurrentSeason(stack);
            ChatPlugin.sendLocalizedHotBarMessageFromServer(player, "item.railcraft.tool.crowbar.seasons.tips.mode", "\u00A75" + aura);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack.copy());
    }

    @Override
    public boolean getIsRepairable(ItemStack itemToRepair, ItemStack stack) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag adv) {
        SeasonPlugin.Season aura = getCurrentSeason(stack);
        String mode = LocalizationPlugin.translate("item.railcraft.tool.crowbar.seasons.tips.mode");

        list.add(String.format(mode, "\u00A75" + aura));
        addToolTips(stack, world, list, adv);
    }

}
