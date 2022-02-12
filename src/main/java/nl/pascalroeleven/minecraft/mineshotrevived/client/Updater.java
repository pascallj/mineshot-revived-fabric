	package nl.pascalroeleven.minecraft.mineshotrevived.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.client.MinecraftClient;
import nl.pascalroeleven.minecraft.mineshotrevived.Mineshot;
import nl.pascalroeleven.minecraft.mineshotrevived.client.config.PropertiesHandler;

public class Updater {
	private static final MinecraftClient MC = MinecraftClient.getInstance();
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
			String releaseVersion = MC.getGame().getVersion().getReleaseTarget();

			// If no direct match for the version, check for wildcard version
			if (jsonObject.get(releaseVersion) == null) {
				SemanticVersion mcVersion = SemanticVersion.parse(releaseVersion);
				int i = mcVersion.getVersionComponentCount();
				do {
					if (i == 0) {
						releaseVersion = null;
						break;
					}
					releaseVersion = buildWildcardVersion(mcVersion, i);
					i--;
				} while (jsonObject.get(releaseVersion) == null);

				if (releaseVersion == null)
					return;
			}

			SemanticVersion update;
			if (properties.get("notifyIncompatible").equalsIgnoreCase("true")) {
				update = SemanticVersion.parse(jsonObject.get(channel).getAsString());
			} else {
				update = SemanticVersion.parse(jsonObject.get(releaseVersion).getAsJsonObject().get(channel).getAsString());
			}

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
			return JsonParser.parseString(json).getAsJsonObject();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String buildWildcardVersion(SemanticVersion version, int components) {
		// Build the version string with last component as wildcard
		String wildcardVersion = "";
		for (int i = 0; i <= components-1; i++) {
			if (i == components-1) {
				wildcardVersion += "x";
				break;
			}
			wildcardVersion += version.getVersionComponent(i) + "." ;
		}

		return wildcardVersion;
	}
}
