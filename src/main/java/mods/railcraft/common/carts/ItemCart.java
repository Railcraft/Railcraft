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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

import mods.railcraft.common.gui.tooltips.ToolTipLine;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockDispenser;

public class ItemCart extends ItemMinecart implements IMinecartItem {

    private final ICartType type;
    private int rarity = 0;

    public ItemCart(ICartType cart) {
        super(0);
        maxStackSize = RailcraftConfig.getMinecartStackSize();
        this.type = cart;
        setUnlocalizedName(cart.getTag());
        setMaxDamage(0);
        setHasSubtypes(true);
        BlockDispenser.dispenseBehaviorRegistry.putObject(this, null);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("railcraft:" + MiscTools.cleanTag(getUnlocalizedName()));
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int l, float par8, float par9, float par10) {
        if (Game.isNotHost(world))
            return false;
        EntityMinecart placedCart = placeCart(player.getGameProfile(), stack, world, i, j, k);
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
    public EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, int x, int y, int z) {
        return CartUtils.placeCart(type, owner, cartStack, world, x, y, z);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean adv) {
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
