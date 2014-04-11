package edu.utd.minecraft.mod.polycraft.config;

public class CompressedBlock extends Entity {

	public static final EntityRegistry<CompressedBlock> registry = new EntityRegistry<CompressedBlock>();

	public final Ingot type;
	public final int itemsPerBlock;

	public CompressedBlock(final Ingot type) {
		this(type, 9);
	}

	public CompressedBlock(final Ingot type, final int itemsPerBlock) {
		super(type.name);
		this.type = type;
		this.itemsPerBlock = itemsPerBlock;
	}
}