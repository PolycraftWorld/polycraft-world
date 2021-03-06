package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

/**
 * Polycraft recipe that will copy the metadata of any dye blocks given as input to the output result.
 * 
 */
public class ColoringPolycraftRecipe extends PolycraftRecipe {

	public ColoringPolycraftRecipe(PolycraftContainerType containerType,
			Iterable<RecipeInput> inputs, Iterable<RecipeComponent> outputs) {
		super(containerType, inputs, outputs);
	}

	private static boolean itemIsDye(ItemStack item) {
		if (item == null || item.getItem() == null) {
			return false;
		}
		return Item.getIdFromItem(Items.dye) == Item.getIdFromItem(item.getItem());
	}

	/**
	 * @return outputs generated by the recipe.
	 */
	@Override
	public Collection<RecipeComponent> getOutputs(IInventory inventory) {
		if (inventory != null) {
			//look for dyes or polymer blocks
			int metaData = -1;

			for (ContainerSlot containerSlot : this.getContainerType().getSlots(SlotType.INPUT)) {
				containerSlot.getClass();
				ItemStack item = inventory.getStackInSlot(containerSlot.getSlotIndex());
				if (itemIsDye(item)) {
					int dyeMeta = item.getItemDamage();
					if (metaData < 0) {
						// metadata hasn't been initialized
						metaData = dyeMeta;
					} else if (metaData != dyeMeta) {
						// Two different color dyes
						return new ArrayList<RecipeComponent>();
					}
				}
			}

			if (metaData > -1) {
				// Create a new item stack with recolored outputs
				List<RecipeComponent> coloredOutputs = Lists.newArrayList();
				for (RecipeComponent output : this.outputs) {
					coloredOutputs.add(new RecipeComponent(output.slot, new ItemStack(output.itemStack.getItem(), output.itemStack.stackSize, metaData)));
				}
				return coloredOutputs;
			}
		}
		return super.getOutputs(inventory);
	}
}
