/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.logic.*;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static mods.railcraft.common.blocks.structures.BlockSteamOven.FACING;
import static mods.railcraft.common.blocks.structures.BlockSteamOven.ICON;

public final class TileSteamOven extends TileCrafter implements ISteamUser, ITileRotate {

    private static final List<StructurePattern> patterns = new ArrayList<>();

    static {
        char[][][] map = {
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'B', 'B', 'O'},
                        {'O', 'B', 'B', 'O'},
                        {'*', 'O', 'O', '*'}
                },
                {
                        {'*', 'O', 'O', '*'},
                        {'O', 'O', 'O', 'O'},
                        {'O', 'O', 'O', 'O'},
                        {'*', 'O', 'O', '*'},
                },
        };
        patterns.add(new StructurePattern(map));
    }

    public TileSteamOven() {
        setLogic(new StructureLogic("steam_oven", this, patterns,
                        new SteamOvenLogic(Logic.Adapter.of(this))
                                .addSubLogic(new RotationLogic(Logic.Adapter.of(this)))
                )
                        .addSubLogic(new CrafterParticleEffectLogic(Logic.Adapter.of(this), () -> {
                            for (int i = 0; i < 16; i++)
                                ClientEffects.INSTANCE.steamEffect(theWorldAsserted(), this, +0.25);
                        }))
        );
    }

    public static void placeSteamOven(World world, BlockPos pos, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        StructurePattern pattern = TileSteamOven.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.STEAM_OVEN.getDefaultState());
        Optional<TileLogic> tile = pattern.placeStructure(world, pos, blockMapping);
        tile.flatMap(t -> t.getLogic(StructureLogic.class)).ifPresent(structure -> {
            structure.getFunctionalLogic(InventoryLogic.class).ifPresent(logic -> {
                for (int slot = 0; slot < 9; slot++) {
                    if (input != null && slot < input.size())
                        logic.setInventorySlotContents(RockCrusherLogic.SLOT_INPUT + slot, input.get(slot));
                    if (output != null && slot < output.size())
                        logic.setInventorySlotContents(RockCrusherLogic.SLOT_OUTPUT + slot, output.get(slot));
                }
            });
        });
    }

    @Override
    public EnumFacing getFacing() {
        return RotationLogic.getRotationLogic(this).getFacing();
    }

    @Override
    public void setFacing(EnumFacing facing) {
        Preconditions.checkArgument(canRotate(facing), "Cannot set facing to up or down.");
        RotationLogic.getRotationLogic(this).setFacing(facing);
    }

    @Override
    public EnumFacing[] getValidRotations() {
        return EnumFacing.HORIZONTALS;
    }

    @Override
    public EnumGui getGui() {
        return EnumGui.STEAM_OVEN;
    }

    enum Icon implements IStringSerializable {

        DOOR_TL, DOOR_TR, DOOR_BL, DOOR_BR, DEFAULT;

        IBlockState remapState(IBlockState state) {
            return state.withProperty(ICON, this);
        }

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    @Override
    public IBlockState getActualState(IBlockState base) {
        EnumFacing side = getFacing();
        IBlockState facedState;
        try {
            facedState = base.withProperty(FACING, side);
        } catch (IllegalArgumentException ex) {
            facedState = base.withProperty(FACING, EnumFacing.NORTH);
        }
        IBlockState actualState = facedState;
        return getLogic(StructureLogic.class).filter(StructureLogic::isStructureValid).map(l -> {
            BlockPos pos = l.getPatternPosition();
            assert pos != null;
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            switch (side) {
                case NORTH:
                    if (y == 2) {
                        if (x == 2)
                            return Icon.DOOR_TL.remapState(actualState);
                        return Icon.DOOR_TR.remapState(actualState);
                    }
                    if (x == 2)
                        return Icon.DOOR_BL.remapState(actualState);
                    return Icon.DOOR_BR.remapState(actualState);
                case WEST:
                    if (y == 2) {
                        if (z == 1)
                            return Icon.DOOR_TL.remapState(actualState);
                        return Icon.DOOR_TR.remapState(actualState);
                    }
                    if (z == 1)
                        return Icon.DOOR_BL.remapState(actualState);
                    return Icon.DOOR_BR.remapState(actualState);
                case SOUTH:
                    if (y == 2) {
                        if (x == 1)
                            return Icon.DOOR_TL.remapState(actualState);
                        return Icon.DOOR_TR.remapState(actualState);
                    }
                    if (x == 1)
                        return Icon.DOOR_BL.remapState(actualState);
                    return Icon.DOOR_BR.remapState(actualState);
                case EAST:
                    if (y == 2) {
                        if (z == 2)
                            return Icon.DOOR_TL.remapState(actualState);
                        return Icon.DOOR_TR.remapState(actualState);
                    }
                    if (z == 2)
                        return Icon.DOOR_BL.remapState(actualState);
                    return Icon.DOOR_BR.remapState(actualState);
                default:
                    return Icon.DEFAULT.remapState(actualState);
            }
        }).orElseGet(() -> Icon.DEFAULT.remapState(actualState));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        // This might cause issues, needs testing with different pipe mods.
        // If the structure isn't valid, it will return NORTH.
        if (facing == getFacing())
            return false;
        return super.hasCapability(capability, facing);
    }
}
