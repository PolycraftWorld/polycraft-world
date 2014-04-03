package edu.utd.minecraft.mod.polycraft.crafting;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class PolyraftRecipeTest {
	private Item Coal = new Item().setUnlocalizedName("coal");
	private Item Charcoal = new Item().setUnlocalizedName("charcoal");
	private Item ScubaTank = new Item().setUnlocalizedName("scuba_tank");
	private Item FilledScubaTank = new Item().setUnlocalizedName("filled_scuba_tank");
	private Item AirCanister = new Item().setUnlocalizedName("air_canister");
	private Item NitroxCanister = new Item().setUnlocalizedName("nitrox_canister");
	private Item EmptyAirCanister = new Item().setUnlocalizedName("empty_air_canister");
	
	private Item TestInputItem1 = new Item().setUnlocalizedName("item1");
	private Item TestInputItem2 = new Item().setUnlocalizedName("item2");
	private Item TestInputItem3 = new Item().setUnlocalizedName("item3");
	private Item TestInputItem4 = new Item().setUnlocalizedName("item4");
	private Item TestInputItem5 = new Item().setUnlocalizedName("item5");
	private Item TestInputItem6 = new Item().setUnlocalizedName("item6");
	private Item TestInputItem7 = new Item().setUnlocalizedName("item7");
	private Item TestInputItem8 = new Item().setUnlocalizedName("item8");
	private Item TestOutputItem1 = new Item().setUnlocalizedName("output1");
	private Item TestOutputItem2 = new Item().setUnlocalizedName("output2");
	
	@Test
	public void testShapelessRecipe() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapelessInput(new ItemStack(TestInputItem1, 2)),
						RecipeInput.shapelessInput(new ItemStack(TestInputItem2, 3))),
				ImmutableList.of(new RecipeComponent(10, new ItemStack(TestOutputItem1))));
		Collection<Set<RecipeComponent>> recipeCombos = recipe.getShapelessCombinations();
		assertSame(1, recipeCombos.size());
		assertSame(0, recipe.getShapedCombinations().size());
	}

	@Test
	public void testShapelessRecipeOneOf() {
		// Recipe: item1 + item3 = output1 OR item1 + item4 = output1 OR item2 + item3 = output1 OR item2 + item4 = output1
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						// Insert items in reverse order.  Should create keys in sorted order.
						RecipeInput.shapelessAnyOneOf(ImmutableList.of(new ItemStack(TestInputItem3, 2), new ItemStack(TestInputItem4, 2))),
						RecipeInput.shapelessAnyOneOf(ImmutableList.of(new ItemStack(TestInputItem1, 2), new ItemStack(TestInputItem2, 2)))
				),
				ImmutableList.of(new RecipeComponent(10, new ItemStack(TestOutputItem1))));
		Collection<Set<RecipeComponent>> recipeCombos = recipe.getShapelessCombinations();
		assertSame(4, recipeCombos.size());
	}

	@Test
	public void testShapelessWithFixed() {
		// Recipe: item1 + item3 = output1 OR item1 + item4 = output1 OR item2 + item3 = output1 OR item2 + item4 = output1
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapedAnyOneOf(new RecipeSlot(1), ImmutableList.of(new ItemStack(TestInputItem5, 1), new ItemStack(TestInputItem6, 3))),
						RecipeInput.shapedAnyOneOf(new RecipeSlot(2), ImmutableList.of(new ItemStack(TestInputItem7, 2), new ItemStack(TestInputItem8, 4))),
						// Insert items in reverse order.  Should create keys in sorted order.
						RecipeInput.shapelessAnyOneOf(ImmutableList.of(new ItemStack(TestInputItem3, 1), new ItemStack(TestInputItem4, 3))),
						RecipeInput.shapelessAnyOneOf(ImmutableList.of(new ItemStack(TestInputItem1, 2), new ItemStack(TestInputItem2, 4)))
				),
				ImmutableList.of(new RecipeComponent(10, new ItemStack(TestOutputItem1))));
		Collection<Set<RecipeComponent>> shapeless = recipe.getShapelessCombinations();
		assertSame(4, shapeless.size());
		assertTrue(shapeless.contains(
				ImmutableSet.of(
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem1, 5)),
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem3, 5)))));
		assertTrue(shapeless.contains(
				ImmutableSet.of(
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem1, 5)),
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem4, 5)))));
		assertTrue(shapeless.contains(
				ImmutableSet.of(
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem2, 5)),
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem3, 5)))));
		assertTrue(shapeless.contains(
				ImmutableSet.of(
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem2, 5)),
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem4, 5)))));
		assertFalse(shapeless.contains(
				ImmutableSet.of(
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem1, 5)),
						new RecipeComponent(RecipeSlot.ANY, new ItemStack(TestInputItem2, 5)))));

		Collection<Set<RecipeComponent>> shaped = recipe.getShapedCombinations();
		assertSame(4, shapeless.size());
	}

	// Test that recipes with inputs in the same slot throw an exception
	@Test(expected=IllegalArgumentException.class)
	public void testRecipesWithInputsInSameSlots() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(
						RecipeInput.shapedInput(new RecipeSlot(1), new ItemStack(ScubaTank, 1)),
						RecipeInput.shapedInput(new RecipeSlot(1), new ItemStack(FilledScubaTank, 1))
				),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));
	}

	// Test that recipes with outputs in the same slot throw an exception
	@Test(expected=IllegalArgumentException.class)
	public void testRecipesWithOutputsInSameSlots() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1))),
				ImmutableList.of(
						// Two outputs in the same slot
						new RecipeComponent(1, new ItemStack(FilledScubaTank, 1)),
						new RecipeComponent(1, new ItemStack(EmptyAirCanister, 1))));
	}

	// Test that recipes with no inputs throw an exception
	@Test(expected=IllegalArgumentException.class)
	public void testRecipesWithNoInputs() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				new ArrayList<RecipeInput>(),
				ImmutableList.of(new RecipeComponent(1, new ItemStack(FilledScubaTank, 1))));
	}	
		
	// Test that recipes with no inputs throw an exception
	@Test(expected=IllegalArgumentException.class)
	public void testRecipesWithNoOutputs() {
		PolycraftRecipe recipe = new PolycraftRecipe(PolycraftContainerType.CHEMICAL_PROCESSOR,
				ImmutableList.of(RecipeInput.shapelessInput(new ItemStack(ScubaTank, 1))),
				new ArrayList<RecipeComponent>());
	}

}
