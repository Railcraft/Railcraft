/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemCart extends ItemMinecart implements IMinecartItem {

    private final ICartType type;
    private int rarity = 0;

    public ItemCart(ICartType cart) {
        super(EntityMinecart.EnumMinecartType.RIDEABLE);
        maxStackSize = RailcraftConfig.getMinecartStackSize();
        this.type = cart;
        setUnlocalizedName(cart.getTag());
        setMaxDamage(0);
        setHasSubtypes(true);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, new BehaviorDefaultDispenseItem());
    }

    public ItemCart setRarity(int rarity) {
        this.rarity = rarity;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.values()[rarity];
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (Game.isNotHost(world))
            return false;
        EntityMinecart placedCart = placeCart(player.getGameProfile(), stack, world, pos);
        if (placedCart != null) {
            stack.stackSize--;
            return true;
        }
        return false;
    }

    public ICartType getCartType() {
        return type;
    }

    @Override
    public boolean canBePlacedByNonPlayer(ItemStack cart) {
        return true;
    }

    @Override
    public EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, BlockPos pos) {
        return CartUtils.placeCart(type, owner, cartStack, world, pos);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        ToolTip tip = ToolTip.buildToolTip(stack.getUnlocalizedName() + ".tip");
        if (tip != null)
            info.addAll(tip.convertToStrings());
        ItemStack filter = EntityCartFiltered.getFilterFromCartItem(stack);
        if (filter != null) {
            info.add(EnumChatFormatting.BLUE + LocalizationPlugin.translate("railcraft.gui.filter") + ": " + filter.getDisplayName());
        }
    }

}
