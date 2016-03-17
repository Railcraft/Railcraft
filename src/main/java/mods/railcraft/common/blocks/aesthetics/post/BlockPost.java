/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.api.core.IPostConnection;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumColor;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockPost extends BlockPostBase implements IPostConnection {

    public static BlockPost block;

    protected BlockPost(int renderType) {
        super(renderType);
        setBlockName("railcraft.post");
    }

    public static void registerBlock() {
        if (block != null)
            return;

        if (RailcraftConfig.isBlockEnabled("post")) {
            block = new BlockPost(Railcraft.getProxy().getRenderId());

            GameRegistry.registerTileEntity(TilePostEmblem.class, "RCPostEmblemTile");

            RailcraftRegistry.register(block, ItemPost.class);

            for (EnumPost post : EnumPost.VALUES) {
                ItemStack stack = post.getItem();
                RailcraftRegistry.register(stack);
            }

//            HarvestPlugin.setHarvestLevel(block, "crowbar", 0);
            HarvestPlugin.setHarvestLevel(block, EnumPost.WOOD.ordinal(), "axe", 0);
            HarvestPlugin.setHarvestLevel(block, EnumPost.STONE.ordinal(), "pickaxe", 1);
            HarvestPlugin.setHarvestLevel(block, EnumPost.METAL_UNPAINTED.ordinal(), "pickaxe", 2);
            HarvestPlugin.setHarvestLevel(block, EnumPost.EMBLEM.ordinal(), "pickaxe", 2);
            HarvestPlugin.setHarvestLevel(block, EnumPost.WOOD_PLATFORM.ordinal(), "axe", 0);
            HarvestPlugin.setHarvestLevel(block, EnumPost.STONE_PLATFORM.ordinal(), "pickaxe", 1);
            HarvestPlugin.setHarvestLevel(block, EnumPost.METAL_PLATFORM_UNPAINTED.ordinal(), "pickaxe", 2);

            ForestryPlugin.addBackpackItem("builder", block);
        }
    }

    @Override
    public boolean isPlatform(int meta) {
        switch (EnumPost.fromId(meta)) {
            case WOOD_PLATFORM:
            case STONE_PLATFORM:
            case METAL_PLATFORM_UNPAINTED:
                return true;
        }
        return false;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumPost post : EnumPost.values()) {
            if (post == EnumPost.EMBLEM) continue;
            list.add(post.getItem());
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        IIcon woodIcon = iconRegister.registerIcon("railcraft:post.wood");
        IIcon stoneIcon = iconRegister.registerIcon("railcraft:concrete");
        IIcon metalIcon = iconRegister.registerIcon("railcraft:post.metal");
        EnumPost.WOOD.setTexture(woodIcon);
        EnumPost.WOOD_PLATFORM.setTexture(woodIcon);
        EnumPost.STONE.setTexture(stoneIcon);
        EnumPost.STONE_PLATFORM.setTexture(stoneIcon);
        EnumPost.METAL_UNPAINTED.setTexture(metalIcon);
        EnumPost.METAL_PLATFORM_UNPAINTED.setTexture(metalIcon);
        EnumPost.EMBLEM.setTexture(metalIcon);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return EnumPost.fromId(meta).getIcon();
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == EnumPost.EMBLEM.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                EnumColor color = post.getColor();
                if (color != null && BlockPostMetal.textures != null)
                    return BlockPostMetal.textures[color.ordinal()];
            }
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        if (metadata == EnumPost.EMBLEM.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);
                InvTools.setItemColor(drops.get(0), post.getColor());
                ItemPost.setEmblem(drops.get(0), post.getEmblem());
                return drops;
            }
        }
        return super.getDrops(world, x, y, z, metadata, fortune);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        player.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        player.addExhaustion(0.025F);
        if (Game.isHost(world) && !player.capabilities.isCreativeMode)
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        return world.setBlockToAir(x, y, z);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == EnumPost.EMBLEM.ordinal())
            return false;
        return side == ForgeDirection.DOWN || side == ForgeDirection.UP;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (metadata == EnumPost.EMBLEM.ordinal())
            return new TilePostEmblem();
        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == EnumPost.EMBLEM.ordinal();
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == EnumPost.WOOD.ordinal())
            return 300;
        return 0;
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (metadata == EnumPost.WOOD.ordinal())
            return 5;
        return 0;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        int metadata = world.getBlockMetadata(x, y, z);
        return metadata == EnumPost.WOOD.ordinal();
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == EnumPost.METAL_UNPAINTED.ordinal())
            if (BlockPostMetal.post != null) {
                world.setBlock(x, y, z, BlockPostMetal.post, 15 - colour, 3);
                return true;
            }
        if (meta == EnumPost.EMBLEM.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem tileEmblem = (TilePostEmblem) tile;
                tileEmblem.setColor(EnumColor.fromId(15 - colour));
                return true;
            }
        }
        return false;
    }

    @Override
    public ConnectStyle connectsToPost(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TilePostEmblem) {
            TilePostEmblem tileEmblem = (TilePostEmblem) tile;
            if (tileEmblem.getFacing() == side)
                return ConnectStyle.NONE;
        }
        return ConnectStyle.TWO_THIN;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == EnumPost.EMBLEM.ordinal()) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TilePostEmblem) {
                TilePostEmblem post = (TilePostEmblem) tile;
                post.onBlockPlacedBy(entity, stack);
            }
        }
    }

}
