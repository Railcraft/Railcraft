package mods.railcraft.common.items;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by GeneralCamo on 6/4/2017.
 *
 * @author GeneralCamo
 *         Created for Railcraft <http://www.railcraft.info>
 */
public class ItemCrowbarDiamond extends ItemCrowbar {

    public ItemCrowbarDiamond() {
        super(ItemMaterials.Material.DIAMOND, ToolMaterial.DIAMOND);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean advInfo) {
        info.add(LocalizationPlugin.translate("item.railcraft.tool.crowbar.diamond.tips"));
        info.add(LocalizationPlugin.translate("item.railcraft.tool.crowbar.tips"));
    }

}
