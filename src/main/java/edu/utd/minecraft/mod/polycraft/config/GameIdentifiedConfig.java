package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.item.ItemStack;

public abstract class GameIdentifiedConfig<S extends Config> extends Config {

	public final String gameID;

	public GameIdentifiedConfig(final int[] version, final String gameID, final String name) {
		super(version, name);
		this.gameID = gameID;
	}

	public GameIdentifiedConfig(final int[] version, final String gameID, final String name, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, name, paramNames, paramValues, paramsOffset);
		this.gameID = gameID;
	}

	public ItemStack getItemStack() {
		return getItemStack(1);
	}
	
	public static void checkItemJSONs(Config config, String path){
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
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	public static void checkFluidJSONs(Config config, String path){
		String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "blockstates\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//BlockState file
				String fileContent = String.format("{\n" +
						"  \"forge_marker\": 1, {\n" +
						"  \"defaults\": {\n" +
						"    \"model\": \"forge:fluid\" \n" +
						"  },\n" +
						"	\"variant\": {\n"+
						"		\"oil\": {\n"+
						"			\"custom\": { \"fluid\": \"oil\" }\n"+
						"		 }\n"+
						"	  }\n"+
						"	}\n"+
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();

//				//Model file
//				fileContent = String.format("{\n" +
//						"  \"parent\": \"block/cube_all\",\n" +
//						"  \"textures\": {\n" +
//						"    \"all\": \"polycraft:blocks/%s\"\n" +
//						"  }\n" +
//						"}", texture);
//
//				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + texture + ".json"));
//
//				writer.write(fileContent);
//				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
	
	public static void checkBlockJSONs(Config config, String path){
		String texture = PolycraftMod.getFileSafeName(config.name);
		File json = new File(path + "blockstates\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{
				//BlockState file
				String fileContent = String.format("{\n" +
						"  \"variants\": {\n" +
						"    \"normal\": { \"model\": \"polycraft:%s\" }\n" +
						"  }\n" +
						"}", texture);

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();

				//Model file
				fileContent = String.format("{\n" +
						"  \"parent\": \"block/cube_all\",\n" +
						"  \"textures\": {\n" +
						"    \"all\": \"polycraft:blocks/%s\"\n" +
						"  }\n" +
						"}", texture);

				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();

				//Item model file
				fileContent = String.format("{\n" +
						"    \"parent\": \"polycraft:%s\",\n" +
						"    \"display\": {\n" +
						"        \"thirdperson\": {\n" +
						"            \"rotation\": [ 10, -45, 170 ],\n" +
						"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
						"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
						"        }\n" +
						"    }\n" +
						"}", texture);

				writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public abstract ItemStack getItemStack(final int size);
}
