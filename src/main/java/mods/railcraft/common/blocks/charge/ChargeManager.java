/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.charge;

import com.google.common.collect.MapMaker;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by CovertJaguar on 7/26/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ChargeManager {
    private static final Map<World, ChargeNetwork> chargeNetworks = new MapMaker().weakKeys().makeMap();

    public static ChargeNetwork getNetwork(World world) {
        ChargeNetwork chargeNetwork = chargeNetworks.get(world);
        if (chargeNetwork == null) {
            chargeNetwork = new ChargeNetwork(world);
            chargeNetworks.put(world, chargeNetwork);
        }
        return chargeNetwork;
    }

    public static ChargeManager getEventListener() {
        return new ChargeManager();
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.SERVER)
            getNetwork(event.world).tick();
    }

    public static void forConnections(World world, BlockPos pos, BiConsumer<BlockPos, IChargeBlock.ChargeDef> action) {
        IBlockState state = WorldPlugin.getBlockState(world, pos);
        if (state.getBlock() instanceof IChargeBlock) {
            IChargeBlock block = (IChargeBlock) state.getBlock();
            IChargeBlock.ChargeDef chargeDef = block.getChargeDef(state, world, pos);
            if (chargeDef != null) {
                Map<BlockPos, EnumSet<IChargeBlock.ConnectType>> possibleConnections = chargeDef.getConnectType().getPossibleConnectionLocations(pos);
                for (Map.Entry<BlockPos, EnumSet<IChargeBlock.ConnectType>> connection : possibleConnections.entrySet()) {
                    IBlockState otherState = WorldPlugin.getBlockState(world, connection.getKey());
                    if (otherState.getBlock() instanceof IChargeBlock) {
                        IChargeBlock.ChargeDef other = ((IChargeBlock) otherState.getBlock()).getChargeDef(WorldPlugin.getBlockState(world, connection.getKey()), world, connection.getKey());
                        if (other != null && other.getConnectType().getPossibleConnectionLocations(connection.getKey()).get(pos).contains(chargeDef.getConnectType())) {
                            action.accept(connection.getKey(), other);
                        }
                    }
                }
            }
        }
    }
}
