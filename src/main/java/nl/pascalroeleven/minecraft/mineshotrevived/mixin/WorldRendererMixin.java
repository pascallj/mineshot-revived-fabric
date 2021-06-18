package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@ModifyVariable(method = "render", at = @At("HEAD"))
	private Matrix4f onRender(Matrix4f matrix4f, MatrixStack matrices, float tickDelta) {
		Mineshot.getScreenshotHandler().onRenderTick();
		Matrix4f newMatrix4f = Mineshot.getOrthoViewHandler().onWorldRenderer(tickDelta);

		if (newMatrix4f == null) {
			return matrix4f;
		} else {
			return newMatrix4f;
		}
	}
}
