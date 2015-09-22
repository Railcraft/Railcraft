/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

public class ModuleExtras extends RailcraftModule {

    @Override
    public void initFirst() {
        RailcraftBlocks.registerBlockTrack();
        RailcraftBlocks.registerBlockRailElevator();

        // Define Wood TNT Cart
        EnumCart cart = EnumCart.TNT_WOOD;
        if (cart.setup()) {
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "WTW",
                    "WWW",
                    'T', Blocks.tnt,
                    'W', "slabWood");
            LootPlugin.addLootRailway(cart.getCartItem(), 1, 3, cart.getTag());
        }

        // Define Work Cart
        cart = EnumCart.WORK;
        if (cart.setup()) {
            CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                    "B",
                    "M",
                    'B', "craftingTableWood",
                    'M', Items.minecart);
            CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), cart.getCartItem());
            LootPlugin.addLootRailway(cart.getCartItem(), 1, 1, cart.getTag());
        }

        Block blockTrack = RailcraftBlocks.getBlockTrack();
        if (blockTrack != null) {
            MiscTools.registerTrack(EnumTrack.PRIMING);
            MiscTools.registerTrack(EnumTrack.LAUNCHER);
            MiscTools.registerTrack(EnumTrack.SUSPENDED);
        }
    }

}
