package edu.utd.minecraft.mod.polycraft.config;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import scala.actors.threadpool.Arrays;

public class ConfigRegistry<C extends Config> extends LinkedHashMap<String, C> {

	private static final Logger logger = LogManager.getLogger();

	public C register(final C config) {
		if (config.name.contains("spit")) {
			System.out.println(config.name);
			System.out.println(Arrays.toString(config.version));
			System.out.println(PolycraftRegistry.isTargetVersion(config.version));
		}
		if (PolycraftRegistry.isTargetVersion(config.version)) {
			if (containsKey(config))
				throw new RuntimeException("Registry already contains " + config.name);
			put(config.name, config);
			if (config instanceof GameIdentifiedConfig)
				logger.debug("Registered {} {}: {} ({})", config.getClass().getSimpleName(), size(), config.name, ((GameIdentifiedConfig) config).gameID);
			else
				logger.debug("Registered {} {}: {}", config.getClass().getSimpleName(), size(), config.name);
		}
		return config;
	}
}
