package edu.utd.minecraft.mod.polycraft.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.commons.lang3.StringUtils;

public abstract class Entity {
	private static Logger logger = LogManager.getLogger();

	protected static final String getVariableName(String name) {
		name = name.replaceAll("[^A-Za-z0-9]", "");
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

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

	public static String generate(final String type, final String variable, final String[] params) {
		return String.format("public static final %s %s = registry.register(new %s(%s));", type, variable, type, StringUtils.join(params, ", "));
	}
}