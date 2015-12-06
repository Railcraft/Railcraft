/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.client.gui.GuiRoutingTable;
import mods.railcraft.common.blocks.signals.RoutingLogic;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.plugins.forge.NBTPlugin.NBTList;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemRoutingTable extends ItemRailcraft implements IEditableItem {

    public static final IStackFilter FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            if (stack == null || item == null)
                return false;
            return stack.getItem() == item;
        }

    };
    public static final int LINE_LENGTH = 37;
    public static final int LINES_PER_PAGE = 13;
    public static ItemRoutingTable item;

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

    @Override
    public boolean validateNBT(NBTTagCompound nbt) {
        return validBookTagContents(nbt);
    }

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (!validBookTagPages(nbt))
            return false;
        else if (nbt.hasKey("title")) {
            String s = nbt.getString("title");
            return s != null && s.length() <= 16 ? nbt.hasKey("author") : false;
        }
        return true;
    }

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
                    if (line.func_150285_a_() == null)
                        return false;

                    if (line.func_150285_a_().length() > LINE_LENGTH)
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
                if (line.func_150285_a_() == null)
                    continue;
                contents.add(line.func_150285_a_());
            }
        }
        return contents;
    }

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
                if (line.func_150285_a_() == null)
                    continue;
                page.add(line.func_150285_a_());
            }
        }
        return contents;
    }

    public static void setPages(ItemStack routingTable, LinkedList<LinkedList<String>> pages) {
        cleanEmptyPages(pages);

        NBTTagList data = new NBTTagList();
        ListIterator<LinkedList<String>> pageIt = pages.listIterator();
        while (pageIt.hasNext()) {
            List<String> page = pageIt.next();
            NBTTagList pageNBT = new NBTTagList();
            data.appendTag(pageNBT);
            ListIterator<String> lineIt = page.listIterator();
            while (lineIt.hasNext()) {
                String line = lineIt.next();
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
            Iterator<String> lineIt = page.iterator();
            while (lineIt.hasNext()) {
                String line = lineIt.next();
                if (!line.equals(""))
                    return;
            }
            pageIt.remove();
        }
    }

    public static String getOwner(ItemStack ticket) {
        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
            return "";
        NBTTagCompound nbt = ticket.getTagCompound();
        if (nbt == null)
            return "";
        return nbt.getString("author");
    }

    public ItemRoutingTable() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:routing.table");
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
     * allows items to add custom lines of information to the mouseover
     * description
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagString author = (NBTTagString) nbt.getTag("author");

            if (author != null)
                list.add(EnumChatFormatting.GRAY + String.format(LocalizationPlugin.translate("railcraft.gui.routing.table.editor"), author.func_150285_a_()));
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
