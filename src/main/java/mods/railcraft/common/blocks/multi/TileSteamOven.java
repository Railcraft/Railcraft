/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import mods.railcraft.client.util.effects.ClientEffects;
import mods.railcraft.common.blocks.RailcraftBlocks;
import mods.railcraft.common.blocks.TileCrafter;
import mods.railcraft.common.blocks.interfaces.ITileRotate;
import mods.railcraft.common.blocks.logic.CrafterLogic;
import mods.railcraft.common.blocks.logic.Logic;
import mods.railcraft.common.blocks.logic.SteamOvenLogic;
import mods.railcraft.common.blocks.logic.StructureLogic;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.steam.ISteamUser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static mods.railcraft.common.blocks.multi.BlockSteamOven.FACING;
import static mods.railcraft.common.blocks.multi.BlockSteamOven.ICON;
import static net.minecraft.util.EnumFacing.NORTH;

public final class TileSteamOven extends TileCrafter implements ISteamUser, ITileRotate {

    private static final List<MultiBlockPattern> patterns = new ArrayList<>();

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
        patterns.add(new MultiBlockPattern(map));
    }

    private boolean wasProcessing;

    public TileSteamOven() {
        setLogic(new StructureLogic("steam_oven", this, patterns, new SteamOvenLogic(Logic.Adapter.of(this))));
    }

    public static void placeSteamOven(World world, BlockPos pos, @Nullable List<ItemStack> input, @Nullable List<ItemStack> output) {
        MultiBlockPattern pattern = TileSteamOven.patterns.get(0);
        Char2ObjectMap<IBlockState> blockMapping = new Char2ObjectOpenHashMap<>();
        blockMapping.put('B', RailcraftBlocks.STEAM_OVEN.getDefaultState());
        TileEntity tile = pattern.placeStructure(world, pos, blockMapping);
        if (tile instanceof TileSteamOven) {
            TileSteamOven master = (TileSteamOven) tile;
            // FIXME
//            for (int slot = 0; slot < 9; slot++) {
//                if (input != null && slot < input.size())
//                    master.inv.setInventorySlotContents(SteamOvenLogic.SLOT_INPUT + slot, input.get(slot));
//                if (output != null && slot < output.size())
//                    master.inv.setInventorySlotContents(SteamOvenLogic.SLOT_OUTPUT + slot, output.get(slot));
//            }
        }
    }

    @Override
    public void update() {
        super.update();

        if (Game.isClient(getWorld())) {
            boolean isProcessing = getLogic(CrafterLogic.class).map(CrafterLogic::isProcessing).orElse(false);
            if (wasProcessing != isProcessing && !isProcessing) {
                for (int i = 0; i < 16; i++)
                    ClientEffects.INSTANCE.steamEffect(world, this, +0.25);
            }
            wasProcessing = isProcessing;
        }
    }

    @Override
    public void onBlockPlacedBy(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(state, placer, stack);
        if (placer != null)
            // This is kind of ugly, but it works. Normally we can't access our logic directly, only through the master.
            getLogic(StructureLogic.class)
                    .ifPresent(l -> ((SteamOvenLogic) l.masterLogic).setFacing(MiscTools.getHorizontalSideFacingPlayer(placer)));
    }

    @Override
    public EnumFacing getFacing() {
        return getLogic(SteamOvenLogic.class).map(SteamOvenLogic::getFacing).orElse(NORTH);
    }

    @Override
    public void setFacing(EnumFacing facing) {
        getLogic(SteamOvenLogic.class).ifPresent(l -> l.setFacing(facing));
        markBlockForUpdate();
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
        IBlockState actualState = base.withProperty(FACING, side);
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
