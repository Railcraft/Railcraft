/*------------------------------------------------------------------------------
 Copyright (c) CovertJaguar, 2011-2019
 http://railcraft.info

 This code is the property of CovertJaguar
 and may only be used with explicit written
 permission unless otherwise specified on the
 license page at http://railcraft.info/wiki/info:license.
 -----------------------------------------------------------------------------*/

package mods.railcraft.client.render.models.resource;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;

/**
 * Created by CovertJaguar on 8/27/2016 for Railcraft.
 *
 * @author CovertJaguar <http://www.railcraft.info>
 */
public final class Transforms {
    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

    public static IModelState getItem() {
        TRSRTransformation thirdPerson = get(0, 3, 1, 0, 0, 0, 0.55f);
        TRSRTransformation firstPerson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(ItemCameraTransforms.TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        builder.put(ItemCameraTransforms.TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
        builder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdPerson));
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
        builder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstPerson));
        return new SimpleModelState(builder.build());
    }

    private static TRSRTransformation leftify(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
                new Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)),
                new Vector3f(s, s, s),
                null));
    }

    private Transforms() {}
}
