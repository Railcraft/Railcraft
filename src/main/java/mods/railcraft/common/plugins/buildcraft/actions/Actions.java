/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mods.railcraft.common.plugins.buildcraft.actions;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import buildcraft.api.statements.IActionExternal;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.StatementManager;

import mods.railcraft.common.plugins.forge.LocalizationPlugin;


/**
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public enum Actions implements IActionExternal {

    PAUSE("pause"),
    SEND_CART("sendcart");
    public static final Actions[] VALUES = values();
    private final String tag;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite sprite;

    Actions(String tag) {
        this.tag = tag;
    }

    public static void init() {
        for (Actions action : VALUES) {
            StatementManager.registerStatement(action);
            StatementManager.statements.put("railcraft." + action.tag, action);
        }
    }

    @Override
    public String getUniqueTag() {
        return "railcraft:" + tag;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getGuiSprite() {
        return sprite;
    }
    
    @Override
    public String getDescription() {
        return LocalizationPlugin.translate("gates.action." + tag);
    }

    public void registerSprites(TextureMap textureMap) {
        sprite = textureMap.registerSprite(new ResourceLocation("railcraft:buildcraft.gate.action." + tag));
    }

    @Override
    public IActionExternal rotateLeft() {
        return this;
    }

    @Override
    public void actionActivate(TileEntity tile, EnumFacing side, IStatementContainer isc, IStatementParameter[] isps) {
        if (tile instanceof IActionReceptor)
            ((IActionReceptor) tile).actionActivated(this);
    }

    @Override
    public int maxParameters() {
        return 0;
    }

    @Override
    public int minParameters() {
        return 0;
    }

    @Override
    public IStatementParameter createParameter(int i) {
        return null;
    }
}
