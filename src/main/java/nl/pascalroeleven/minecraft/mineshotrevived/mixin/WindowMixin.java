package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.util.Window;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;
import nl.pascalroeleven.minecraft.mineshotrevived.client.ScreenshotHandler.fbChangeTask;

@Mixin(Window.class)
public class WindowMixin {
	@Shadow
	private void onFramebufferSizeChanged(long window, int width, int height) {
	}

	@Shadow
	@Final
	private long handle;

	@Inject(method = "getHandle", at = @At("HEAD"))
	private void onGetHandle(CallbackInfoReturnable<Long> info) {
		fbChangeTask task = Mineshot.getScreenshotHandler().processFbChangeTask();
		if (task != null) {
			onFramebufferSizeChanged(handle, task.width, task.height);
		}
	}
}
