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
import edu.utd.minecraft.mod.polycraft.block.BlockPolymerHelper.EnumColor;

public class PolymerBlock extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerBlock> registry = new ConfigRegistry<PolymerBlock>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBlock.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new PolymerBlock(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerPellets.registry.get(line[4]), // polymerPellets
						Integer.parseInt(line[6]) // bounceHeight
				));
			}
	}

	public final int bounceHeight;
	public final String itemName;
	public final String itemGameID;

	public PolymerBlock(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerPellets source, final int bounceHeight) {
		super(version, blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.itemGameID = itemGameID;
		this.itemName = name + " Item";
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 15); //15 is white by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
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
	
	public static void checkBlockJSONs(PolymerBlock config, String path){
		String blockName = PolycraftMod.getFileSafeName(config.name);
		String texture = "";
		String blockStateContent = "{\n" +
				"  \"variants\": {\n";
		BufferedWriter writer;
		
		File json = new File(path + "blockstates\\" + blockName + ".json");
		if (false)
				return;
		else{
			try{
				for(EnumColor color: EnumColor.values()) {
					texture = "polymer_" + color.getName();
					blockStateContent += String.format("    \"color=%s\": { \"model\": \"polycraft:%s\" }%s\n",color.getName(), blockName + "_" + color.getName(), color.ordinal() == 15? "": ",");
					
					//Model file
					String fileContent = String.format("{\n" +
							"  \"parent\": \"block/cube_all\",\n" +
							"  \"textures\": {\n" +
							"    \"all\": \"polycraft:blocks/%s\",\n" +
							"    \"normal\": \"polycraft:blocks/%s\"\n" +
							"  }\n" +
							"}", texture, texture);

					writer = new BufferedWriter(new FileWriter(path + "models\\block\\" + blockName + "_" + color.getName() + ".json"));

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
							"}", blockName + "_" + color.getName());

					writer = new BufferedWriter(new FileWriter(path + "models\\item\\" + blockName + "_" + color.getName() + ".json"));

					writer.write(fileContent);
					writer.close();
					
					//blockstate files for items (apparently needed)
					//Model file
					fileContent = String.format("{\n" + 
							"  \"variants\": {\n" + 
							"    \"normal\": { \"model\": \"polycraft:%s\" }\n" + 
							"  }\n" + 
							"}", blockName + "_" + color.getName());

					writer = new BufferedWriter(new FileWriter(path + "blockstates\\\\" + blockName + "_" + color.getName() + ".json"));

					writer.write(fileContent);
					writer.close();
				}
				
				blockStateContent += "  }\n" +
						"}";
				writer = new BufferedWriter(new FileWriter(path + "blockstates\\" + blockName + ".json"));

				writer.write(blockStateContent);
				writer.close();

				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}