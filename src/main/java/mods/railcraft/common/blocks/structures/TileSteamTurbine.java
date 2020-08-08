/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import mods.railcraft.api.charge.Charge;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.blocks.structures.BlockSteamTurbine.Texture;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.buildcraft.triggers.INeedsMaintenance;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class TileSteamTurbine extends TileLogic implements INeedsMaintenance, ISteamUser {

    private static final int WATER_OUTPUT = 4;
    private static final int FE_OUTPUT = 900;
    private static final int IC2_TIER = 3;
    private static final List<StructurePattern> patterns = new ArrayList<>();

    static {
        char[][][] map1 = {
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'B', 'W', 'B', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O', 'O'}
                }
        };
        StructurePattern pattern = new StructurePattern(map1, Axis.X);
        patterns.add(pattern);

        char[][][] map2 = {
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'W', 'W', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'O', 'O', 'O'}
                },
                {
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'}
                }
        };
        StructurePattern otherPattern = new StructurePattern(map2, Axis.Z);
        patterns.add(otherPattern);
    }

    public double guageReadout;

    public TileSteamTurbine() {
        setLogic(new StructureLogic("flux", this, patterns, new SteamTurbineLogic(Logic.Adapter.of(this))
                        .addSubLogic(new ChargeSourceLogic(Logic.Adapter.of(this), Charge.distribution))
                ) {
                    @Override
                    protected void onMasterReset() {
                        super.onMasterReset();
                        try {
                            getLogic(IC2EmitterLogic.class).ifPresent(IC2EmitterLogic::dropFromNet);
                        } catch (Throwable error) {
                            Game.log().api("IndustrialCraft", error);
                        }
                    }
                }
                        .addSubLogic(new ChargeComparatorLogic(Logic.Adapter.of(this), Charge.distribution))
                        .addSubLogic(new FluidPushLogic(Logic.Adapter.of(this), SteamTurbineLogic.TANK_WATER, WATER_OUTPUT, EnumFacing.HORIZONTALS))
                        .addSubLogic(new EnergyPushLogic(Logic.Adapter.of(this), FE_OUTPUT, EnumFacing.VALUES))
                        .addSubLogic(new ChargeToFEAdapterLogic(Logic.Adapter.of(this), FE_OUTPUT))
                        .addSubLogic(new IC2EmitterLogic(Logic.Adapter.of(this), IC2_TIER, SteamTurbineLogic.CHARGE_OUTPUT))
        );
    }

    private void dropFromNet() {
        try {
            getLogic(IC2EmitterLogic.class).ifPresent(IC2EmitterLogic::dropFromNet);
        } catch (Throwable error) {
            Game.log().api("IndustrialCraft", error);
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        dropFromNet();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        dropFromNet();
    }

    @Override
    public boolean needsMaintenance() {
        return getLogic(SteamTurbineLogic.class).map(SteamTurbineLogic::needsMaintenance).orElse(false);
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            getLogic(SteamTurbineLogic.class).ifPresent(logic -> logic.operatingRatio = value * 0.01F);
            return true;
        }
        return super.receiveClientEvent(id, value);
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.TURBINE;
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        return getLogic(StructureLogic.class)
                .filter(StructureLogic::isStructureValid)
                .map(logic -> {
                    Axis axis = requireNonNull(logic.getPattern()).getAttachedDataOr(0, Axis.X);
                    IBlockState actualState = base
                            .withProperty(BlockSteamTurbine.WINDOW, logic.getPatternMarker() == 'W')
                            .withProperty(BlockSteamTurbine.LONG_AXIS, axis);
                    BlockPos pos = logic.getPatternPosition();
                    final Texture texture;
                    if (axis == Axis.X) {
                        // x = 2, left; y = 1, bottom
                        if (pos.getX() == 2) {
                            if (pos.getY() == 1) {
                                texture = Texture.BOTTOM_LEFT;
                            } else {
                                texture = Texture.TOP_LEFT;
                            }
                        } else {
                            if (pos.getY() == 1) {
                                texture = Texture.BOTTOM_RIGHT;
                            } else {
                                texture = Texture.TOP_RIGHT;
                            }
                        }
                    } else {
                        if (pos.getZ() == 1) {
                            if (pos.getY() == 1) {
                                texture = Texture.BOTTOM_LEFT;
                            } else {
                                texture = Texture.TOP_LEFT;
                            }
                        } else {
                            if (pos.getY() == 1) {
                                texture = Texture.BOTTOM_RIGHT;
                            } else {
                                texture = Texture.TOP_RIGHT;
                            }
                        }
                    }
                    return actualState.withProperty(BlockSteamTurbine.TEXTURE, texture);
                })
                .orElse(base);
    }
}
