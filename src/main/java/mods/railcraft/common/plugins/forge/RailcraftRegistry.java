/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.plugins.forge;

import cpw.mods.fml.common.registry.GameRegistry;
import mods.railcraft.api.core.items.TagList;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.modules.ModuleManager;
import net.minecraft.item.ItemStack;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/**
 * This class contains a registry of all currently active Railcraft items. Which
 * items are registered depends on the user's settings in "railcraft.cfg", so
 * the available items may vary from one installation to the next.
 * <p/>
 * Initialization of the registry will occur during the pre-init and init
 * stages. It is strongly recommended you wait until the post-init stage to
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
    public static ItemStack getItem(String tag, int qty) {
        tag = MiscTools.cleanTag(tag);
        return GameRegistry.findItemStack(Railcraft.getModId(), tag, qty);
    }

    /**
     * Registers a new item with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft itself while the mod is
     * initializing during the pre-init and init stages.
     *
     * @param tag   The tag name
     * @param stack The item
     */
    public static void register(String tag, ItemStack stack) {
        if (stack == null)
            throw new RuntimeException("Don't register null items!");
        tag = MiscTools.cleanTag(tag);
        TagList.addTag(tag);
//        System.out.println(tag);
        Item existingItem = GameRegistry.findItem(Railcraft.getModId(), tag);
        Block existingBlock = GameRegistry.findBlock(Railcraft.getModId(), tag);
        if (existingItem == null && existingBlock == null)
            GameRegistry.registerCustomItemStack(tag, stack);
        else
            throw new RuntimeException("ItemStack registrations must be unique!");
    }

    /**
     * Registers a new item with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft itself while the mod is
     * initializing during the pre-init and init stages.
     *
     * @param stack The item
     */
    public static void register(ItemStack stack) {
        if (stack == null)
            throw new RuntimeException("Don't register null items!");
        register(stack.getUnlocalizedName(), stack);
    }

    /**
     * Registers a new item with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft itself while the mod is
     * initializing during the pre-init and init stages.
     *
     * @param item The item
     */
    public static void register(Item item) {
        if (ModuleManager.getStage() != ModuleManager.Stage.PRE_INIT && ModuleManager.getStage() != ModuleManager.Stage.INIT_FIRST)
            throw new RuntimeException("Items must be initialized in PreInit or InitFirst!");
        _register(item);
    }

    public static void registerInit(Item item) {
        if (ModuleManager.getStage() != ModuleManager.Stage.INIT_SECOND)
            throw new RuntimeException("This item must be initialized in InitSecond!");
        _register(item);
    }

    private static void _register(Item item) {
        String tag = item.getUnlocalizedName();
        tag = MiscTools.cleanTag(tag);
        TagList.addTag(tag);
        GameRegistry.registerItem(item, tag);
    }

    /**
     * Registers a new block with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft itself while the mod is
     * initializing during the pre-init and init stages.
     *
     * @param block The block
     */
    public static void register(Block block) {
        register(block, ItemBlock.class);
    }

    /**
     * Registers a new block with the GameRegistry.
     * <p/>
     * This should generally only be called by Railcraft itself while the mod is
     * initializing during the pre-init and init stages.
     *
     * @param block     The block
     * @param itemclass
     */
    public static void register(Block block, Class<? extends ItemBlock> itemclass) {
        if (ModuleManager.getStage() != ModuleManager.Stage.PRE_INIT && ModuleManager.getStage() != ModuleManager.Stage.INIT_FIRST)
            throw new RuntimeException("Blocks must be initialized in PreInit or InitFirst!");
        String tag = block.getUnlocalizedName();
        tag = MiscTools.cleanTag(tag);
        TagList.addTag(tag);
        GameRegistry.registerBlock(block, itemclass, tag);
    }
}
