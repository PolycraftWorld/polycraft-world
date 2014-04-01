package edu.utd.minecraft.mod.polycraft.crafting;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;
import org.junit.experimental.theories.Theory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PolycraftRecipeManagerTest {
	private PolycraftRecipeManager manager = new PolycraftRecipeManager();
	
	// Some fake items
	private Item Coal = new Item().setUnlocalizedName("coal");
	private Item Charcoal = new Item().setUnlocalizedName("charcoal");
	private Item ScubaTank = new Item().setUnlocalizedName("scuba_tank");
	private Item FilledScubaTank = new Item().setUnlocalizedName("filled_scuba_tank");
	private Item AirCanister = new Item().setUnlocalizedName("air_canister");
	private Item NitroxCanister = new Item().setUnlocalizedName("nitrox_canister");
	private Item EmptyAirCanister = new Item().setUnlocalizedName("empty_air_canister");
	
	// Test recipes that have multiple things in common
	@Test
	public void testCommonRecipes() {
		PolycraftRecipe air_tank = new PolycraftRecipe<PolycraftCraftingContainer>(
				ImmutableList.of(
						// Scuba tank can go anywhere
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						// Air canister can go anywhere
						RecipeInput.shapelessInput(new ItemStack(AirCanister, 1)),
						// Fuel can either be coal or charcoal
						RecipeInput.shapedAnyOneOf(new RecipeSlot(9), ImmutableList.of(
								new ItemStack(Coal, 1), new ItemStack(Charcoal, 1)))),
				// Recipe returns a filled scuba tank and an empty air canister
				ImmutableList.of(
						new SingleRecipeInput(1, new ItemStack(FilledScubaTank, 1)),
						new SingleRecipeInput(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, air_tank);

		PolycraftRecipe nitrox_tank = new PolycraftRecipe<PolycraftCraftingContainer>(
				ImmutableList.of(
						// Scuba tank can go anywhere
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						// Air canister can go anywhere
						RecipeInput.shapelessInput(new ItemStack(NitroxCanister, 1)),
						// Fuel can either be coal or charcoal
						RecipeInput.shapedAnyOneOf(new RecipeSlot(9), ImmutableList.of(
								new ItemStack(Coal, 1), new ItemStack(Charcoal, 1)))),
				// Recipe returns a filled scuba tank and an empty air canister
				ImmutableList.of(
						new SingleRecipeInput(1, new ItemStack(FilledScubaTank, 1)),
						new SingleRecipeInput(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, nitrox_tank);
		
		// Test a valid recipe input
		assertEquals(air_tank, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, ScubaTank, 1), new SingleRecipeInput(2, AirCanister, 1),
				new SingleRecipeInput(9, Coal, 1))));

		// Test another valid recipe input
		assertEquals(nitrox_tank, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, ScubaTank, 1), new SingleRecipeInput(2, NitroxCanister, 1),
				new SingleRecipeInput(9, Coal, 1))));

		// Test an invalid combination of the two
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, ScubaTank, 1),
				new SingleRecipeInput(2, NitroxCanister, 1), new SingleRecipeInput(3, AirCanister, 1),
				new SingleRecipeInput(9, Coal, 1))));
	}
	
	// Test a recipe with shaped and shapeless entries.
	@Test
	public void testRecipes() {
		PolycraftRecipe recipe = new PolycraftRecipe<PolycraftCraftingContainer>(
				ImmutableList.of(
						// Scuba tank can go anywhere
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						// Air canister can go anywhere
						RecipeInput.shapelessInput(new ItemStack(AirCanister, 1)),
						// Fuel can either be coal or charcoal
						RecipeInput.shapedAnyOneOf(new RecipeSlot(9), ImmutableList.of(
								new ItemStack(Coal, 1), new ItemStack(Charcoal, 1)))),
				// Recipe returns a filled scuba tank and an empty air canister
				ImmutableList.of(
						new SingleRecipeInput(1, new ItemStack(FilledScubaTank, 1)),
						new SingleRecipeInput(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, recipe);
		
		// Test a valid recipe input
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, ScubaTank, 1), new SingleRecipeInput(2, AirCanister, 1),
				new SingleRecipeInput(9, Coal, 1))));

		// Test another valid recipe
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, ScubaTank, 1),
				new SingleRecipeInput(9, Coal, 1))));		
		
		// Test another valid recipe
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(10, AirCanister, 1), new SingleRecipeInput(11, ScubaTank, 1),
				new SingleRecipeInput(9, Coal, 1))));		

		// Test with the coal moved around
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, ScubaTank, 1),
				new SingleRecipeInput(3, Coal, 1))));		
		
		// Test with the coal moved around
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, ScubaTank, 1),
				new SingleRecipeInput(10, Coal, 1))));		
		
		// Test with no coal
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, ScubaTank, 1))));		
		
		// Test with missing shapeless items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, ScubaTank, 1), new SingleRecipeInput(3, Coal, 1))));		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(3, Coal, 1))));		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(3, ScubaTank, 1))));
		
		// Test with too many items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, ScubaTank, 1),
				new SingleRecipeInput(3, AirCanister, 1), new SingleRecipeInput(9, Coal, 1))));		
	}
	
	// Test shaped configurations with multiple placements of the same item
	@Test
	public void testShapedWithDuplicatedItems() {
		PolycraftRecipe recipe = new PolycraftRecipe<PolycraftCraftingContainer>(
				ImmutableList.of(
						// AirCanister at slot 1
						RecipeInput.shapedInput(new RecipeSlot(1), new ItemStack(AirCanister, 1)),
						// AirCanister at slot 2
						RecipeInput.shapedInput(new RecipeSlot(2), new ItemStack(AirCanister, 1))),
				// Who knew that air can be recycled into coal
				ImmutableList.of(new SingleRecipeInput(1, new ItemStack(Coal, 1))));
		manager.addRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, recipe);
		
		// Test a valid recipe input
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, AirCanister, 1))));

		// Test a recipe with too few items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(1, AirCanister, 1), new SingleRecipeInput(2, AirCanister, 1),
				new SingleRecipeInput(3, AirCanister, 1))));
		
		// Test a recipe with too many inputs
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new SingleRecipeInput(3, AirCanister, 1))));
		
	}

}
