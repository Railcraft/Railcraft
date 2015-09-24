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
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import static mods.railcraft.common.items.ItemTicket.getOwner;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTicketGold extends ItemTicket implements IEditableItem {

    public static final IStackFilter FILTER = new IStackFilter() {
        @Override
        public boolean matches(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemTicketGold;
        }

    };
    public static ItemTicketGold item;

    public static void registerItem() {
        if (item == null) {
            String tag = "railcraft.routing.ticket.gold";

            if (RailcraftConfig.isItemEnabled(tag)) {
                item = new ItemTicketGold();
                item.setUnlocalizedName(tag);
                item.setRarity(1);
                RailcraftRegistry.register(item);

                CraftingPlugin.addShapelessRecipe(new ItemStack(item), Items.paper, "nuggetGold");
            }
        }
    }

    public static ItemStack getTicket() {
        if (item == null)
            return null;
        return new ItemStack(item);
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        stack = stack.copy();
        stack.stackSize = 1;
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:ticket.gold");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (Game.isHost(world))
            if (canPlayerEdit(player, stack))
                PacketBuilder.instance().sendGoldenTicketGuiPacket((EntityPlayerMP) player);
        return stack;
    }

    @Override
    public boolean canPlayerEdit(EntityPlayer player, ItemStack stack) {
        boolean canEdit = PlayerPlugin.isPlayerOp(player.getGameProfile());
        if (!canEdit && !RailcraftConfig.isRoutingOpsOnly()) {
            GameProfile owner = getOwner(stack);
            canEdit |= owner.getId() == null || owner.equals(player.getCommandSenderName());
        }
        return canEdit;
    }

}
