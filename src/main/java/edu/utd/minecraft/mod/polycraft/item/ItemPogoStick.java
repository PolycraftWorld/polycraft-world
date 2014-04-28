package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;

public class ItemPogoStick extends PolycraftUtilityItem {

	public final PogoStick config;

	public ItemPogoStick(final PogoStick config) {
		this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(config.maxBounces);
		this.config = config;
	}
}
