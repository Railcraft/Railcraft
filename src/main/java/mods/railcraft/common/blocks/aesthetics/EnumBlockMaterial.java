/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.blocks.aesthetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mods.railcraft.client.sounds.RailcraftSound;
import mods.railcraft.common.blocks.aesthetics.brick.BlockBrick;
import mods.railcraft.common.blocks.aesthetics.brick.EnumBrick;
import mods.railcraft.common.blocks.aesthetics.cube.BlockCube;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum EnumBlockMaterial implements IDerivedBlock {

    SANDY_BRICK,
    INFERNAL_BRICK,
    CONCRETE,
    SNOW,
    ICE,
    PACKED_ICE,
    IRON,
    GOLD,
    DIAMOND,
    FROSTBOUND_BRICK,
    QUARRIED_BRICK,
    BLEACHEDBONE_BRICK,
    BLOODSTAINED_BRICK,
    ABYSSAL_BRICK,
    SANDY_FITTED,
    INFERNAL_FITTED,
    FROSTBOUND_FITTED,
    QUARRIED_FITTED,
    BLEACHEDBONE_FITTED,
    BLOODSTAINED_FITTED,
    ABYSSAL_FITTED,
    NETHER_FITTED,
    SANDY_BLOCK,
    INFERNAL_BLOCK,
    FROSTBOUND_BLOCK,
    QUARRIED_BLOCK,
    BLEACHEDBONE_BLOCK,
    BLOODSTAINED_BLOCK,
    ABYSSAL_BLOCK,
    NETHER_BLOCK,
    SANDY_COBBLE,
    INFERNAL_COBBLE,
    FROSTBOUND_COBBLE,
    QUARRIED_COBBLE,
    BLEACHEDBONE_COBBLE,
    BLOODSTAINED_COBBLE,
    ABYSSAL_COBBLE,
    NETHER_COBBLE,
    CREOSOTE,
    OBSIDIAN,
    COPPER,
    TIN,
    LEAD,
    STEEL;
    public static final EnumBlockMaterial[] VALUES = values();
    public static final Map<String, EnumBlockMaterial> NAMES = new HashMap<String, EnumBlockMaterial>();
    public static final List<EnumBlockMaterial> creativeList = new ArrayList<EnumBlockMaterial>();
    private static boolean needsInit = true;
    private SoundType sound;
    private Block source;
    private int sourceMeta = 0;
    private String oreTag = null;
    private String toolClass = "pickaxe";
    private int toolLevel = 0;

    public static void initialize() {
        if (!needsInit)
            return;
        needsInit = false;
        INFERNAL_BRICK.source = EnumBrick.INFERNAL.getBlock();
        SANDY_BRICK.source = EnumBrick.SANDY.getBlock();
        FROSTBOUND_BRICK.source = EnumBrick.FROSTBOUND.getBlock();
        QUARRIED_BRICK.source = EnumBrick.QUARRIED.getBlock();
        BLEACHEDBONE_BRICK.source = EnumBrick.BLEACHEDBONE.getBlock();
        BLOODSTAINED_BRICK.source = EnumBrick.BLOODSTAINED.getBlock();
        ABYSSAL_BRICK.source = EnumBrick.ABYSSAL.getBlock();

        SANDY_FITTED.source = EnumBrick.SANDY.getBlock();
        SANDY_FITTED.sourceMeta = 1;
        INFERNAL_FITTED.source = EnumBrick.INFERNAL.getBlock();
        INFERNAL_FITTED.sourceMeta = 1;
        FROSTBOUND_FITTED.source = EnumBrick.FROSTBOUND.getBlock();
        FROSTBOUND_FITTED.sourceMeta = 1;
        QUARRIED_FITTED.source = EnumBrick.QUARRIED.getBlock();
        QUARRIED_FITTED.sourceMeta = 1;
        BLEACHEDBONE_FITTED.source = EnumBrick.BLEACHEDBONE.getBlock();
        BLEACHEDBONE_FITTED.sourceMeta = 1;
        BLOODSTAINED_FITTED.source = EnumBrick.BLOODSTAINED.getBlock();
        BLOODSTAINED_FITTED.sourceMeta = 1;
        ABYSSAL_FITTED.source = EnumBrick.ABYSSAL.getBlock();
        ABYSSAL_FITTED.sourceMeta = 1;
        NETHER_FITTED.source = EnumBrick.NETHER.getBlock();
        NETHER_FITTED.sourceMeta = 1;

        SANDY_BLOCK.source = EnumBrick.SANDY.getBlock();
        SANDY_BLOCK.sourceMeta = 2;
        INFERNAL_BLOCK.source = EnumBrick.INFERNAL.getBlock();
        INFERNAL_BLOCK.sourceMeta = 2;
        FROSTBOUND_BLOCK.source = EnumBrick.FROSTBOUND.getBlock();
        FROSTBOUND_BLOCK.sourceMeta = 2;
        QUARRIED_BLOCK.source = EnumBrick.QUARRIED.getBlock();
        QUARRIED_BLOCK.sourceMeta = 2;
        BLEACHEDBONE_BLOCK.source = EnumBrick.BLEACHEDBONE.getBlock();
        BLEACHEDBONE_BLOCK.sourceMeta = 2;
        BLOODSTAINED_BLOCK.source = EnumBrick.BLOODSTAINED.getBlock();
        BLOODSTAINED_BLOCK.sourceMeta = 2;
        ABYSSAL_BLOCK.source = EnumBrick.ABYSSAL.getBlock();
        ABYSSAL_BLOCK.sourceMeta = 2;
        NETHER_BLOCK.source = EnumBrick.NETHER.getBlock();
        NETHER_BLOCK.sourceMeta = 2;

        SANDY_COBBLE.source = EnumBrick.SANDY.getBlock();
        SANDY_COBBLE.sourceMeta = 5;
        INFERNAL_COBBLE.source = EnumBrick.INFERNAL.getBlock();
        INFERNAL_COBBLE.sourceMeta = 5;
        FROSTBOUND_COBBLE.source = EnumBrick.FROSTBOUND.getBlock();
        FROSTBOUND_COBBLE.sourceMeta = 5;
        QUARRIED_COBBLE.source = EnumBrick.QUARRIED.getBlock();
        QUARRIED_COBBLE.sourceMeta = 5;
        BLEACHEDBONE_COBBLE.source = EnumBrick.BLEACHEDBONE.getBlock();
        BLEACHEDBONE_COBBLE.sourceMeta = 5;
        BLOODSTAINED_COBBLE.source = EnumBrick.BLOODSTAINED.getBlock();
        BLOODSTAINED_COBBLE.sourceMeta = 5;
        ABYSSAL_COBBLE.source = EnumBrick.ABYSSAL.getBlock();
        ABYSSAL_COBBLE.sourceMeta = 5;
        NETHER_COBBLE.source = EnumBrick.NETHER.getBlock();
        NETHER_COBBLE.sourceMeta = 5;

        CONCRETE.source = BlockCube.getBlock();
        CONCRETE.sourceMeta = EnumCube.CONCRETE_BLOCK.ordinal();
        CREOSOTE.source = BlockCube.getBlock();
        CREOSOTE.sourceMeta = EnumCube.CREOSOTE_BLOCK.ordinal();

        SNOW.source = Blocks.snow;
        SNOW.toolClass = "shovel";
        ICE.source = Blocks.ice;
        PACKED_ICE.source = Blocks.packed_ice;

        IRON.source = Blocks.iron_block;
        GOLD.source = Blocks.gold_block;
        DIAMOND.source = Blocks.diamond_block;

        OBSIDIAN.source = Blocks.obsidian;

        COPPER.source = BlockCube.getBlock();
        COPPER.sourceMeta = EnumCube.COPPER_BLOCK.ordinal();
        COPPER.oreTag = "blockCopper";
        TIN.source = BlockCube.getBlock();
        TIN.sourceMeta = EnumCube.TIN_BLOCK.ordinal();
        TIN.oreTag = "blockTin";
        LEAD.source = BlockCube.getBlock();
        LEAD.sourceMeta = EnumCube.LEAD_BLOCK.ordinal();
        LEAD.oreTag = "blockLead";
        STEEL.source = BlockCube.getBlock();
        STEEL.sourceMeta = EnumCube.STEEL_BLOCK.ordinal();
        STEEL.oreTag = "blockSteel";

        for (EnumBlockMaterial mat : VALUES) {
            NAMES.put(mat.name(), mat);
            switch (mat) {
                case CONCRETE:
                    mat.sound = Block.soundTypeStone;
                    break;
                case CREOSOTE:
                    mat.sound = Block.soundTypeWood;
                    break;
                case COPPER:
                case TIN:
                case LEAD:
                case STEEL:
                    mat.sound = Block.soundTypeMetal;
                    break;
                default:
                    mat.sound = mat.source.stepSound;
            }
            if (mat.sound == RailcraftSound.getInstance())
                throw new RuntimeException("Invalid Sound Defined!");
        }

        creativeList.add(SNOW);
        creativeList.add(ICE);
        creativeList.add(PACKED_ICE);
        creativeList.add(IRON);
        creativeList.add(STEEL);
        creativeList.add(COPPER);
        creativeList.add(TIN);
        creativeList.add(LEAD);
        creativeList.add(GOLD);
        creativeList.add(DIAMOND);
        creativeList.add(OBSIDIAN);
        creativeList.add(CONCRETE);
        creativeList.add(CREOSOTE);
        creativeList.add(ABYSSAL_BRICK);
        creativeList.add(ABYSSAL_FITTED);
        creativeList.add(ABYSSAL_BLOCK);
        creativeList.add(ABYSSAL_COBBLE);
        creativeList.add(INFERNAL_BRICK);
        creativeList.add(INFERNAL_FITTED);
        creativeList.add(INFERNAL_BLOCK);
        creativeList.add(INFERNAL_COBBLE);
        creativeList.add(BLOODSTAINED_BRICK);
        creativeList.add(BLOODSTAINED_FITTED);
        creativeList.add(BLOODSTAINED_BLOCK);
        creativeList.add(BLOODSTAINED_COBBLE);
        creativeList.add(SANDY_BRICK);
        creativeList.add(SANDY_FITTED);
        creativeList.add(SANDY_BLOCK);
        creativeList.add(SANDY_COBBLE);
        creativeList.add(BLEACHEDBONE_BRICK);
        creativeList.add(BLEACHEDBONE_FITTED);
        creativeList.add(BLEACHEDBONE_BLOCK);
        creativeList.add(BLEACHEDBONE_COBBLE);
        creativeList.add(NETHER_FITTED);
        creativeList.add(NETHER_BLOCK);
        creativeList.add(NETHER_COBBLE);
        creativeList.add(QUARRIED_BRICK);
        creativeList.add(QUARRIED_FITTED);
        creativeList.add(QUARRIED_BLOCK);
        creativeList.add(QUARRIED_COBBLE);
        creativeList.add(FROSTBOUND_BRICK);
        creativeList.add(FROSTBOUND_FITTED);
        creativeList.add(FROSTBOUND_BLOCK);
        creativeList.add(FROSTBOUND_COBBLE);
    }

    public static EnumBlockMaterial fromOrdinal(int id) {
        if (id < 0 || id >= VALUES.length)
            return VALUES[0];
        return VALUES[id];
    }

    public static EnumBlockMaterial fromName(String name) {
        EnumBlockMaterial stair = NAMES.get(name);
        if (stair != null)
            return stair;
        return SANDY_BRICK;
    }

    public IIcon getIcon(int side) {
        return getSourceBlock().getIcon(side, sourceMeta);
    }

    @Override
    public Block getSourceBlock() {
        if (source == null) return Blocks.stonebrick;
        return source;
    }

    @Override
    public int getSourceMeta() {
        return sourceMeta;
    }

    public String getOreTag() {
        return oreTag;
    }

    public SoundType getSound() {
        return sound;
    }

    public ItemStack getSourceItem() {
        if (source == null) return null;
        return new ItemStack(source, 1, sourceMeta);
    }

    public Object getCraftingEquivelent() {
        if (oreTag != null) return oreTag;
        if (source == null) return null;
        return new ItemStack(source, 1, sourceMeta);
    }

    public boolean isTransparent() {
        return this == ICE;
    }

    public float getBlockHardness(World world, int x, int y, int z) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getHardness();
            case COPPER:
                return EnumCube.COPPER_BLOCK.getHardness();
            case TIN:
                return EnumCube.TIN_BLOCK.getHardness();
            case LEAD:
                return EnumCube.LEAD_BLOCK.getHardness();
            case STEEL:
                return EnumCube.STEEL_BLOCK.getHardness();
            default:
                Block block = getSourceBlock();
                if (block == null)
                    return Blocks.brick_block.getBlockHardness(world, x, y, z);
                return block.getBlockHardness(world, x, y, z);
        }
    }

    public float getExplosionResistance(Entity entity) {
        switch (this) {
            case CONCRETE:
                return EnumCube.CONCRETE_BLOCK.getResistance() * 3f / 5f;
            case COPPER:
                return EnumCube.COPPER_BLOCK.getResistance() * 3f / 5f;
            case TIN:
                return EnumCube.TIN_BLOCK.getResistance() * 3f / 5f;
            case LEAD:
                return EnumCube.LEAD_BLOCK.getResistance() * 3f / 5f;
            case STEEL:
                return EnumCube.STEEL_BLOCK.getResistance() * 3f / 5f;
            default:
                Block block = getSourceBlock();
                if (block == null)
                    return Blocks.brick_block.getExplosionResistance(entity);
                return block.getExplosionResistance(entity);
        }
    }

}
