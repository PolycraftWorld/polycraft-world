package edu.utd.minecraft.mod.polycraft.crafting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

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
	
	// Test that a shapeless only recipe can be found
	@Test
	public void testShapelessRecipe() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1))),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(FilledScubaTank, 1))));
		manager.addRecipe(recipe);
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, new ItemStack(ScubaTank, 1)))));
	}
	
	// Test recipes that have multiple things in common
	@Test
	public void testCommonRecipes() {
		PolycraftRecipe air_tank = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
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
						new RecipeComponent(1, new ItemStack(FilledScubaTank, 1)),
						new RecipeComponent(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(air_tank);

		PolycraftRecipe nitrox_tank = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
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
						new RecipeComponent(1, new ItemStack(FilledScubaTank, 1)),
						new RecipeComponent(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(nitrox_tank);
		
		// Test a valid recipe input
		assertEquals(air_tank, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, AirCanister, 1),
				new RecipeComponent(9, Coal, 1))));
		assertEquals(air_tank, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, AirCanister, 1),
				new RecipeComponent(9, Charcoal, 1))));

		// Test another valid recipe input
		assertEquals(nitrox_tank, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, NitroxCanister, 1),
				new RecipeComponent(9, Coal, 1))));

		// Test an invalid combination of the two
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1),
				new RecipeComponent(2, NitroxCanister, 1), new RecipeComponent(3, AirCanister, 1),
				new RecipeComponent(9, Coal, 1))));
	}
	
	// Test a recipe with shaped and shapeless entries.
	@Test
	public void testRecipes() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
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
						new RecipeComponent(1, new ItemStack(FilledScubaTank, 1)),
						new RecipeComponent(2, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(recipe);
		
		// Test a valid recipe input
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, AirCanister, 1),
				new RecipeComponent(9, Coal, 1))));

		// Test another valid recipe
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, ScubaTank, 1),
				new RecipeComponent(9, Coal, 1))));		
		
		// Test another valid recipe
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(10, AirCanister, 1), new RecipeComponent(11, ScubaTank, 1),
				new RecipeComponent(9, Coal, 1))));		

		// Test with the coal moved around
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, ScubaTank, 1),
				new RecipeComponent(3, Coal, 1))));		
		
		// Test with the coal moved around
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, ScubaTank, 1),
				new RecipeComponent(10, Coal, 1))));		
		
		// Test with no coal
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, ScubaTank, 1))));		
		
		// Test with missing shapeless items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(3, Coal, 1))));		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(3, Coal, 1))));		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(3, ScubaTank, 1))));
		
		// Test with too many items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, ScubaTank, 1),
				new RecipeComponent(3, AirCanister, 1), new RecipeComponent(9, Coal, 1))));		
	}
	
	// Test shaped configurations with multiple placements of the same item
	@Test
	public void testShapedWithDuplicatedItems() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						// AirCanister at slot 1
						RecipeInput.shapedInput(new RecipeSlot(1), new ItemStack(AirCanister, 1)),
						// AirCanister at slot 2
						RecipeInput.shapedInput(new RecipeSlot(2), new ItemStack(AirCanister, 1))),
				// Who knew that air can be recycled into coal
				ImmutableList.of(new RecipeComponent(1, new ItemStack(Coal, 1))));
		manager.addRecipe(recipe);
		
		// Test a valid recipe input
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, AirCanister, 1))));

		// Test a recipe with too few items
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, AirCanister, 1), new RecipeComponent(2, AirCanister, 1),
				new RecipeComponent(3, AirCanister, 1))));
		
		// Test a recipe with too many inputs
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(3, AirCanister, 1))));
		
	}

	// Test recipes with shaped and shapeless inputs of the same type.
	@Test	
	public void testRecipesWithDuplicateShapelessAndShapedInputs1() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						RecipeInput.shapedInput(new RecipeSlot(1), new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));
		manager.addRecipe(recipe);
		
		// Test valid recipe inputs
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, ScubaTank, 1))));
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(6, ScubaTank, 1))));
		
		// Incorrect ordering
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(9, ScubaTank, 1))));
		
		// Too few inputs		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1))));
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(10, ScubaTank, 1))));
		
		// Too many inputs
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(10, ScubaTank, 1))));
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(11, ScubaTank, 1))));		
	}

	// Test recipes with shaped and shapeless inputs of the same type.
	@Test
	public void testRecipesWithDuplicateShapelessAndShapedInputs2() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapedInput(new RecipeSlot(10), new ItemStack(ScubaTank, 1)),
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));		
		manager.addRecipe(recipe);
		
		// Test valid recipe inputs
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(10, ScubaTank, 1))));
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(10, ScubaTank, 1))));

		// Incorrect ordering
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(9, ScubaTank, 1))));
		
		// Too few inputs		
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1))));
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(10, ScubaTank, 1))));
		
		// Too many inputs
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(10, ScubaTank, 1))));
		assertNull(manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(1, ScubaTank, 1), new RecipeComponent(2, ScubaTank, 1), new RecipeComponent(11, ScubaTank, 1))));
	}

	static class TestContainerSlot implements ContainerSlot {
		private final int slotIndex;
		
		public TestContainerSlot(int value) { slotIndex = value; }
		
		@Override
		public int getSlotIndex() { return slotIndex; }

		@Override
		public SlotType getSlotType() { return null; }

		@Override
		public int getRelativeX() { return 0; }

		@Override
		public int getRelativeY() { return 0; }
	}
	
	// Tests that recipes can use different ContainerSlot types
	@Test
	public void testRecipeWithDifferentContainerSlotTypes() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapedInput(new TestContainerSlot(0), new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(new TestContainerSlot(1), new ItemStack(EmptyAirCanister, 1))));		
		manager.addRecipe(recipe);
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR, ImmutableSet.of(
				new RecipeComponent(0, ScubaTank, 1))));
	}
	/*
	// Tests that shaped recipes can be positioned anywhere
	@Test
	public void testRecipeShapeIsRelative() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE,
				ImmutableList.of(
						// Start recipe offset to test that the manager translates it appropriately when adding it
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_MIDDLE_MIDDLE, new ItemStack(ScubaTank, 1)),
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_BOTTOM_RIGHT, new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));		
		manager.addRecipe(recipe);
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CRAFTING_TABLE, ImmutableSet.of(
				new RecipeComponent(GenericCraftingSlot.INPUT_TOP_LEFT.getSlotIndex(), ScubaTank, 1),
				new RecipeComponent(GenericCraftingSlot.INPUT_MIDDLE_MIDDLE.getSlotIndex(), ScubaTank, 1))));
		assertEquals(recipe, manager.findRecipe(PolycraftContainerType.CRAFTING_TABLE, ImmutableSet.of(
				new RecipeComponent(GenericCraftingSlot.INPUT_TOP_MIDDLE.getSlotIndex(), ScubaTank, 1),
				new RecipeComponent(GenericCraftingSlot.INPUT_MIDDLE_RIGHT.getSlotIndex(), ScubaTank, 1))));
	}
	*/
	
	/**
	 * Test that the recipe manager throws an exception if duplicate recipes are entered
	 */
	/*
	@Test(expected=Exception.class)
	public void testRecipeManagerIdentifiesExplicitDuplicates() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE,
				ImmutableList.of(
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_MIDDLE_MIDDLE, new ItemStack(ScubaTank, 1)),
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_BOTTOM_RIGHT, new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));		
		manager.addRecipe(recipe);
		PolycraftRecipe duplicateRecipe = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE,
				ImmutableList.of(
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_MIDDLE_MIDDLE, new ItemStack(ScubaTank, 1)),
						RecipeInput.shapedInput(GenericCraftingSlot.INPUT_BOTTOM_RIGHT, new ItemStack(ScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));
		assertTrue(recipe.equals(duplicateRecipe));
		manager.addRecipe(duplicateRecipe);		
	}
	*/
	/**
	 * Test that the recipe manager throws an exception if duplicate recipes are entered
	 */
	/*
	@Test(expected=Exception.class)
	public void testRecipeManagerIdentifiesShapelessDuplicates2() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE,
				ImmutableList.of(
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						RecipeInput.shapelessInput(new ItemStack(AirCanister, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));		
		manager.addRecipe(recipe);
		PolycraftRecipe duplicateRecipe = new PolycraftRecipe(PolycraftContainerType.CRAFTING_TABLE,
				ImmutableList.of(
						RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1)),
						RecipeInput.shapelessInput(new ItemStack(AirCanister, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));		
		assertTrue(recipe.equals(duplicateRecipe));
		manager.addRecipe(duplicateRecipe);		
	}
	*/
}
