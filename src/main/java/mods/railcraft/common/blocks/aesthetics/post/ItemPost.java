/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemPost extends ItemBlockRailcraft<BlockPost> {

    public ItemPost(BlockPost block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setTranslationKey("railcraft.post");
    }

    public static void setEmblem(ItemStack stack, String emblemIdentifier) {
        NBTTagCompound nbt = InvTools.getItemData(stack);
        nbt.setString("emblem", emblemIdentifier);
    }

    public static String getEmblem(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null || !nbt.hasKey("emblem"))
            return "";
        return nbt.getString("emblem");
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return EnumPost.fromId(stack.getItemDamage()).getTag().replace('_', '.');
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        if (stack.getItemDamage() == EnumPost.EMBLEM.ordinal() && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");

            if (emblemIdent == null || EmblemToolsClient.packageManager == null)
                return;

            Emblem emblem = EmblemToolsClient.packageManager.getEmblem(emblemIdent.getString());
            if (emblem != null)
                tooltip.add(TextFormatting.GRAY + emblem.displayName);
        }
    }
}
