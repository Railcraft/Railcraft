/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2017
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.items;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.genetics.IAllele;
import mods.railcraft.api.core.items.IFilterItem;
import mods.railcraft.api.core.items.InvToolsAPI;
import mods.railcraft.common.plugins.forestry.ForestryPlugin;
import mods.railcraft.common.plugins.forge.CraftingPlugin;
import mods.railcraft.common.plugins.forge.LocalizationPlugin;
import mods.railcraft.common.plugins.forge.NBTPlugin;
import mods.railcraft.common.plugins.misc.Mod;
import mods.railcraft.common.util.inventory.InvTools;
import mods.railcraft.common.util.misc.EnumTools;
import mods.railcraft.common.util.misc.Game;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by CovertJaguar on 5/29/2017 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public class ItemFilterBeeGenome extends ItemRailcraft implements IFilterItem {
    private static String WILDCARD = "item.railcraft.filter.bee.genome.tips.wildcard";

    public ItemFilterBeeGenome() {
    }

    @Override
    public void finalizeDefinition() {
        CraftingPlugin.addShapelessRecipe(getStack(), RailcraftItems.FILTER_BLANK, ModItems.HONEYCOMB);
    }

    public static ItemStack setBeeFilter(ItemStack filter, String type, @Nullable ItemStack active, @Nullable ItemStack inactive) {
        filter = filter.copy();
        NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(filter, "filter", true);
        nbt.setString("type", type);
        NBTPlugin.writeItemStack(nbt, "active", active);
        NBTPlugin.writeItemStack(nbt, "inactive", inactive);
        InvToolsAPI.setItemDataRailcraft(filter, "filter", nbt);
        return filter;
    }

    @Nullable
    public static BeeFilter getBeeFilter(ItemStack stack) {
        NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, "filter");
        if (nbt != null) {
            try {
                String typeName = nbt.getString("type");
                EnumBeeType type = null;
                try {
                    type = EnumBeeType.valueOf(typeName);
                } catch (IllegalArgumentException ignored) {
                }

                EnumBeeChromosome chromosome = EnumBeeChromosome.SPECIES;
                try {
                    chromosome = EnumBeeChromosome.valueOf(nbt.getString("chromosome"));
                } catch (IllegalArgumentException ignored) {
                }

                ItemStack active = NBTPlugin.readItemStack(nbt, "active");
                ItemStack inactive = NBTPlugin.readItemStack(nbt, "inactive");
                return new BeeFilter(type, chromosome, active, inactive);
            } catch (Throwable ignored) {
            }
        }
        return new BeeFilter(null, EnumBeeChromosome.SPECIES, null, null);
    }

    @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        NBTTagCompound nbt = InvToolsAPI.getItemDataRailcraft(stack, "filter");
        if (nbt != null) {
            try {
                EnumBeeChromosome chromosome = EnumBeeChromosome.SPECIES;
                try {
                    chromosome = EnumBeeChromosome.valueOf(nbt.getString("chromosome"));
                } catch (IllegalArgumentException ignored) {
                }
                chromosome = EnumTools.next(chromosome, EnumBeeChromosome.values());
                nbt.setString("chromosome", chromosome.name());
            } catch (Throwable throwable) {
                Game.logErrorAPI(Mod.FORESTRY.modId, throwable, EnumBeeChromosome.class);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack.copy());
    }

    @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
    @Override
    public boolean matches(ItemStack matcher, ItemStack target) {
        BeeFilter beeFilter = getBeeFilter(matcher);
        return beeFilter != null && beeFilter.matches(target);
    }

    @Optional.Method(modid = ForestryPlugin.FORESTRY_ID)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean adv) {
        super.addInformation(stack, player, info, adv);
        try {
            BeeFilter beeFilter = getBeeFilter(stack);
            if (beeFilter != null) {

                info.add(tr("item.railcraft.filter.bee.genome.tips.type",
                        tr(translateType(beeFilter.type))));

                info.add(tr("item.railcraft.filter.bee.genome.tips.chromosome",
                        tr(translateChromosome(beeFilter.chromosome))));

                String active;
                if (beeFilter.active != null) {
                    IAllele allele = beeFilter.getActiveChromosome(beeFilter.active);
                    if (allele != null)
                        active = StringUtils.capitalize(allele.getName());
                    else
                        active = wildcard();
                } else
                    active = wildcard();
                info.add(tr("item.railcraft.filter.bee.genome.tips.active", active));

                String inactive;
                if (beeFilter.inactive != null) {
                    IAllele allele = beeFilter.getInactiveChromosome(beeFilter.inactive);
                    if (allele != null)
                        inactive = StringUtils.capitalize(allele.getName());
                    else
                        inactive = wildcard();
                } else
                    inactive = wildcard();
                info.add(tr("item.railcraft.filter.bee.genome.tips.inactive", inactive));
            }
        } catch (Throwable throwable) {
            Game.logErrorAPI(Mod.FORESTRY.modId, throwable, BeeManager.class);
        }
    }

    private String tr(String tag, Object... args) {
        return LocalizationPlugin.translate(tag, args);
    }

    private String translateType(@Nullable EnumBeeType type) {
        if (type == null)
            return WILDCARD;
        switch (type) {
            case DRONE:
                return "for.bees.grammar.drone.type";
            case LARVAE:
                return "for.bees.grammar.larvae.type";
            case PRINCESS:
                return "for.bees.grammar.princess.type";
            case QUEEN:
                return "for.bees.grammar.queen.type";
        }
        return "";
    }

    private String translateChromosome(EnumBeeChromosome chromosome) {
        switch (chromosome) {
            case SPECIES:
                return "for.gui.species";
            case SPEED:
                return "for.gui.speed";
            case LIFESPAN:
                return "for.gui.lifespan";
            case FERTILITY:
                return "for.gui.fertility";
            case TEMPERATURE_TOLERANCE:
                return "for.gui.climate";
            case NEVER_SLEEPS:
                return "for.gui.nocturnal";
            case HUMIDITY_TOLERANCE:
                return "for.gui.humidity";
            case TOLERATES_RAIN:
                return "for.gui.flyer";
            case CAVE_DWELLING:
                return "for.gui.cave";
            case FLOWER_PROVIDER:
                return "for.gui.flowers";
            case FLOWERING:
                return "for.gui.pollination";
            case TERRITORY:
                return "for.gui.area";
            case EFFECT:
                return "for.gui.effect";
        }
        return "";
    }

    private String wildcard() {
        return LocalizationPlugin.translate(WILDCARD);
    }

    private static class BeeFilter {
        private final EnumBeeType type;
        private final EnumBeeChromosome chromosome;
        private final ItemStack active, inactive;

        public BeeFilter(@Nullable EnumBeeType type, EnumBeeChromosome chromosome, @Nullable ItemStack active, @Nullable ItemStack inactive) {
            this.type = type;
            this.chromosome = chromosome;
            this.active = active;
            this.inactive = inactive;
        }

        @Nullable
        IAllele getActiveChromosome(ItemStack stack) {
            if (!InvTools.isEmpty(stack)) {
                IBee bee = BeeManager.beeRoot.getMember(stack);
                if (bee != null)
                    return bee.getGenome().getActiveAllele(chromosome);
            }
            return null;
        }

        @Nullable
        IAllele getInactiveChromosome(ItemStack stack) {
            if (!InvTools.isEmpty(stack)) {
                IBee bee = BeeManager.beeRoot.getMember(stack);
                if (bee != null)
                    return bee.getGenome().getActiveAllele(chromosome);
            }
            return null;
        }

        boolean matches(ItemStack bee) {
            try {
                if (!ForestryPlugin.instance().isAnalyzedBee(bee))
                    return false;
                if (type != null) {
                    EnumBeeType beeType = BeeManager.beeRoot.getType(bee);
                    if (type != beeType)
                        return false;
                }

                if (active != null) {
                    if (getActiveChromosome(active) != getActiveChromosome(bee))
                        return false;
                }
                if (inactive != null) {
                    if (getInactiveChromosome(inactive) != getInactiveChromosome(bee))
                        return false;
                }
            } catch (Throwable throwable) {
                Game.logErrorAPI(Mod.FORESTRY.modId, throwable, BeeManager.class);
                return false;
            }
            return true;
        }
    }
}
