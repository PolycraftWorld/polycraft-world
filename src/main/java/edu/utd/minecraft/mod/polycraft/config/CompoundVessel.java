package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class CompoundVessel extends SourcedVesselConfig<Compound> {

	public static final SourcedVesselConfigRegistry<Compound, CompoundVessel> registry = new SourcedVesselConfigRegistry<Compound, CompoundVessel>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CompoundVessel.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				final int[] version = PolycraftMod.getVersionNumeric(line[0]);
				if (version != null) {
					for (int i = 0; i < 3; i++) {
						registry.register(new CompoundVessel(
								version, //version
								line[i + 1], //gameID
								line[i + 5], //name
								Compound.registry.get(line[9]), //source
								Vessel.Type.readFromConfig(line[i + 11]) //type
						));
					}
				}
			}
	}

	public CompoundVessel(final int[] version, final String gameID, final String name, final Compound source, final Vessel.Type vesselType) {
		super(version, gameID, name, source, vesselType);
	}
	
	public static void checkItemJSONs(CompoundVessel config, String path){
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