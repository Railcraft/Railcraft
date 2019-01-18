/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.core;

import mods.railcraft.api.core.IVariantEnum;
import mods.railcraft.common.blocks.IRailcraftBlock;
import mods.railcraft.common.blocks.IRailcraftItemBlock;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.carts.RailcraftCarts;
import mods.railcraft.common.items.IRailcraftItemSimple;
import mods.railcraft.common.items.RailcraftItems;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by CovertJaguar on 3/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class RailcraftObjects {

    public static void processBlocks(@Nullable BiConsumer<IRailcraftBlock, IRailcraftItemBlock> processBlock, BiConsumer<IRailcraftBlock, IVariantEnum> processVariants) {
        for (RailcraftBlocks blockContainer : RailcraftBlocks.VALUES) {
            blockContainer.getObject().ifPresent(block -> {
                if (processBlock != null)
                    processBlock.accept(block, (IRailcraftItemBlock) blockContainer.item());
                IVariantEnum[] variants = block.getVariants();
                if (variants != null) {
                    for (IVariantEnum variant : variants) {
                        processVariants.accept(block, variant);
                    }
                } else processVariants.accept(block, null);
            });
        }
    }

    public static void processBlockVariants(BiConsumer<IRailcraftBlock, IVariantEnum> processVariants) {
        processBlocks(null, processVariants);
    }

    private static Set<IRailcraftObjectContainer<IRailcraftItemSimple>> getItems() {
        Set<IRailcraftObjectContainer<IRailcraftItemSimple>> items = new LinkedHashSet<>();
        items.addAll(Arrays.asList(RailcraftItems.VALUES));
        items.addAll(Arrays.asList(RailcraftCarts.VALUES));
        return items;
    }

    public static void processItems(Consumer<IRailcraftItemSimple> processItem) {
        for (IRailcraftObjectContainer<IRailcraftItemSimple> itemContainer : getItems()) {
            itemContainer.getObject().ifPresent(processItem);
        }
    }

    public static void processItemVariants(BiConsumer<IRailcraftItemSimple, IVariantEnum> processVariants) {
        for (IRailcraftObjectContainer<IRailcraftItemSimple> itemContainer : getItems()) {
            itemContainer.getObject().ifPresent(item ->
            {
                IVariantEnum[] variants = item.getVariants();
                if (variants != null) {
                    for (IVariantEnum variant : variants) {
                        processVariants.accept(item, variant);
                    }
                } else processVariants.accept(item, null);
            });
        }
    }
}
