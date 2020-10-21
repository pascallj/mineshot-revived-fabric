package nl.pascalroeleven.minecraft.mineshotrevived.client.capture.task;

import java.nio.file.Path;

import net.minecraft.client.MinecraftClient;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;
import nl.pascalroeleven.minecraft.mineshotrevived.client.capture.FramebufferCapturer;
import nl.pascalroeleven.minecraft.mineshotrevived.client.capture.FramebufferWriter;
import nl.pascalroeleven.minecraft.mineshotrevived.client.config.PropertiesHandler;

public class CaptureTask implements RenderTickTask {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private PropertiesHandler properties = Mineshot.getPropertiesHandler();
	private final Path file;

	private int frame;
	private int displayWidth;
	private int displayHeight;

	public CaptureTask(Path file) {
		this.file = file;
	}

	@Override
	public boolean onRenderTick() throws Exception {
		switch (frame) {
		// Override viewport size (the following frame will be black)
		case 0:
			displayWidth = MC.getWindow().getFramebufferWidth();
			displayHeight = MC.getWindow().getFramebufferHeight();

			int width = Integer.parseInt(properties.get("captureWidth"));
			int height = Integer.parseInt(properties.get("captureHeight"));

			// Resize viewport/framebuffer
			Mineshot.getScreenshotHandler().setFbChangeTask(width, height);
			// Custom code is injected into getHandle. We can't inject new functions into
			// Window.class because final class
			MC.getWindow().getHandle();
			break;

		// Capture screenshot and restore viewport size
		case 3:
			try {
				FramebufferCapturer fbc = new FramebufferCapturer();
				FramebufferWriter fbw = new FramebufferWriter(file, fbc);
				fbw.write();
			} finally {
				// Restore viewport/framebuffer
				Mineshot.getScreenshotHandler().setFbChangeTask(displayWidth, displayHeight);
				// Custom code is injected into getHandle. We can't inject new functions into
				// Window.class because final class
				MC.getWindow().getHandle();
			}
			break;
		}

		frame++;
		return frame > 3;
	}
}
