/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.modules;

import mods.railcraft.api.core.RailcraftModule;
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
@RailcraftModule(value = "railcraft:chunk_loading", description = "world anchors, world anchor carts")
public class ModuleChunkLoading extends RailcraftModulePayload {

    public ModuleChunkLoading() {
        setEnabledEventHandler(new ModuleEventHandler() {
            @Override
            public void construction() {
                add(
                        RailcraftCarts.ANCHOR_WORLD,
                        RailcraftCarts.ANCHOR_ADMIN,
                        RailcraftCarts.ANCHOR_PERSONAL
//                        RailcraftBlocks.machine_alpha,
//                        RailcraftBlocks.machine_beta
                );
            }

            @Override
            public void preInit() {
                ForgeChunkManager.setForcedChunkLoadingCallback(Railcraft.getMod(), ChunkManager.getInstance());
                MinecraftForge.EVENT_BUS.register(ChunkManager.getInstance());

                EnumMachineAlpha alpha = EnumMachineAlpha.ANCHOR_WORLD;
                if (alpha.isAvailable() && RailcraftConfig.canCraftAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getStack(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', "gemDiamond",
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                alpha = EnumMachineAlpha.ANCHOR_PERSONAL;
                if (alpha.isAvailable() && RailcraftConfig.canCraftPersonalAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getStack(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', "gemEmerald",
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                alpha = EnumMachineAlpha.ANCHOR_PASSIVE;
                if (alpha.isAvailable() && RailcraftConfig.canCraftPassiveAnchors()) {
                    CraftingPlugin.addRecipe(alpha.getStack(),
                            "gog",
                            "dpd",
                            "gog",
                            'd', new ItemStack(Blocks.PRISMARINE, 1, 1),
                            'g', "ingotGold",
                            'p', Items.ENDER_PEARL,
                            'o', new ItemStack(Blocks.OBSIDIAN));
                }

                EnumMachineBeta beta = EnumMachineBeta.SENTINEL;
                if (beta.isAvailable()) {
                    ItemStack stack = beta.getStack();
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
            }
        });
    }
}
