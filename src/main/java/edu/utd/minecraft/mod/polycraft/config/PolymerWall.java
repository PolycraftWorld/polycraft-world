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

public class PolymerWall extends SourcedConfig<PolymerBlock> {

	public static final ConfigRegistry<PolymerWall> registry = new ConfigRegistry<PolymerWall>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, PolymerWall.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new PolymerWall(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], // blockGameID
						line[2], // itemGameID
						line[3], // name
						PolymerBlock.registry.get(line[4]), // source
						Integer.parseInt(line[6]) // bounceHeight
				));
	}

	public final int bounceHeight;
	public final String blockWallGameID;
	public final String blockWallName;
	public final String itemWallName;
	public final String itemWallGameID;

	public PolymerWall(final int[] version, final String blockGameID, final String itemGameID, final String name, final PolymerBlock source, final int bounceHeight) {
		super(version, blockGameID, name, source);
		this.bounceHeight = bounceHeight;
		this.blockWallGameID = blockGameID;
		this.itemWallGameID = itemGameID;
		this.blockWallName = name;
		this.itemWallName = blockWallName + " Item";

	}

	@Override
	public ItemStack getItemStack(final int size) {
		return new ItemStack(PolycraftRegistry.getItem(itemWallName), size, 15); //15 is white by default
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
	
	public static void checkWallJSONs(PolymerWall polymerWall, String path){
		String texture = PolycraftMod.getFileSafeName(polymerWall.name);
		File json = new File(path + "blockstates\\" + texture + ".json");
		if (json.exists())
				return;
		else{
			try{

				//Item model file
				String fileContent = String.format("{\n" +
						"    \"parent\": \"polycraft:%s\",\n" +
						"    \"display\": {\n" +
						"        \"thirdperson\": {\n" +
						"            \"rotation\": [ 10, -45, 170 ],\n" +
						"            \"translation\": [ 0, 1.5, -2.75 ],\n" +
						"            \"scale\": [ 0.375, 0.375, 0.375 ]\n" +
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
}