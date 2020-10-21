package nl.pascalroeleven.minecraft.mineshotrevived.client.capture.task;

public interface RenderTickTask {

	/**
	 * Called on every frame to update the task.
	 * 
	 * @return true if the task is done and can be disposed or false if it should
	 *         continue to be updated.
	 * @throws Exception
	 */
	boolean onRenderTick() throws Exception;
}
