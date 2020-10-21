	package nl.pascalroeleven.minecraft.mineshotrevived.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;
import nl.pascalroeleven.minecraft.mineshotrevived.client.config.PropertiesHandler;

public class Updater {
	private PropertiesHandler properties = Mineshot.getPropertiesHandler();
	private String newVersion = null;
	private final String DOWNLOAD_URL = "https://pascallj.github.io/mineshot-revived-fabric/versions.json";

	public Updater() {
	}

	public void checkVersion() {
		String channel = "latest";
		String current = FabricLoader.getInstance().getModContainer("mineshotrevived").get()
				.getMetadata().getVersion().getFriendlyString();
		JsonObject jsonObject = null;
		String json = downloadJson();

		if (json == null) {
			return;
		}

		jsonObject = parseJson(json);

		if (jsonObject == null) {
			return;
		}

		if (properties.get("notifyDev").equalsIgnoreCase("true")) {
			channel = "dev";
		}

		try {
			SemanticVersion update = SemanticVersion.parse(jsonObject.get(channel).getAsString());
			SemanticVersion currentVersion = SemanticVersion.parse(current);
			if (currentVersion.compareTo(update) < 0)
				newVersion = update.getFriendlyString();
		} catch (VersionParsingException e) {
			e.printStackTrace();
		}
	}
	
	public String getNewVersion() {
		return newVersion;
	}

	private String downloadJson() {
		URLConnection request;

		try {
			URL url = new URL(DOWNLOAD_URL);
			request = url.openConnection();
			request.connect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		try {
			return IOUtils.toString(new InputStreamReader((InputStream) request.getContent()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private JsonObject parseJson(String json) {
		try {
			return new JsonParser().parse(json).getAsJsonObject();
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

		return null;
	}
}
