/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.carts;

import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.single.BlockTradeStation;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.AIPlugin;
import mods.railcraft.common.util.entity.ai.EntityAISearchForEntity;
import mods.railcraft.common.util.entity.ai.EntityAIWatchEntity;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.TradeStationLogic;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 *
 */
public class EntityCartTradeStation extends CartBaseLogic {

    protected EntityCartTradeStation(World world) {
        super(world);
    }

    public EntityCartTradeStation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    {
        setLogic(new TradeStationLogic(Logic.Adapter.of(this)) {
            @Override
            protected void modifyNearbyAI() {
                for (EntityVillager villager : findNearbyVillagers(20)) {
                    AIPlugin.addAITask(villager, 9, new EntityAIWatchEntity(villager, entity -> entity instanceof EntityCartTradeStation, 4, 0.08F));
                    AIPlugin.addAITask(villager, 9, new EntityAISearchForEntity(villager, entity -> entity instanceof EntityCartTradeStation, 16, 0.002F));
                }
            }

            @Override
            protected EntityPlayer getOwnerEntityOrFake() {
                return CartTools.getCartOwnerEntity(EntityCartTradeStation.this);
            }
        });
    }

    @Override
    public IBlockState getDefaultDisplayTile() {
        return RailcraftBlocks.TRADE_STATION.getDefaultState().withProperty(BlockTradeStation.FACING, EnumFacing.WEST);
    }

    @Override
    public boolean doInteract(EntityPlayer player, EnumHand hand) {
        if (!super.doInteract(player, hand))
            return false;
        getLogic(TradeStationLogic.class).ifPresent(logic -> {
            player.addExperience(logic.getXpCollected());
            logic.clearXp();
        });
        return true;
    }

    @Override
    protected void openRailcraftGui(EntityPlayer player) {
        super.openRailcraftGui(player);
    }

    @Override
    protected EnumGui getGuiType() {
        return EnumGui.TRADE_STATION;
    }

    @Override
    public IRailcraftCartContainer getCartType() {
        return RailcraftCarts.TRADE_STATION;
    }
}
