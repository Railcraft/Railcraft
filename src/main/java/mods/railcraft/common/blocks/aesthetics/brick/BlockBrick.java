/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.brick;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.plugins.forge.CreativePlugin;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.List;

public class BlockBrick extends Block {
    private final EnumBrick theme;
    private IIcon[] icons;

    public BlockBrick(EnumBrick theme) {
        super(Material.rock);
        this.theme = theme;
        setResistance(15);
        setHardness(5);
        setStepSound(Block.soundTypeStone);
        setCreativeTab(CreativePlugin.RAILCRAFT_TAB);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:brick." + theme.themeTag(), BrickVariant.VALUES.length);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta >= icons.length)
            meta = 0;
        return icons[meta];
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (BrickVariant variant : BrickVariant.VALUES) {
            list.add(theme.get(variant, 1));
        }
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }
}
