package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
	@ModifyVariable(method = "render", at = @At("HEAD"))
	private Matrix4f changeProjection(Matrix4f matrix4f) {
		Matrix4f newMatrix4f = Mineshot.getOrthoViewHandler().onWorldRenderer();

		if (newMatrix4f == null) {
			return matrix4f;
		} else {
			return newMatrix4f;
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline,
			Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f,
			CallbackInfo info) {
		Mineshot.getOrthoViewHandler().onRenderTick(tickDelta);
		Mineshot.getScreenshotHandler().onRenderTick();
	}
}
