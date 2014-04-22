package edu.utd.minecraft.mod.polycraft.inventory;

public enum BlockFace {
	DOWN(0),
	UP(1),
	NORTH(2),
	SOUTH(3),
	WEST(4),
	EAST(5),
	// Special block face to represent the default face.
	DEFAULT(-1),
	// Special block face to represent the front face.
	FRONT(-2);
	
	public static BlockFace getBlockFace(int value) {
		switch(value) {
		case 0:
			return DOWN;
		case 1:
			return UP;
		case 2:
			return NORTH;
		case 3:
			return SOUTH;
		case 4:
			return WEST;
		case 5:
			return EAST;
		}
		return DEFAULT;
	}
	
	private final int value;
	
	public int getValue() {
		return this.value;
	}
	
	private BlockFace(int value) {
		this.value = value;
	}
}
