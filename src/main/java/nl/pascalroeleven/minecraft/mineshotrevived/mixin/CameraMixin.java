package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.Camera;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(Camera.class)
public class CameraMixin {
	@Inject(method = "update", at = @At("RETURN"))
	private void onUpdate(CallbackInfo info) {
		Mineshot.getOrthoViewHandler().onCameraUpdate();
	}

	@Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;moveBy(DDD)V"), cancellable = true)
	private void onInvokeMoveBy(CallbackInfo ci) {
		if (Mineshot.getOrthoViewHandler().onCameraUpdate()) {
			// Only cancel MoveBy invocation if OVH is on
			ci.cancel();
		}
	}
}
