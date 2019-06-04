package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class DNASampler extends GameIdentifiedConfig {

	public static final ConfigRegistry<DNASampler> registry = new ConfigRegistry<DNASampler>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, DNASampler.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new DNASampler(
						PolycraftMod.getVersionNumeric(line[index++]), //version
						line[index++], //gameID sampler,
						line[index++], //name 
						line[index++], //source animal
						Integer.parseInt(line[index++]),//maxStackSize
						Integer.parseInt(line[index++]) //Level
				));
			}
	}

	public int maxStackSize, level;
	public String sourceEntity;

	public DNASampler(final int[] version, final String gameID, final String name, final String sourceEntity,
			final int maxStackSize, final int level) {
		super(version, gameID, name);
		this.maxStackSize = maxStackSize;
		this.sourceEntity = sourceEntity;
		this.level = level;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
	
	public static void checkItemJSONs(DNASampler config, String path){
		String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "models\\item\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//Item model file
				String fileContent="";
				if(texture.equals("dna_sampler_expert")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_expert\"\n" + 
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
							"}", texture);
					}
				else if(texture.equals("dna_sampler_advanced")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_advanced\"\n" + 
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
							"}", texture);
					}
				else if(texture.equals("dna_sampler_nether2")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_nether2\"\n" + 
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
							"}", texture);
					}
				else if(texture.equals("dna_sampler_intermediate")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_intermediate\"\n" + 
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
							"}", texture);
					}
				else if(texture.equals("dna_sampler_nether")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_nether\"\n" + 
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
							"}", texture);
					}
				else if(texture.equals("dna_sampler_beginner")) {
					fileContent = String.format("{\n" + 
							"    \"parent\": \"builtin/generated\",\n" + 
							"    \"textures\": {\n" + 
							"        \"layer0\": \"polycraft:items/dna_sampler_beginner\"\n" + 
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
							"}", texture);
					}
					else{
						fileContent = String.format("{\n" + 
								"    \"parent\": \"builtin/generated\",\n" + 
								"    \"textures\": {\n" + 
								"        \"layer0\": \"polycraft:items/dna_sampler_used\"\n" + 
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
								"}", texture);
						}

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}