/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2020
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.structures;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.TileRailcraftTicking;
import mods.railcraft.common.events.MultiBlockEvent.Form;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.gui.GuiHandler;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.Timer;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileRequest;
import mods.railcraft.common.util.network.RailcraftInputStream;
import mods.railcraft.common.util.network.RailcraftOutputStream;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.*;

public abstract class TileMultiBlock extends TileRailcraftTicking implements ISmartTile, IMultiBlockTile {

    //    private static final int UNKNOWN_STATE_RECHECK = 256;
    private static final int NETWORK_RECHECK = 16;
    private final Timer netTimer = new Timer();
    private final List<? extends StructurePattern> patterns;
    private final List<TileMultiBlock> components = new ArrayList<>();
    private final List<TileMultiBlock> componentsView = Collections.unmodifiableList(components);
    public final ListMultimap<StructurePattern.State, StructurePattern> patternStates = Multimaps.newListMultimap(new EnumMap<>(StructurePattern.State.class), ArrayList::new);
    protected boolean isMaster;
    private BlockPos posInPattern = new BlockPos(0, 0, 0);
    private boolean requestPacket;
    private MultiBlockState state;
    private @Nullable TileMultiBlock masterBlock;
    private @Nullable StructurePattern currentPattern;
    private @Nullable UUID uuidMaster;

    protected TileMultiBlock(List<? extends StructurePattern> patterns) {
        this.patterns = patterns;
        currentPattern = null;
        state = MultiBlockState.UNTESTED;
        components.add(this);
    }

    public @Nullable UUID getMasterUUID() {
        return uuidMaster;
    }

    public List<TileMultiBlock> getComponents() {
        TileMultiBlock mBlock = getMasterBlock();
        if (mBlock != null)
            return mBlock.componentsView;
        return componentsView;
    }

    @OverridingMethodsMustInvokeSuper
    protected void onMasterChanged() {
    }

    private void setMaster(TileMultiBlock master) {
        this.masterBlock = master;

        if (uuidMaster != null && !uuidMaster.equals(master.getUUID()))
            onMasterChanged();

        this.uuidMaster = master.getUUID();
    }

    protected void onPatternLock(StructurePattern pattern) {
    }

    protected void onPatternChanged() {
        if (!isMaster && this instanceof IInventory)
            InvTools.spewInventory(InventoryMapper.make((IInventory) this), world, getPos());
    }

    public final char getPatternMarker() {
        if (currentPattern == null || !isStructureValid())
            return 'O';
        return currentPattern.getPatternMarker(posInPattern.getX(), posInPattern.getY(), posInPattern.getZ());
    }

    @Override
    public final BlockPos getPatternPosition() {
        return posInPattern;
    }

    private void setPatternPosition(int x, int y, int z) {
        posInPattern = new BlockPos(x, y, z);
    }

    public final @Nullable StructurePattern getPattern() {
        assert !(isStructureValid() && (currentPattern == null));
        return currentPattern;
    }

    public final void setPattern(@Nullable StructurePattern pattern) {
        if (Game.isHost(world) && currentPattern != pattern) {
            onPatternChanged();
        }
        this.currentPattern = pattern;
    }

    public final byte getPatternIndex() {
        return currentPattern == null ? -1 : (byte) patterns.indexOf(currentPattern);
    }

    public final BlockPos getMasterPos() {
        TileMultiBlock mBlock = getMasterBlock();
        return mBlock == null ? pos : mBlock.pos;
    }

    protected int getMaxRecursionDepth() {
        return 12;
    }

    @Override
    public MultiBlockState getState() {
        return state;
    }

    @Override
    public void update() {
        super.update();
        if (Game.isHost(world)) {
            if (state == MultiBlockState.UNTESTED
//                    || (state == MultiBlockState.UNKNOWN && clock % UNKNOWN_STATE_RECHECK == 0)
            )
                testIfMasterBlock(); //                ClientProxy.getMod().totalMultiBlockUpdates++;
        } else if (requestPacket && netTimer.hasTriggered(world, NETWORK_RECHECK)) {
            PacketDispatcher.sendToServer(new PacketTileRequest(this));
            requestPacket = false;
        }
    }

