/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.wire;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CovertJaguar on 7/25/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public interface IChargeBlock {

    @Nullable
    ChargeDef getChargeDef(IBlockState state, IBlockAccess world, BlockPos pos);

    class ChargeDef {
        @Nonnull
        private final ConnectType connectType;

        public ChargeDef(@Nonnull ConnectType connectType) {
            this.connectType = connectType;
        }

        ConnectType getConnectType() {
            return connectType;
        }
    }

    enum ConnectType {

        TRACK {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos, IChargeBlock chargeBlock) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);
                EnumSet<ConnectType> notWire = EnumSet.complementOf(EnumSet.of(ConnectType.WIRE));
                EnumSet<ConnectType> track = EnumSet.of(ConnectType.TRACK);

                positions.put(new BlockPos(x + 1, y, z), notWire);
                positions.put(new BlockPos(x - 1, y, z), notWire);

                positions.put(new BlockPos(x + 1, y + 1, z), track);
                positions.put(new BlockPos(x + 1, y - 1, z), track);

                positions.put(new BlockPos(x - 1, y + 1, z), track);
                positions.put(new BlockPos(x - 1, y - 1, z), track);

                positions.put(new BlockPos(x, y - 1, z), all);

                positions.put(new BlockPos(x, y, z + 1), notWire);
                positions.put(new BlockPos(x, y, z - 1), notWire);

                positions.put(new BlockPos(x, y + 1, z + 1), track);
                positions.put(new BlockPos(x, y - 1, z + 1), track);

                positions.put(new BlockPos(x, y + 1, z - 1), track);
                positions.put(new BlockPos(x, y - 1, z - 1), track);
                return positions;
            }

        },
        WIRE {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos, IChargeBlock chargeBlock) {
                int x = pos.getX();
                int y = pos.getY();
                int z = pos.getZ();
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);
                EnumSet<ConnectType> notTrack = EnumSet.complementOf(EnumSet.of(ConnectType.TRACK));

                positions.put(new BlockPos(x + 1, y, z), notTrack);
                positions.put(new BlockPos(x - 1, y, z), notTrack);
                positions.put(new BlockPos(x, y + 1, z), all);
                positions.put(new BlockPos(x, y - 1, z), notTrack);
                positions.put(new BlockPos(x, y, z + 1), notTrack);
                positions.put(new BlockPos(x, y, z - 1), notTrack);
                return positions;
            }

        },
        BLOCK {
            @Override
            public Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos, IChargeBlock chargeBlock) {
                Map<BlockPos, EnumSet<ConnectType>> positions = new HashMap<BlockPos, EnumSet<ConnectType>>();

                EnumSet<ConnectType> all = EnumSet.allOf(ConnectType.class);

                for (EnumFacing facing : EnumFacing.VALUES) {
                    positions.put(pos.offset(facing), all);
                }
                return positions;
            }

        };

        @Nonnull
        public abstract Map<BlockPos, EnumSet<ConnectType>> getPossibleConnectionLocations(BlockPos pos, IChargeBlock chargeBlock);

    }
}
