package edu.utd.minecraft.mod.polycraft.util;

import java.util.Map;

import com.google.common.collect.Maps;

import edu.utd.minecraft.mod.polycraft.config.Fuel.QuantifiedFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class FuelHandler  implements IFuelHandler{

	public static final Map<Item, QuantifiedFuel> quantifiedFuelsByItem = Maps.newLinkedHashMap();
	public static FuelHandler instance = new FuelHandler();

	@Override
	public int getBurnTime(ItemStack fuel) {
		if(fuel.getItem() != null) {
			if(quantifiedFuelsByItem.containsKey(fuel.getItem())) {
				return (int) (quantifiedFuelsByItem.get(fuel.getItem()).getHeatDuration() * 20);
			}
		}
		return 0;
	}
}
