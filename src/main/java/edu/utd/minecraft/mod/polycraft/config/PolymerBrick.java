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

public class PolymerBrick extends SourcedConfig<PolymerPellets> {

	public static final ConfigRegistry<PolymerBrick> registry = new ConfigRegistry<PolymerBrick>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerBrick.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				registry.register(new PolymerBrick(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // tileEntityID
						line[4], // name
						PolymerPellets.registry.get(line[5]), // L-Bricks
						Integer.parseInt(line[7]), // width
						Integer.parseInt(line[8]), // length
						PolymerBrick.registry.get(line[9]), //subBlockname: null means return same item as one destroyed
						Mold.registry.get(line[10]),
						Integer.parseInt(line[11]), //craftingPellets
						Float.parseFloat(line[12]) //craftingDurationSeconds

				));
			}
	}

	public final int width, length;
	public final String itemName;
	public final String itemGameID;
	public final String tileEntityID;
	public final PolymerBrick subBrick;
	public final Mold brickMold;
	public final int craftingPellets;
	public final float craftingDurationSeconds;

	public PolymerBrick(final int[] version, final String blockGameID, final String itemGameID, final String tileEntityID, final String name,
			final PolymerPellets polymer, final int width, final int length, final PolymerBrick subBrick, final Mold brickMold,
			final int craftingPellets, final float craftingDurationSeconds) {
		super(version, blockGameID, name, polymer);
		this.width = width;
		this.length = length;
		this.itemGameID = itemGameID;
		this.tileEntityID = tileEntityID;
		this.itemName = name + " Item";
		this.subBrick = subBrick;
		this.brickMold = brickMold;
		this.craftingPellets = craftingPellets;
		this.craftingDurationSeconds = craftingDurationSeconds;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getBlock(this), size, 0); // 0 is rainbow by default
	}

	public ItemStack getItemStack(int size, int metadata) {
		if (metadata <= 15)
			return new ItemStack(PolycraftRegistry.getBlock(this), size, metadata);
		else
			return new ItemStack(PolycraftRegistry.getBlock(this), size, 0);

	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Width", "Length");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(width), PolycraftMod.numFormat.format(width));
	}
	
	public static void checkBlockJSONs(PolymerBrick config, String path){
		String blockName = PolycraftMod.getFileSafeName(config.name);
		String textureTop = "";
		String textureBottom = "";
		String textureSide = "";
		String blockStateContent = "{\n" +
				"  \"variants\": {\n";
		BufferedWriter writer;
		
		File json = new File(path + "blockstates\\" + blockName + ".json");
		if (false)
				return;
		else{
			try{
				for(EnumColor color: EnumColor.values()) {
					textureTop = "brick_top_" + color.getName();
					textureBottom = "brick_bottom_" + color.getName();
					textureSide = "brick_side_" + color.getName();
					blockStateContent += String.format("    \"color=%s\": { \"model\": \"polycraft:%s\" }%s\n",color.getName(), blockName + "_" + color.getName(), color.ordinal() == 15? "": ",");
					
					//Model file
					String fileContent = String.format("{\n" +
							"  \"parent\": \"block/cube\",\n" +
							"  \"textures\": {\n" +
							"    \"down\": \"polycraft:blocks/%s\",\r\n" + 
							"    \"up\": \"polycraft:blocks/%s\",\r\n" + 
							"    \"north\": \"polycraft:blocks/%s\",\r\n" + 
							"    \"south\": \"polycraft:blocks/%s\",\r\n" + 
							"    \"east\": \"polycraft:blocks/%s\",\r\n" + 
							"    \"west\": \"polycraft:blocks/%s\"" +
							"  }\n" +
							"}", textureBottom, textureTop, textureSide, textureSide, textureSide, textureSide);

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