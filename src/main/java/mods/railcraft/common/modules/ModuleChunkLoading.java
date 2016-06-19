/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.epsilon.EnumMachineEpsilon;
import mods.railcraft.common.carts.EnumCart;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.util.misc.ChunkManager;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ModuleChunkLoading extends RailcraftModule {
    @Override
    public void initFirst() {
        ForgeChunkManager.setForcedChunkLoadingCallback(Railcraft.getMod(), ChunkManager.getInstance());
        MinecraftForge.EVENT_BUS.register(ChunkManager.getInstance());

        EnumMachineAlpha alpha = EnumMachineAlpha.WORLD_ANCHOR;
        if (RailcraftConfig.isSubBlockEnabled(alpha.getTag())) {
            Block block = RailcraftBlocks.registerBlockMachineAlpha();
            if (block != null && RailcraftConfig.canCraftAnchors()) {
                CraftingPlugin.addShapedRecipe(alpha.getItem(),
                        "gog",
                        "dpd",
                        "gog",
                        'd', "gemDiamond",
                        'g', "ingotGold",
                        'p', Items.ender_pearl,
                        'o', new ItemStack(Blocks.obsidian));
            }
        }

        alpha = EnumMachineAlpha.PERSONAL_ANCHOR;
        if (RailcraftConfig.isSubBlockEnabled(alpha.getTag())) {
            Block block = RailcraftBlocks.registerBlockMachineAlpha();
            if (block != null && RailcraftConfig.canCraftPersonalAnchors()) {
                CraftingPlugin.addShapedRecipe(alpha.getItem(),
                        "gog",
                        "dpd",
                        "gog",
                        'd', "gemEmerald",
                        'g', "ingotGold",
                        'p', Items.ender_pearl,
                        'o', new ItemStack(Blocks.obsidian));
            }
        }

        alpha = EnumMachineAlpha.PASSIVE_ANCHOR;
        if (RailcraftConfig.isSubBlockEnabled(alpha.getTag())) {
            Block block = RailcraftBlocks.registerBlockMachineAlpha();
            if (block != null && RailcraftConfig.canCraftPassiveAnchors()) {
                CraftingPlugin.addShapedRecipe(alpha.getItem(),
                        "gog",
                        "dpd",
                        "gog",
                        'd', "dyeCyan",
                        'g', "ingotGold",
                        'p', Items.ender_pearl,
                        'o', new ItemStack(Blocks.obsidian));
            }
        }

        alpha = EnumMachineAlpha.ADMIN_ANCHOR;
        if (RailcraftConfig.isSubBlockEnabled(alpha.getTag())) {
            RailcraftBlocks.registerBlockMachineAlpha();
        }

        EnumMachineBeta beta = EnumMachineBeta.SENTINEL;
        if (RailcraftConfig.isSubBlockEnabled(beta.getTag())) {
            Block block = RailcraftBlocks.registerBlockMachineBeta();
            if (block != null) {
                ItemStack stack = beta.getItem();
                if (RailcraftConfig.canCraftAnchors()) {
                    CraftingPlugin.addShapedRecipe(stack,
                            " p ",
                            " o ",
                            "ogo",
                            'g', "ingotGold",
                            'p', Items.ender_pearl,
                            'o', new ItemStack(Blocks.obsidian));
                }
            }
        }

        // Define Anchor Cart
        EnumCart cart = EnumCart.ANCHOR;
        if (EnumMachineAlpha.WORLD_ANCHOR.isAvaliable() && cart.setup()) {
            ItemStack anchor = EnumMachineAlpha.WORLD_ANCHOR.getItem();
            if (RailcraftConfig.canCraftAnchors()) {
                CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                        "A",
                        "M",
                        'A', anchor,
                        'M', Items.minecart);
                CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), cart.getCartItem());
            }
            cart.setContents(anchor);
        }


        // Define Personal Anchor Cart
        cart = EnumCart.ANCHOR_PERSONAL;
        if (EnumMachineAlpha.PERSONAL_ANCHOR.isAvaliable() && cart.setup()) {
            ItemStack anchor = EnumMachineAlpha.PERSONAL_ANCHOR.getItem();
            if (RailcraftConfig.canCraftPersonalAnchors()) {
                CraftingPlugin.addShapedRecipe(cart.getCartItem(),
                        "A",
                        "M",
                        'A', anchor,
                        'M', Items.minecart);
                CraftingPlugin.addShapelessRecipe(new ItemStack(Items.minecart), cart.getCartItem());
            }
            cart.setContents(anchor);
        }

        // Define Admin Anchor Cart
        cart = EnumCart.ANCHOR_ADMIN;
        if (EnumMachineAlpha.ADMIN_ANCHOR.isAvaliable() && cart.setup()) {
            ItemStack anchor = EnumMachineAlpha.ADMIN_ANCHOR.getItem();
            cart.setContents(anchor);
        }
    }
}
