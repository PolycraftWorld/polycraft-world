package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.entity.player.InventoryPlayer;

public class GravelCannonInventory extends CannonInventory {
	
	private static Inventory config;
	public boolean useVelocity;
	public boolean useYaw;
	public boolean usePitch;
	

	public GravelCannonInventory() {
		super();
		this.velocity=20.0;
		this.theta=0.0;
		this.mass=1.0;
		this.phi=0.0;
	}
	
	
	public static void register(final Inventory config) {
		GravelCannonInventory.config = config;
		config.containerType = PolycraftContainerType.CANNON;
		PolycraftInventory.register(new GravelCannonBlock(config, GravelCannonInventory.class));
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 133, false);
		return new GravelCannonGui(this, playerInventory,config);
	}
	

}
