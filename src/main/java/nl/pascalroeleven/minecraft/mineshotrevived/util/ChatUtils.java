package nl.pascalroeleven.minecraft.mineshotrevived.util;

import static net.minecraft.text.ClickEvent.Action.OPEN_FILE;

import java.io.File;
import java.io.IOException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.util.Formatting;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ChatUtils {
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	public static void print(String msg, Formatting format, Object... args) {
		if (MC.inGameHud == null) {
			return;
		}

		ChatHud chat = MC.inGameHud.getChatHud();
		MutableText ret = Text.translatable(msg, args);
		ret.getStyle().withColor(format);

		chat.addMessage(ret);
	}

	public static void print(String msg, Object... args) {
		print(msg, null, args);
	}

	public static void printFileLink(String msg, File file) {
		MutableText text = Text.translatable(file.getName());
		String path;

		try {
			path = file.getAbsoluteFile().getCanonicalPath();
		} catch (IOException ex) {
			path = file.getAbsolutePath();
		}

		text.getStyle().withClickEvent(new ClickEvent(OPEN_FILE, path));
		text.getStyle().withUnderline(true);

		print(msg, text);
	}
}
