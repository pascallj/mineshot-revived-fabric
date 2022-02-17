package nl.pascalroeleven.minecraft.mineshotrevived;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import nl.pascalroeleven.minecraft.mineshotrevived.client.OrthoViewHandler;
import nl.pascalroeleven.minecraft.mineshotrevived.client.ScreenshotHandler;
import nl.pascalroeleven.minecraft.mineshotrevived.client.Updater;
import nl.pascalroeleven.minecraft.mineshotrevived.client.config.PropertiesHandler;

public class Mineshot implements ModInitializer {
	private static PropertiesHandler properties = new PropertiesHandler();
	private static OrthoViewHandler ovh = new OrthoViewHandler();
	private static ScreenshotHandler ssh = new ScreenshotHandler();
	private static Updater updater = new Updater();

	@Override
	public void onInitialize() {
		ClientTickEvents.START_CLIENT_TICK.register((client) -> {
			ovh.onClientTickEvent();
		});

		updater.checkVersion();
	}
	
	public static Updater getUpdater() {
		return updater;
	}
	
	public static OrthoViewHandler getOrthoViewHandler() {
		return ovh;
	}
	
	public static ScreenshotHandler getScreenshotHandler() {
		return ssh;
	}
	
	public static PropertiesHandler getPropertiesHandler() {
		return properties;
	}
}
