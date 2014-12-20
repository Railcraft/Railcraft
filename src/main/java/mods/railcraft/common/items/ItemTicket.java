/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTicket extends ItemRailcraft {

    public static final IStackFilter FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemTicket;
        }

    };
    public static final int LINE_LENGTH = 32;
    public static ItemTicket item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.routing.ticket";
            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemTicket();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);
            }
        }
    }

    public static ItemStack getTicket() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public static boolean isNBTValid(NBTTagCompound nbt) {
        if (nbt == null)
            return false;
        else if (!nbt.hasKey("dest"))
            return false;

        NBTTagString dest = (NBTTagString) nbt.getTag("dest");
        if (dest.func_150285_a_() == null)
            return false;

        return dest.func_150285_a_().length() <= LINE_LENGTH;
    }

    public static ItemStack copyTicket(ItemStack source) {
        if (item == null)
            return null;
        if (source == null)
            return null;
        if (source.getItem() instanceof ItemTicket) {
            ItemStack ticket = getTicket();
            NBTTagCompound nbt = source.getTagCompound();
            if (nbt != null)
                ticket.setTagCompound((NBTTagCompound) nbt.copy());
            return ticket;
        }
        return null;
    }

    public static boolean setTicketData(ItemStack ticket, String dest, String title, GameProfile owner) {
        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
            return false;
        if (dest.length() > LINE_LENGTH)
            return false;
        if (owner == null || owner.equals(""))
            return false;
        NBTTagCompound data = InvTools.getItemData(ticket);
        data.setString("dest", dest);
        data.setString("title", title);
        PlayerPlugin.writeOwnerToNBT(data, owner);
        return true;
    }

    public static String getDestination(ItemStack ticket) {
        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
            return "";
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return "";
        return nbt.getString("dest");
    }

    public static boolean matchesOwnerOrOp(ItemStack ticket, GameProfile player) {
        if (!(item instanceof ItemTicket))
            return false;
        return PlayerPlugin.isOwnerOrOp(getOwner(ticket), player);
    }

    public static GameProfile getOwner(ItemStack ticket) {
        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
            return new GameProfile(null, "[Unknown]");
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return new GameProfile(null, "[Unknown]");
        return PlayerPlugin.readOwnerFromNBT(nbt);
    }

    public boolean validateNBT(NBTTagCompound nbt) {
        String dest = nbt.getString("dest");
        return dest.length() < LINE_LENGTH;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:ticket");
    }

//    @Override
//    public String getItemDisplayName(ItemStack stack) {
//        String dest = getDestination(stack);
//
//        if (!dest.equals("")) {
//            return super.getItemDisplayName(stack) + " - " + dest.substring(dest.lastIndexOf("/") + 1);
//        }
//
//        return super.getItemDisplayName(stack);
//    }
    /**
     * allows items to add custom lines of information to the mouseover
     * description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (stack.hasTagCompound()) {
            GameProfile owner = getOwner(stack);
            if (owner.getId() != null) {
                list.add(EnumChatFormatting.WHITE + LocalizationPlugin.translate("railcraft.gui.routing.ticket.tip.issuer"));
                list.add(EnumChatFormatting.GRAY + PlayerPlugin.getUsername(player.worldObj, owner));
            }

            String dest = getDestination(stack);
            if (!dest.equals("")) {
                list.add(EnumChatFormatting.WHITE + LocalizationPlugin.translate("railcraft.gui.routing.ticket.tip.dest"));
                list.add(EnumChatFormatting.GRAY + dest);
            }
        } else
            list.add(LocalizationPlugin.translate("railcraft.gui.routing.ticket.tip.blank"));
    }

}
