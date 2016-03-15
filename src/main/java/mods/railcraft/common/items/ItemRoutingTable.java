/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.api.core.items.StackFilter;
import mods.railcraft.client.gui.GuiRoutingTable;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemRoutingTable extends ItemRailcraft implements IEditableItem {

    public static final int LINE_LENGTH = 37;
    public static final int LINES_PER_PAGE = 13;
    public static ItemRoutingTable item;
    public static final StackFilter FILTER = new StackFilter() {
        @Override
        public boolean apply(ItemStack stack) {
            return !(stack == null || item == null) && stack.getItem() == item;
        }

    };

    @SuppressWarnings("WeakerAccess")
    public ItemRoutingTable() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.routing.table";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemRoutingTable();
                item.setUnlocalizedName(tag);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapelessRecipe(new ItemStack(item), Items.writable_book, "dyeBlue");
            }
        }
    }

    public static ItemStack getItem() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (!validBookTagPages(nbt))
            return false;
        else if (nbt.hasKey("title")) {
            String s = nbt.getString("title");
            return (s != null && s.length() <= 16) && nbt.hasKey("author");
        }
        return true;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static boolean validBookTagPages(NBTTagCompound nbt) {
        if (nbt == null)
            return false;
        else if (!nbt.hasKey("pages"))
            return false;
        else {
            NBTList<NBTTagList> pages = NBTPlugin.getNBTList(nbt, "pages", NBTPlugin.EnumNBTType.LIST);
            for (NBTTagList pageNBT : pages) {
                NBTList<NBTTagString> page = new NBTList<NBTTagString>(pageNBT);
                if (page.size() > LINES_PER_PAGE)
                    return false;

                for (NBTTagString line : page) {
                    if (line.getString() == null)
                        return false;

                    if (line.getString().length() > LINE_LENGTH)
                        return false;
                }
            }

            return true;
        }
    }

    public static RoutingLogic getLogic(ItemStack routingTable) {
        LinkedList<String> routingData = ItemRoutingTable.getContents(routingTable);
        return RoutingLogic.buildLogic(routingData);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static LinkedList<String> getContents(ItemStack routingTable) {
        if (routingTable == null || routingTable.getItem() != item)
            return null;
        NBTTagCompound nbt = routingTable.getTagCompound();
        if (nbt == null)
            return null;
        LinkedList<String> contents = new LinkedList<String>();
        NBTList<NBTTagList> pages = NBTPlugin.getNBTList(nbt, "pages", NBTPlugin.EnumNBTType.LIST);
        for (NBTTagList page : pages) {
            NBTList<NBTTagString> lines = new NBTList<NBTTagString>(page);
            for (NBTTagString line : lines) {
                if (line.getString() == null)
                    continue;
                contents.add(line.getString());
            }
        }
        return contents;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static LinkedList<LinkedList<String>> getPages(ItemStack routingTable) {
        if (routingTable == null || routingTable.getItem() != item)
            return null;
        NBTTagCompound nbt = routingTable.getTagCompound();
        if (nbt == null)
            return null;

        NBTList<NBTTagList> pagesList = NBTPlugin.getNBTList(nbt, "pages", NBTPlugin.EnumNBTType.LIST);
        LinkedList<LinkedList<String>> contents = new LinkedList<LinkedList<String>>();
        for (NBTTagList pageNBT : pagesList) {
            NBTList<NBTTagString> pageList = new NBTList<NBTTagString>(pageNBT);
            LinkedList<String> page = new LinkedList<String>();
            contents.add(page);
            for (NBTTagString line : pageList) {
                if (line.getString() == null)
                    continue;
                page.add(line.getString());
            }
        }
        return contents;
    }

    public static void setPages(ItemStack routingTable, LinkedList<LinkedList<String>> pages) {
        cleanEmptyPages(pages);

        NBTTagList data = new NBTTagList();
        for (LinkedList<String> page : pages) {
            NBTTagList pageNBT = new NBTTagList();
            data.appendTag(pageNBT);
            for (String line : page) {
                pageNBT.appendTag(new NBTTagString(line));
            }
        }

        NBTTagCompound nbt = InvTools.getItemData(routingTable);
        nbt.setTag("pages", data);
    }

    private static void cleanEmptyPages(LinkedList<LinkedList<String>> pages) {
        Iterator<LinkedList<String>> pageIt = pages.descendingIterator();
        while (pageIt.hasNext()) {
            List<String> page = pageIt.next();
            for (String line : page) {
                if (!line.equals(""))
                    return;
            }
            pageIt.remove();
        }
    }

    @SuppressWarnings("unused")
    public static String getOwner(ItemStack ticket) {
        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
            return "";
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return "";
        return nbt.getString("author");
    }

    @Override
    public boolean validateNBT(NBTTagCompound nbt) {
        return validBookTagContents(nbt);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString title = (NBTTagString) nbt.getTag("title");

            if (title != null)
                return super.getItemStackDisplayName(stack) + " - " + title.toString();
        }

        return super.getItemStackDisplayName(stack);
    }

    /**
     * allows items to add custom lines of information to the mouse over
     * description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString author = (NBTTagString) nbt.getTag("author");

            if (author != null)
                list.add(EnumChatFormatting.GRAY + String.format(LocalizationPlugin.translate("railcraft.gui.routing.table.editor"), author.getString()));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Game.isNotHost(world))
            Minecraft.getMinecraft().displayGuiScreen(new GuiRoutingTable(player, stack));
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack par1ItemStack) {
        return false;
    }

    @Override
    public boolean canPlayerEdit(EntityPlayer player, ItemStack stack) {
        return true;
    }

}
