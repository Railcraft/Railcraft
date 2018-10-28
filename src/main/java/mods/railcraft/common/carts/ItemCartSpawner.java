/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import com.mojang.authlib.GameProfile;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.util.crafting.EggInfoCopyRecipe;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        WeightedSpawnerEntity entry = NBTPlugin.obtainEntityTagSafe(world, cartStack, owner);

        cart.handleEntry(entry);

        return cart;
    }

    @Override
    public void defineRecipes() {
        super.defineRecipes();
        CraftingPlugin.addRecipe(new EggInfoCopyRecipe());
    }
}
