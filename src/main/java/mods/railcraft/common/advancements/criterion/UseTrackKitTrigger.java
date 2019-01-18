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
import mods.railcraft.common.advancements.criterion.UseTrackKitTrigger.Instance;
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

final class UseTrackKitTrigger extends BaseTrigger<Instance> {

    static final ResourceLocation ID = RailcraftConstantsAPI.locationOf("use_track_kit");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        NBTPredicate nbt = JsonTools.whenPresent(json, "data", NBTPredicate::deserialize, NBTPredicate.ANY);
        ItemPredicate used = JsonTools.whenPresent(json, "used", ItemPredicate::deserialize, ItemPredicate.ANY);
        LocationPredicate location = JsonTools.whenPresent(json, "location", LocationPredicate::deserialize, LocationPredicate.ANY);
        return new Instance(nbt, used, location);
    }

    static final class Instance implements ICriterionInstance {

        final NBTPredicate blockEntityNbt;
        final ItemPredicate itemPredicate;
        final LocationPredicate locationPredicate;

        Instance(NBTPredicate nbtPredicate, ItemPredicate itemPredicate, LocationPredicate locationPredicate) {
            this.blockEntityNbt = nbtPredicate;
            this.itemPredicate = itemPredicate;
            this.locationPredicate = locationPredicate;
        }

        boolean test(WorldServer world, BlockPos location, ItemStack stack) {
            return itemPredicate.test(stack)
                    && locationPredicate.test(world, location.getX(), location.getY(), location.getZ())
                    && WorldPlugin.getTileEntity(world, location).map(te -> te.writeToNBT(new NBTTagCompound())).map(blockEntityNbt::test).orElse(false);
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }
    }
}
