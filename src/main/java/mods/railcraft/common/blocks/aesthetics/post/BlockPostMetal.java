/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics.post;

import mods.railcraft.client.util.textures.TextureAtlasSheet;
import mods.railcraft.common.core.Railcraft;
import mods.railcraft.common.core.RailcraftConfig;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.HarvestPlugin;
import mods.railcraft.common.plugins.forge.RailcraftRegistry;
import mods.railcraft.common.util.misc.EnumColor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class BlockPostMetal extends BlockPostBase {

    public static BlockPostMetal post;
    public static BlockPostMetal platform;
    public static IIcon[] textures;
    public final boolean isPlatform;

    private BlockPostMetal(int renderType, boolean isPlatform) {
        super(renderType);
        setStepSound(Block.soundTypeMetal);
        this.isPlatform = isPlatform;
    }

    public static void registerPost() {
        if (post == null && RailcraftConfig.isBlockEnabled("post.metal"))
            post = BlockPostMetal.make("post.metal", false);
    }

    public static void registerPlatform() {
        if (platform == null && RailcraftConfig.isBlockEnabled("post.metal.platform"))
            platform = BlockPostMetal.make("post.metal.platform", true);
    }

    private static BlockPostMetal make(String tag, boolean isPlatform) {
        BlockPostMetal block = new BlockPostMetal(Railcraft.getProxy().getRenderId(), isPlatform);
        block.setBlockName("railcraft." + tag);
        RailcraftRegistry.register(block, ItemPostMetal.class);

//        HarvestPlugin.setHarvestLevel(block, "crowbar", 0);
        HarvestPlugin.setHarvestLevel(block, "pickaxe", 2);

        ForestryPlugin.addBackpackItem("builder", block);

        for (EnumColor color : EnumColor.values()) {
            ItemStack stack = block.getItem(1, color.ordinal());
            RailcraftRegistry.register(stack);
        }

        return block;
    }

    @Override
    public boolean isPlatform(int meta) {
        return isPlatform;
    }

    public ItemStack getItem() {
        return getItem(1, 3);
    }

    public ItemStack getItem(int qty) {
        return getItem(qty, 3);
    }

    public ItemStack getItem(int qty, int color) {
        return new ItemStack(this, qty, color);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumColor color : EnumColor.values()) {
            list.add(getItem(1, color.ordinal()));
        }
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        return true;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        if (isPlatform)
            list.add(EnumPost.METAL_PLATFORM_UNPAINTED.getItem());
        else
            list.add(EnumPost.METAL_UNPAINTED.getItem());
        return list;
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        if (!isPlatform)
            textures = TextureAtlasSheet.unstitchIcons(iconRegister, "railcraft:post.metal.painted", 16);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return textures[meta];
    }

    @Override
    public boolean recolourBlock(World world, int x, int y, int z, ForgeDirection side, int colour) {
        int c = 15 - colour;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta != c) {
            world.setBlockMetadataWithNotify(x, y, z, c, 3);
            return true;
        }
        return false;
    }

}
