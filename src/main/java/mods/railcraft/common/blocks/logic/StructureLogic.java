/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.blocks.logic;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import mods.railcraft.common.blocks.TileLogic;
import mods.railcraft.common.blocks.TileRailcraft;
import mods.railcraft.common.blocks.interfaces.IDropsInv;
import mods.railcraft.common.blocks.structures.StructurePattern;
import mods.railcraft.common.events.MultiBlockEvent;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.collections.Streams;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Optionals;
import mods.railcraft.common.util.misc.Timer;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileRequest;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by CovertJaguar on 12/17/2018 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class StructureLogic extends Logic {
    private static final int RECHECK = 40;
    private final Timer resetTimer = new Timer();
    private final TileLogic tile;
    private final String structureKey;
    public final Logic functionalLogic;
    private final List<? extends StructurePattern> patterns;
    private final int maxSize;
    private final List<TileLogic> components = new ArrayList<>();
    private final List<TileLogic> componentsView = Collections.unmodifiableList(components);
    public final ListMultimap<StructurePattern.State, StructurePattern> patternStates = Multimaps.newListMultimap(new EnumMap<>(StructurePattern.State.class), ArrayList::new);
    protected boolean isMaster;
    private boolean requestPacket;
    private StructureState state = StructureState.UNTESTED;
    private @Nullable BlockPos masterPos;
    private @Nullable StructurePattern currentPattern;
    private @Nullable BlockPos posInPattern;
    private char marker = 'O';
    private boolean updatingNeighbors;

    public StructureLogic(String structureKey, TileLogic tile, List<? extends StructurePattern> patterns, Logic functionalLogic) {
        super(Adapter.of(tile));
        this.structureKey = structureKey;
        this.tile = tile;
        this.patterns = patterns;
        this.maxSize = patterns.stream().mapToInt(StructurePattern::getPatternSize).max().orElse(7 * 7 * 7);

        // Note we don't set the parent.
        // So getLogic() calls from the functional logic can only see the functional logic tree.
        this.functionalLogic = functionalLogic;
        components.add(tile);
    }

    @Override
    public <L> Optional<L> getLogic(Class<L> logicClass) {
        Optional<L> imp = super.getLogic(logicClass);
        if (imp.isPresent())
            return imp;
        return getMasterLogic(logicClass);
    }

    public <L> Optional<L> getMasterLogic(Class<L> logicClass) {
        return getMasterLogic().map(m -> m.functionalLogic).flatMap(l -> l.getLogic(logicClass));
    }

    public final Optional<StructureLogic> getMasterLogic() {
        if (masterPos != null) {
            return WorldPlugin.getTileEntity(theWorldAsserted(), masterPos, ILogicContainer.class, true)
                    .flatMap(t -> t.getLogic(StructureLogic.class))
                    .filter(StructureLogic::isValidMaster);
        }
        return Optional.empty();
    }

    public final <L> Optional<L> getFunctionalLogic(Class<L> logicClass) {
        return functionalLogic.getLogic(logicClass);
    }

    public List<TileLogic> getComponents() {
        return getMasterLogic().map(m -> m.componentsView).orElseGet(Collections::emptyList);
    }

    public final char getPatternMarker() {
        return marker;
    }

    public final @Nullable BlockPos getPatternPosition() {
        return posInPattern;
    }

    public final @Nullable StructurePattern getPattern() {
        return currentPattern;
    }

    private void setPattern(@Nullable StructurePattern pattern, @Nullable BlockPos posInPattern) {
        this.currentPattern = pattern;
        if (!Objects.equals(this.posInPattern, posInPattern)) {
            this.posInPattern = posInPattern == null ? null : posInPattern.toImmutable();
        }

        if (currentPattern == null || posInPattern == null) {
            this.masterPos = null;
            marker = 'O';
        } else {
            this.masterPos = currentPattern.getMasterPosition(getPos(), this.posInPattern);
            marker = currentPattern.getPatternMarker(this.posInPattern);
        }

        // Possible side effects?
        if (masterPos == null)
            state = StructureState.INVALID;
        else
            state = StructureState.VALID;

        onPatternChanged();
        sendUpdateToClient();
    }

    private void onPatternChanged() {
        updatingNeighbors = true;
        adapter.updateModels();
        if (theWorld() == null || !Game.isHost(theWorldAsserted())) return;
        if (!isMaster) {
            functionalLogic.getLogic(IDropsInv.class).ifPresent(i -> i.spewInventory(theWorldAsserted(), getPos()));
        }
        boolean isComplete = getPattern() != null;
        Object[] attachedData = isComplete ? getPattern().getAttachedData() : new Object[]{};
        onStructureChanged(isComplete, isMaster, attachedData);
        if (isMaster)
            functionalLogic.onStructureChanged(isComplete, isMaster, attachedData);
        tile.notifyBlocksOfNeighborChange();
        updatingNeighbors = false;
    }

    public boolean isUpdatingNeighbors() {
        return updatingNeighbors;
    }

    public final byte getPatternIndex() {
        return (byte) patterns.indexOf(currentPattern);
    }

    public final @Nullable BlockPos getMasterPos() {
        return masterPos;
    }

    public StructureState getState() {
        return state;
    }

    @Override
    public void update() {
        super.update();
        if (isValidMaster())
            functionalLogic.update();
    }

    @Override
    protected void updateClient() {
        if (requestPacket && resetTimer.hasTriggered(theWorldAsserted(), RECHECK)) {
            PacketDispatcher.sendToServer(new PacketTileRequest(tile));
            requestPacket = false;
        }
    }

    @Override
    protected void updateServer() {
        if (state == StructureState.UNKNOWN && resetTimer.hasTriggered(theWorldAsserted(), RECHECK)) {
            state = StructureState.UNTESTED;
        }
        if (state == StructureState.UNTESTED)
            testIfMasterBlock();
        //                ClientProxy.getMod().totalMultiBlockUpdates++;
    }

    protected void testIfMasterBlock() {
//        System.out.println("testing structure");
        testPatterns();

        List<TileRailcraft> old = new ArrayList<>(components);

        components.clear();
        components.add(tile);

        if (patternStates.containsKey(StructurePattern.State.VALID)) {
            state = StructureState.VALID;
            isMaster = true;
//             System.out.println("structure complete");

            StructurePattern pattern = patternStates.get(StructurePattern.State.VALID).get(0);

            int xWidth = pattern.getPatternWidthX();
            int zWidth = pattern.getPatternWidthZ();
            int height = pattern.getPatternHeight();

            BlockPos offset = getPos().subtract(pattern.getMasterOffset());

            Map<BlockPos, StructureLogic> newComponents = new HashMap<>();

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
                                    components.add(l.tile);
                                    newComponents.put(patternPos, l);
                                });
                    }
                }
            }
            newComponents.forEach((pos, logic) -> logic.setPattern(pattern, pos));

            MinecraftForge.EVENT_BUS.post(new MultiBlockEvent.Form(tile));
        } else if (patternStates.containsKey(StructurePattern.State.NOT_LOADED)) {
            state = StructureState.UNKNOWN;
        } else {
            state = StructureState.INVALID;
            functionalLogic.getLogic(IDropsInv.class).ifPresent(i -> i.spewInventory(theWorldAsserted(), getPos()));
            if (isMaster) {
                isMaster = false;
                onMasterReset();
                sendUpdateToClient();
            }
        }

        old.removeAll(components);
        old.stream().filter(t -> !t.isInvalid()).map(tileToLogic())
                .flatMap(Streams.unwrap()).forEach(t -> t.setPattern(null, null));
    }

    protected void onMasterReset() {
    }

    public boolean isMapPositionOtherBlock(char mapPos) {
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

    public boolean isPart(Block block) {
        return block == tile.getBlockType();
    }

    private void testPatterns() {
        patternStates.clear();
        // This specifically tests all patterns in order to provide complete data to the MagGlass
        patterns.forEach(map -> patternStates.put(map.testPattern(this), map));
    }

    public void onBlockChange() {
        Optional<StructureLogic> masterLogic = getMasterLogic();
        spreadChange(theWorldAsserted(), new HashSet<>(maxSize), getPos(), this::isPart, maxSize);
        masterLogic.ifPresent(master -> master.getComponents().forEach(t -> t.getLogic(StructureLogic.class).ifPresent(s -> s.state = StructureState.UNTESTED)));
    }

    private static void spreadChange(World world, Set<BlockPos> visited, BlockPos pos, Predicate<Block> isPart, int max) {
        if (visited.size() > max || visited.contains(pos))
            return;
        visited.add(pos);
        if (visited.size() == 1 || isPart.test(WorldPlugin.getBlock(world, pos))) {
            WorldPlugin.getTileEntity(world, pos, TileLogic.class).ifPresent(tile -> {
                tile.getLogic(StructureLogic.class).ifPresent(logic -> {
                    if (logic.state != StructureState.UNTESTED) {
                        logic.state = StructureState.UNTESTED;
                        logic.getMasterLogic().ifPresent(master -> spreadChange(world, visited, master.getPos(), isPart, max));
                    }
                });
            });
            for (EnumFacing side : EnumFacing.VALUES) {
                spreadChange(world, visited, pos.offset(side), isPart, max);
            }
        }
    }

    protected boolean canMatch(StructureLogic logic) {
        return logic.structureKey.equals(structureKey);
    }

    private Function<TileEntity, Optional<StructureLogic>> tileToLogic() {
        return t -> Optional.of(t)
                .map(Optionals.toType(ILogicContainer.class))
                .flatMap(c -> c.getLogic(StructureLogic.class))
                .filter(this::canMatch);
    }

    @Override
    public void placed(IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
        super.placed(state, placer, stack);
        functionalLogic.placed(state, placer, stack);
    }

    @Override
    public boolean interact(EntityPlayer player, EnumHand hand) {
        return getMasterLogic().map(m -> m.functionalLogic.interact(player, hand)).orElse(false) || super.interact(player, hand);
    }

    @Override
    public @Nullable EnumGui getGUI() {
        return functionalLogic.getGUI();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        functionalLogic.writeToNBT(data);
        data.setBoolean("master", isMaster);
        data.setString("marker", String.valueOf(marker));
        data.setByte("pattern", getPatternIndex());
        if (posInPattern != null)
            NBTPlugin.writeBlockPos(data, "posInPattern", posInPattern);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        functionalLogic.readFromNBT(data);
        isMaster = data.getBoolean("master");
        if (data.hasKey("marker"))
            marker = data.getString("marker").charAt(0);
        StructurePattern pat = null;
        try {
            byte index = data.getByte("pattern");
            pat = index < 0 ? null : patterns.get(index);
        } catch (Exception ex) {
            //NOOP
        }
        BlockPos pos = NBTPlugin.readBlockPos(data, "posInPattern");
        setPattern(pat, pos);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        data.writeEnum(state);
        data.writeChar(marker);
        if (state == StructureState.VALID) {
            data.writeByte(getPatternIndex());
            data.writeBlockPos(Objects.requireNonNull(posInPattern));
        }
        super.writePacketData(data);
        functionalLogic.writePacketData(data);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void readPacketData(RailcraftInputStream data) throws IOException {
        requestPacket = false;
        state = data.readEnum(StructureState.VALUES);
        marker = data.readChar();
        if (state == StructureState.VALID) {
            int patternIndex = data.readByte();
            patternIndex = MathHelper.clamp(patternIndex, 0, patterns.size() - 1);
            StructurePattern pat = patterns.get(patternIndex);

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

        super.readPacketData(data);
        functionalLogic.readPacketData(data);
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

    public List<? extends StructurePattern> getPatterns() {
        return patterns;
    }

    public enum StructureState {

        VALID, INVALID, UNKNOWN, UNTESTED;

        static final StructureState[] VALUES = values();
    }
}
