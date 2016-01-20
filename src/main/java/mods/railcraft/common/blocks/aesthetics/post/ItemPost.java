/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.client.emblems.Emblem;
import mods.railcraft.client.emblems.EmblemToolsClient;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemPost extends ItemBlock {

    public ItemPost(Block block) {
        super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
        setUnlocalizedName("railcraft.post");
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
    public String getUnlocalizedName(ItemStack stack) {
        return EnumPost.fromId(stack.getItemDamage()).getTag();
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
        if (stack.getItemDamage() == EnumPost.EMBLEM.ordinal() && stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString emblemIdent = (NBTTagString) nbt.getTag("emblem");

            if (emblemIdent == null || EmblemToolsClient.packageManager == null)
                return;

            Emblem emblem = EmblemToolsClient.packageManager.getEmblem(emblemIdent.getString());
            if (emblem != null)
                info.add(EnumChatFormatting.GRAY + emblem.displayName);
        }
    }
}
