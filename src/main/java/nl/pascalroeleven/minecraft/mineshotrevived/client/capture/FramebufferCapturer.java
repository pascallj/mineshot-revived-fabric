package nl.pascalroeleven.minecraft.mineshotrevived.client.capture;

import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGetTexImage;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL12.GL_BGR;

import java.awt.Dimension;
import java.nio.ByteBuffer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public class FramebufferCapturer {
	private static final int BPP = 3;
	private static final int TYPE = GL_UNSIGNED_BYTE;
	private static final MinecraftClient MC = MinecraftClient.getInstance();

	private final ByteBuffer bb;
	private final Dimension dim;
	private final byte[] line1;
	private final byte[] line2;
	private boolean flipColors = false;
	private boolean flipLines = false;

	public FramebufferCapturer() {
		dim = getCurrentDimension();
		bb = ByteBuffer.allocateDirect((int) (dim.getWidth() * dim.getHeight() * BPP));
		line1 = new byte[MC.getWindow().getFramebufferWidth() * BPP];
		line2 = new byte[MC.getWindow().getFramebufferWidth() * BPP];
	}

	public void setFlipColors(boolean flipColors) {
		this.flipColors = flipColors;
	}

	public boolean isFlipColors() {
		return flipColors;
	}

	public void setFlipLines(boolean flipLines) {
		this.flipLines = flipLines;
	}

	public boolean isFlipLines() {
		return flipLines;
	}

	public int getBytesPerPixel() {
		return BPP;
	}

	public ByteBuffer getByteBuffer() {
		bb.rewind();
		return bb.duplicate();
	}

	public Dimension getCaptureDimension() {
		return dim;
	}

	public void capture() {
		// Check if the dimensions are still the same
		Dimension dim1 = getCurrentDimension();
		Dimension dim2 = getCaptureDimension();
		if (!dim1.equals(dim2)) {
			throw new IllegalStateException(String.format("Display size changed! %dx%d != %dx%d",
					dim1.getWidth(), dim1.getHeight(), dim2.getWidth(), dim2.getHeight()));
		}

		// Set alignment flags
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		int format = flipColors ? GL_BGR : GL_RGB;

		Framebuffer fb = MC.getFramebuffer();

		// Read texture from framebuffer if enabled, otherwise use slower glReadPixels
		if (fb.fbo >= 0) {
			glBindTexture(GL_TEXTURE_2D, fb.getColorAttachment());
			glGetTexImage(GL_TEXTURE_2D, 0, format, TYPE, bb);
		} else {
			glReadPixels(0, 0, MC.getWindow().getFramebufferWidth(),
					MC.getWindow().getFramebufferHeight(), format, TYPE, bb);
		}

		if (!flipLines) {
			return;
		}

		// flip buffer vertically
		for (int i = 0; i < MC.getWindow().getFramebufferHeight() / 2; i++) {
			int ofs1 = i * MC.getWindow().getFramebufferWidth() * BPP;
			int ofs2 = (MC.getWindow().getFramebufferHeight() - i - 1)
					* MC.getWindow().getFramebufferWidth() * BPP;

			// read lines
			bb.position(ofs1);
			bb.get(line1);
			bb.position(ofs2);
			bb.get(line2);

			// write lines at swapped positions
			bb.position(ofs2);
			bb.put(line1);
			bb.position(ofs1);
			bb.put(line2);
		}
	}

	private Dimension getCurrentDimension() {
		return new Dimension(MC.getWindow().getFramebufferWidth(),
				MC.getWindow().getFramebufferHeight());
	}
}
