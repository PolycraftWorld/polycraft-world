package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.PolycraftTool;

public class GrippedTool extends SourcedConfig<MinecraftItem> {

	public static final ConfigRegistry<GrippedTool> registry = new ConfigRegistry<GrippedTool>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, GrippedTool.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new GrippedTool(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						MinecraftItem.registry.get(line[index++]), //source
						MoldedItem.registry.get(line[index++]), //grip
						PolycraftTool.Material.valueOf(line[index++]), //toolMaterial
						Float.parseFloat(line[index++]), //durabilityBuff
						Float.parseFloat(line[index++]) //speedBuff
				));
			}
	}

	public final MoldedItem grip;
	public final PolycraftTool.Material toolMaterial;
	public final float durabilityBuff;
	public final float speedBuff;

	public GrippedTool(final int[] version, final String gameID, final String name, final MinecraftItem source, final MoldedItem grip, final PolycraftTool.Material toolMaterial, final float durabilityBuff, final float speedBuff) {
		super(version, gameID, name, source);
		this.grip = grip;
		this.toolMaterial = toolMaterial;
		this.durabilityBuff = durabilityBuff;
		this.speedBuff = speedBuff;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Durability Buff", "Speed Buff");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(durabilityBuff),
				PolycraftMod.numFormat.format(speedBuff));
	}
}