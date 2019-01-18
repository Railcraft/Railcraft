/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.carts;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class is used to register new Locomotive Skins with Railcraft.
 *
 * Usage example: LocomotiveRenderType.STEAM_SOLID.registerRenderer(new
 * MyRenderer());
 *
 * Registration must be done in the Client side initialization.
 *
 * @author CovertJaguar <http://www.railcraft.info/>
 */
public enum LocomotiveRenderType {

    STEAM_SOLID("cart.loco.steam.solid"),
    STEAM_MAGIC("cart.loco.steam.magic"),
    ELECTRIC("cart.loco.electric"),
    CREATIVE("cart.loco.electric");
    private final Map<String, LocomotiveModelRenderer> renderers = new HashMap<>();
    private final String cartTag;

    LocomotiveRenderType(String cartTag) {
        this.cartTag = cartTag;
    }

    /**
     * This is how you register a new renderer. It can be a model renderer, an
     * obj renderer, or anything else you want. It just needs to extend
     * LocomotiveModelRenderer.
     */
    public void registerRenderer(LocomotiveModelRenderer renderer) {
        renderers.put(renderer.getRendererTag(), renderer);
    }

    /**
     * Railcraft calls this method, you don't need to worry about it.
     */
    public LocomotiveModelRenderer getRenderer(String tag) {
        LocomotiveModelRenderer renderer = renderers.get(tag);
        if (renderer == null)
            renderer = renderers.get("railcraft:default");
        return renderer;
    }

    /**
     * This function will return a Locomotive item with the skin identifier
     * saved in the NBT. Use it to create a recipe for your skin.
     */
    @Contract("_, null -> null; _, !null -> !null")
    public @Nullable ItemStack getItemWithRenderer(String rendererTag, @Nullable ItemStack stack) {
        if (stack == null)
            return null;
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("model", rendererTag);
        stack.setTagCompound(nbt);
        return stack;
    }

    /**
     * Railcraft calls this method, you don't need to worry about it.
     */
    public Set<String> getRendererTags() {
        return renderers.keySet();
    }

}
