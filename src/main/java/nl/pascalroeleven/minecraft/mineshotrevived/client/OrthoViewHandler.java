package nl.pascalroeleven.minecraft.mineshotrevived.client;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;

import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import nl.pascalroeleven.minecraft.mineshotrevived.mixin.CameraInvoker;
import nl.pascalroeleven.minecraft.mineshotrevived.util.ChatUtils;

public class OrthoViewHandler {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
	private static final String KEY_CATEGORY = "key.categories.mineshotrevived";
	private static final float ZOOM_STEP = 0.5f;
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

	private boolean enabled;
	private boolean freeCam;
	private boolean clip;

	private float zoom;
	private float xRot;
	private float yRot;

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

		reset();
	}

	// Called by CameraMixin
	public boolean onCameraUpdate() {
		if (!enabled) {
			return false;
		}

		if (!freeCam) {
			((CameraInvoker) MC.gameRenderer.getCamera()).InvokeSetRotation(yRot + 180, xRot);
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

	// Called by BackgroundRendererMixin
	public void onRenderTick(float tickDelta) {
		if (!enabled) {
			return;
		}

		// Update zoom and rotation
		if (!modifierKeyPressed()) {
			int ticksElapsed = tick - tickPrevious;
			double partial = tickDelta;
			double elapsed = ticksElapsed + (partial - partialPrevious);
			elapsed *= SECONDS_PER_TICK * ROTATE_SPEED;
			updateZoomAndRotation(elapsed);

			tickPrevious = tick;
			partialPrevious = partial;
		}

		float width = zoom * (MC.getWindow().getFramebufferWidth()
				/ (float) MC.getWindow().getFramebufferHeight());
		float height = zoom;

		// Override projection matrix
		RenderSystem.matrixMode(GL_PROJECTION);
		RenderSystem.loadIdentity();
		RenderSystem.ortho(-width, width, -height, height, clip ? 0 : -9999, 9999);
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
		} else if (!enabled) {
			return;
		} else if (keyClip.isPressed()) {
			clip = !clip;
		} else if (keyRotateT.isPressed()) {
			xRot = mod ? -90 : 90;
			yRot = 0;
		} else if (keyRotateF.isPressed()) {
			xRot = 0;
			yRot = mod ? -90 : 90;
		} else if (keyRotateS.isPressed()) {
			xRot = 0;
			yRot = mod ? 180 : 0;
		}

		// Update stepped rotation/zoom controls
		// Note: the smooth controls are handled in onRenderTick, since they need to be
		// executed on every frame
		if (mod) {
			updateZoomAndRotation(1);
			// Snap values to step units
			xRot = Math.round(xRot / ROTATE_STEP) * ROTATE_STEP;
			yRot = Math.round(yRot / ROTATE_STEP) * ROTATE_STEP;
			zoom = Math.round(zoom / ZOOM_STEP) * ZOOM_STEP;
		}
	}

	private void reset() {
		freeCam = false;
		clip = false;

		zoom = 8;
		xRot = 30;
		yRot = -45;
		tick = 0;
		tickPrevious = 0;
		partialPrevious = 0;
	}

	private void enable() {
		// Disable in multiplayer
		// Of course, programmers could just delete this check and abuse the
		// orthographic camera, but at least the official build won't support it
		if (!MC.isInSingleplayer()) {
			ChatUtils.print("mineshotrevived.orthomp");
			return;
		}

		if (!enabled) {
			/* TODO: Implement Clippinghelper
			 * clippingEnabled = clippingHelper.isEnabled();
			 * clippingHelper.setEnabled(false);
			 */
			reset();
		}

		enabled = true;
	}

	private void disable() {
		if (enabled) {
			/* clippingHelper.setEnabled(clippingEnabled); */
		}

		enabled = false;
	}

	private void toggle() {
		if (enabled) {
			disable();
		} else {
			enable();
		}
	}

	private boolean modifierKeyPressed() {
		return keyMod.isPressed();
	}

	private void updateZoomAndRotation(double multi) {
		if (keyZoomIn.isPressed()) {
			zoom *= 1 - ZOOM_STEP * multi;
		}
		if (keyZoomOut.isPressed()) {
			zoom *= 1 + ZOOM_STEP * multi;
		}

		if (keyRotateL.isPressed()) {
			yRot += ROTATE_STEP * multi;
		}
		if (keyRotateR.isPressed()) {
			yRot -= ROTATE_STEP * multi;
		}

		if (keyRotateU.isPressed()) {
			xRot += ROTATE_STEP * multi;
		}
		if (keyRotateD.isPressed()) {
			xRot -= ROTATE_STEP * multi;
		}
	}
}
