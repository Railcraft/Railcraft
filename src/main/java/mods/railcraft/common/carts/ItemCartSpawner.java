/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.crafting.Ingredients;
import mods.railcraft.common.util.crafting.NBTCopyRecipe;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 *
 */
public class ItemCartSpawner extends ItemCart {

    public ItemCartSpawner(IRailcraftCartContainer cart) {
        super(cart);
    }

    @Override
    public @Nullable EntityMinecart placeCart(GameProfile owner, ItemStack cartStack, World world, BlockPos pos) {
        EntityCartSpawner cart = (EntityCartSpawner) super.placeCart(owner, cartStack, world, pos);
        if (cart == null) {
            return null;
        }

        if (cartStack.hasTagCompound()) {
            NBTTagCompound cartTag = cart.writeToNBT(new NBTTagCompound());
            NBTTagCompound spawner = cartStack.getOrCreateSubCompound("Spawner");
            ResourceLocation id = ItemMonsterPlacer.getNamedIdFrom(cartStack);
            if (id != null) {
                NBTTagCompound spawnData = spawner.getCompoundTag("SpawnData");
                spawnData.setString("id", id.toString());
                spawner.setTag("SpawnData", spawnData);
            }
            cartTag.merge(spawner);
            cart.readFromNBT(cartTag);
            cart.sendToClient();
        }

        return cart;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag adv) {
        super.addInformation(stack, world, info, adv);
        ResourceLocation id = ItemMonsterPlacer.getNamedIdFrom(stack);
        if (id != null)
            info.add(id.toString());
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        CraftingPlugin.addRecipe(new NBTCopyRecipe("spawn_cart_egg_copy",
                Ingredients.from(Items.SPAWN_EGG),
                Ingredients.from(RailcraftCarts.SPAWNER),
                RailcraftCarts.SPAWNER.getStack()));
    }
}
