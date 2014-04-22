package edu.utd.minecraft.mod.polycraft.config;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigRegistry<V extends Config> extends LinkedHashMap<String, V> {

	private static final Logger logger = LogManager.getLogger();

	public V register(final V config) {
		if (containsKey(config))
			throw new RuntimeException("Registry already contains " + config.name);
		put(config.name, config);
		if (config instanceof GameIdentifiedConfig)
			logger.info("Registered {} {}: {} ({})", config.getClass().getSimpleName(), size(), config.name, ((GameIdentifiedConfig) config).gameID);
		else
			logger.info("Registered {} {}: {}", config.getClass().getSimpleName(), size(), config.name);
		return config;
	}
}
