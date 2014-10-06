/* 
 * Copyright (c) CovertJaguar, 2014 http://railcraft.info
 * 
 * This code is the property of CovertJaguar
 * and may only be used with explicit written
 * permission unless otherwise specified on the
 * license page at http://railcraft.info/wiki/info:license.
 */
package mods.railcraft.common.modules;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.*;
import mods.railcraft.common.blocks.BlockFactory;
import mods.railcraft.common.gui.EnumGui;
import mods.railcraft.common.modules.ModuleManager.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

public abstract class RailcraftModule {

    public final List<BlockFactory> blockFactories = new ArrayList<BlockFactory>();

    public final void addBlockFactory(BlockFactory factory) {
        if (ModuleManager.getStage() != ModuleManager.Stage.PRE_INIT)
            throw new RuntimeException("You can only define Block Factories in Pre-Init!");
        blockFactories.add(factory);
    }

    public final void initBlocks() {
        for (BlockFactory factory : blockFactories) {
            factory.initBlock();
        }
    }

    public final void initRecipes(Module module) {
        for (BlockFactory factory : blockFactories) {
            factory.initRecipes(module);
        }
    }

    public final void finalizeBlocks(Module module) {
        for (BlockFactory factory : blockFactories) {
            factory.finalizeBlocks(module);
        }
    }

    public Set<Module> getDependencies() {
        return EnumSet.noneOf(Module.class);
    }

    public void preInit() {
    }

    public void initFirst() {
    }

    public void initSecond() {
    }

    public void postInit() {
    }

    public void postInitNotLoaded() {
    }

    public boolean canModuleLoad() {
        return true;
    }

    public void printLoadError() {
    }

    @SideOnly(Side.CLIENT)
    public GuiScreen getGuiScreen(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        return null;
    }

    public Container getGuiContainer(EnumGui gui, InventoryPlayer inv, Object obj, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
