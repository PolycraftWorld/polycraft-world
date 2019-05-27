package edu.utd.minecraft.mod.polycraft.config;

import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.ItemMold;

public class Mold extends GameIdentifiedConfig {

	public enum Type {
		Mold, MetalDie
	}

	public static final ConfigRegistry<Mold> registry = new ConfigRegistry<Mold>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Mold.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Mold(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						Type.valueOf(line[3].replaceAll(" ", "")), //type
						PolymerObject.registry.get(line[4]), //polymerObject
						Integer.parseInt(line[5]) //craftingMaxDamage
				));
	}

	public final Type moldType;
	public final PolymerObject polymerObject;
	public final int craftingMaxDamage;

	public Mold(final int[] version, final String gameID, final String name, final Type moldType, final PolymerObject polymerObject, final int craftingMaxDamage) {
		super(version, gameID, name);
		this.moldType = moldType;
		this.polymerObject = polymerObject;
		this.craftingMaxDamage = craftingMaxDamage;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}

	public ItemStack getItemStack(final Ingot ingot) {
		return ItemMold.setDamagePerUse(new ItemStack(PolycraftRegistry.getItem(this)), ingot.moldDamagePerUse);
	}

	public List<String> PROPERTY_NAMES = ImmutableList.of("Crafting Max Damage");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(PolycraftMod.numFormat.format(craftingMaxDamage));
	}
}
