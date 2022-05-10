package nl.pascalroeleven.minecraft.mineshotrevived.client;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Matrix4f;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;
import nl.pascalroeleven.minecraft.mineshotrevived.client.config.PropertiesHandler;
import nl.pascalroeleven.minecraft.mineshotrevived.mixin.CameraInvoker;

public class OrthoViewHandler {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static final PropertiesHandler properties = Mineshot.getPropertiesHandler();
	private static final String KEY_CATEGORY = "key.categories.mineshotrevived";
	private static final float ZOOM_STEP = 2f;
	private static final float ROTATE_STEP = 15;
	private static final float ROTATE_SPEED = 4;
	private static final float SECONDS_PER_TICK = 1f / 20f;

	private final KeyBinding keyToggle = new KeyBinding("key.mineshotrevived.ortho.toggle",
			GLFW_KEY_KP_5, KEY_CATEGORY);
	private final KeyBinding keyZoomIn = new KeyBinding("key.mineshotrevived.ortho.zoom_in",
			GLFW_KEY_KP_ADD, KEY_CATEGORY);
	private final KeyBinding keyZoomOut = new KeyBinding("key.mineshotrevived.ortho.zoom_out",
			GLFW_KEY_KP_SUBTRACT, KEY_CATEGORY);
	private final KeyBinding keyRotateL = new KeyBinding("key.mineshotrevived.ortho.rotate_l",
			GLFW_KEY_KP_4, KEY_CATEGORY);
	private final KeyBinding keyRotateR = new KeyBinding("key.mineshotrevived.ortho.rotate_r",
			GLFW_KEY_KP_6, KEY_CATEGORY);
	private final KeyBinding keyRotateU = new KeyBinding("key.mineshotrevived.ortho.rotate_u",
			GLFW_KEY_KP_8, KEY_CATEGORY);
	private final KeyBinding keyRotateD = new KeyBinding("key.mineshotrevived.ortho.rotate_d",
			GLFW_KEY_KP_2, KEY_CATEGORY);
	private final KeyBinding keyRotateT = new KeyBinding("key.mineshotrevived.ortho.rotate_t",
			GLFW_KEY_KP_7, KEY_CATEGORY);
	private final KeyBinding keyRotateF = new KeyBinding("key.mineshotrevived.ortho.rotate_f",
			GLFW_KEY_KP_1, KEY_CATEGORY);
	private final KeyBinding keyRotateS = new KeyBinding("key.mineshotrevived.ortho.rotate_s",
			GLFW_KEY_KP_3, KEY_CATEGORY);
	private final KeyBinding keyClip = new KeyBinding("key.mineshotrevived.ortho.clip",
			GLFW_KEY_KP_MULTIPLY, KEY_CATEGORY);
	private final KeyBinding keyMod = new KeyBinding("key.mineshotrevived.ortho.mod",
			GLFW_KEY_LEFT_ALT, KEY_CATEGORY);
	private final KeyBinding key360 = new KeyBinding("key.mineshotrevived.ortho.render360",
			GLFW_KEY_KP_DIVIDE, KEY_CATEGORY);
	private final KeyBinding keyBackground = new KeyBinding("key.mineshotrevived.ortho.background",
			GLFW_KEY_KP_DECIMAL, KEY_CATEGORY);
	private final KeyBinding keySaveCam = new KeyBinding("key.mineshotrevived.ortho.save_cam",
			GLFW_KEY_KP_0, KEY_CATEGORY);

	private boolean enabled;
	private boolean camSaved;

	private boolean render360;
	private boolean frustumUpdate;
	private boolean freeCam;
	private boolean clip;
	private int background;

	private float zoom;
	private float xRot;
	private float yRot;
	private float zoomSaved;
	private float xRotSaved;
	private float yRotSaved;

	private int tick;
	private int tickPrevious;
	private double partialPrevious;

