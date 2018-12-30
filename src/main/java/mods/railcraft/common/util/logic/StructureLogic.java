/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.util.logic;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.multi.MultiBlockPattern;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Optionals;
import mods.railcraft.common.util.misc.Timer;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileRequest;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Created by CovertJaguar on 12/17/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StructureLogic extends Logic {
    private static final int NETWORK_RECHECK = 16;
    private final Timer netTimer = new Timer();
    private final TileRailcraft tile;
    private final String structureKey;
    private final Logic logic;
    private final List<? extends MultiBlockPattern> patterns;
    private final List<TileRailcraft> components = new ArrayList<>();
    private final List<TileRailcraft> componentsView = Collections.unmodifiableList(components);
    public final ListMultimap<MultiBlockPattern.State, MultiBlockPattern> patternStates = Multimaps.newListMultimap(new EnumMap<>(MultiBlockPattern.State.class), ArrayList::new);
    protected boolean isMaster;
    private boolean requestPacket;
    private StructureState state;
    private @Nullable BlockPos masterPos;
    private @Nullable MultiBlockPattern currentPattern;
    private @Nullable BlockPos posInPattern;

    protected StructureLogic(String structureKey, TileRailcraft tile, List<? extends MultiBlockPattern> patterns, Logic logic) {
        super(Adapter.of(tile));
        this.structureKey = structureKey;
        this.tile = tile;
        this.patterns = patterns;
        this.logic = logic;
        state = StructureState.UNTESTED;
        components.add(tile);
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        if (logicClass.isInstance(this))
            return Optional.of(logicClass.cast(this));
        return getMasterLogic().map(m -> m.logic).filter(logicClass::isInstance).map(logicClass::cast);
    }

    public List<TileRailcraft> getComponents() {
        return getMasterLogic().map(m -> m.componentsView).orElseGet(Collections::emptyList);
    }

    @OverridingMethodsMustInvokeSuper
    protected void onPatternChanged() {
        tile.markBlockForUpdate();
//        if (!isMaster && this instanceof IInventory)
//            InvTools.spewInventory(InventoryMapper.make((IInventory) this), theWorldAsserted(), getPos());
    }

    public final char getPatternMarker() {
        if (currentPattern == null || posInPattern == null || !isStructureValid())
            return 'O';
        return currentPattern.getPatternMarker(posInPattern);
    }

    public final @Nullable BlockPos getPatternPosition() {
        return posInPattern;
    }

    public final @Nullable MultiBlockPattern getPattern() {
        return currentPattern;
    }

    private void setPattern(@Nullable MultiBlockPattern pattern, @Nullable BlockPos posInPattern) {
        boolean changed = false;
        if (!Objects.equals(currentPattern, pattern)) {
            changed = true;
            this.currentPattern = pattern;
        }
        if (!Objects.equals(this.posInPattern, posInPattern)) {
            changed = true;
            this.posInPattern = posInPattern == null ? null : posInPattern.toImmutable();
        }
        BlockPos newMaster = pattern == null || posInPattern == null ? null
                : pattern.getMasterPosition(getPos(), this.posInPattern);
        if (!Objects.equals(masterPos, newMaster)) {
            changed = true;
            this.masterPos = newMaster;
        }

        // Possible side effects?
        if (masterPos == null)
            state = StructureState.INVALID;
        else
            state = StructureState.VALID;

        if (changed)
            onPatternChanged();
    }

    public final byte getPatternIndex() {
        return (byte) patterns.indexOf(currentPattern);
    }

    public final @Nullable BlockPos getMasterPos() {
        return masterPos;
    }

    protected int getMaxRecursionDepth() {
        return 12;
    }

    public StructureState getState() {
        return state;
    }

    @Override
    public void update() {
        super.update();
        if (isValidMaster())
            logic.update();
    }

    @Override
    protected void updateClient() {
        if (requestPacket && netTimer.hasTriggered(theWorldAsserted(), NETWORK_RECHECK)) {
            PacketDispatcher.sendToServer(new PacketTileRequest(tile));
            requestPacket = false;
        }
    }

    @Override
    protected void updateServer() {
        if (state == StructureState.UNTESTED)
            testIfMasterBlock();
        //                ClientProxy.getMod().totalMultiBlockUpdates++;
    }

    private void testIfMasterBlock() {
//        System.out.println("testing structure");
        testPatterns();
        components.clear();
        components.add(tile);

        if (patternStates.containsKey(MultiBlockPattern.State.VALID)) {
            state = StructureState.VALID;
            isMaster = true;
//             System.out.println("structure complete");

            MultiBlockPattern pattern = patternStates.get(MultiBlockPattern.State.VALID).get(0);

            int xWidth = pattern.getPatternWidthX();
            int zWidth = pattern.getPatternWidthZ();
            int height = pattern.getPatternHeight();

            BlockPos offset = getPos().subtract(pattern.getMasterOffset());

            for (int px = 0; px < xWidth; px++) {
                for (int py = 0; py < height; py++) {
                    for (int pz = 0; pz < zWidth; pz++) {

                        char marker = pattern.getPatternMarker(px, py, pz);
                        if (isMapPositionOtherBlock(marker))
                            continue;

                        BlockPos patternPos = new BlockPos(px, py, pz);
                        BlockPos pos = patternPos.add(offset);

                        WorldPlugin.getTileEntity(theWorldAsserted(), pos)
                                .flatMap(tileToLogic())
                                .ifPresent(l -> {
                                    l.setPattern(pattern, patternPos);
                                    // FIXME components.add
                                });
                    }
                }
            }

            // FIXME  MinecraftForge.EVENT_BUS.post(new MultiBlockEvent.Form(tile));
        } else if (patternStates.containsKey(MultiBlockPattern.State.NOT_LOADED)) {
            state = StructureState.UNKNOWN;
        } else {
            state = StructureState.INVALID;
            if (isMaster) {
                isMaster = false;
                onMasterReset();
                sendUpdateToClient();
            }
        }
    }

    protected void onMasterReset() {
    }

    protected boolean isMapPositionOtherBlock(char mapPos) {
        switch (mapPos) {
            case 'A':
            case 'O':
            case '*':
                return true;
            default:
                return false;
        }
    }

    public boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState self = tile.getBlockState();
        IBlockState other = WorldPlugin.getBlockState(theWorldAsserted(), pos);
        switch (mapPos) {
            case 'O': // Other
                if (self == other)
                    return false;
                break;
            case 'W': // Window
            case 'B': // Block
                if (self != other)
                    return false;
                break;
            case 'A': // Air
                if (!other.getBlock().isAir(other, theWorldAsserted(), pos))
                    return false;
                break;
            case '*': // Anything
                return true;
        }
        return true;
    }

    private void testPatterns() {
        patternStates.clear();
        // This specifically tests all patterns in order to provide complete data to the MagGlass
        patterns.forEach(map -> patternStates.put(map.testPattern(this), map));
    }

    public void onBlockChange() {
        spreadChange(getMaxRecursionDepth());
    }

    private void spreadChange(int depth) {
        for (EnumFacing side : EnumFacing.VALUES) {
            tile.getTileCache().onSide(side)
                    .flatMap(tileToLogic())
                    .ifPresent(l -> l.markChange(getMaxRecursionDepth()));
        }
    }

    private void markChange(int depth) {
        depth--;
        if (depth < 0)
            return;
        if (state != StructureState.UNTESTED) {
            state = StructureState.UNTESTED;

            getMasterLogic().ifPresent(StructureLogic::onBlockChange);

            spreadChange(depth);
        }
    }

    protected boolean canMatch(StructureLogic logic) {
        return logic.structureKey.equals(structureKey);
    }

    private Function<TileEntity, Optional<StructureLogic>> tileToLogic() {
        return t -> Optional.of(t)
                .flatMap(Optionals.toType(ILogicContainer.class))
                .flatMap(c -> c.getLogic(StructureLogic.class))
                .filter(this::canMatch);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        data = logic.writeToNBT(data);
        data.setBoolean("master", isMaster);
        data.setByte("pattern", getPatternIndex());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        logic.readFromNBT(data);
        isMaster = data.getBoolean("master");
        try {
            byte index = data.getByte("pattern");
            currentPattern = index < 0 ? null : patterns.get(index);
        } catch (Exception ex) {
            //NOOP
        }
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        data.writeEnum(state);
        if (state == StructureState.VALID) {
            data.writeByte(getPatternIndex());
            data.writeBlockPos(Objects.requireNonNull(posInPattern));
        }
        logic.writePacketData(data);
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        requestPacket = false;
        state = data.readEnum(StructureState.VALUES);
        if (state == StructureState.VALID) {
            int patternIndex = data.readByte();
            patternIndex = MathHelper.clamp(patternIndex, 0, patterns.size() - 1);
            MultiBlockPattern pat = patterns.get(patternIndex);

            BlockPos posInPattern = data.readBlockPos();
            setPattern(pat, posInPattern);

            isMaster = pat.isMasterPosition(posInPattern);

            // TODO is this still necessary?
            if (!getMasterLogic().isPresent())
                requestPacket = true;
        } else {
            isMaster = false;
            setPattern(null, null);
        }

        logic.readPacketData(data);
    }

    public final boolean isValidMaster() {
        return isMaster && state == StructureState.VALID && !tile.isInvalid();
    }

    public final void scheduleMasterRetest() {
        if (Game.isClient(theWorldAsserted()))
            return;
        getMasterLogic().ifPresent(m -> m.state = StructureState.UNTESTED);
    }

    public final boolean isStructureValid() {
        return getMasterLogic().isPresent();
    }

    public final Optional<StructureLogic> getMasterLogic() {
        if (masterPos != null) {
            return WorldPlugin.getTileEntity(theWorldAsserted(), masterPos, ILogicContainer.class, true)
                    .flatMap(t -> t.getLogic(StructureLogic.class))
                    .filter(StructureLogic::isValidMaster);
        }
        return Optional.empty();
    }

    public List<? extends MultiBlockPattern> getPatterns() {
        return patterns;
    }

    public enum StructureState {

        VALID, INVALID, UNKNOWN, UNTESTED;

        static final StructureState[] VALUES = values();
    }
}
