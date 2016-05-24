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
import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.api.core.StackFilter;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.PlayerPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.network.IEditableItem;
import mods.railcraft.common.util.network.PacketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemTicketGold extends ItemTicket implements IEditableItem {

    public static final IStackFilter FILTER = new StackFilter() {
        @Override
        public boolean apply(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemTicketGold;
        }

    };

    @Override
    public void initializeDefinintion() {
        setRarity(1);
    }

    @Override
    public void defineRecipes() {
        CraftingPlugin.addShapelessRecipe(new ItemStack(this), Items.PAPER, "nuggetGold");
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
            canEdit = owner.getId() == null || PlayerPlugin.isOwnerOrOp(owner, player);
        }
        return canEdit;
    }

}
