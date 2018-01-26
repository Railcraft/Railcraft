//package mods.railcraft.common.plugins.ic2.crops;

//import ic2.api.crops.CropCard;
//import ic2.api.crops.CropProperties;
//import ic2.api.crops.ICropTile;
//import mods.railcraft.common.items.RailcraftItems;
//import net.minecraft.item.ItemStack;
//import net.minecraft.init.Items;

//public class CropCreosote extends RailcraftCropCard {
//
//    public String getId() {
//        return "creosote";
//    }
//
//    public String getDiscoveredBy() {
//        return "Forecaster";
//    }
//
//    public CropProperties getProperties() {
//        return new CropProperties(5, 3, 0, 3, 0, 2);
//    }
//
//    public String[] getAttributes() {
//        return new String[]{"Brown", "Fire", "Vine", "Flower", "Oil"};
//    }
//
//    public int getMaxSize() {
//        return 4;
//    }
//
//    public boolean canGrow(ICropTile crop) {
//        return crop.getCurrentSize() < 4;
//    }
//
//    public int getWeightInfluences(ICropTile crop, int humidity, int nutrients, int air) {
//        return (int)((double)humidity + (double)nutrients + (double)air * 0.8D);
//    }
//
//    public boolean canBeHarvested(ICropTile crop) {
//        return crop.getCurrentSize() > 1;
//    }
//
//    public int getOptimalHarvestSize(ICropTile crop) {
//        return 4;
//    }
//
//    public ItemStack getGain(ICropTile crop) {
//        return crop.getCurrentSize() <= 3 ?  new ItemStack(Items.DYE, crop.getCurrentSize() - 1, 11):RailcraftItems.TARBERRY.getStack(1);
//    }
//
//
//}
