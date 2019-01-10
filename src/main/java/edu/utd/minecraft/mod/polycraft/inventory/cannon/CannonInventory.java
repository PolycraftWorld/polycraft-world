package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagInventory;
import net.minecraft.util.IIcon;

public class CannonInventory extends PolycraftInventory {
	
	private static Inventory config;


	public CannonInventory() {
		super(PolycraftContainerType.CANNON, config);
	}
	
	public static final void register(final Inventory config) {
		CannonInventory.config = config;
		config.containerType = PolycraftContainerType.CANNON;
		PolycraftInventory.register(new CannonBlock(config, CannonInventory.class));
	}

}