    private void testIfMasterBlock() {
//        System.out.println("testing structure");
        testPatterns();
        components.clear();
        components.add(this);

        if (patternStates.containsKey(StructurePattern.State.VALID)) {
            state = MultiBlockState.VALID;
            isMaster = true;
//             System.out.println("structure complete");

            StructurePattern pattern = patternStates.get(StructurePattern.State.VALID).get(0);

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

                        BlockPos pos = new BlockPos(px, py, pz).add(offset);

                        TileEntity tile = world.getTileEntity(pos);
                        if (tile instanceof TileMultiBlock) {
                            TileMultiBlock multiBlock = (TileMultiBlock) tile;
                            if (multiBlock != this) {
                                multiBlock.components.clear();
                                components.add(multiBlock);
                            }
                            multiBlock.setMaster(this);
                            multiBlock.state = MultiBlockState.VALID;
                            multiBlock.setPattern(pattern);
                            multiBlock.setPatternPosition(px, py, pz);
                        }
                    }
                }
            }

            components.forEach(tile -> {
                tile.onPatternLock(pattern);
                tile.sendUpdateToClient();
            });

            MinecraftForge.EVENT_BUS.post(new Form(this));
        } else if (patternStates.containsKey(StructurePattern.State.NOT_LOADED)) {
            state = MultiBlockState.UNKNOWN;
        } else {
            state = MultiBlockState.INVALID;
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

    protected boolean isMapPositionValid(BlockPos pos, char mapPos) {
        IBlockState self = getBlockState();
        IBlockState other = WorldPlugin.getBlockState(world, pos);
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
                if (!other.getBlock().isAir(other, world, pos))
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

    @Override
    public void onBlockAdded() {
        if (Game.isClient(world)) return;
        onBlockChange();
    }

    @Override
    public void onBlockRemoval() {
        if (Game.isClient(world)) return;
        onBlockChange();
        isMaster = false;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        onBlockChange();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (Game.isClient(world)) return;
        state = MultiBlockState.UNTESTED;
        scheduleMasterRetest();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void invalidate() {
        if (world == null || Game.isHost(world)) {
            state = MultiBlockState.UNTESTED;
            scheduleMasterRetest();
        }
        super.invalidate();
    }

    private void onBlockChange() {
        for (EnumFacing side : EnumFacing.VALUES) {
            TileEntity tile = tileCache.getTileOnSide(side);
            if (isStructureTile(tile))
                ((TileMultiBlock) tile).onBlockChange(getMaxRecursionDepth());
        }
    }

    private void onBlockChange(int depth) {
        depth--;
        if (depth < 0)
            return;
        if (state != MultiBlockState.UNTESTED) {
            state = MultiBlockState.UNTESTED;

            TileMultiBlock mBlock = getMasterBlock();
            if (mBlock != null) {
                mBlock.onBlockChange(getMaxRecursionDepth());
                return;
            }

            for (EnumFacing side : EnumFacing.VALUES) {
                TileEntity tile = tileCache.getTileOnSide(side);
                if (isStructureTile(tile))
                    ((TileMultiBlock) tile).onBlockChange(depth);
            }
        }
    }

    @Contract("null -> false")
    protected boolean isStructureTile(@Nullable TileEntity tile) {
        return tile != null && tile.getClass() == getClass();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!isMaster) {
            TileMultiBlock mBlock = getMasterBlock();
            if (mBlock != null)
                mBlock.markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("master", isMaster);
        data.setByte("pattern", getPatternIndex());

        NBTPlugin.writeUUID(data, "uuidMaster", uuidMaster);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);

        isMaster = data.getBoolean("master");
        try {
            byte index = data.getByte("pattern");
            currentPattern = index < 0 ? null : patterns.get(index);
        } catch (Exception ex) {
            //NOOP
        }

        uuidMaster = NBTPlugin.readUUID(data, "uuidMaster");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        MultiBlockState state = getState();
        data.writeEnum(state);
        if (state == MultiBlockState.VALID) {
            byte patternIndex = getPatternIndex();
            data.writeByte(patternIndex);

            data.writeByte(posInPattern.getX());
            data.writeByte(posInPattern.getY());
            data.writeByte(posInPattern.getZ());
        }
    }

    @Override
    public void readPacketData(RailcraftInputStream data) throws IOException {
        super.readPacketData(data);

        requestPacket = false;
        boolean needsRenderUpdate = false;

        MultiBlockState state = data.readEnum(MultiBlockState.VALUES);

        if (state == MultiBlockState.VALID) {
            this.state = MultiBlockState.VALID;
            byte patternIndex = data.readByte();
            patternIndex = (byte) Math.max(patternIndex, 0);
            patternIndex = (byte) Math.min(patternIndex, patterns.size() - 1);
            StructurePattern pat = patterns.get(patternIndex);

            byte pX = data.readByte();
            byte pY = data.readByte();
            byte pZ = data.readByte();

//            System.out.println("HasMaster :" + pX + ", " + pY + ", " + pZ);
            byte oldPatternIndex = getPatternIndex();
            if (oldPatternIndex != patternIndex || posInPattern.getX() != pX || posInPattern.getY() != pY || posInPattern.getZ() != pZ) {
                posInPattern = new BlockPos(pX, pY, pZ);
                needsRenderUpdate = true;
            }

            isMaster = pat.isMasterPosition(posInPattern);

            setPattern(pat);

            BlockPos masterPos = pat.getMasterPosition(getPos(), posInPattern);

            TileEntity tile = null;
            if (world != null)
                tile = world.getTileEntity(masterPos);
            if (tile != null)
                if (masterBlock != tile && isStructureTile(tile)) {
                    needsRenderUpdate = true;
                    masterBlock = (TileMultiBlock) tile;
                }
            if (getMasterBlock() == null)
                requestPacket = true;
        } else if (state != getState() || masterBlock != null) {
            needsRenderUpdate = true;
            masterBlock = null;
            isMaster = false;
            this.state = state;
            setPattern(null);
        }

        if (needsRenderUpdate)
            markBlockForUpdate();

//        System.out.printf("marker=%c, pattern=%d, x=%d, y=%d, z=%d%n", currentPattern.getPatternMarkerChecked(patternX, patternY, patternZ), patternIndex, patternX, patternY, patternZ);
//        if(masterBlock != null)
//        System.out.printf("tested=%b, invalid=%b, isMaster=%b%n" ,masterBlock.tested, masterBlock.isInvalid(), masterBlock.isMaster());
    }

    @Override
    public final boolean isValidMaster() {
        return isMaster && isStructureValid();
    }

    public final void scheduleMasterRetest() {
        if (Game.isClient(world))
            return;
        if (masterBlock != null)
            masterBlock.state = MultiBlockState.UNTESTED;
    }

    @Override
    public final boolean isStructureValid() {
        boolean valid = masterBlock != null && masterBlock.state == MultiBlockState.VALID && !masterBlock.isInvalid();
        if (valid) {
            assert masterBlock.isMaster;
            // assert !masterBlock.isInvalid(); May be invalid if the block is suddenly broken by a player, etc.
        }
        return masterBlock != null && masterBlock.state == MultiBlockState.VALID && masterBlock.isMaster && !masterBlock.isInvalid();
    }

    @Override
    public final @Nullable TileMultiBlock getMasterBlock() {
        if (masterBlock != null && !isStructureValid()) {
            masterBlock = null;
            sendUpdateToClient();
        }
        return masterBlock;
    }

    @Override
    public final boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();

        if (isStructureValid() && masterBlock != null) {
            EnumGui gui = masterBlock.getGui();
            if (gui != null)
                GuiHandler.openGui(gui, player, world, masterBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
        return (!(isStructureValid() && getPatternPosition().getY() < 2));
    }

    @Override
    public @Nullable StructurePattern getCurrentPattern() {
        return currentPattern;
    }

    @Override
    public List<? extends StructurePattern> getPatterns() {
        return patterns;
    }

    public enum MultiBlockState {

        VALID, INVALID, UNKNOWN, UNTESTED;

        static final MultiBlockState[] VALUES = values();
    }

}
