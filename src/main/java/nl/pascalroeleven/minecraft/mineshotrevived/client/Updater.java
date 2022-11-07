	package nl.pascalroeleven.minecraft.mineshotrevived.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
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
			int[] mcVersionComponents = ((SemanticVersionImpl) FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion()).getVersionComponents();
			SemanticVersion releaseTarget = new SemanticVersionImpl(mcVersionComponents, null, null);

			// If no direct match for the version, check for wildcard version
			if (jsonObject.get(releaseTarget.getFriendlyString()) == null) {
				int[] versionComponents = Arrays.copyOfRange(mcVersionComponents, 0, releaseTarget.getVersionComponentCount() + 1);

				for (int i = versionComponents.length; i > 1; i--) {
					versionComponents[i-1] = SemanticVersion.COMPONENT_WILDCARD;
					releaseTarget = new SemanticVersionImpl(Arrays.copyOfRange(versionComponents, 0, i) , null, null);

					if (jsonObject.get(releaseTarget.getFriendlyString()) != null)
						break;
				}

				// No version could be matched
				if (jsonObject.get(releaseTarget.getFriendlyString()) == null)
					return;
			}

			SemanticVersion update;
			if (properties.get("notifyIncompatible").equalsIgnoreCase("true")) {
				update = SemanticVersion.parse(jsonObject.get(channel).getAsString());
			} else {
				update = SemanticVersion.parse(jsonObject.get(releaseTarget.getFriendlyString()).getAsJsonObject().get(channel).getAsString());
			}

			SemanticVersion currentVersion = SemanticVersion.parse(current);
			if (currentVersion.compareTo((Version) update) < 0)
				newVersion = update.getFriendlyString();
			else
				newVersion = null;
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
}
