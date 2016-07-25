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
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.ChunkManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
@RailcraftModule("railcraft:chunk_loading")
public class ModuleChunkLoading extends RailcraftModulePayload {

    public ModuleChunkLoading() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftBlocks.machine_alpha,
                        RailcraftBlocks.machine_beta
                );
            }

            @Override
            public void preInit() {
                ForgeChunkManager.setForcedChunkLoadingCallback(Railcraft.getMod(), ChunkManager.getInstance());
                MinecraftForge.EVENT_BUS.register(ChunkManager.getInstance());

                EnumMachineAlpha alpha = EnumMachineAlpha.ANCHOR_WORLD;
                if (alpha.isEnabled() && RailcraftConfig.canCraftAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getItem(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', "gemDiamond",
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                alpha = EnumMachineAlpha.ANCHOR_PERSONAL;
                if (alpha.isEnabled() && RailcraftConfig.canCraftPersonalAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getItem(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', "gemEmerald",
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                alpha = EnumMachineAlpha.ANCHOR_PASSIVE;
                if (alpha.isEnabled() && RailcraftConfig.canCraftPassiveAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getItem(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', new ItemStack(Blocks.PRISMARINE, 1, 1),
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                EnumMachineBeta beta = EnumMachineBeta.SENTINEL;
                if (beta.isEnabled()) {
                    ItemStack stack = beta.getItem();
                    if (RailcraftConfig.canCraftAnchors()) {
                        CraftingPlugin.addRecipe(stack,
                                " p ",
                                " o ",
                                "ogo",
                                'g', "ingotGold",
                                'p', Items.ENDER_PEARL,
                                'o', new ItemStack(Blocks.OBSIDIAN));
                    }
                }

                // Define Anchor Cart
                RailcraftCarts cart = RailcraftCarts.ANCHOR;
                if (EnumMachineAlpha.ANCHOR_WORLD.isAvailable() && cart.setup()) {
                    ItemStack anchor = EnumMachineAlpha.ANCHOR_WORLD.getItem();
                    if(anchor != null) {
                        if (RailcraftConfig.canCraftAnchors()) {
                            CraftingPlugin.addRecipe(cart.getCartItem(),
                                    "A",
                                    "M",
                                    'A', anchor,
                                    'M', Items.MINECART);
                        }
                        cart.setContents(anchor);
                    }
                }


                // Define Personal Anchor Cart
                cart = RailcraftCarts.ANCHOR_PERSONAL;
                if (EnumMachineAlpha.ANCHOR_PERSONAL.isAvailable() && cart.setup()) {
                    ItemStack anchor = EnumMachineAlpha.ANCHOR_PERSONAL.getItem();
                    if (RailcraftConfig.canCraftPersonalAnchors()) {
                        CraftingPlugin.addRecipe(cart.getCartItem(),
                                "A",
                                "M",
                                'A', anchor,
                                'M', Items.MINECART);
                    }
                    cart.setContents(anchor);
                }

                // Define Admin Anchor Cart
                cart = RailcraftCarts.ANCHOR_ADMIN;
                if (EnumMachineAlpha.ANCHOR_ADMIN.isAvailable() && cart.setup()) {
                    ItemStack anchor = EnumMachineAlpha.ANCHOR_ADMIN.getItem();
                    cart.setContents(anchor);
                }
            }
        });
    }
}
