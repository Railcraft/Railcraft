/*******************************************************************************
 * Copyright (c) CovertJaguar, 2011-2016
 * http://railcraft.info
 *
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 ******************************************************************************/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.tracks.EnumTrack;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LootPlugin;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

@RailcraftModule("railcraft:extras")
public class ModuleExtras extends RailcraftModulePayload {
    public ModuleExtras() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.track,
                        RailcraftBlocks.trackElevator
                );
            }

            @Override
            public void preInit() {
                EnumTrack.PRIMING.register();
                EnumTrack.LAUNCHER.register();
                EnumTrack.SUSPENDED.register();

                // Define Wood TNT Cart
                RailcraftCarts cart = RailcraftCarts.TNT_WOOD;
                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "WTW",
                            "WWW",
                            'T', Blocks.TNT,
                            'W', "slabWood");
                    LootPlugin.addLoot(cart.getCartItem(), 1, 3, LootPlugin.Type.RAILWAY, cart.getTag());
                }

                // Define Work Cart
                cart = RailcraftCarts.WORK;
                if (cart.setup()) {
                    CraftingPlugin.addRecipe(cart.getCartItem(),
                            "B",
                            "M",
                            'B', "craftingTableWood",
                            'M', Items.MINECART);
                    LootPlugin.addLoot(cart.getCartItem(), 1, 1, LootPlugin.Type.RAILWAY, cart.getTag());
                }
            }
        });
    }
}
