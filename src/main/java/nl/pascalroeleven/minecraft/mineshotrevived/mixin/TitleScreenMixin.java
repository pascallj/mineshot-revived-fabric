package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	private TitleScreenMixin(Text title) {
		super(title);
	}

	private int l;

	@ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), ordinal = 4)
	private int getL(int variable) {
		return l = variable;
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta,
			CallbackInfo info) {
		String newVersion = Mineshot.getUpdater().getNewVersion();
		if (newVersion != null) {
			String updateMsg = "Version '" + newVersion + "' of Mineshot Revived is available!";
			drawStringWithShadow(matrices, this.textRenderer, updateMsg, 2, 2, 16777215 | l);
		}
	}
}
