/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import mods.railcraft.api.core.IStackFilter;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.util.inventory.filters.StackFilters;
import mods.railcraft.common.util.inventory.iterators.IExtInvSlot;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestMetals extends TileChestRailcraft {

    private static final int TICK_PER_CONDENSE = 16;
    private static final Map<Metal, IStackFilter> nuggetFilters = new EnumMap<Metal, IStackFilter>(Metal.class);
    private static final Map<Metal, IStackFilter> ingotFilters = new EnumMap<Metal, IStackFilter>(Metal.class);
    private static final Map<Metal, IStackFilter> blockFilters = new EnumMap<Metal, IStackFilter>(Metal.class);

    static {
        for (Metal m : Metal.VALUES) {
            nuggetFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.NUGGET)).and(m.nuggetFilter));
            ingotFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.INGOT)).and(m.ingotFilter));
            blockFilters.put(m, StackFilters.noneOf(m.getStack(Metal.Form.BLOCK)).and(m.blockFilter));
        }
    }

    private Target target = Target.NUGGET_CONDENSE;

    @Override
    public EnumMachineBeta getMachineType() {
        return EnumMachineBeta.METALS_CHEST;
    }

    @Override
    public void update() {
        super.update();

        if (clock % TICK_PER_CONDENSE == 0 && Game.isHost(worldObj))
            if (!target.evaluate(this))
                target = target.next();
    }

    enum Target {

        NUGGET_CONDENSE {
            @Override
            public boolean evaluate(IInventory inv) {
                InventoryManipulator<IExtInvSlot> im = InventoryManipulator.get(inv);
                for (Metal metal : Metal.VALUES) {
                    ItemStack ingotStack = metal.getStack(Metal.Form.INGOT);
                    if (ingotStack != null && im.canRemoveItems(metal.nuggetFilter, 9) && im.canAddStack(ingotStack)) {
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
                    if (blockStack != null && im.canRemoveItems(metal.ingotFilter, 9) && im.canAddStack(blockStack)) {
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
                    IStackFilter filter = nuggetFilters.get(metal);
                    ItemStack nuggetStack = metal.getStack(Metal.Form.NUGGET);
                    if (nuggetStack != null && im.canRemoveItems(filter, 1) && im.canAddStack(nuggetStack)) {
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
                    IStackFilter filter = ingotFilters.get(metal);
                    ItemStack ingotStack = metal.getStack(Metal.Form.INGOT);
                    if (ingotStack != null && im.canRemoveItems(filter, 1) && im.canAddStack(ingotStack)) {
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
                    IStackFilter filter = blockFilters.get(metal);
                    ItemStack blockStack = metal.getStack(Metal.Form.BLOCK);
                    if (blockStack != null && im.canRemoveItems(filter, 1) && im.canAddStack(blockStack)) {
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
