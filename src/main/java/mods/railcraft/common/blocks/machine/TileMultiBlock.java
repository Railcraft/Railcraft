/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.machine;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.inventory.wrappers.InventoryMapper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.common.util.misc.Game;
import mods.railcraft.common.util.misc.MiscTools;
import mods.railcraft.common.util.misc.Timer;
import mods.railcraft.common.util.network.PacketDispatcher;
import mods.railcraft.common.util.network.PacketTileRequest;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.IInventory;

public abstract class TileMultiBlock extends TileMachineBase {
    private static final int UNKNOWN_STATE_RECHECK = 256;
    private static final int NETWORK_RECHECK = 64;
    private final Timer netTimer = new Timer();
    private final List<? extends MultiBlockPattern> patterns;
    private final List<TileEntity> components = new LinkedList<TileEntity>();
    private final List<TileEntity> componentsImmutable = Collections.unmodifiableList(components);
    public ListMultimap<MultiBlockStateReturn, Integer> patternStates = ArrayListMultimap.create();
    protected boolean isMaster;
    private byte patternX;
    private byte patternY;
    private byte patternZ;
    private boolean tested;
    private boolean requestPacket;
    private MultiBlockState state;
    private TileMultiBlock masterBlock;
    private MultiBlockPattern currentPattern;
    private UUID uuidMaster;
    public TileMultiBlock(List<? extends MultiBlockPattern> patterns) {
        this.patterns = patterns;
        currentPattern = patterns.get(0);
        tested = FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER;
    }

    public UUID getMasterUUID() {
        return uuidMaster;
    }

