/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
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
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTicket extends ItemRailcraft {

    public static final Predicate<ItemStack> FILTER = stack -> stack != null && stack.getItem() instanceof ItemTicket;
    public static final int LINE_LENGTH = 32;

    public static boolean isNBTValid(@Nullable NBTTagCompound nbt) {
        if (nbt == null)
            return false;
        else if (!nbt.hasKey("dest"))
            return false;

        NBTTagString dest = (NBTTagString) nbt.getTag("dest");
        return !dest.getString().isEmpty() && dest.getString().length() <= LINE_LENGTH;

    }

    public static ItemStack copyTicket(ItemStack source) {
        if (InvTools.isEmpty(source))
            return ItemStack.EMPTY;
        if (source.getItem() instanceof ItemTicket) {
            ItemStack ticket = RailcraftItems.TICKET.getStack();
            if(InvTools.isEmpty(ticket))
                return ItemStack.EMPTY;
            NBTTagCompound nbt = source.getTagCompound();
            if (nbt != null)
                ticket.setTagCompound(nbt.copy());
            return ticket;
        }
        return ItemStack.EMPTY;
    }

    public static boolean setTicketData(ItemStack ticket, String dest, String title, @Nullable GameProfile owner) {
        if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
            return false;
        if (dest.length() > LINE_LENGTH)
            return false;
        if (owner == null)
            return false;
        NBTTagCompound data = InvTools.getItemData(ticket);
        data.setString("dest", dest);
        data.setString("title", title);
        PlayerPlugin.writeOwnerToNBT(data, owner);
        return true;
    }

    public static String getDestination(ItemStack ticket) {
        if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
            return "";
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return "";
        return nbt.getString("dest");
    }

    public static boolean matchesOwnerOrOp(ItemStack ticket, GameProfile player) {
        return ticket.getItem() instanceof ItemTicket && PlayerPlugin.isOwnerOrOp(getOwner(ticket), player);
    }

    public static GameProfile getOwner(ItemStack ticket) {
        if (InvTools.isEmpty(ticket) || !(ticket.getItem() instanceof ItemTicket))
            return new GameProfile(null, RailcraftConstantsAPI.UNKNOWN_PLAYER);
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return new GameProfile(null, RailcraftConstantsAPI.UNKNOWN_PLAYER);
        return PlayerPlugin.readOwnerFromNBT(nbt);
    }

    public boolean validateNBT(NBTTagCompound nbt) {
        String dest = nbt.getString("dest");
        return dest.length() < LINE_LENGTH;
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
     * allows items to add custom lines of information to the mouse over
     * description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag par4) {
        if (stack.hasTagCompound()) {
            GameProfile owner = getOwner(stack);
            if (owner.getId() != null) {
                list.add(TextFormatting.WHITE + LocalizationPlugin.translate("gui.railcraft.routing.ticket.tips.issuer"));
                list.add(TextFormatting.GRAY + PlayerPlugin.getUsername(world, owner));
            }

            String dest = getDestination(stack);
            if (!"".equals(dest)) {
                list.add(TextFormatting.WHITE + LocalizationPlugin.translate("gui.railcraft.routing.ticket.tips.dest"));
                list.add(TextFormatting.GRAY + dest);
            }
        } else
            list.add(LocalizationPlugin.translate("gui.railcraft.routing.ticket.tips.blank"));
    }

}
