/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.common.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mods.railcraft.api.core.RailcraftConstantsAPI;
import mods.railcraft.common.advancements.criterion.SpikeMaulUseTrigger.Instance;
import mods.railcraft.common.plugins.forge.WorldPlugin;
import mods.railcraft.common.util.json.JsonTools;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.NBTPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

final class SpikeMaulUseTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("spike_maul_use");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        NBTPredicate nbt = JsonTools.whenPresent(json, "nbt", NBTPredicate::deserialize, NBTPredicate.ANY);
        ItemPredicate tool = JsonTools.whenPresent(json, "tool", ItemPredicate::deserialize, ItemPredicate.ANY);
        LocationPredicate locationPredicate = JsonTools.whenPresent(json, "location", LocationPredicate::deserialize, LocationPredicate.ANY);
        return new Instance(nbt, tool, locationPredicate);
    }

    static final class Instance implements ICriterionInstance {

        final NBTPredicate nbt;
        final ItemPredicate tool;
        final LocationPredicate locationPredicate;

        Instance(NBTPredicate nbt, ItemPredicate tool, LocationPredicate predicate) {
            this.nbt = nbt;
            this.tool = tool;
            this.locationPredicate = predicate;
        }

        boolean test(ItemStack item, WorldServer world, BlockPos pos) {
            return WorldPlugin.getTileEntity(world, pos).map(te -> te.writeToNBT(new NBTTagCompound())).map(nbt::test).orElse(false)
                    && tool.test(item)
                    && locationPredicate.test(world, pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
