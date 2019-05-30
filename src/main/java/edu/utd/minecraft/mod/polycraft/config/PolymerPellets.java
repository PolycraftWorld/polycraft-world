package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class PolymerPellets extends SourcedVesselConfig<Polymer> {

	public static final SourcedVesselConfigRegistry<Polymer, PolymerPellets> registry = new SourcedVesselConfigRegistry<Polymer, PolymerPellets>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerPellets.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null) {
					for (int i = 0; i < 3; i++) {
						final String name = line[i + 5];
						registry.register(new PolymerPellets(
								version, //version
								line[i + 1], //gameID
								name, //name
								Polymer.registry.get(line[9]), //source
								Vessel.Type.readFromConfig(name.substring(0, name.indexOf("("))), //type
								Integer.parseInt(line[10]), // craftingMinHeatIntensity
								Integer.parseInt(line[11]) // craftingMaxHeatIntensity
						));
					}
				}
			}
	}

	public final int craftingMinHeatIntensity;
	public final int craftingMaxHeatIntensity;

	public PolymerPellets(final int[] version, final String gameID, final String name, final Polymer source, final Vessel.Type vesselType, final int craftingMinHeatIntensity, final int craftingMaxHeatIntensity) {
		super(version, gameID, name, source, vesselType);
		this.craftingMinHeatIntensity = craftingMinHeatIntensity;
		this.craftingMaxHeatIntensity = craftingMaxHeatIntensity;
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Crafting Min Heat Intensity", "Crafting Max Heat Intensity");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(craftingMinHeatIntensity),
				PolycraftMod.numFormat.format(craftingMaxHeatIntensity));
	}
	
	public static void checkItemJSONs(PolymerPellets config, String path){
		String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "models\\item\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//Item model file
				String fileContent = String.format("{\n" + 
						"    \"parent\": \"builtin/generated\",\n" + 
						"    \"textures\": {\n" + 
						"        \"layer0\": \"polycraft:items/%s\"\n" + 
						"    },\n" + 
						"    \"display\": {\n" + 
						"        \"thirdperson\": {\n" + 
						"            \"rotation\": [ -90, 0, 0 ],\n" + 
						"            \"translation\": [ 0, 1, -3 ],\n" + 
						"            \"scale\": [ 0.55, 0.55, 0.55 ]\n" + 
						"        },\n" + 
						"        \"firstperson\": {\n" + 
						"            \"rotation\": [ 0, -135, 25 ],\n" + 
						"            \"translation\": [ 0, 4, 2 ],\n" + 
						"            \"scale\": [ 1.7, 1.7, 1.7 ]\n" + 
						"        }\n" + 
						"    }\n" + 
						"}", PolycraftMod.getFileSafeName("vessel" + "_" + config.vesselType.toString()));

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}