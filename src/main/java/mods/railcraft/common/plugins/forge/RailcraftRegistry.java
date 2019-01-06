/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.plugins.forge;

import mods.railcraft.api.core.IRailcraftRegistryEntry;
import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

/**
 * This class contains a registry of all currently active Railcraft items. Which
 * items are registered depends on the user's settings in "railcraft.cfg", so
 * the available items may vary from one installation to the next.
 * <p/>
 * Initialization of the registry will occur during the pre-initializeDefinition and initializeDefinition
 * stages. It is strongly recommended you wait until the post-initializeDefinition stage to
 * reference the registry.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class RailcraftRegistry {
    private RailcraftRegistry() {
    }

    public static void register(IRailcraftRegistryEntry<?> object, IVariantEnum variant, ItemStack stack) {
        assert !InvTools.isEmpty(stack) : "Do not register null or empty items!";
    }

//    /**
//     * Registers a new item with the GameRegistry.
//     * <p/>
//     * This should generally only be called by Railcraft or a Railcraft Module while the mod is
//     * initializing during the pre-initializeDefinition and initializeDefinition stages.
//     *
//     * @param stack The item
//     */
//    public static void register(ItemStack stack) {
//        assert stack != null : "Do not register null items!";
//        register(stack.getTranslationKey(), stack);
//    }

    /**
     * Registers a new item with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft or a Railcraft Module while the mod is
     * initializing during the pre-initializeDefinition and initializeDefinition stages.
     *
     * @param item The item
     */
    public static void register(Item item) {
        if (RailcraftModuleManager.getStage() != RailcraftModuleManager.Stage.CONSTRUCTION && RailcraftModuleManager.getStage() != RailcraftModuleManager.Stage.PRE_INIT)
            throw new RuntimeException("Items must be initialized in Construction or PreInit:" + item.getRegistryName());
        ForgeRegistries.ITEMS.register(item);
        Game.log("registry").msg(Level.INFO, "Item registered: {0}, {1}", item.getClass(), item.getRegistryName());
    }

    /**
     * Registers a new block with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft or a Railcraft Module while the mod is
     * initializing during the pre-initializeDefinition and initializeDefinition stages.
     *
     * @param block The block
     */
    public static void register(Block block, @Nullable ItemBlock item) {
        if (RailcraftModuleManager.getStage() != RailcraftModuleManager.Stage.CONSTRUCTION && RailcraftModuleManager.getStage() != RailcraftModuleManager.Stage.PRE_INIT)
            throw new RuntimeException("Blocks must be initialized in PreInit or InitFirst!");
        ForgeRegistries.BLOCKS.register(block);
        if (item != null)
            ForgeRegistries.ITEMS.register(item);
        Game.log("registry").msg(Level.INFO, "Block registered: {0}, {1}", block.getClass(), block.getRegistryName());
    }

    public static void register(Class<? extends TileEntity> tileEntity, String tag) {
        GameRegistry.registerTileEntity(tileEntity, RailcraftConstantsAPI.locationOf(tag));
    }

    public static void register(Class<? extends TileEntity> tileEntity, String tag, String... oldTags) {
        GameRegistry.registerTileEntity(tileEntity, RailcraftConstantsAPI.locationOf(tag) /*, oldTags*/);
    }
}
