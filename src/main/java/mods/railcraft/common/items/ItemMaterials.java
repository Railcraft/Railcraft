/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2016
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/
package mods.railcraft.common.items;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemMaterials {

    enum Tool {
        CROWBAR;

        static {
            CROWBAR.addAttribute(Material.IRON, Attribute.ATTACK_DAMAGE, 2.5F);

            CROWBAR.addAttribute(Material.IRON, Attribute.ATTACK_SPEED, -2.8F);
            CROWBAR.addAttribute(Material.STEEL, Attribute.ATTACK_SPEED, -2.7F);
            CROWBAR.addAttribute(Material.THAUMIUM, Attribute.ATTACK_SPEED, -2.6F);
            CROWBAR.addAttribute(Material.VOID, Attribute.ATTACK_SPEED, -2.5F);
        }

        private final Map<Tuple<Material, Attribute>, Float> floatAttributes = new HashMap<>();

        void addAttribute(Material mat, Attribute at, float value) {
            floatAttributes.put(new Tuple<>(mat, at), value);
        }

        public float getAttributeF(@Nullable Material mat, Attribute at) {
            if (mat == null)
                return 0F;
            Float f = floatAttributes.get(new Tuple<>(mat, at));
            if (f == null)
                return getAttributeF(mat.previousTier, at);
            return f;
        }
    }

    public enum Material {
        IRON(null),
        STEEL(IRON),
        THAUMIUM(STEEL),
        VOID(THAUMIUM);
        private final Material previousTier;

        Material(Material previousTier) {
            this.previousTier = previousTier;
        }
    }

    enum Attribute {
        ATTACK_SPEED,
        ATTACK_DAMAGE
    }

    public static ToolMaterial STEEL_TOOL = EnumHelper.addToolMaterial("RAILCRAFT_STEEL", 2, 500, 7, 2.5F, 9);

    //TODO: texture?
    public static ArmorMaterial STEEL_ARMOR = EnumHelper.addArmorMaterial("RAILCRAFT_STEEL", "texture?", 25, new int[]{2, 5, 6, 2}, 8, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0F);
    public static ArmorMaterial GOGGLES = EnumHelper.addArmorMaterial("RAILCRAFT_GOGGLES", "texture?", 20, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0F);
    public static ArmorMaterial OVERALLS = EnumHelper.addArmorMaterial("RAILCRAFT_OVERALLS", "texture?", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0F);
}
