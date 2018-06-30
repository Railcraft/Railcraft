/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
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
import mods.railcraft.api.core.RailcraftItemStackRegistry;
import mods.railcraft.common.modules.RailcraftModuleManager;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;

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

    /**
     * This function will return an ItemStack containing the item that
     * corresponds to the provided tag.
     * <p/>
     * Generally item tags will correspond to the tags used in "railcraft.cfg",
     * but there will be some exceptions.
     * <p/>
     * This function can and will return null for just about every item if the
     * item is disabled via the configuration files. You must test the return
     * value for safety.
     * <p/>
     * For list of available tags see the printItemTags() function.
     *
     * @param tag The item tag
     * @param qty The stackSize of the returned item
     * @return The ItemStack or null if no item exists for that tag
     */
    @Nullable
    public static ItemStack getItem(String tag, int qty) {
        tag = MiscTools.cleanTag(tag);
        return RailcraftItemStackRegistry.getStack(tag, qty).orElse(null);
//        return GameRegistry.findItemStack(Railcraft.getModId(), tag, qty);
    }

//    /**
//     * Registers a new item with the GameRegistry.
//     * <p/>
//     * This should generally only be called by Railcraft itself while the mod is
//     * initializing during the pre-initializeDefinition and initializeDefinition stages.
//     *
//     * @param tag   The tag name
//     * @param stack The item
//     */
//    private static void register(String tag, ItemStack stack) {
//        assert stack != null : "Do not register null items!";
//        tag = MiscTools.cleanTag(tag);
////        TagList.addTag(tag);
////        System.out.println(tag);
//        Item existingItem = GameRegistry.findItem(Railcraft.MOD_ID, tag);
//        Block existingBlock = GameRegistry.findBlock(Railcraft.MOD_ID, tag);
//        if (existingItem == null && existingBlock == Blocks.AIR) {
////            GameRegistry.registerCustomItemStack(tag, stack);
//            RailcraftItemStackRegistry.register(tag, stack);
//        } else
//            throw new RuntimeException("ItemStack registrations must be unique!");
//    }

    public static void register(IRailcraftRegistryEntry<?> object, IVariantEnum variant, ItemStack stack) {
        assert !InvTools.isEmpty(stack) : "Do not register null or empty items!";
        RailcraftItemStackRegistry.register(object, variant, stack);
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
//        register(stack.getUnlocalizedName(), stack);
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
        RailcraftItemStackRegistry.register(item, new ItemStack(item));
        if (Game.DEVELOPMENT_ENVIRONMENT)
            Game.log(Level.INFO, "Item registered: {0}, {1}", item.getClass(), item.getRegistryName().toString());
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
        RailcraftItemStackRegistry.register(block, new ItemStack(block));
        if (Game.DEVELOPMENT_ENVIRONMENT)
            Game.log(Level.INFO, "Block registered: {0}, {1}", block.getClass(), block.getRegistryName().toString());
    }

    public static void register(Class<? extends TileEntity> tileEntity, String tag) {
        GameRegistry.registerTileEntity(tileEntity, RailcraftConstantsAPI.locationOf(tag));
    }

    public static void register(Class<? extends TileEntity> tileEntity, String tag, String... oldTags) {
        GameRegistry.registerTileEntity(tileEntity, RailcraftConstantsAPI.locationOf(tag) /*, oldTags*/);
    }
}
