package edu.utd.minecraft.mod.polycraft.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class Entity {
	private static Logger logger = LogManager.getLogger();
	
	protected static final String getSafeName(final String name) {
		return name.replaceAll("[^_A-Za-z0-9]", "_").toLowerCase();
	}

	public final String gameName;
	public final String name;

	public Entity(final String gameName, final String name) {
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("name");
		}
		if (Character.isLowerCase(name.charAt(0))) {
			logger.warn("Warning: Entity name doesn't match naming convention: " + name);			
		}
		this.gameName = gameName;
		this.name = name;
	}

	public String export(final String delimiter) {
		return name;
	}
}