	public OrthoViewHandler() {
		KeyBindingHelper.registerKeyBinding(keyToggle);
		KeyBindingHelper.registerKeyBinding(keyZoomIn);
		KeyBindingHelper.registerKeyBinding(keyZoomOut);
		KeyBindingHelper.registerKeyBinding(keyRotateL);
		KeyBindingHelper.registerKeyBinding(keyRotateR);
		KeyBindingHelper.registerKeyBinding(keyRotateU);
		KeyBindingHelper.registerKeyBinding(keyRotateD);
		KeyBindingHelper.registerKeyBinding(keyRotateT);
		KeyBindingHelper.registerKeyBinding(keyRotateF);
		KeyBindingHelper.registerKeyBinding(keyRotateS);
		KeyBindingHelper.registerKeyBinding(keyClip);
		KeyBindingHelper.registerKeyBinding(keyMod);
		KeyBindingHelper.registerKeyBinding(key360);
		KeyBindingHelper.registerKeyBinding(keyBackground);
		KeyBindingHelper.registerKeyBinding(keySaveCam);

		reset();
	}

	// Called by CameraMixin
	public boolean onCameraUpdate() {
		if (!enabled) {
			return false;
		}
		Camera cam = MC.gameRenderer.getCamera();
		if (!freeCam) {
			((CameraInvoker) cam).InvokeSetRotation(yRot + 180, xRot);
		} else {
			yRot = cam.getYaw() - 180;
			xRot = cam.getPitch();
		}

		return true;
	}

	// Registered to ClientTickEvents
	public void onClientTickEvent() {
		if (!enabled) {
			return;
		}

		tick++;
	}

	// Called by WorldRendererMixin
	public Matrix4f onWorldRenderer(float tickDelta) {
		if (!enabled) {
			return null;
		}

		// Update zoom and rotation
		if (!modifierKeyPressed()) {
			int ticksElapsed = tick - tickPrevious;
			double partial = tickDelta;
			double elapsed = ticksElapsed + (partial - partialPrevious);
			elapsed *= SECONDS_PER_TICK * ROTATE_SPEED;
			updateZoomAndRotation(elapsed, false);

			tickPrevious = tick;
			partialPrevious = partial;
		}

		float width = zoom * (MC.getWindow().getFramebufferWidth()
				/ (float) MC.getWindow().getFramebufferHeight());
		float height = zoom;

		// Override projection matrix
		// Top and bottom are swapped inside projectionMatrix (which is basically equivalent to glOrtho)
		Matrix4f matrix4f = Matrix4f.projectionMatrix(-width, width, height, -height, clip ? 0 : -9999, 9999);
		RenderSystem.setProjectionMatrix(matrix4f);
		return matrix4f;
	}

	// Called by WorldRendererMixin
	public Matrix4f onSetupFrustum() {
		if (frustumUpdate) {
			MC.worldRenderer.scheduleTerrainUpdate();
			frustumUpdate = false;
		}

		if (!enabled || !render360) {
			return null;
		}

		float width = zoom * (MC.getWindow().getFramebufferWidth()
				/ (float) MC.getWindow().getFramebufferHeight());
		float height = zoom;

		// Override projection matrix
		// Top and bottom are swapped inside projectionMatrix (which is basically equivalent to glOrtho)
		// FIXME: For some reason the client crashes now when clipping too much here.
		Matrix4f matrix4f = Matrix4f.projectionMatrix(-Math.max(10, width), Math.max(10, width), Math.max(10, height), -Math.max(10, height), -9999, 9999);
		return matrix4f;
	}

	public int getBackground() {
		return background;
	}

