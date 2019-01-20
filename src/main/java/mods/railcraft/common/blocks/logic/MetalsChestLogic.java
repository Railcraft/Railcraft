/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import com.google.common.collect.Lists;
import mods.railcraft.api.items.IMetalsChestCondenseRule;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * The logic behind the metals chest.
 */
public class MetalsChestLogic extends InventoryLogic {
    private static final int TICK_PER_CONDENSE = 16;

    public MetalsChestLogic(Adapter adapter) {
        super(adapter, 27);
    }

    @Override
    public void updateServer() {
        if (clock(TICK_PER_CONDENSE)) {
            for (IMetalsChestCondenseRule condense : IMetalsChestCondenseRule.rules) {
                Predicate<ItemStack> filter = condense.getPredicate();
                ItemStack ingotStack = condense.getResult();
                if (canFit(ingotStack) && removeItems(condense.removeCount(), filter)) {
                    addStack(ingotStack);
                    break;
                }
            }
        }
    }
}
