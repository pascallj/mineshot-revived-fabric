package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(Keyboard.class)
public class KeyboardMixin {
	@Inject(method = "onKey", at = @At("RETURN"))
	private void onOnKey(CallbackInfo info) {
		Mineshot.getOrthoViewHandler().onKeyEvent();
		Mineshot.getScreenshotHandler().onKeyEvent();
	}
}
