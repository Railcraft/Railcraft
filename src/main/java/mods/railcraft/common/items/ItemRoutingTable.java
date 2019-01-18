/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import mods.railcraft.client.gui.GuiBookRoutingTable;
import mods.railcraft.common.core.RailcraftConstants;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import mods.railcraft.common.util.routing.RoutingLogic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@SuppressWarnings("WeakerAccess")
public class ItemRoutingTable extends ItemRailcraft implements IEditableItem {

    public static final Predicate<ItemStack> FILTER = StackFilters.of(ItemRoutingTable.class);

    public ItemRoutingTable() {
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    public static boolean isRoutingTable(ItemStack stack) {
        return FILTER.test(stack);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(new ItemStack(this), Items.WRITABLE_BOOK, "dyeBlue");
    }

    public static boolean validBookTagContents(NBTTagCompound nbt) {
        if (!validBookTagPages(nbt))
            return false;
        else if (nbt.hasKey("title")) {
            String s = nbt.getString("title");
            return s.length() <= 16 && nbt.hasKey("author");
        }
        return true;
    }

    public static boolean validBookTagPages(NBTTagCompound nbt) {
        if (!nbt.hasKey("pages"))
            return false;
        else {
            List<NBTTagList> pages = NBTPlugin.getNBTList(nbt, "pages", NBTTagList.class);
            for (NBTTagList pageNBT : pages) {
                List<NBTTagString> page = NBTPlugin.asList(pageNBT);
                if (page.size() > RailcraftConstants.BOOK_LINES_PER_PAGE)
                    return false;

                for (NBTTagString line : page) {
                    if (line.getString().length() > RailcraftConstants.BOOK_LINE_LENGTH)
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

    public static @Nullable Deque<String> getContents(ItemStack routingTable) {
        if (InvTools.isEmpty(routingTable) || !isRoutingTable(routingTable))
            return null;
        NBTTagCompound nbt = routingTable.getTagCompound();
        if (nbt == null)
            return null;
        Deque<String> contents = new LinkedList<>();
        List<NBTTagList> pages = NBTPlugin.getNBTList(nbt, "pages", NBTTagList.class);
        for (NBTTagList page : pages) {
            List<NBTTagString> lines = NBTPlugin.asList(page);
            for (NBTTagString line : lines) {
                contents.add(line.getString());
            }
        }
        return contents;
    }

    public static @Nullable List<List<String>> getPages(ItemStack routingTable) {
        if (InvTools.isEmpty(routingTable) || !isRoutingTable(routingTable))
            return null;
        NBTTagCompound nbt = routingTable.getTagCompound();
        if (nbt == null)
            return null;

        List<NBTTagList> pagesList = NBTPlugin.getNBTList(nbt, "pages", NBTTagList.class);
        List<List<String>> contents = new ArrayList<>();
        for (NBTTagList pageNBT : pagesList) {
            List<NBTTagString> pageList = NBTPlugin.asList(pageNBT);
            List<String> page = new ArrayList<>();
            contents.add(page);
            for (NBTTagString line : pageList) {
                page.add(line.getString());
            }
        }
        return contents;
    }

    public static void setPages(ItemStack routingTable, List<List<String>> pages) {
        cleanEmptyPages(pages);

        NBTTagList data = new NBTTagList();
        for (List<String> page : pages) {
            NBTTagList pageNBT = new NBTTagList();
            data.appendTag(pageNBT);
            for (String line : page) {
                pageNBT.appendTag(new NBTTagString(line));
            }
        }

        NBTTagCompound nbt = InvTools.getItemData(routingTable);
        nbt.setTag("pages", data);
    }

    private static void cleanEmptyPages(List<List<String>> pages) {
        ListIterator<List<String>> pageIt = pages.listIterator(pages.size() - 1);
        while (pageIt.hasPrevious()) {
            List<String> page = pageIt.previous();
            for (String line : page) {
                if (!line.isEmpty())
                    return;
            }
            pageIt.remove();
        }
    }

//    @SuppressWarnings("unused")
//    public static String getOwner(@Nullable ItemStack ticket) {
//        if (ticket == null || !(ticket.getItem() instanceof ItemTicket))
//            return "";
//        NBTTagCompound nbt = ticket.getTagCompound();
//        if (nbt == null)
//            return "";
//        return nbt.getString("author");
//    }

    @Override
    public boolean validateNBT(NBTTagCompound nbt) {
        return validBookTagContents(nbt);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag par4) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            NBTTagString author = (NBTTagString) nbt.getTag("author");

            if (author != null)
                list.add(TextFormatting.GRAY + String.format(LocalizationPlugin.translate("gui.railcraft.routing.table.editor"), author.getString()));
        }
    }



    @SideOnly(Side.CLIENT)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (Game.isClient(world))
            Minecraft.getMinecraft().displayGuiScreen(new GuiBookRoutingTable(player, stack));
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
