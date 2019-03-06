/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.carts;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mods.railcraft.api.carts.IBoreHead;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.ItemMaterials;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public abstract class ItemBoreHead extends ItemTool implements IBoreHead, IRailcraftItemSimple {

    protected ItemBoreHead() {
        super(ItemMaterials.DUMMY, Collections.emptySet());
        maxStackSize = 1;
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
    }

    @Override
    public Item getObject() {
        return this;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return 1;
    }

    @Override
    public abstract boolean getIsRepairable(ItemStack toRepair, ItemStack repair);

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
        if (player instanceof FakePlayer)
            return IBoreHead.super.getHarvestLevel(stack, toolClass, player, blockState);
        return -1;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return IBoreHead.super.getToolClasses(stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 1F;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return false;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return ImmutableMultimap.of();
    }
}
