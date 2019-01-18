/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.client.gui;

import com.google.common.base.Strings;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.items.ItemRoutingTable;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketItemNBT;
import mods.railcraft.common.util.routing.ITileRouting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

@SideOnly(Side.CLIENT)
public class GuiBookRoutingTable extends GuiBook {

    public static final ResourceLocation TEXTURE = GuiTools.findTexture("routing_table.png");
    public static final String TABLE_LOC_TAG = "gui.railcraft.routing.table.";
    private final @Nullable TileEntity tile;
    private final ItemStack bookStack;
    /**
     * The player editing the book
     */
    protected final EntityPlayer player;

    public GuiBookRoutingTable(EntityPlayer player, ItemStack stack) {
        this(player, null, stack);
    }

    public GuiBookRoutingTable(EntityPlayer player, @Nullable TileEntity tile, ItemStack stack) {
        super(TEXTURE, TABLE_LOC_TAG, getTitle(stack), getAuthor(player, stack), ItemRoutingTable.getPages(stack), true);
        this.player = player;
        this.tile = tile;
        this.bookStack = stack;
    }

    private static String getTitle(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            assert nbt != null;
            return nbt.getString("title");
        }
        return "";
    }

    private static String getAuthor(EntityPlayer player, ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            assert nbt != null;
            return nbt.getString("author");
        }
        return Railcraft.proxy.getPlayerUsername(player);
    }

    @Override
    protected void sendBookToServer() {
        ItemRoutingTable.setPages(bookStack, bookPages);

        NBTTagCompound nbt = InvTools.getItemData(bookStack);

        nbt.setString("author", author);
        if (!Strings.isNullOrEmpty(bookTitle))
            nbt.setString("title", bookTitle);

        PacketItemNBT pkt;
        if (tile instanceof ITileRouting) {
            pkt = new PacketItemNBT.RoutableTile(player, tile, bookStack);
        } else {
            pkt = new PacketItemNBT.CurrentItem(player, bookStack);
        }
        PacketDispatcher.sendToServer(pkt);
    }

}
