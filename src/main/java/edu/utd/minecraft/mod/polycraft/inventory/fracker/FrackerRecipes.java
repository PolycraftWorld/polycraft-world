package edu.utd.minecraft.mod.polycraft.inventory.fracker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FrackerRecipes {

	/**
	 * The list of fracking results.
	 */
	private static final Map frackingList = new HashMap();
	private static final Map experienceList = new HashMap();

	public static void addRecipe(Block input, ItemStack output, float xp) {
		addRecipe(Item.getItemFromBlock(input), output, xp);
	}

	public static void addRecipe(Item input, ItemStack output, float xp) {
		addRecipe(new ItemStack(input, 1, 32767), output, xp);
	}

	public static void addRecipe(ItemStack input, ItemStack output, float xp) {
		frackingList.put(input, output);
		experienceList.put(output, Float.valueOf(xp));
	}

	/**
	 * Returns the fracking result of an item.
	 */
	public static ItemStack getFrackingResult(ItemStack p_151395_1_) {
		Iterator iterator = frackingList.entrySet().iterator();
		Entry entry;

		do {
			if (!iterator.hasNext()) {
				return null;
			}

			entry = (Entry) iterator.next();
		} while (!func_151397_a(p_151395_1_, (ItemStack) entry.getKey()));

		return (ItemStack) entry.getValue();
	}

	private static boolean func_151397_a(ItemStack p_151397_1_, ItemStack p_151397_2_) {
		return p_151397_2_.getItem() == p_151397_1_.getItem() && (p_151397_2_.getItemDamage() == 32767 || p_151397_2_.getItemDamage() == p_151397_1_.getItemDamage());
	}

	public static Map getFrackingList() {
		return frackingList;
	}

	public static float func_151398_b(ItemStack p_151398_1_) {
		float ret = p_151398_1_.getItem().getSmeltingExperience(p_151398_1_);
		if (ret != -1)
			return ret;

		Iterator iterator = experienceList.entrySet().iterator();
		Entry entry;

		do {
			if (!iterator.hasNext()) {
				return 0.0F;
			}

			entry = (Entry) iterator.next();
		} while (!func_151397_a(p_151398_1_, (ItemStack) entry.getKey()));

		return ((Float) entry.getValue()).floatValue();
	}
}