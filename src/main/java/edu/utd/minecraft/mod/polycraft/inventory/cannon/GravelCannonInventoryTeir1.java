package edu.utd.minecraft.mod.polycraft.inventory.cannon;

import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventory;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryGui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GravelCannonInventoryTeir1 extends GravelCannonInventory {
	
	private static Inventory config;
	public boolean useVelocity;
	public boolean useYaw;
	public boolean usePitch;
	

	public GravelCannonInventoryTeir1() {
		super();
		this.useVelocity=this.config.params.getBoolean(0);
		this.useYaw=this.config.params.getBoolean(1);
		this.usePitch=this.config.params.getBoolean(2);
		this.velocity=15.0;
		this.theta=0.0;
		this.phi=0.0;
		this.mass=1.0;
	}
	
	
	public static final void register(final Inventory config) {
		GravelCannonInventoryTeir1.config = config;
		config.containerType = PolycraftContainerType.CANNON;
		PolycraftInventory.register(new GravelCannonBlock(config, GravelCannonInventoryTeir1.class));
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	public PolycraftInventoryGui getGui(final InventoryPlayer playerInventory) {
		// return new PolycraftInventoryGui(this, playerInventory, 133, false);
		return new GravelCannonGui(this, playerInventory,config);
	}
	

}
