//package mods.railcraft.common.plugins.ic2.crops;
//
//import ic2.api.crops.CropCard;
//import net.minecraft.util.ResourceLocation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class RailcraftCropCard extends CropCard {
//
//    public RailcraftCropCard() {
//    }
//
//    public String getOwner() {
//        return "railcraft";
//    }
//
//    public String getTranslationKey() {
//        return "railcraft.ic2.crop." + this.getId();
//    }
//
//    public List<ResourceLocation> getTexturesLocation() {
//        List<ResourceLocation> ret = new ArrayList(this.getMaxSize());
//
//        for(int size = 1; size <= this.getMaxSize(); ++size) {
//            ret.add(new ResourceLocation("railcraft", "blocks/crop/" + this.getId() + "_" + size));
//        }
//
//        return ret;
//    }
//
//}
//