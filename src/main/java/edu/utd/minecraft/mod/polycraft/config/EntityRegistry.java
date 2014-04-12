package edu.utd.minecraft.mod.polycraft.config;

import java.util.LinkedHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityRegistry<V extends Entity> extends LinkedHashMap<String, V> {

	private static final Logger logger = LogManager.getLogger();

	public V register(final V entity) {
		if (containsKey(entity))
			throw new RuntimeException("Registry already contains entity for " + entity.name);
		put(entity.name, entity);
		logger.info("Registered {}: {}", entity.getClass().getSimpleName(), entity.name);
		return entity;
	}
}
