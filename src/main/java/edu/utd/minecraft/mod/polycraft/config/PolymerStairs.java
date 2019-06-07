package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerStairs extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerStairs> registry = new ConfigRegistry<PolymerStairs>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerStairs.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerStairs(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerBlock.registry.get(line[4]), // source
						Integer.parseInt(line[6]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String blockStairsGameID;
	public final String blockStairsName;
	public final String itemStairsName;
	public final String itemStairsGameID;

	public PolymerStairs(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerBlock source, final int bounceHeight) {
		super(version, blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.blockStairsGameID = blockGameID;
		this.itemStairsGameID = itemGameID;
		this.blockStairsName = name;
		this.itemStairsName = blockStairsName + " Item";

	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemStairsName), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Bounce Height");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(bounceHeight));
	}
	
	public static void checkStairsJSONs(PolymerStairs polymerStairs, String path){
		String texture = PolycraftMod.getFileSafeName(polymerStairs.name);
		File json = new File(path + "models\\block\\" + "polymer_stairs" + ".json");
		//First check for polymer_wall JSON files. These will be parent for all polymer walls
		if (!json.exists()) {
			ArrayList<String> wallTypes = new ArrayList<String>();
			wallTypes.add("stairs");
			wallTypes.add("inner_stairs");
			wallTypes.add("outer_stairs");
			
			try{
				for(String type: wallTypes) {
					//Item model file
					String fileContent = String.format("{\n" + 
							"    \"parent\": \"block/%s\",\n" + 
							"    \"textures\": {\n" + 
							"        \"bottom\": \"polycraft:blocks/polymer_white\",\n" + 
							"        \"top\": \"polycraft:blocks/polymer_white\",\n" + 
							"        \"side\": \"polycraft:blocks/polymer_white\"\n" + 
							"    }\n" + 
							"}", type);

					BufferedWriter writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + "polymer_" + type + ".json"));

					writer.write(fileContent);
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Now Check for this polymer type's wall JSONs
		json = new File(path + "blockstates\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{

				//Block model file
				String fileContent = String.format("{\n" + 
						"    \"variants\": {\n" + 
						"        \"facing=east,half=bottom,shape=straight\":  { \"model\": \"polycraft:polymer_stairs\" },\n" + 
						"        \"facing=west,half=bottom,shape=straight\":  { \"model\": \"polycraft:polymer_stairs\", \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=bottom,shape=straight\": { \"model\": \"polycraft:polymer_stairs\", \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=bottom,shape=straight\": { \"model\": \"polycraft:polymer_stairs\", \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=bottom,shape=outer_right\":  { \"model\": \"polycraft:polymer_outer_stairs\" },\n" + 
						"        \"facing=west,half=bottom,shape=outer_right\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=bottom,shape=outer_right\": { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=bottom,shape=outer_right\": { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=bottom,shape=outer_left\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=west,half=bottom,shape=outer_left\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=south,half=bottom,shape=outer_left\": { \"model\": \"polycraft:polymer_outer_stairs\" },\n" + 
						"        \"facing=north,half=bottom,shape=outer_left\": { \"model\": \"polycraft:polymer_outer_stairs\", \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=east,half=bottom,shape=inner_right\":  { \"model\": \"polycraft:polymer_inner_stairs\" },\n" + 
						"        \"facing=west,half=bottom,shape=inner_right\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=bottom,shape=inner_right\": { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=bottom,shape=inner_right\": { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=bottom,shape=inner_left\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=west,half=bottom,shape=inner_left\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=south,half=bottom,shape=inner_left\": { \"model\": \"polycraft:polymer_inner_stairs\" },\n" + 
						"        \"facing=north,half=bottom,shape=inner_left\": { \"model\": \"polycraft:polymer_inner_stairs\", \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=east,half=top,shape=straight\":  { \"model\": \"polycraft:polymer_stairs\", \"x\": 180, \"uvlock\": true },\n" + 
						"        \"facing=west,half=top,shape=straight\":  { \"model\": \"polycraft:polymer_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=top,shape=straight\": { \"model\": \"polycraft:polymer_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=top,shape=straight\": { \"model\": \"polycraft:polymer_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=top,shape=outer_right\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"uvlock\": true },\n" + 
						"        \"facing=west,half=top,shape=outer_right\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=top,shape=outer_right\": { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=top,shape=outer_right\": { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=top,shape=outer_left\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=west,half=top,shape=outer_left\":  { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=south,half=top,shape=outer_left\": { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=north,half=top,shape=outer_left\": { \"model\": \"polycraft:polymer_outer_stairs\", \"x\": 180, \"uvlock\": true },\n" + 
						"        \"facing=east,half=top,shape=inner_right\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"uvlock\": true },\n" + 
						"        \"facing=west,half=top,shape=inner_right\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=south,half=top,shape=inner_right\": { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=north,half=top,shape=inner_right\": { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=east,half=top,shape=inner_left\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" + 
						"        \"facing=west,half=top,shape=inner_left\":  { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" + 
						"        \"facing=south,half=top,shape=inner_left\": { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" + 
						"        \"facing=north,half=top,shape=inner_left\": { \"model\": \"polycraft:polymer_inner_stairs\", \"x\": 180, \"uvlock\": true },\n" + 
						"		 \"normal\":  { \"model\": \"polycraft:polymer_stairs\", \"y\": 180, \"uvlock\": true }\n" + 
						"    }\n" + 
						"}");

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
				
				
				//Item model file
				fileContent = String.format("{\n" + 
						"    \"parent\": \"polycraft:polymer_stairs\",\n" + 
						"    \"display\": {\n" + 
						"        \"thirdperson\": {\n" + 
						"            \"rotation\": [ 10, -45, 170 ],\n" + 
						"            \"translation\": [ 0, 1.5, -2.75 ],\n" + 
						"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" + 
						"        },\n" +
						"        \"gui\": {\n" + 
						"            \"rotation\": [ 0, 180, 0 ]\n" + 
						"        }\n" +
						"    }\n" + 
						"}");

				writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}