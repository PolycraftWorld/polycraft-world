package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class GrippedTool extends SourcedConfig<MinecraftItem> {

	public static final ConfigRegistry<GrippedTool> registry = new ConfigRegistry<GrippedTool>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, GrippedTool.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new GrippedTool(
						line[0], //gameID
						line[1], //name
						MinecraftItem.registry.get(line[2]), //source
						MoldedItem.registry.get(line[3]), //grip
						Tool.Material.valueOf(line[4]), //toolMaterial
						Float.parseFloat(line[5]), //durabilityBuff
						Float.parseFloat(line[6]) //speedBuff
				));
	}

	public final MoldedItem grip;
	public final Tool.Material toolMaterial;
	public final float durabilityBuff;
	public final float speedBuff;

	public GrippedTool(final String gameID, final String name, final MinecraftItem source, final MoldedItem grip, final Tool.Material toolMaterial, final float durabilityBuff, final float speedBuff) {
		super(gameID, name, source);
		this.grip = grip;
		this.toolMaterial = toolMaterial;
		this.durabilityBuff = durabilityBuff;
		this.speedBuff = speedBuff;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
}