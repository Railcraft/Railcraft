/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2022
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.single;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.TradeStationLogic;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.entity.ai.EntityAISearchForBlock;
import mods.railcraft.common.util.entity.ai.EntityAIWatchBlock;
import net.minecraft.entity.passive.EntityVillager;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class TileTradeStation extends TileLogic {

    public TileTradeStation() {
        setRootLogic(new TradeStationLogic(Logic.Adapter.of(this)) {

            @Override
            protected void modifyNearbyAI() {
                for (EntityVillager villager : findNearbyVillagers(20)) {
                    AIPlugin.addAITask(villager, 9, new EntityAIWatchBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 4, 0.08F));
                    AIPlugin.addAITask(villager, 9, new EntityAISearchForBlock(villager, RailcraftBlocks.TRADE_STATION.getDefaultState(), 16, 0.002F));
                }
            }
        });
    }
}
