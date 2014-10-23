/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.signals;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.List;
import org.apache.logging.log4j.Level;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.items.IActivationBlockingItem;
import mods.railcraft.common.plugins.forge.PowerPlugin;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

public class BlockSignal extends BlockContainer implements IPostConnection {

    public static IIcon[] texturesBox;
    public static IIcon[] texturesLampTop;
    public static IIcon[] texturesLampBottom;
    public static IIcon[] texturesLampBox;
    public static IIcon[] texturesSignalSingle;
    public static IIcon[] texturesSignalDual;
    public static IIcon[] texturesSwitch;
    public static IIcon[] texturesSwitchTarget;
    public static IIcon texturesSwitchLever;
    private final int renderType;

    public BlockSignal(int renderType) {
        super( new MaterialStructure());
        this.renderType = renderType;
        setBlockName("railcraft.signal");
        setStepSound(Block.soundTypeMetal);
        setResistance(50);

        setCreativeTab(CreativeTabs.tabTransport);

        GameRegistry.registerTileEntity(TileBoxController.class, "RCTileStructureControllerBox");
        GameRegistry.registerTileEntity(TileBoxAnalogController.class, "RCTileStructureAnalogControllerBox");
        GameRegistry.registerTileEntity(TileBoxReceiver.class, "RCTileStructureReceiverBox");
        GameRegistry.registerTileEntity(TileBoxCapacitor.class, "RCTileStructureCapacitorBox");
        GameRegistry.registerTileEntity(TileBoxBlockRelay.class, "RCTileStructureSignalBox");
        GameRegistry.registerTileEntity(TileBoxSequencer.class, "RCTileStructureSequencerBox");
        GameRegistry.registerTileEntity(TileBoxInterlock.class, "RCTileStructureInterlockBox");
        GameRegistry.registerTileEntity(TileSwitchMotor.class, "RCTileStructureSwitchMotor");
        GameRegistry.registerTileEntity(TileSwitchLever.class, "RCTileStructureSwitchLever");
        GameRegistry.registerTileEntity(TileSwitchRouting.class, "RCTileStructureSwitchRouting");
        GameRegistry.registerTileEntity(TileSignalDistantSignal.class, "RCTileStructureDistantSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadBlockSignal.class, "RCTileStructureDualHeadBlockSignal");
        GameRegistry.registerTileEntity(TileSignalBlockSignal.class, "RCTileStructureBlockSignal");
        GameRegistry.registerTileEntity(TileSignalDualHeadDistantSignal.class, "RCTileStructureDualHeadDistantSignal");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumSignal type : EnumSignal.getCreativeList()) {
            if (type.isEnabled()) {
                list.add(type.getItem());
            }
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        texturesBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.box", 6);
        texturesLampBox = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.box", 4);
        
        // TODO: insert color blind mode here!
        texturesLampTop = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.top", 4);
        texturesLampBottom = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.lamp.bottom", 4);
        texturesSignalSingle = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.single", 5);
        texturesSignalDual = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.dual", 5);
        texturesSwitch = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.switch", 2);
        texturesSwitchTarget = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:signal.switch.target", 2);
        texturesSwitchLever = iconRegister.registerIcon("railcraft:signal.switch.throw");

        EnumSignal.BOX_BLOCK_RELAY.setIcon(iconRegister.registerIcon("railcraft:signal.box.block.relay"));
        EnumSignal.BOX_CAPACITOR.setIcon(iconRegister.registerIcon("railcraft:signal.box.capacitor"));
        EnumSignal.BOX_CONTROLLER.setIcon(iconRegister.registerIcon("railcraft:signal.box.controller"));
        EnumSignal.BOX_ANALOG_CONTROLLER.setIcon(iconRegister.registerIcon("railcraft:signal.box.analog"));
        EnumSignal.BOX_RECEIVER.setIcon(iconRegister.registerIcon("railcraft:signal.box.receiver"));
        EnumSignal.BOX_SEQUENCER.setIcon(iconRegister.registerIcon("railcraft:signal.box.sequencer"));
        EnumSignal.BOX_INTERLOCK.setIcon(iconRegister.registerIcon("railcraft:signal.box.interlock"));

        EnumSignal.SWITCH_MOTOR.setIcon(iconRegister.registerIcon("railcraft:signal.switch.motor"));
        EnumSignal.SWITCH_LEVER.setIcon(iconRegister.registerIcon("railcraft:signal.switch.lever"));
        EnumSignal.SWITCH_ROUTING.setIcon(iconRegister.registerIcon("railcraft:signal.switch.routing"));
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        EnumSignal type = EnumSignal.fromId(meta);
        switch (type) {
            case DUAL_HEAD_BLOCK_SIGNAL:
            case DUAL_HEAD_DISTANT_SIGNAL:
                return texturesSignalDual[0];
            case DISTANT_SIGNAL:
            case BLOCK_SIGNAL:
                return texturesSignalSingle[0];
            default:
                return type.getIcon();
        }
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float u1, float u2, float u3) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null) {
            if (current.getItem() instanceof IActivationBlockingItem) {
                return false;
            }
        }
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).blockActivated(side, player);
        }
        return false;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).rotateBlock(axis);
        }
        return false;
    }

    @Override
    public ForgeDirection[] getValidRotations(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).getValidRotations();
        }
        return super.getValidRotations(world, x, y, z);
    }

    @Override
    public void onPostBlockPlaced(World world, int i, int j, int k, int meta) {
        super.onPostBlockPlaced(world, i, j, k, meta);
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            ((TileSignalFoundation) tile).onBlockPlaced();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack stack) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            ((TileSignalFoundation) tile).onBlockPlacedBy(entityliving);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        try {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileSignalFoundation) {
                TileSignalFoundation structure = (TileSignalFoundation) tile;
                if (structure.getSignalType().needsSupport() && !world.isSideSolid(x, y - 1, z, ForgeDirection.UP)) {
                    world.func_147480_a(x, y, z, true);
                } else {
                    structure.onNeighborBlockChange(block);
                }
            }
        } catch (StackOverflowError error) {
            Game.logThrowable(Level.ERROR, "Error in BlockSignal.onNeighborBlockChange()", 10, error);
            throw error;
        }
    }

    @Override
    public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            ((TileSignalFoundation) tile).onBlockRemoval();
        }
        super.breakBlock(world, i, j, k, block, meta);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            ((TileSignalFoundation) tile).setBlockBoundsBasedOnState(world, i, j, k);
        } else {
            setBlockBounds(0, 0, 0, 1, 1, 1);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).getCollisionBoundingBoxFromPool(world, i, j, k);
        }
        setBlockBounds(0, 0, 0, 1, 1, 1);
        return super.getCollisionBoundingBoxFromPool(world, i, j, k);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).getSelectedBoundingBoxFromPool(world, x, y, z);
        }
        return AxisAlignedBB.getBoundingBox((double) x + minX, (double) y + minY, (double) z + minZ, (double) x + maxX, (double) y + maxY, (double) z + maxZ);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        if (y < 0) {
            return 0;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ISignalTile) {
            return ((ISignalTile) tile).getLightValue();
        }
        return 0;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).getHardness();
        }
        int md = world.getBlockMetadata(x, y, z);
        return EnumSignal.fromId(md).getHardness();
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int i, int j, int k, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).isSideSolid(world, i, j, k, side);
        }
        return false;
    }

//    @Override
//    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
//        int md = world.getBlockMetadata(x, y, z);
//        EnumSignal type = EnumSignal.fromId(md);
//        return super.canPlaceBlockOnSide(world, x, y, z, side) && (!type.needsSupport() || world.isSideSolid(x, y - 1, z, ForgeDirection.UP));
//    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int meta) {
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return EnumSignal.fromId(metadata).getBlockEntity();
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int i, int j, int k, int dir) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).canConnectRedstone(dir);
        }
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int i, int j, int k, int side) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if (tile instanceof TileSignalFoundation) {
            return ((TileSignalFoundation) tile).getPowerOutput(side);
        }
        return PowerPlugin.NO_POWER;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity t = world.getTileEntity(x, y, z);
        if (t instanceof ISignalTile) {
            return ConnectStyle.TWO_THIN;
        }
        return ConnectStyle.NONE;
    }
    
    

}
