/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import mods.railcraft.api.core.StackFilter;
import mods.railcraft.client.gui.GuiRoutingTable;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("WeakerAccess")
public class ItemRoutingTable extends ItemRailcraft implements IEditableItem {

    public static final int LINE_LENGTH = 37;
    public static final int LINES_PER_PAGE = 13;
    public static final StackFilter FILTER = StackFilters.of(ItemRoutingTable.class);

    public ItemRoutingTable() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static boolean isRoutingTable(ItemStack stack) {
        return FILTER.apply(stack);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(new ItemStack(this), Items.writable_book, "dyeBlue");
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
        Deque<String> routingData = ItemRoutingTable.getContents(routingTable);
        return RoutingLogic.buildLogic(routingData);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public static Deque<String> getContents(ItemStack routingTable) {
        if (routingTable == null || !isRoutingTable(routingTable))
            return null;
        NBTTagCompound nbt = routingTable.getTagCompound();
        if (nbt == null)
            return null;
        Deque<String> contents = new LinkedList<String>();
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
        if (routingTable == null || !isRoutingTable(routingTable))
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
                return super.getItemStackDisplayName(stack) + " - " + title.getString();
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
                list.add(TextFormatting.GRAY + String.format(LocalizationPlugin.translate("railcraft.gui.routing.table.editor"), author.getString()));
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
