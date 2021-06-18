package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Matrix4f;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@ModifyVariable(method = "render", at = @At("HEAD"))
	private Matrix4f changeProjection(Matrix4f matrix4f) {
		Matrix4f newmatrix4f = Mineshot.getOrthoViewHandler().onWorldRenderer();
		if (newmatrix4f.equals(new Matrix4f())) {
			return matrix4f;
		} else {
			return newmatrix4f;
		}
	}
}
