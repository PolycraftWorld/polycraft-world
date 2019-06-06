package edu.utd.minecraft.mod.polycraft.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class PolymerSlab extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerSlab> registry = new ConfigRegistry<PolymerSlab>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerSlab.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerSlab(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //itemSlabGameID
						line[2], //itemDoubleSlabGameID
						line[3], //blockSlabGameID
						line[4], //blockDoubleSlabGameID
						line[5], //name
						PolymerBlock.registry.get(line[6]), //source
						Integer.parseInt(line[8]) //bounceHeight
				));
	}

	public final int bounceHeight;
	public final String itemSlabGameID;
	public final String itemDoubleSlabGameID;
	public final String itemSlabName;
	public final String itemDoubleSlabName;
	public final String blockSlabGameID;
	public final String blockDoubleSlabGameID;
	public final String blockSlabName;
	public final String blockDoubleSlabName;

	public PolymerSlab(final int[] version, final String itemSlabGameID, final String itemDoubleSlabGameID, final String blockSlabGameID, final String blockDoubleSlabGameID,
			final String name, final PolymerBlock source, final int bounceHeight) {
		super(version, itemSlabGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.itemSlabGameID = itemSlabGameID;
		this.itemDoubleSlabGameID = itemDoubleSlabGameID;
		this.blockSlabGameID = blockSlabGameID;
		this.blockDoubleSlabGameID = blockDoubleSlabGameID;
		this.blockSlabName = name;
		this.blockDoubleSlabName = "Double " + blockSlabName;
		this.itemSlabName = blockSlabName + " Item";
		this.itemDoubleSlabName = blockDoubleSlabName + " Item";
	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemSlabName), size);
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
	
	public static void checkSlabJSONs(PolymerSlab polymerSlab, String path){
		String texture = PolycraftMod.getFileSafeName(polymerSlab.name);
		File json = new File(path + "blockstates\\" + texture + "_half" + ".json");
		if (json.exists())
				return;
		else{
			try{
				//BlockState files
				//single slab
				String fileContent = String.format("{\n" + 
						"    \"variants\": {\n" + 
						"        \"half=bottom,seamless=false,variant=polymer\": { \"model\": \"polycraft:%s\" },\n" + 
						"        \"half=top,seamless=false,variant=polymer\": { \"model\": \"polycraft:%s\" },\n" + 
						"        \"normal\": { \"model\": \"polycraft:%s\" },\n" + 
						"		 \"all\": { \"model\": \"polycraft:%s\" }\n" +
						"    }\n" + 
						"}", texture + "_bottom", texture + "_top", texture + "_bottom", texture + "_bottom");

				BufferedWriter writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + texture + "_half" + ".json"));

				writer.write(fileContent);
				writer.close();
				
				//double slab
				fileContent = String.format("{\n" + 
						"    \"variants\": {\n" + 
						"        \"half=bottom,seamless=false,variant=polymer\": { \"model\": \"polycraft:%s\" },\n" +  
						"        \"normal\": { \"model\": \"polycraft:%s\" },\n" + 
						"		 \"all\": { \"model\": \"polycraft:%s\" }\n" +
						"    }\n" + 
						"}", "block_natural_rubber_white", "block_natural_rubber_white", "block_natural_rubber_white");	//TODO: Eventually add different colors for slabs

				writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + "double_" + texture + "_item" + ".json"));

				writer.write(fileContent);
				writer.close();
				

				//Model file top
				fileContent = String.format("{\r\n" + 
						"\"textures\": {\r\n" + 
						"	\"particle\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"bottom\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"top\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"side\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"overlay\": \"polycraft:blocks/%s\"\r\n" + 
						"},\r\n" + 
						"\"elements\": [\r\n" + 
						"        {   \"from\": [ 0, 8, 0 ],\r\n" + 
						"            \"to\": [ 16, 16, 16 ],\r\n" + 
						"            \"faces\": {\r\n" + 
						"                \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#bottom\" },\r\n" + 
						"                \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#top\", \"tintindex\": 0 },\r\n" + 
						"                \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"north\" },\r\n" + 
						"                \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"south\" },\r\n" + 
						"                \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"west\" },\r\n" + 
						"                \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"east\" }\r\n" + 
						"            }\r\n" + 
						"        },\r\n" + 
						"        {   \"from\": [ 0, 8, 0 ],\r\n" + 
						"            \"to\": [ 16, 16, 16 ],\r\n" + 
						"            \"faces\": {\r\n" + 
						"                \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"north\" },\r\n" + 
						"                \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"south\" },\r\n" + 
						"                \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"west\" },\r\n" + 
						"                \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"east\" }\r\n" + 
						"            }\r\n" + 
						"        }\r\n" + 
						"    ]\r\n" + 
						"}", "polymer_white", "polymer_white", "polymer_white", "polymer_white", "polymer_white");

				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + texture + "_top" + ".json"));

				writer.write(fileContent);
				writer.close();
				
				//model file bottom
				fileContent = String.format("{\r\n" + 
						"\"textures\": {\r\n" + 
						"	\"particle\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"bottom\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"top\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"side\": \"polycraft:blocks/%s\",\r\n" + 
						"        \"overlay\": \"polycraft:blocks/%s\"\r\n" + 
						"},\r\n" + 
						"\"elements\": [\r\n" + 
						"        {   \"from\": [ 0, 0, 0 ],\r\n" + 
						"            \"to\": [ 16, 8, 16 ],\r\n" + 
						"            \"faces\": {\r\n" + 
						"                \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#bottom\" },\r\n" + 
						"                \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#top\", \"tintindex\": 0 },\r\n" + 
						"                \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"north\" },\r\n" + 
						"                \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"south\" },\r\n" + 
						"                \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"west\" },\r\n" + 
						"                \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#side\",   \"cullface\": \"east\" }\r\n" + 
						"            }\r\n" + 
						"        },\r\n" + 
						"        {   \"from\": [ 0, 0, 0 ],\r\n" + 
						"            \"to\": [ 16, 8, 16 ],\r\n" + 
						"            \"faces\": {\r\n" + 
						"                \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"north\" },\r\n" + 
						"                \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"south\" },\r\n" + 
						"                \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"west\" },\r\n" + 
						"                \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"texture\": \"#overlay\", \"tintindex\": 0, \"cullface\": \"east\" }\r\n" + 
						"            }\r\n" + 
						"        }\r\n" + 
						"    ]\r\n" + 
						"}", "polymer_white", "polymer_white", "polymer_white", "polymer_white", "polymer_white");

				writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + texture + "_bottom" + ".json"));

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
						"}", texture + "_half");

				writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + texture + "_half" + ".json"));

				writer.write(fileContent);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}