package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.ArmorSlot;

public class CustomObject extends GameIdentifiedConfig {

	public static final ConfigRegistry<CustomObject> registry = new ConfigRegistry<CustomObject>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, CustomObject.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
			{
				//System.out.println("Custom object: "+line[2]);
				registry.register(new CustomObject(
						PolycraftMod.getVersionNumeric(line[0]),
						line[1], //gameID
						line[2], //name
						line.length > 7 ? line[7] : null, //maxStackSize
						line.length > 8 ? line[8] : null, //maxStackSize
						line.length > 9 ? line[9].split(",") : null, //paramNames
						line, 10 //params
				));
			}
	}

	public final int maxStackSize;
	public final int flashlightRange;

	public CustomObject(final int[] version, final String gameID, final String name, final String maxStackSize, final String flashlightRange, final String[] paramNames, final String[] paramValues, final int paramsOffset) {
		super(version, gameID, name, paramNames, paramValues, paramsOffset);
		this.maxStackSize = StringUtils.isEmpty(maxStackSize) ? 0 : Integer.parseInt(maxStackSize);
		this.flashlightRange = StringUtils.isEmpty(flashlightRange) ? 0 : Integer.parseInt(flashlightRange);
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftRegistry.getItem(this), size);
	}
	
	public static int getEquippedFlashlightRange(final EntityPlayer player) {
		ItemStack item = player.getCurrentEquippedItem();
		if (item != null) {
			final CustomObject co = PolycraftRegistry.customObjectItems.get(item.getItem());
			if (co != null)
				return co.flashlightRange;
		}
		for (final ArmorSlot armorSlot : ArmorSlot.values()) {
			item = player.getCurrentArmor(armorSlot.getInventoryArmorSlot());
			if (item != null) {
				if (PolycraftRegistry.customObjectItems.containsKey(item.getItem())) {
					final CustomObject co = PolycraftRegistry.customObjectItems.get(item.getItem());
					if (co != null && co.flashlightRange > 0)
						return co.flashlightRange;
				}
			}
		}
		return 0;
	}
}