/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.api.core.items.IMinecartItem;
import mods.railcraft.common.blocks.tracks.TrackTools;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCart extends ItemMinecart implements IMinecartItem, IRailcraftItemSimple {

    private final IRailcraftCartContainer type;
    private int rarity;

    public ItemCart(IRailcraftCartContainer cart) {
        super(EntityMinecart.Type.RIDEABLE);
        maxStackSize = RailcraftConfig.getMinecartStackSize();
        this.type = cart;
        setMaxDamage(0);
        setHasSubtypes(true);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, new BehaviorDefaultDispenseItem());
    }

    @Override
    public Item getObject() {
        return this;
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
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!TrackTools.isRailBlockAt(world, pos))
            return EnumActionResult.FAIL;
        if (Game.isHost(world)) {
            EntityMinecart placedCart = placeCart(player.getGameProfile(), stack, world, pos);
            if (placedCart != null) {
                stack.stackSize--;
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public IRailcraftCartContainer getCartType() {
        return type;
    }

    @Override
    public boolean canBePlacedByNonPlayer(ItemStack cart) {
        return true;
    }

    @Nullable
    @Override
    public EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, BlockPos pos) {
        return CartTools.placeCart(type, owner, cartStack, world, pos);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        addToolTips(stack, player, info, adv);
        ItemStack filter = CartBaseFiltered.getFilterFromCartItem(stack);
        if (!InvTools.isEmpty(filter)) {
            info.add(TextFormatting.BLUE + LocalizationPlugin.translate("gui.railcraft.filter") + ": " + filter.getDisplayName());
        }
    }

}
