/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2018
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.blocks.multi;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import mods.railcraft.common.blocks.ISmartTile;
import mods.railcraft.common.blocks.RailcraftTickingTileEntity;
import mods.railcraft.common.events.MultiBlockEvent.Form;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;
import java.util.*;

public abstract class TileMultiBlock extends RailcraftTickingTileEntity implements ISmartTile, IMultiBlockTile {

    private static final int UNKNOWN_STATE_RECHECK = 256;
    private static final int NETWORK_RECHECK = 16;
    private final Timer netTimer = new Timer();
    private final List<? extends MultiBlockPattern> patterns;
    private final List<TileMultiBlock> components = new ArrayList<>();
    private final List<TileMultiBlock> componentsView = Collections.unmodifiableList(components);
    public final ListMultimap<MultiBlockStateReturn, Integer> patternStates = Multimaps.newListMultimap(new EnumMap<>(MultiBlockStateReturn.class), ArrayList::new);
    protected boolean isMaster;
    private BlockPos posInPattern = new BlockPos(0, 0, 0);
    private boolean tested;
    private boolean requestPacket;
    private MultiBlockState state;
    private @Nullable TileMultiBlock masterBlock;
    private MultiBlockPattern currentPattern;
    private @Nullable UUID uuidMaster;

    protected TileMultiBlock(List<? extends MultiBlockPattern> patterns) {
        this.patterns = patterns;
        currentPattern = patterns.get(0);
        tested = FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER;
    }

    public @Nullable UUID getMasterUUID() {
        return uuidMaster;
    }

