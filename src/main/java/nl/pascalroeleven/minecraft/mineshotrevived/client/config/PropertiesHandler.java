package nl.pascalroeleven.minecraft.mineshotrevived.client.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;

public class PropertiesHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int MAX_TARGA_SIZE = 0xffff;

	private Path configDir = FabricLoader.getInstance().getConfigDir();
	private File configFile = new File(configDir.toFile(), "mineshot-revived.properties");;

	private Properties defaults = new Properties();
	private Properties properties = new Properties();

	private boolean writeConfig = false;

	public PropertiesHandler() {
		defaults.setProperty("captureWidth", "3840");
		defaults.setProperty("captureHeight", "2160");
		defaults.setProperty("notifyDev", "false");
		defaults.setProperty("notifyIncompatible", "false");

		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (IOException e) {
				LOGGER.warn("[Mineshot] Could not read property file '"
						+ configFile.getAbsolutePath() + "'", e);
			}
		} else {
			writeConfig = true;
		}

		// Validate all values
		for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (!validate(key)) {
				properties.setProperty(key, defaults.getProperty(key));
				writeConfig = true;
			}
		}

		// Make sure defaults are also present in properties file
		for (Enumeration<?> e = defaults.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			if (properties.getProperty(key) == null) {
				properties.setProperty(key, defaults.getProperty(key));
				writeConfig = true;
			}
		}

		storeProperties();
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	public void set(String key, String value) {
		// Only set if validated and only store if value is different
		if (validate(key, value) && !properties.setProperty(key, value).equals(value))
			writeConfig = true;
	}

	public void storeProperties() {
		if (!Files.exists(configDir)) {
			try {
				Files.createDirectory(configDir);
			} catch (IOException e) {
				LOGGER.warn("[Mineshot] Could not create configuration directory: "
						+ configDir.toAbsolutePath());
			}
		}

		if (writeConfig) {
			try (FileOutputStream stream = new FileOutputStream(configFile)) {
				properties.store(stream, "Mineshot Revived properties file");
				writeConfig = false;
			} catch (IOException e) {
				LOGGER.warn("[Mineshot] Could not store property file '" + configFile.getAbsolutePath()
						+ "'", e);
			}
		}
	}

	private static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
		return (i >= minValueInclusive && i <= maxValueInclusive);
	}

	private boolean validate(String key) {
		return validate(key, properties.getProperty(key));
	}

	private boolean validate(String key, String value) {
		switch (key) {
			case "captureWidth":
			case "captureHeight":
				try {
					int i = Integer.parseInt(value);
					if (between(i, 1, MAX_TARGA_SIZE))
						return true;
				} catch (NumberFormatException e) {
					return false;
				}
				break;
			case "notifyDev":
			case "notifyIncompatible":
				if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
					return true;
				break;
		}

		return false;
	}
}