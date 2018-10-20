/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.chest;

import mods.railcraft.common.items.Metal;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * The logic behind the metals chest.
 */
public class MetalsChestLogic extends ChestLogic {

    static final Map<Metal, Predicate<ItemStack>> nuggetFilters = new EnumMap<>(Metal.class);
    static final Map<Metal, Predicate<ItemStack>> ingotFilters = new EnumMap<>(Metal.class);
    static final Map<Metal, Predicate<ItemStack>> blockFilters = new EnumMap<>(Metal.class);

    static {
        //TODO add compressions e.g. emerald, lapis, coke, diamond, redstone
        for (Metal m : Metal.VALUES) {
            nuggetFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.NUGGET)).and(m.nuggetFilter));
            ingotFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.INGOT)).and(m.ingotFilter));
            blockFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.BLOCK)).and(m.blockFilter));
        }
    }

    private Target target = Target.NUGGET_CONDENSE;

    public MetalsChestLogic(World world, IInventory inventory) {
        super(world, inventory);
    }

    @Override
    public void update() {
        if (!target.evaluate(inventory))
            target = target.next();
    }

    enum Target {

        NUGGET_CONDENSE {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    ItemStack ingotStack = metal.getStack(Metal.Form.INGOT);
                    if (!InvTools.isEmpty(ingotStack) && im.canRemoveItems(metal.nuggetFilter, 9) && im.canAddStack(ingotStack)) {
                        im.removeItems(metal.nuggetFilter, 9);
                        im.addStack(ingotStack);
                        return true;
                    }
                }
                return false;
            }

        },
        INGOT_CONDENSE {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    ItemStack blockStack = metal.getStack(Metal.Form.BLOCK);
                    if (!InvTools.isEmpty(blockStack) && im.canRemoveItems(metal.ingotFilter, 9) && im.canAddStack(blockStack)) {
                        im.removeItems(metal.ingotFilter, 9);
                        im.addStack(blockStack);
                        return true;
                    }
                }
                return false;
            }

        },
        NUGGET_SWAP {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    Predicate<ItemStack> filter = nuggetFilters.get(metal);
                    ItemStack nuggetStack = metal.getStack(Metal.Form.NUGGET);
                    if (!InvTools.isEmpty(nuggetStack) && im.canRemoveItems(filter, 1) && im.canAddStack(nuggetStack)) {
                        im.removeItems(filter, 1);
                        im.addStack(nuggetStack);
                        return true;
                    }
                }
                return false;
            }

        },
        INGOT_SWAP {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    Predicate<ItemStack> filter = ingotFilters.get(metal);
                    ItemStack ingotStack = metal.getStack(Metal.Form.INGOT);
                    if (!InvTools.isEmpty(ingotStack) && im.canRemoveItems(filter, 1) && im.canAddStack(ingotStack)) {
                        im.removeItems(filter, 1);
                        im.addStack(ingotStack);
                        return true;
                    }
                }
                return false;
            }

        },
        BLOCK_SWAP {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    Predicate<ItemStack> filter = blockFilters.get(metal);
                    ItemStack blockStack = metal.getStack(Metal.Form.BLOCK);
                    if (!InvTools.isEmpty(blockStack) && im.canRemoveItems(filter, 1) && im.canAddStack(blockStack)) {
                        im.removeItems(filter, 1);
                        im.addStack(blockStack);
                        return true;
                    }
                }
                return false;
            }

        };

        public static final Target[] VALUES = values();

        public abstract boolean evaluate(IInventory inv);

        public Target next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

    }
}
