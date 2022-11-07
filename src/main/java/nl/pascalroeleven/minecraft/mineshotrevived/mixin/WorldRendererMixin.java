package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
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

	@ModifyVariable(method = "setupFrustum", at = @At("HEAD"), argsOnly = true)
	private Matrix4f onSetupFrustum(Matrix4f matrix4f) {
		Matrix4f newMatrix4f = Mineshot.getOrthoViewHandler().onSetupFrustum();

		if (newMatrix4f == null) {
			return matrix4f;
		} else {
			return newMatrix4f;
		}
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clear(IZ)V"))
	private void onRenderClear(CallbackInfo ci) {
		int background = Mineshot.getOrthoViewHandler().getBackground();
		if (background == 1) {
			RenderSystem.setShaderFogColor(0, 0, 0, 0);
			RenderSystem.clearColor(1.0f, 0, 0, 1.0f);
		} else if (background == 2) {
			RenderSystem.setShaderFogColor(0, 0, 0, 0);
			RenderSystem.clearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}
	}

	@Inject(method = "renderSky(Lnet/minecraft/client/render/BufferBuilder;F)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;", at = @At("HEAD"), cancellable = true)
	private static void onRenderSky(CallbackInfoReturnable<BufferBuilder.BuiltBuffer> ci) {
		if (Mineshot.getOrthoViewHandler().getBackground() != 0)
			ci.cancel();
	}

	@Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
	private void onRenderSky2(CallbackInfo ci) {
		if (Mineshot.getOrthoViewHandler().getBackground() != 0)
			ci.cancel();
	}

	@Inject(method = "renderClouds*", at = @At("HEAD"), cancellable = true)
	private void onRenderWeather(CallbackInfo ci) {
		if (Mineshot.getOrthoViewHandler().getBackground() != 0)
			ci.cancel();
	}
}
