package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.PolycraftTool;

public class GrippedSyntheticTool extends SourcedConfig<Tool> {

	public static final ConfigRegistry<GrippedSyntheticTool> registry = new ConfigRegistry<GrippedSyntheticTool>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, GrippedSyntheticTool.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new GrippedSyntheticTool(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						Tool.registry.get(line[index++]), //source
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
	public final int harvestLevel;
	public final int maxUses;
	public final int efficiency;
	public final int damage;
	public final int enchantability;

	public GrippedSyntheticTool(final int[] version, final String gameID, final String name, final Tool source, final MoldedItem grip, final PolycraftTool.Material toolMaterial, final float durabilityBuff, final float speedBuff) {
		super(version, gameID, name, source);
		this.grip = grip;
		this.toolMaterial = toolMaterial;
		this.durabilityBuff = durabilityBuff;
		this.speedBuff = speedBuff;
		this.efficiency = (int) (this.source.efficiency * this.durabilityBuff);
		this.harvestLevel = this.source.harvestLevel;
		this.maxUses = (int) ((this.source.maxUses * (1 + this.durabilityBuff)) + 1);
		this.damage = this.source.damage;
		this.enchantability = this.source.enchantability;
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