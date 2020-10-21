package nl.pascalroeleven.minecraft.mineshotrevived.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public interface CameraInvoker {
	@Invoker("setRotation")
	public void InvokeSetRotation(float yaw, float pitch);
}
