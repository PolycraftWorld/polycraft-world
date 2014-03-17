package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.block.material.MapColor;

public class CompressedBlock extends Entity {

	public static final EntityRegistry<CompressedBlock> registry = new EntityRegistry<CompressedBlock>();

	static {
		for (final Ingot ingot : Ingot.registry.values())
			registry.register(new CompressedBlock(ingot, MapColor.ironColor, 3, 7, 9));
	}

	public final Entity type;
	public final MapColor color;
	public final float hardness;
	public final float resistance;
	public final int itemsPerBlock;

	public CompressedBlock(Entity type, final MapColor color, final float hardness, final float resistance, final int itemsPerBlock) {
		super("compressed_" + type.gameName, type.name);
		this.type = type;
		this.color = color;
		this.hardness = hardness;
		this.resistance = resistance;
		this.itemsPerBlock = itemsPerBlock;
	}
}