	// Called by KeyboardMixin
	public void onKeyEvent() {
		boolean mod = modifierKeyPressed();

		// Change perspectives, using modifier key for opposite sides
		if (keyToggle.isPressed()) {
			if (mod) {
				freeCam = !freeCam;
			} else {
				toggle();
			}
		} else if (keyBackground.isPressed()) {
			circleBackground();
		} else if (!enabled) {
			return;
		} else if (keyClip.isPressed()) {
			clip = !clip;
		} else if (keyRotateT.isPressed()) {
			setZoom(zoomSaved);
			xRot = mod ? -90 : 90;
			yRot = 0;
		} else if (keyRotateF.isPressed()) {
			setZoom(zoomSaved);
			xRot = 0;
			yRot = mod ? -90 : 90;
		} else if (keyRotateS.isPressed()) {
			setZoom(zoomSaved);
			xRot = 0;
			yRot = mod ? 180 : 0;
		} else if (key360.isPressed()) {
			render360 = !render360;
			frustumUpdate = true;
		} else if (keySaveCam.isPressed()) {
			if (mod) {
				camSaved = false;
			} else {
				zoomSaved = zoom;
				xRotSaved = xRot;
				yRotSaved = yRot;
				camSaved = true;
			}
		}

		// Update stepped rotation/zoom controls
		// Note: the smooth controls are handled in onWorldRenderer, since they need to be
		// executed on every frame
		if (mod) {
			updateZoomAndRotation(1, true);
		}
	}

	private void reset() {
		if (!camSaved) {
			zoomSaved = (float) Math.pow(ZOOM_STEP, 3);
			xRotSaved = Integer.parseInt(properties.get("xRotation"));
			yRotSaved = Integer.parseInt(properties.get("yRotation"));
		}
		zoom = zoomSaved;
		xRot = xRotSaved;
		yRot = yRotSaved;
		freeCam = false;
		clip = false;
		render360 = false;
		tick = 0;
		tickPrevious = 0;
		partialPrevious = 0;
	}

	private void enable() {
		if (!enabled) {
			reset();
		}

		enabled = true;
	}

	private void disable() {
		enabled = false;
	}

	private void toggle() {
		if (enabled) {
			disable();
		} else {
			enable();
		}
	}

	private void circleBackground() {
		if (background == 2) {
			background = 0;
		} else {
			background++;
		}
  }
  
	private void setZoom(float d) {
		zoom = d;
		// Because zooming is not a native game mechanic, it doesn't trigger a terrain
		// update
		if (render360)
			MC.worldRenderer.scheduleTerrainUpdate();
	}

	private boolean modifierKeyPressed() {
		return keyMod.isPressed();
	}

	private void updateZoomAndRotation(double multi, boolean mod) {
		if (keyZoomIn.isPressed()) {
			setZoom((float) Math.max(1E-7, (zoom / (1 + ((ZOOM_STEP - 1) * multi)))));
			if (mod)
				// zoom = 2^(ceil(log2(zoom)))
				zoom = (float) Math.pow(ZOOM_STEP, Math.ceil(Math.log10(zoom) / Math.log10(ZOOM_STEP)));
		}
		if (keyZoomOut.isPressed()) {
			setZoom((float) (zoom * (1 + ((ZOOM_STEP - 1) * multi))));
			if (mod)
				// zoom = 2^(floor(log2(zoom)))
				zoom = (float) Math.pow(ZOOM_STEP, Math.floor(Math.log10(zoom) / Math.log10(ZOOM_STEP)));
		}

		if (keyRotateL.isPressed()) {
			yRot += ROTATE_STEP * multi;
			if (mod)
				yRot = (float) (Math.floor(yRot / ROTATE_STEP) * ROTATE_STEP);
		}
		if (keyRotateR.isPressed()) {
			yRot -= ROTATE_STEP * multi;
			if (mod)
				yRot = (float) (Math.ceil(yRot / ROTATE_STEP) * ROTATE_STEP);
		}

		if (keyRotateU.isPressed()) {
			xRot += ROTATE_STEP * multi;
			if (mod)
				xRot = (float) (Math.floor(xRot / ROTATE_STEP) * ROTATE_STEP);
		}
		if (keyRotateD.isPressed()) {
			xRot -= ROTATE_STEP * multi;
			if (mod)
				xRot = (float) (Math.ceil(xRot / ROTATE_STEP) * ROTATE_STEP);
		}
	}
}
