package edu.utd.minecraft.mod.polycraft;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.material.MapColor;

public class Element {
	public static final Collection<Element> elements = new ArrayList<Element>();
	static {
		elements.add(new Element(78, "Platinum", "Pt", 10, 30, 10, 10, 3, 5, 1, .1f, 1, 4, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(22, "Titanium", "Ta", 10, 30, 10, 10, 3, 5, 1, .1f, 1, 4, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(46, "Palladium", "Pd", 10, 30, 10, 10, 3, 5, 1, .1f, 1, 4, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(27, "Cobalt", "Co", 10, 30, 10, 10, 3, 5, 1, .1f, 2, 5, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(25, "Manganese", "Mn", 10, 30, 10, 10, 3, 5, 1, .1f, 2, 5, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(12, "Magnesium", "Mg", 10, 30, 10, 10, 3, 5, 1, .1f, 2, 5, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(51, "Antimony", "Sb", 10, 30, 10, 10, 3, 5, 1, .1f, 2, 5, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(13, "Aluminum", "Al", 10, 30, 10, 10, 3, 5, 1, .1f, 2, 5, MapColor.ironColor, 3, 7, 9));
		elements.add(new Element(29, "Copper", "Cu", 10, 30, 10, 10, 3, 5, 1, .1f, 3, 7, MapColor.goldColor, 3, 7, 9));
	}

	private final String gameName;
	public final String blockNameOre;
	public final String blockNameCompressed;
	public final String itemNameIngot;

	public final int atomicNumber;
	public final String name;
	public final String symbol;
	public final int oreStartYMin;
	public final int oreStartYMax;
	public final int oreVeinsPerChunk;
	public final int oreBlocksPerVein;
	public final float oreHardness;
	public final float oreResistance;
	public final int oreSmeltingIngotsPerBlock;
	public final float oreSmeltingExperience;
	public final int dropExperienceMin;
	public final int dropExperienceMax;
	public final MapColor compressedColor;
	public final float compressedHardness;
	public final float compressedResistance;
	public final int compressedIngotsPerBlock;

	public Element(final int atomicNumber, final String name, final String symbol,
			final int oreStartYMin, final int oreStartYMax, final int oreVeinsPerChunk, final int oreBlocksPerVein,
			final float oreHardness, final float oreResistance, final int oreSmeltingIngotsPerBlock, final float oreSmeltingExperience,
			final int dropExperienceMin, final int dropExperienceMax,
			final MapColor compressedColor, final float compressedHardness, final float compressedResistance, final int compressedIngotsPerBlock) {
		this.gameName = "element_" + name.toLowerCase();
		this.blockNameOre = gameName + "_ore";
		this.blockNameCompressed = gameName + "_compressed";
		this.itemNameIngot = gameName + "_ingot";
		this.atomicNumber = atomicNumber;
		this.name = name;
		this.symbol = symbol;
		this.oreStartYMin = oreStartYMin;
		this.oreStartYMax = oreStartYMax;
		this.oreVeinsPerChunk = oreVeinsPerChunk;
		this.oreBlocksPerVein = oreBlocksPerVein;
		this.oreHardness = oreHardness;
		this.oreResistance = oreResistance;
		this.oreSmeltingIngotsPerBlock = oreSmeltingIngotsPerBlock;
		this.oreSmeltingExperience = oreSmeltingExperience;
		this.dropExperienceMin = dropExperienceMin;
		this.dropExperienceMax = dropExperienceMax;
		this.compressedColor = compressedColor;
		this.compressedHardness = compressedHardness;
		this.compressedResistance = compressedResistance;
		this.compressedIngotsPerBlock = compressedIngotsPerBlock;
	}

}