    public List<TileEntity> getComponents() {
        return componentsImmutable;
    }

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
            InvTools.dropInventory(new InventoryMapper((IInventory) this), worldObj, xCoord, yCoord, zCoord);
    }

    public final char getPatternMarker() {
        if (currentPattern == null || !isStructureValid())
            return 'O';
        return currentPattern.getPatternMarker(patternX, patternY, patternZ);
    }

    public final int getPatternPositionX() {
        return patternX;
    }

    public final int getPatternPositionY() {
        return patternY;
    }

    public final int getPatternPositionZ() {
        return patternZ;
    }

    private void setPatternPosition(byte x, byte y, byte z) {
        patternX = x;
        patternY = y;
        patternZ = z;
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

    protected int getMaxRecursionDepth() {
        return 12;
    }

    public MultiBlockState getState() {
        return state;
    }

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (Game.isHost(worldObj)) {
            if (!tested && (state != MultiBlockState.UNKNOWN || clock % UNKNOWN_STATE_RECHECK == 0))
                testIfMasterBlock(); //                ClientProxy.getMod().totalMultiBlockUpdates++;
        } else if (requestPacket && netTimer.hasTriggered(worldObj, NETWORK_RECHECK)) {
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

            int xOffset = xCoord - currentPattern.getMasterOffsetX();
            int yOffset = yCoord - currentPattern.getMasterOffsetY();
            int zOffset = zCoord - currentPattern.getMasterOffsetZ();

            for (byte px = 0; px < xWidth; px++) {
                for (byte py = 0; py < height; py++) {
                    for (byte pz = 0; pz < zWidth; pz++) {

                        char marker = currentPattern.getPatternMarker(px, py, pz);
                        if (isMapPositionOtherBlock(marker))
                            continue;

                        int x = px + xOffset;
                        int y = py + yOffset;
                        int z = pz + zOffset;

                        TileEntity tile = worldObj.getTileEntity(x, y, z);
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
        } else if (isMaster) {
            isMaster = false;
            onMasterReset();
            sendUpdateToClient();
        }
    }

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

    protected boolean isMapPositionValid(int x, int y, int z, char mapPos) {
        Block block = WorldPlugin.getBlock(worldObj, x, y, z);
        switch (mapPos) {
            case 'O': // Other
                if (block == getBlockType() && worldObj.getBlockMetadata(x, y, z) == getBlockMetadata())
                    return false;
                break;
            case 'W': // Window
            case 'B': // Block
                if (block != getBlockType() || worldObj.getBlockMetadata(x, y, z) != getBlockMetadata())
                    return false;
                break;
            case 'A': // Air
                if (!worldObj.isAirBlock(x, y, z))
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

        int xOffset = xCoord - map.getMasterOffsetX();
        int yOffset = yCoord - map.getMasterOffsetY();
        int zOffset = zCoord - map.getMasterOffsetZ();

        for (int patX = 0; patX < xWidth; patX++) {
            for (int patY = 0; patY < height; patY++) {
                for (int patZ = 0; patZ < zWidth; patZ++) {
                    int x = patX + xOffset;
                    int y = patY + yOffset;
                    int z = patZ + zOffset;
                    if (!worldObj.blockExists(x, y, z))
                        return MultiBlockStateReturn.NOT_LOADED;
                    if (!isMapPositionValid(x, y, z, map.getPatternMarker(patX, patY, patZ)))
                        return MultiBlockStateReturn.PATTERN_DOES_NOT_MATCH;
                }
            }
        }

        AxisAlignedBB entityCheckBounds = map.getEntityCheckBounds(xCoord, yCoord, zCoord);
//                if(entityCheckBounds != null) {
//                    System.out.println("test entitys: " + entityCheckBounds.toString());
//                }
        if (entityCheckBounds != null && !worldObj.getEntitiesWithinAABB(EntityLivingBase.class, entityCheckBounds).isEmpty())
            return MultiBlockStateReturn.ENTITY_IN_WAY;
        return MultiBlockStateReturn.VALID;
    }

    @Override
    public void onBlockAdded() {
        super.onBlockAdded();
        if (Game.isNotHost(worldObj)) return;
        onBlockChange();
    }

    @Override
    public void onBlockRemoval() {
        super.onBlockRemoval();
        if (Game.isNotHost(worldObj)) return;
        onBlockChange();
        isMaster = false;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (Game.isNotHost(worldObj)) return;
        tested = false;
        scheduleMasterRetest();
    }

    @Override
    public void invalidate() {
        if (worldObj == null || Game.isHost(worldObj)) {
            tested = false;
            scheduleMasterRetest();
        }
        super.invalidate();
    }

    private void onBlockChange() {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
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

            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                TileEntity tile = tileCache.getTileOnSide(side);
                if (isStructureTile(tile))
                    ((TileMultiBlock) tile).onBlockChange(depth);
            }
        }
    }

    protected boolean isStructureTile(TileEntity tile) {
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
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);

        data.setBoolean("master", isMaster);
        data.setByte("pattern", getPatternIndex());

        MiscTools.writeUUID(data, "uuidMaster", uuidMaster);
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

        uuidMaster = MiscTools.readUUID(data, "uuidMaster");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        boolean hasMaster = getMasterBlock() != null;
        data.writeBoolean(hasMaster);
        if (hasMaster) {
            byte patternIndex = getPatternIndex();
            data.writeByte(patternIndex);

            data.writeByte(patternX);
            data.writeByte(patternY);
            data.writeByte(patternZ);
        }
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
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

            if (patternX != pX || patternY != pY || patternZ != pZ) {
                patternX = pX;
                patternY = pY;
                patternZ = pZ;
                needsRenderUpdate = true;
            }

            isMaster = pX == pat.getMasterOffsetX() && pY == pat.getMasterOffsetY() && pZ == pat.getMasterOffsetZ();

            setPattern(pat);

            int masterX = pat.getMasterRelativeX(xCoord, pX);
            int masterY = pat.getMasterRelativeY(yCoord, pY);
            int masterZ = pat.getMasterRelativeZ(zCoord, pZ);

            TileEntity tile = null;
            if (worldObj != null)
                tile = worldObj.getTileEntity(masterX, masterY, masterZ);
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

    public final boolean isMaster() {
        return isMaster;
    }

    public final void setMaster(boolean m) {
        isMaster = m;
    }

    public final void scheduleMasterRetest() {
        if (Game.isNotHost(worldObj))
            return;
        if (masterBlock != null)
            masterBlock.tested = false;
    }

    public final boolean isStructureValid() {
        return masterBlock != null && masterBlock.tested && masterBlock.isMaster && !masterBlock.isInvalid();
    }

    public final TileMultiBlock getMasterBlock() {
        if (masterBlock != null && !isStructureValid()) {
            masterBlock = null;
            sendUpdateToClient();
        }
        return masterBlock;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type) {
        return (isStructureValid() && getPatternPositionY() < 2) ? false : super.canCreatureSpawn(type);
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
