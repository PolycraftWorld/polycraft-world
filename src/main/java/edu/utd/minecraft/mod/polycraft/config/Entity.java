package edu.utd.minecraft.mod.polycraft.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity {
	private static Logger logger = LogManager.getLogger();

	protected static final String getVariableName(String name) {
		name = name.replaceAll("[^A-Za-z0-9]", "");
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public final String name;

	public Entity(final String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("name");
		}
		if (Character.isLowerCase(name.charAt(0))) {
			logger.warn("Warning: Entity name doesn't match naming convention: " + name);
		}
		this.name = name;
	}
}