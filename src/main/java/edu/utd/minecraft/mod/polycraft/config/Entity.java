package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

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

	protected static Collection<String[]> readConfig(final String directory, final String name, final String extension, final String delimeter) {
		Collection<String[]> config = new LinkedList<String[]>();
		final BufferedReader br = new BufferedReader(new InputStreamReader(PolycraftMod.class.getClassLoader().getResourceAsStream(directory + "/" + name + "." + extension)));
		try {
			br.readLine();//skip the first line (headers)
			for (String line; (line = br.readLine()) != null;) {
				config.add(line.split(delimeter));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return config;
	}

	public static void registerFromConfigs(final String directory, final String extension, final String delimeter) {
		Element.registerFromConfig(directory, extension, delimeter);
		Mineral.registerFromConfig(directory, extension, delimeter);
		Alloy.registerFromConfig(directory, extension, delimeter);
		Compound.registerFromConfig(directory, extension, delimeter);
		Polymer.registerFromConfig(directory, extension, delimeter);

		Ore.registerFromConfig(directory, extension, delimeter);
		Ingot.registerFromConfig(directory, extension, delimeter);
		CompressedBlock.registerFromConfig(directory, extension, delimeter);
		Catalyst.registerFromConfig(directory, extension, delimeter);
		//TODO Vessels
		PolymerPellets.registerFromConfig(directory, extension, delimeter);
		PolymerFibers.registerFromConfig(directory, extension, delimeter);
		PolymerSlab.registerFromConfig(directory, extension, delimeter);
		PolymerBlock.registerFromConfig(directory, extension, delimeter);
	}

	private static Map<String, EntityRegistry<? extends Entity>> registriesByType = new HashMap<String, EntityRegistry<? extends Entity>>();
	static
	{
		registriesByType.put(Element.class.getSimpleName(), Element.registry);
		registriesByType.put(Mineral.class.getSimpleName(), Mineral.registry);
		registriesByType.put(Alloy.class.getSimpleName(), Alloy.registry);
		registriesByType.put(Compound.class.getSimpleName(), Compound.registry);
		registriesByType.put(Polymer.class.getSimpleName(), Polymer.registry);

		registriesByType.put(Ore.class.getSimpleName(), Ore.registry);
		registriesByType.put(Ingot.class.getSimpleName(), Ingot.registry);
		registriesByType.put(Catalyst.class.getSimpleName(), Catalyst.registry);
		//TODO Vessels
		registriesByType.put(Catalyst.class.getSimpleName(), Catalyst.registry);
		registriesByType.put(PolymerPellets.class.getSimpleName(), PolymerPellets.registry);
		registriesByType.put(PolymerFibers.class.getSimpleName(), PolymerFibers.registry);
		registriesByType.put(PolymerSlab.class.getSimpleName(), PolymerSlab.registry);
		registriesByType.put(PolymerBlock.class.getSimpleName(), PolymerBlock.registry);
	}

	public static Entity find(final String type, final String name) {
		final EntityRegistry<? extends Entity> registry = registriesByType.get(type);
		if (registry != null) {
			return registry.get(name);
		}
		return null;
	}
}