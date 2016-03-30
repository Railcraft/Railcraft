/*
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BoundingBoxManager {

    public static final BoundingBox DEFAULT = new BoundingBox();
    private static final Map<IEnumMachine<?>, BoundingBox> collisionBoxes = new HashMap<IEnumMachine<?>, BoundingBox>();
    private static final Map<IEnumMachine<?>, BoundingBox> selectionBoxes = new HashMap<IEnumMachine<?>, BoundingBox>();

    private BoundingBoxManager() {

    }

    public static AxisAlignedBB getCollisionBox(World world, BlockPos pos, IEnumMachine<?> machine) {
        BoundingBox box = collisionBoxes.get(machine);
        if (box == null)
            box = DEFAULT;
        return box.getBox(world, pos);
    }

    public static AxisAlignedBB getSelectionBox(World world,  BlockPos pos, IEnumMachine<?> machine) {
        BoundingBox box = selectionBoxes.get(machine);
        if (box == null)
            box = DEFAULT;
        return box.getBox(world, pos);
    }

    public static void registerBoundingBox(IEnumMachine<?> machine, BoundingBox box) {
        registerCollisionBoundingBox(machine, box);
        registerSelectionBoundingBox(machine, box);
    }

    public static void registerCollisionBoundingBox(IEnumMachine<?> machine, BoundingBox box) {
        collisionBoxes.put(machine, box);
    }

    public static void registerSelectionBoundingBox(IEnumMachine<?> machine, BoundingBox box) {
        selectionBoxes.put(machine, box);
    }

    public static class BoundingBox {

        private final double min, max;

        public BoundingBox() {
            this.min = 0;
            this.max = 1;
        }

        public BoundingBox(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public AxisAlignedBB getBox(World world, BlockPos pos) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return AxisAlignedBB.fromBounds(x + min, y + min, z + min, x + max, y + max, z + max);
        }
    }

    public static class ReducedBoundingBox extends ScaledBoundingBox {

        public ReducedBoundingBox(int pixels) {
            super(pixels * 2.0 / 16.0);
        }

    }

    public static class ScaledBoundingBox extends BoundingBox {

        public ScaledBoundingBox(double scale) {
            super((1.0 - scale) / 2.0, 1.0 - ((1.0 - scale) / 2.0));
        }

    }
}
