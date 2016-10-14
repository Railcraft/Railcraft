/*
 * ******************************************************************************
 *  Copyright 2011-2016 CovertJaguar
 *
 *  This work (the API) is licensed under the "MIT" License, see LICENSE.md for details.
 * ***************************************************************************
 */

package mods.railcraft.common.blocks;

import mods.railcraft.common.blocks.aesthetics.brick.BrickVariant;
import mods.railcraft.common.blocks.aesthetics.generic.EnumGeneric;
import mods.railcraft.common.blocks.aesthetics.glass.BlockStrengthGlass;
import mods.railcraft.common.blocks.aesthetics.materials.Materials;
import mods.railcraft.common.blocks.aesthetics.post.EnumPost;
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha;
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta;
import mods.railcraft.common.blocks.machine.manipulator.ManipulatorVariant;
import mods.railcraft.common.blocks.ore.EnumOre;
import mods.railcraft.common.plugins.color.EnumColor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

public final class RailcraftBlockProperties {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyEnum<EnumColor> COLOR = PropertyEnum.create("color", EnumColor.class);
    public static final PropertyEnum<BlockStrengthGlass.Position> GLASS_POSITION = PropertyEnum.create("position", BlockStrengthGlass.Position.class);
    public static final IUnlistedProperty<Materials> MATERIAL_PROPERTY = Properties.toUnlisted(PropertyEnum.create("material", Materials.class));
    public static final IUnlistedProperty<Materials> SLAB_TOP_MATERIAL = Properties.toUnlisted(PropertyEnum.create("top_material", Materials.class));
    public static final IUnlistedProperty<Materials> SLAB_BOTTOM_MATERIAL = Properties.toUnlisted(PropertyEnum.create("bottom_material", Materials.class));
    public static final PropertyEnum<BrickVariant> BRICK_VARIANT = PropertyEnum.create("variant", BrickVariant.class);
    public static final PropertyEnum<EnumPost> POST_VARIANT = PropertyEnum.create("variant", EnumPost.class);
    public static final PropertyEnum<EnumOre> ORE_VARIANT = PropertyEnum.create("variant", EnumOre.class);
    public static final PropertyEnum<EnumMachineAlpha> MACHINE_ALPHA_VARIANT = PropertyEnum.create("variant", EnumMachineAlpha.class);
    public static final PropertyEnum<EnumMachineBeta> MACHINE_BETA_VARIANT = PropertyEnum.create("variant", EnumMachineBeta.class);
    public static final PropertyEnum<EnumGeneric> GENERIC_VARIANT = PropertyEnum.create("variant", EnumGeneric.class);
    public static final PropertyEnum<ManipulatorVariant> MANIPULATOR_VARIANT = PropertyEnum.create("variant", ManipulatorVariant.class);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    private RailcraftBlockProperties() {}
}
