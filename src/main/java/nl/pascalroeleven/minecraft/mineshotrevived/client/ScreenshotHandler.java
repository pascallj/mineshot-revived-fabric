package nl.pascalroeleven.minecraft.mineshotrevived.client;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import nl.pascalroeleven.minecraft.mineshotrevived.client.capture.task.CaptureTask;
import nl.pascalroeleven.minecraft.mineshotrevived.client.capture.task.RenderTickTask;
import nl.pascalroeleven.minecraft.mineshotrevived.util.ChatUtils;

public class ScreenshotHandler {
	private static final Logger L = LogManager.getLogger();
	private static final String KEY_CATEGORY = "key.categories.mineshotrevived";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

	private final KeyBinding keyCapture = new KeyBinding("key.mineshotrevived.capture", GLFW_KEY_F9,
			KEY_CATEGORY);

	private Path taskFile;
	private RenderTickTask task;
	
	private fbChangeTask fbTask = null;

	public ScreenshotHandler() {
		KeyBindingHelper.registerKeyBinding(keyCapture);
	}

	// Called by KeyboardMixin
	public void onKeyEvent() {
		// Don't poll keys when there's an active task
		if (task != null) {
			return;
		}

		if (keyCapture.isPressed()) {
			taskFile = getScreenshotFile();
			task = new CaptureTask(taskFile);
		}
	}

	// Called by BackgroundRendererMixin
	public void onRenderTick() {
		if (task == null) {
			return;
		}

		try {
			if (task.onRenderTick()) {
				task = null;
				ChatUtils.printFileLink("screenshot.success", taskFile.toFile());
			}
		} catch (Exception ex) {
			L.error("Screenshot capture failed", ex);
			ChatUtils.print("screenshot.failure", ex.getMessage());
			task = null;
		}
	}
	
	public fbChangeTask processFbChangeTask() {
		if (fbTask != null) {
			fbChangeTask task = fbTask;
			fbTask = null;
			return task;
		} else {
			return null;
		}
	}
	
	public void setFbChangeTask(int width, int height) {
		fbTask = new fbChangeTask();
		fbTask.width = width;
		fbTask.height = height;
	}

	private Path getScreenshotFile() {
		Path dir = FabricLoader.getInstance().getGameDir().resolve("screenshots");

		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
		} catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}

		int i = 0;
		Path file;
		do {
			file = dir.resolve(
					String.format("huge_%s_%04d.tga", DATE_FORMAT.format(new Date()), i++));
		} while (Files.exists(file));

		return file;
	}
	
	public class fbChangeTask {
		public int width = 0;
		public int height = 0;
	}
}
