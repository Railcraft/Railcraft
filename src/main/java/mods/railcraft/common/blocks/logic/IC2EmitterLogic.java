/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.api.info.ILocatable;
import mods.railcraft.common.plugins.ic2.IC2Plugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CovertJaguar on 8/3/2020 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class IC2EmitterLogic extends Logic implements ILocatable, IMetaDelegate, IEnergySource, IChargeAccessorLogic {
    private final int sourceTier;
    private final int output;
    private List<IEnergyTile> subTiles = NonNullList.create();
    private boolean added;

    public IC2EmitterLogic(Adapter adapter, int sourceTier, int output) {
        super(adapter);
        this.sourceTier = sourceTier;
        this.output = output;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(getAvailableCharge(), output);
    }

    @Override
    public void drawEnergy(double amount) {
        removeCharge(amount);
    }

    @Override
    public int getSourceTier() {
        return sourceTier;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor iEnergyAcceptor, EnumFacing enumFacing) {
        return true;
    }

    public void rebuildSubTiles() {
        subTiles = getLogic(StructureLogic.class)
                .map(l -> l.getComponents().stream()
                        .map(c -> c.getLogic(IC2EmitterLogic.class))
                        .flatMap(Streams.unwrap())
                        .flatMap(Streams.toType(IEnergyTile.class))
                        .collect(Collectors.toCollection(NonNullList::create)))
                .orElseGet(NonNullList::create);
    }

    @Override
    public List<IEnergyTile> getSubTiles() {
        return subTiles;
    }

    @Override
    public void onStructureChanged(boolean isComplete, boolean isMaster, Object[] data) {
        super.onStructureChanged(isComplete, isMaster, data);
        if (isMaster) addToNet();
        else dropFromNet();
    }

    public void addToNet() {
        dropFromNet();
        try {
            rebuildSubTiles();
            IC2Plugin.addTileToNet(this);
        } catch (Throwable error) {
            Game.log().api("IndustrialCraft", error);
        }
        added = true;
    }

    public void dropFromNet() {
        if (added)
            try {
                IC2Plugin.removeTileFromNet(this);
            } catch (Throwable error) {
                Game.log().api("IndustrialCraft", error);
            }
        added = false;
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }

    @Override
    public World getWorldObj() {
        return theWorld();
    }
}
