/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine.beta;

import java.util.EnumMap;
import java.util.Map;
import mods.railcraft.api.core.items.IStackFilter;
import mods.railcraft.common.blocks.machine.IEnumMachine;
import mods.railcraft.common.items.Metal;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.filters.ComplexStackFilter;
import mods.railcraft.common.util.inventory.filters.ExclusionStackFilter;
import mods.railcraft.common.util.inventory.manipulators.InventoryManipulator;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.inventory.IInventory;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class TileChestMetals extends TileChestRailcraft {

    private static final int TICK_PER_CONDENSE = 16;
    private static final Map<Metal, IStackFilter> nuggetFilters = new EnumMap<Metal, IStackFilter>(Metal.class);
    private static final Map<Metal, IStackFilter> ingotFilters = new EnumMap<Metal, IStackFilter>(Metal.class);
    private static final Map<Metal, IStackFilter> blockFilters = new EnumMap<Metal, IStackFilter>(Metal.class);

    static {
        for (Metal m : Metal.VALUES) {
            nuggetFilters.put(m, ComplexStackFilter.and(new ExclusionStackFilter(m.getNugget()), m.nuggetFilter));
            ingotFilters.put(m, ComplexStackFilter.and(new ExclusionStackFilter(m.getIngot()), m.ingotFilter));
            blockFilters.put(m, ComplexStackFilter.and(new ExclusionStackFilter(m.getBlock()), m.blockFilter));
        }
    }

    enum Target {

        NUGGET_CONDENSE {

                    @Override
                    public boolean evaluate(IInventory inv) {
                        InventoryManipulator im = InventoryManipulator.get(inv);
                        for (Metal metal : Metal.VALUES) {
                            if (metal.getIngot() != null && im.canRemoveItems(metal.nuggetFilter, 9) && im.canAddStack(metal.getIngot())) {
                                im.removeItems(metal.nuggetFilter, 9);
                                im.addStack(metal.getIngot());
                                return true;
                            }
                        }
                        return false;
                    }

                },
        INGOT_CONDENSE {

                    @Override
                    public boolean evaluate(IInventory inv) {
                        InventoryManipulator im = InventoryManipulator.get(inv);
                        for (Metal metal : Metal.VALUES) {
                            if (metal.getBlock() != null && im.canRemoveItems(metal.ingotFilter, 9) && im.canAddStack(metal.getBlock())) {
                                im.removeItems(metal.ingotFilter, 9);
                                im.addStack(metal.getBlock());
                                return true;
                            }
                        }
                        return false;
                    }

                },
        NUGGET_SWAP {

                    @Override
                    public boolean evaluate(IInventory inv) {
                        InventoryManipulator im = InventoryManipulator.get(inv);
                        for (Metal metal : Metal.VALUES) {
                            IStackFilter filter = nuggetFilters.get(metal);
                            if (metal.getNugget() != null && im.canRemoveItems(filter, 1) && im.canAddStack(metal.getNugget())) {
                                im.removeItems(filter, 1);
                                im.addStack(metal.getNugget());
                                return true;
                            }
                        }
                        return false;
                    }

                },
        INGOT_SWAP {

                    @Override
                    public boolean evaluate(IInventory inv) {
                        InventoryManipulator im = InventoryManipulator.get(inv);
                        for (Metal metal : Metal.VALUES) {
                            IStackFilter filter = ingotFilters.get(metal);
                            if (metal.getIngot() != null && im.canRemoveItems(filter, 1) && im.canAddStack(metal.getIngot())) {
                                im.removeItems(filter, 1);
                                im.addStack(metal.getIngot());
                                return true;
                            }
                        }
                        return false;
                    }

                },
        BLOCK_SWAP {

                    @Override
                    public boolean evaluate(IInventory inv) {
                        InventoryManipulator im = InventoryManipulator.get(inv);
                        for (Metal metal : Metal.VALUES) {
                            IStackFilter filter = blockFilters.get(metal);
                            if (metal.getBlock() != null && im.canRemoveItems(filter, 1) && im.canAddStack(metal.getBlock())) {
                                im.removeItems(filter, 1);
                                im.addStack(metal.getBlock());
                                return true;
                            }
                        }
                        return false;
                    }

                };

        public final static Target[] VALUES = values();

        public abstract boolean evaluate(IInventory inv);

        public Target next() {
            Target next = VALUES[(ordinal() + 1) % VALUES.length];
            return next;
        }

    }

    private Target target = Target.NUGGET_CONDENSE;

    @Override
    public IEnumMachine getMachineType() {
        return EnumMachineBeta.METALS_CHEST;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (clock % TICK_PER_CONDENSE == 0 && Game.isHost(worldObj))
            if (!target.evaluate(this))
                target = target.next();
    }

}