    public List<TileMultiBlock> getComponents() {
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

    protected void onPatternLock(MultiBlockPattern pattern) {
    }

    protected void onPatternChanged() {
        if (!isMaster && this instanceof IInventory)
            InvTools.dropInventory(new InventoryMapper((IInventory) this), world, getPos());
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

    public final MultiBlockPattern getPattern() {
        return currentPattern;
    }

    public final void setPattern(MultiBlockPattern pattern) {
        if (currentPattern != pattern)
            onPatternChanged();
        this.currentPattern = pattern;
        onPatternLock(pattern);
    }

    public final byte getPatternIndex() {
        return (byte) patterns.indexOf(currentPattern);
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
            if (!tested && (state != MultiBlockState.UNKNOWN || clock % UNKNOWN_STATE_RECHECK == 0))
                testIfMasterBlock(); //                ClientProxy.getMod().totalMultiBlockUpdates++;
        } else if (requestPacket && netTimer.hasTriggered(world, NETWORK_RECHECK)) {
            PacketDispatcher.sendToServer(new PacketTileRequest(this));
            requestPacket = false;
        }
    }

    private void testIfMasterBlock() {
//        System.out.println("testing structure");
        state = getMasterBlockState();
        tested = true;
        components.clear();

        if (state == MultiBlockState.UNKNOWN)
            tested = false;
        else if (state == MultiBlockState.VALID) {
            isMaster = true;
//             System.out.println("structure complete");

            int xWidth = currentPattern.getPatternWidthX();
            int zWidth = currentPattern.getPatternWidthZ();
            int height = currentPattern.getPatternHeight();

            BlockPos offset = getPos().subtract(currentPattern.getMasterOffset());

            for (int px = 0; px < xWidth; px++) {
                for (int py = 0; py < height; py++) {
                    for (int pz = 0; pz < zWidth; pz++) {

                        char marker = currentPattern.getPatternMarker(px, py, pz);
                        if (isMapPositionOtherBlock(marker))
                            continue;

                        BlockPos pos = new BlockPos(px, py, pz).add(offset);

                        TileEntity tile = world.getTileEntity(pos);
                        if (tile instanceof TileMultiBlock) {
                            TileMultiBlock multiBlock = (TileMultiBlock) tile;
                            if (multiBlock != this)
                                multiBlock.components.clear();
                            components.add(multiBlock);
                            multiBlock.tested = true;
                            multiBlock.setMaster(this);
                            multiBlock.setPattern(currentPattern);
                            multiBlock.setPatternPosition(px, py, pz);
                            multiBlock.sendUpdateToClient();
                        }
                    }
                }
            }

            MinecraftForge.EVENT_BUS.post(new Form(this));
        } else if (isMaster) {
            isMaster = false;
            onMasterReset();
            sendUpdateToClient();
        }
    }

    @OverridingMethodsMustInvokeSuper
    protected void onMasterReset() {
        components.clear();
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

    private MultiBlockState getMasterBlockState() {
        MultiBlockState endResult = MultiBlockState.INVALID;
        patternStates.clear();
        for (MultiBlockPattern map : patterns) {
            MultiBlockStateReturn result = isPatternValid(map);
            patternStates.put(result, patterns.indexOf(map));
            switch (result.type) {
                case VALID:
                    setPattern(map);
                    return result.type;
                case UNKNOWN:
                    endResult = MultiBlockState.UNKNOWN;
            }
        }

        return endResult;
    }

    private MultiBlockStateReturn isPatternValid(MultiBlockPattern map) {
        int xWidth = map.getPatternWidthX();
        int zWidth = map.getPatternWidthZ();
        int height = map.getPatternHeight();

        BlockPos offset = getPos().subtract(map.getMasterOffset());

        BlockPos.PooledMutableBlockPos now = BlockPos.PooledMutableBlockPos.retain();
        for (int patX = 0; patX < xWidth; patX++) {
            for (int patY = 0; patY < height; patY++) {
                for (int patZ = 0; patZ < zWidth; patZ++) {
                    int x = patX + offset.getX();
                    int y = patY + offset.getY();
                    int z = patZ + offset.getZ();
                    now.setPos(x, y, z);
                    if (!world.isBlockLoaded(now))
                        return MultiBlockStateReturn.NOT_LOADED;
                    if (!isMapPositionValid(now, map.getPatternMarker(patX, patY, patZ)))
                        return MultiBlockStateReturn.PATTERN_DOES_NOT_MATCH;
                }
            }
        }
        now.release();

        AxisAlignedBB entityCheckBounds = map.getEntityCheckBounds(getPos());
//                if(entityCheckBounds != null) {
//                    System.out.println("test entities: " + entityCheckBounds.toString());
//                }
        if (entityCheckBounds != null && !world.getEntitiesWithinAABB(EntityLivingBase.class, entityCheckBounds).isEmpty())
            return MultiBlockStateReturn.ENTITY_IN_WAY;
        return MultiBlockStateReturn.VALID;
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
    public void onChunkUnload() {
        super.onChunkUnload();
        if (Game.isClient(world)) return;
        tested = false;
        scheduleMasterRetest();
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void invalidate() {
        if (world == null || Game.isHost(world)) {
            tested = false;
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
        if (tested) {
            tested = false;

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
            currentPattern = patterns.get(data.getByte("pattern"));
        } catch (Exception ex) {
            //NOOP
        }

        uuidMaster = NBTPlugin.readUUID(data, "uuidMaster");
    }

    @Override
    public void writePacketData(RailcraftOutputStream data) throws IOException {
        super.writePacketData(data);
        boolean hasMaster = getMasterBlock() != null;
        data.writeBoolean(hasMaster);
        if (hasMaster) {
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

        boolean hasMaster = data.readBoolean();
        if (hasMaster) {
            byte patternIndex = data.readByte();
            patternIndex = (byte) Math.max(patternIndex, 0);
            patternIndex = (byte) Math.min(patternIndex, patterns.size() - 1);
            MultiBlockPattern pat = patterns.get(patternIndex);

            byte pX = data.readByte();
            byte pY = data.readByte();
            byte pZ = data.readByte();

//            System.out.println("HasMaster :" + pX + ", " + pY + ", " + pZ);

            if (posInPattern == null || posInPattern.getX() != pX || posInPattern.getY() != pY || posInPattern.getZ() != pZ) {
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
        } else if (masterBlock != null) {
            needsRenderUpdate = true;
            masterBlock = null;
            isMaster = false;
        }

        if (needsRenderUpdate)
            markBlockForUpdate();

//        System.out.printf("marker=%c, pattern=%d, x=%d, y=%d, z=%d%n", currentPattern.getPatternMarkerChecked(patternX, patternY, patternZ), patternIndex, patternX, patternY, patternZ);
//        if(masterBlock != null)
//        System.out.printf("tested=%b, invalid=%b, isMaster=%b%n" ,masterBlock.tested, masterBlock.isInvalid(), masterBlock.isMaster());
    }

    @Override
    public final boolean isMaster() {
        return isMaster;
    }

    public final void setMaster(boolean m) {
        isMaster = m;
    }

    public final void scheduleMasterRetest() {
        if (Game.isClient(world))
            return;
        if (masterBlock != null)
            masterBlock.tested = false;
    }

    @Override
    public final boolean isStructureValid() {
        return masterBlock != null && masterBlock.tested && masterBlock.isMaster && !masterBlock.isInvalid();
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
    public boolean openGui(EntityPlayer player) {
        TileMultiBlock masterBlock = getMasterBlock();

        if (masterBlock != null && isStructureValid() && masterBlock.getGui() != null) {
            GuiHandler.openGui(masterBlock.getGui(), player, world, masterBlock.getPos());
            return true;
        }
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EntityLiving.SpawnPlacementType type) {
        return (!(isStructureValid() && getPatternPosition().getY() < 2));
    }

    @Override
    public MultiBlockPattern getCurrentPattern() {
        return currentPattern;
    }

    @Override
    public Collection<? extends MultiBlockPattern> getPatterns() {
        return patterns;
    }

    public enum MultiBlockState {

        VALID, INVALID, UNKNOWN
    }

    public enum MultiBlockStateReturn {

        VALID(MultiBlockState.VALID, "railcraft.multiblock.state.valid"),
        ENTITY_IN_WAY(MultiBlockState.INVALID, "railcraft.multiblock.state.invalid.entity"),
        PATTERN_DOES_NOT_MATCH(MultiBlockState.INVALID, "railcraft.multiblock.state.invalid.pattern"),
        NOT_LOADED(MultiBlockState.UNKNOWN, "railcraft.multiblock.state.unknown.unloaded");
        public final MultiBlockState type;
        public final String message;

        MultiBlockStateReturn(MultiBlockState type, String msg) {
            this.type = type;
            this.message = msg;
        }

    }
}
