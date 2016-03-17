/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.frame;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.blocks.ItemBlockRailcraft;
import mods.railcraft.common.blocks.machine.delta.EnumMachineDelta;
import mods.railcraft.common.blocks.machine.delta.TileWire;
import mods.railcraft.common.blocks.machine.delta.TileWire.AddonType;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.items.ItemPlate.EnumPlate;
import mods.railcraft.common.items.RailcraftItem;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.*;
import mods.railcraft.common.util.inventory.InvTools;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public class BlockFrame extends Block implements IPostConnection {

    private static BlockFrame instance;

    public static BlockFrame getBlock() {
        return instance;
    }

    public static void registerBlock() {
        if (instance == null)
            if (RailcraftConfig.isBlockEnabled("frame")) {
                instance = new BlockFrame(Railcraft.proxy.getRenderId());
                RailcraftRegistry.register(instance, ItemBlockRailcraft.class);

//                HarvestPlugin.setHarvestLevel(instance, "crowbar", 0);
                HarvestPlugin.setHarvestLevel(instance, "pickaxe", 1);

                ForestryPlugin.addBackpackItem("builder", instance);

                CraftingPlugin.addShapedRecipe(getItem(6),
                        "PPP",
                        "I I",
                        "III",
                        'P', RailcraftItem.plate, EnumPlate.IRON,
                        'I', RailcraftItem.rebar);
            }
    }

    public static ItemStack getItem() {
        return getItem(1);
    }

    public static ItemStack getItem(int qty) {
        if (instance == null) return null;
        return new ItemStack(instance, qty, 0);
    }

    private final int renderId;
    private IIcon[] icons;
    public static boolean flipTextures;
    public static boolean poweredTexture;

    public BlockFrame(int renderId) {
        super(Material.glass);
        this.renderId = renderId;
        setResistance(10);
        setHardness(5);
        setStepSound(Block.soundTypeMetal);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setBlockName("railcraft.frame");
    }

    @Override
    public int getRenderType() {
        return renderId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:frame", 3);
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        return getIcon(side, 0);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (ForgeDirection.UP.ordinal() == side) {
            if (flipTextures)
                return icons[1];
            return poweredTexture ? icons[2] : icons[0];
        }
        if (flipTextures && ForgeDirection.DOWN.ordinal() == side)
            return icons[0];
        return icons[1];
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return flipTextures || super.shouldSideBeRendered(world, x, y, z, side);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null && InvTools.isItemEqualIgnoreNBT(current, EnumMachineDelta.WIRE.getItem()))
            if (WorldPlugin.setBlock(world, x, y, z, EnumMachineDelta.WIRE.getBlock(), EnumMachineDelta.WIRE.ordinal(), 2)) {
                TileEntity tile = WorldPlugin.getBlockTile(world, x, y, z);
                if (tile instanceof TileWire) {
                    TileWire wire = (TileWire) tile;
                    wire.setAddon(AddonType.FRAME);
                }
                if (!player.capabilities.isCreativeMode)
                    player.setCurrentItemOrArmor(0, InvTools.depleteItem(current));
                return true;
            }
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return ConnectStyle.TWO_THIN;
    }

}
