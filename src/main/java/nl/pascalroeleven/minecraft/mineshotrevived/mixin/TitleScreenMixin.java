package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.client.font.TextRenderer;
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

	@ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V", ordinal = 0), index = 5)
	private int getColor(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
		String newVersion = Mineshot.getUpdater().getNewVersion();

		if (newVersion != null) {
			String updateMsg = "Version '" + newVersion + "' of Mineshot Revived is available!";
			drawStringWithShadow(matrices, textRenderer, updateMsg, 2, 2, color);
		}

		return color;
	}
}
