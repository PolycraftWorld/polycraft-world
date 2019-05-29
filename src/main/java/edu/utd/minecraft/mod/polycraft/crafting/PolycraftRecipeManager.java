package edu.utd.minecraft.mod.polycraft.crafting;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.item.PolycraftItemHelper;
import edu.utd.minecraft.mod.polycraft.util.SetMap;

/**
 * Utility class to manage recipes for each type of container and handle fast searching for both shaped and shapeless recipes.
 */
public class PolycraftRecipeManager {
	private static Logger logger = LogManager.getLogger();

	private static PolycraftRecipeFactory defaultRecipeFactory = new GenericPolycraftRecipeFactory();

	// IRecipe implementation to process Polycraft recipes as generic crafting recipes, giving
	// the generic crafting recipes the benefits of the PolycraftRecipeManager.
	public static class CustomGenericCraftingRecipe implements IRecipe {
		private final PolycraftRecipeManager recipeManager;

		public CustomGenericCraftingRecipe(final PolycraftRecipeManager recipeManager) {
			this.recipeManager = recipeManager;
		}

		public static Set<RecipeComponent> getComponentsFromInventory(IInventory inventory) {
			Set<RecipeComponent> inputs = Sets.newLinkedHashSet();
			for (int i = 0; i < inventory.getSizeInventory(); ++i) {
				ItemStack itemStack = inventory.getStackInSlot(i);
				if (itemStack != null) {
					inputs.add(new RecipeComponent(i, itemStack));
				}
			}
			return inputs;
		}

		@Override
		public boolean matches(InventoryCrafting inventory, World world) {
			return recipeManager.findRecipe(PolycraftContainerType.CRAFTING_TABLE,
					getComponentsFromInventory(inventory)) != null;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inventory) {
			Set<RecipeComponent> inputs = getComponentsFromInventory(inventory);
			PolycraftRecipe recipe = recipeManager.findRecipe(PolycraftContainerType.CRAFTING_TABLE, inputs);
			if (recipe != null) {
				Collection<RecipeComponent> outputs = recipe.getOutputs(inventory);

				if (outputs.size() > 1) {
					logger.warn("Generic crafting result is returning more than one output! Only the first will be returned ("
							+ outputs.iterator().next().itemStack.getItem().getUnlocalizedName() + ")");
				}

				// Flag this result item as being processed by the PolycraftRecipeManager.  This way the
				// onItemCraftedEvent callback can recognize it.
				ItemStack item = outputs.iterator().next().itemStack.copy();
				if (item.getTagCompound() == null) {
					//PolycraftItemHelper.createTagCompound(item);
					//	item.stackTagCompound.setByte("is-recipe-null-tag-compound", (byte) 1);
				}
				//markItemStackAsFromPolycraftRecipe(item);
				return item;
			}
			return null;
		}

		@Override
		public int getRecipeSize() {
			// not implemented
			return 0;
		}

		@Override
		public ItemStack getRecipeOutput() {
			// not implemented
			return null;
		}

		@Override
		public ItemStack[] getRemainingItems(InventoryCrafting inv) {
			return new ItemStack[0];
		}

	}

	public static void markItemStackAsFromPolycraftRecipe(final ItemStack itemStack)
	{
		if (itemStack.getTagCompound() == null)
			PolycraftItemHelper.createTagCompound(itemStack);
		itemStack.getTagCompound().setByte("polycraft-recipe", (byte) 1);
	}

	public static boolean isItemStackFromPolycraftRecipe(final ItemStack itemStack)
	{
//		if (itemStack.getTagCompound() != null)
//			return itemStack.getTagCompound().hasKey("polycraft-recipe");
		if(PolycraftRegistry.items.containsValue(itemStack.getItem()))
			return true;
		if(itemStack.getItem() instanceof ItemBlock)
			return PolycraftRegistry.blocks.containsValue(((ItemBlock)itemStack.getItem()).block);
		return false;
	}

	private final Map<PolycraftContainerType, Set<PolycraftRecipe>> recipesByContainer = Maps.newLinkedHashMap();

	private final Map<PolycraftContainerType, SetMap<RecipeComponent, PolycraftRecipe>> shapedRecipesByContainer = Maps.newLinkedHashMap();
	private final Map<PolycraftContainerType, SetMap<String, PolycraftRecipe>> shapelessRecipesByContainer = Maps.newLinkedHashMap();

	@SuppressWarnings("unchecked")
	public PolycraftRecipeManager() {
		CraftingManager.getInstance().getRecipeList().add(
				new CustomGenericCraftingRecipe(this));

		// For onCraftedItem callback
		//FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * @return All recipes known to the Recipe manager.
	 */
	public Collection<PolycraftRecipe> getAllRecipes() {
		List<PolycraftRecipe> recipes = Lists.newArrayList();
		for (PolycraftContainerType container : recipesByContainer.keySet()) {
			recipes.addAll(recipesByContainer.get(container));
		}
		return recipes;
	}

	public Collection<PolycraftRecipe> getRecipesByContainerType(final PolycraftContainerType containerType) {
		if (recipesByContainer.containsKey(containerType))
			return ImmutableList.copyOf(recipesByContainer.get(containerType));
		return null;
	}

	/**
	 * @return All recipes known to the Recipe manager by each ingredient, and further by container type
	 */
	public Map<Object, Map<PolycraftContainerType, Collection<PolycraftRecipe>>> getRecipesByIngredientContainerType() {
		final Map<Object, Map<PolycraftContainerType, Collection<PolycraftRecipe>>> recipesByIngredientContainerType = Maps.newLinkedHashMap();
		for (final PolycraftContainerType containerType : recipesByContainer.keySet()) {
			for (final PolycraftRecipe recipe : recipesByContainer.get(containerType)) {
				for (final RecipeInput recipeInput : recipe.getInputs()) {
					for (final ItemStack input : recipeInput.inputs)
						addIngredientRecipe(recipesByIngredientContainerType, input.getItem(), recipe);
					for (final RecipeComponent output : recipe.getOutputs(null))
						addIngredientRecipe(recipesByIngredientContainerType, output.itemStack.getItem(), recipe);
				}
			}
		}
		return recipesByIngredientContainerType;
	}

	private static void addIngredientRecipe(final Map<Object, Map<PolycraftContainerType, Collection<PolycraftRecipe>>> recipesByIngredientContainerType,
			final Item ingredient, final PolycraftRecipe recipe) {
		Map<PolycraftContainerType, Collection<PolycraftRecipe>> containerTypeRecipes = recipesByIngredientContainerType.get(ingredient);
		if (containerTypeRecipes == null)
			recipesByIngredientContainerType.put(ingredient, containerTypeRecipes = Maps.newLinkedHashMap());
		Collection<PolycraftRecipe> ingredientRecipes = containerTypeRecipes.get(recipe.getContainerType());
		if (ingredientRecipes == null)
			containerTypeRecipes.put(recipe.getContainerType(), ingredientRecipes = Sets.newLinkedHashSet());
		ingredientRecipes.add(recipe);
	}

	/**
	 * Add a recipe to the manager to be indexed for searching.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addRecipe(final PolycraftRecipe recipe) {
		PolycraftContainerType containerType = recipe.getContainerType();
		if (!recipesByContainer.containsKey(containerType)) {
			recipesByContainer.put(containerType, new HashSet<PolycraftRecipe>());
			shapedRecipesByContainer.put(containerType, new SetMap());
			shapelessRecipesByContainer.put(containerType, new SetMap());
		}

		Preconditions.checkArgument(
				!recipesByContainer.get(containerType).contains(recipe),
				"Recipe in:" + containerType.name() + " contains " + recipe.getInputs().toString() + "already exists!");

		recipesByContainer.get(containerType).add(recipe);

		// Add shapeless recipes to the SetMap.  If there are no shapeless inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<RecipeComponent>> shapelessCombinations = recipe.getShapelessCombinations();
		if (shapelessCombinations.size() != 0) {
			for (final Set<RecipeComponent> inputs : shapelessCombinations) {
				Set<String> itemSet = Sets.newLinkedHashSet();
				for (final RecipeComponent input : inputs) {
					itemSet.add(input.itemStack.getItem().toString());
				}
				shapelessRecipesByContainer.get(containerType).add(itemSet, recipe);
			}
		} else {
			shapelessRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}

		// Add shaped recipes to the SetMap.  If there are no shaped inputs,
		// then the recipe is added as an empty set into the shapemap
		final Collection<Set<RecipeComponent>> shapedCombinations = recipe.getShapedCombinations();
		if (shapedCombinations.size() != 0) {
			for (final Set<RecipeComponent> inputs : shapedCombinations) {
				shapedRecipesByContainer.get(containerType).add(inputs, recipe);
			}
		} else {
			shapedRecipesByContainer.get(containerType).add(Collections.EMPTY_SET, recipe);
		}
	}

	/**
	 * Shifts the shaped inputs the amounts specified. Does not check if shifting is possible.
	 */
	private static Set<RecipeComponent> shiftInputs(final PolycraftContainerType containerType,
			final Set<RecipeComponent> inputs, final int dX, final int dY) {
		Set<RecipeComponent> newInputs = Sets.newLinkedHashSet();
		for (RecipeComponent inputComponent : inputs) {
			// Make sure the container slot is from the container itself, so relative x and y are right
			ContainerSlot usedSlot = containerType.getContainerSlotByIndex(inputComponent.slot);
			ContainerSlot inputSlot = containerType.getRelativeContainerSlot(SlotType.INPUT,
					usedSlot.getRelativeX() - dX, usedSlot.getRelativeY() - dY);
			newInputs.add(new RecipeComponent(inputSlot, inputComponent.itemStack));
		}
		return newInputs;
	}

	private static boolean canShiftInputs(final PolycraftContainerType containerType,
			final Set<RecipeComponent> inputs, final int dX, final int dY) {
		return false;
		//TODO allowing inputs to shift results in incorrect shapes for inventories like the machining mill
		/*
		if (inputs.size() == 0) {
			return false;
		}

		for (RecipeComponent inputComponent : inputs) {
			// Make sure the container slot is from the container itself, so relative x and y are right
			ContainerSlot usedSlot = containerType.getContainerSlotByIndex(inputComponent.slot);
			ContainerSlot inputSlot = containerType.getRelativeContainerSlot(SlotType.INPUT,
					usedSlot.getRelativeX() - dX, usedSlot.getRelativeY() - dY);
			if (inputSlot == null) {
				return false;
			}
		}
		return true;
		*/
	}

	private PolycraftRecipe findShapedRecipe(final PolycraftContainerType container, final Set<RecipeComponent> inputs) {
		if (!recipesByContainer.containsKey(container)) {
			return null;
		}

		// Shift positions of inputs if possible, to ensure shaped recipes always match.
		Set<RecipeComponent> inputsToCompare = inputs;
		while (canShiftInputs(container, inputsToCompare, 1, 0)) {
			inputsToCompare = shiftInputs(container, inputsToCompare, 1, 0);
		}
		while (canShiftInputs(container, inputsToCompare, 0, 1)) {
			inputsToCompare = shiftInputs(container, inputsToCompare, 0, 1);
		}

		// Check shaped recipe in initial positions
		List<PolycraftRecipe> validRecipes = Lists.newArrayList();
		final Set<PolycraftRecipe> shapedSet = shapedRecipesByContainer.get(container).getAnySubset(inputsToCompare);
		for (final PolycraftRecipe recipe : shapedSet) {
			if (recipe.isShapedOnly() && recipe.areInputsValid(inputsToCompare)) {
				validRecipes.add(recipe);
			}
		}

		if (validRecipes.size() == 0) {
			return null;
		}
		Collections.sort(validRecipes, new Comparator<PolycraftRecipe>() {
			@Override
			public int compare(PolycraftRecipe o1, PolycraftRecipe o2) {
				return PolycraftMod.compareInt(o2.getMaxInputStackSize(), o1.getMaxInputStackSize());
			}
		});
		return validRecipes.get(0);
	}

	private PolycraftRecipe findShapelessRecipe(final PolycraftContainerType container, final Set<RecipeComponent> inputs) {
		if (!recipesByContainer.containsKey(container)) {
			return null;
		}
		Set<String> itemSet = Sets.newLinkedHashSet();
		for (final RecipeComponent input : inputs) {
			itemSet.add(input.itemStack.getItem().toString());
		}

		List<PolycraftRecipe> validRecipes = Lists.newArrayList();
		final Set<PolycraftRecipe> shapelessSet = shapelessRecipesByContainer.get(container).getAnySubset(itemSet);
		for (final PolycraftRecipe recipe : shapelessSet) {
			if (recipe.areInputsValid(inputs)) {
				validRecipes.add(recipe);
			}
		}

		if (validRecipes.size() == 0) {
			return null;
		}
		Collections.sort(validRecipes, new Comparator<PolycraftRecipe>() {
			@Override
			public int compare(PolycraftRecipe o1, PolycraftRecipe o2) {
				return PolycraftMod.compareInt(o2.getMaxInputStackSize(), o1.getMaxInputStackSize());
			}
		});
		return validRecipes.get(0);
	}

	/**
	 * Searches the recipes available to the container type for the given set of recipe inputs.
	 */
	public PolycraftRecipe findRecipe(final PolycraftContainerType container, final Set<RecipeComponent> inputs) {
		if (inputs.size() == 0 || !recipesByContainer.containsKey(container)) {
			return null;
		}

		// Look in shaped recipes by ingredient first.  This ensures that when a shaped
		// recipe and a shapeless recipe look similar, the shaped ones take precedence
		PolycraftRecipe shapedRecipe = findShapedRecipe(container, inputs);
		if (shapedRecipe != null) {
			return shapedRecipe;
		}

		// Look at shapeless recipes next.		
		return findShapelessRecipe(container, inputs);
	}

	/**
	 * Creates a generic shapeless recipe from the inputs and adds it to the recipe inputs for searching.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final Iterable<ItemStack> inputs, final Iterable<ItemStack> outputs) {
		Preconditions.checkNotNull(containerType);
		final List<ContainerSlot> inputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.INPUT));
		final List<ContainerSlot> outputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.OUTPUT));

		List<RecipeInput> recipeInputs = Lists.newArrayList();
		int index = 0;
		for (final ItemStack stack : inputs) {
			if (index >= inputSlots.size()) {
				throw new IllegalArgumentException("Too many inputs for this container.");
			}
			recipeInputs.add(RecipeInput.shapelessInput(stack));
			index++;
		}

		final List<RecipeComponent> recipeOutputs = Lists.newArrayList();
		index = 0;
		for (final ItemStack stack : outputs) {
			if (index >= outputSlots.size()) {
				throw new IllegalArgumentException("Too many outputs for this container.");
			}
			recipeOutputs.add(new RecipeComponent(outputSlots.get(index), stack));
			index++;
		}
		PolycraftRecipe newRecipe = new PolycraftRecipe(containerType, recipeInputs, recipeOutputs);
		this.addRecipe(newRecipe);
		return newRecipe;
	}

	/**
	 * Creates and adds a shapeless recipe. If the crafting recipe type is a generic or smelting recipe, it is added to Forge via the GameRegistry API.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final Iterable<ItemStack> inputItems) {
		return addShapelessRecipe(containerType, resultItem, inputItems, 0);
	}

	/**
	 * Creates and adds a shapeless recipe. If the crafting recipe type is a generic or smelting recipe, it is added to Forge via the GameRegistry API.
	 */
	public PolycraftRecipe addShapelessRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final Iterable<ItemStack> inputItems,
			final double experience) {
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(resultItem);
		Preconditions.checkArgument(resultItem.stackSize > 0, "Result stack size must be > 0!");
		Preconditions.checkNotNull(inputItems);

		List<RecipeInput> recipeInputs = Lists.newArrayList();
		for (ItemStack inputItem : inputItems) {
			recipeInputs.add(RecipeInput.shapelessInput(inputItem));
		}
		Preconditions.checkArgument(recipeInputs.size() != 0, "No inputs were given!");

		ContainerSlot firstOutput = containerType.getSlots(SlotType.OUTPUT).iterator().next();
		PolycraftRecipe newRecipe = new PolycraftRecipe(containerType, recipeInputs,
				ImmutableList.of(new RecipeComponent(firstOutput, resultItem)), experience);

		if (containerType.equals(PolycraftContainerType.FURNACE)) {
			Preconditions.checkArgument(newRecipe.getInputCount() == 1, "Furnace recipes may only have one input!");
			ItemStack singleInput = inputItems.iterator().next();
			GameRegistry.addSmelting(singleInput, resultItem, (float) experience);
		}
		this.addRecipe(newRecipe);
		return newRecipe;
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API. If the crafting recipe container type is a generic or smelting recipe, it will be added to Forge via the GameRegistry APi.
	 * 
	 * @param containerType
	 *            The container the recipe is created in
	 * @param resultItem
	 *            The item generated from the recipe
	 * @param inputShape
	 *            The shape of the items, one string per row. Any letter can be used to represent an item input; spaces represent no input.
	 * param inputItems
	 *            Map of character representation to item stack needed.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap) {
		return addShapedRecipe(containerType, resultItem, inputShape, itemStackMap, 0);
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API. If the crafting recipe container type is a generic or smelting recipe, it will be added to Forge via the GameRegistry APi.
	 * 
	 * @param containerType
	 *            The container the recipe is created in
	 * @param resultItem
	 *            The item generated from the recipe
	 * @param inputShape
	 *            The shape of the items, one string per row. Any letter can be used to represent an item input; spaces represent no input.
	 * param inputItems
	 *            Map of character representation to item stack needed.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftRecipeFactory recipeFactory,
			final PolycraftContainerType containerType,
			final ItemStack resultItem, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap) {
		return addShapedRecipe(recipeFactory, containerType, ImmutableList.of(resultItem), inputShape, itemStackMap, 0);
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API. If the crafting recipe container type is a generic or smelting recipe, it will be added to Forge via the GameRegistry APi.
	 * 
	 * @param containerType
	 *            The container the recipe is created in
	 * @param resultItem
	 *            The item generated from the recipe
	 * @param inputShape
	 *            The shape of the items, one string per row. Any letter can be used to represent an item input; spaces represent no input.
	 * param inputItems
	 *            Map of character representation to item stack needed.
	 * @param experience
	 *            The amount of experience yielded by crafting the recipe.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final ItemStack resultItem, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap, double experience) {
		return addShapedRecipe(containerType, ImmutableList.of(resultItem), inputShape, itemStackMap, experience);
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API. If the crafting recipe container type is a generic or smelting recipe, it will be added to Forge via the GameRegistry APi.
	 * 
	 * @param containerType
	 *            The container the recipe is created in
	 * @param resultItems
	 *            The items generated from the recipe
	 * @param inputShape
	 *            The shape of the items, one string per row. Any letter can be used to represent an item input; spaces represent no input.
	 * param inputItems
	 *            Map of character representation to item stack needed.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final Iterable<ItemStack> resultItems, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap) {
		return addShapedRecipe(containerType, resultItems, inputShape, itemStackMap, 0);
	}

	public PolycraftRecipe addShapedRecipe(final PolycraftContainerType containerType,
			final Iterable<ItemStack> resultItems, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap, double experience) {
		return addShapedRecipe(defaultRecipeFactory, containerType, resultItems, inputShape, itemStackMap, experience);
	}

	/**
	 * Adds a shaped recipe from the inputs, in a similar format to the Forge API. If the crafting recipe container type is a generic or smelting recipe, it will be added to Forge via the GameRegistry APi.
	 * 
	 * @param containerType
	 *            The container the recipe is created in
	 * @param resultItems
	 *            The items generated from the recipe
	 * @param inputShape
	 *            The shape of the items, one string per row. Any letter can be used to represent an item input; spaces represent no input.
	 * param inputItems
	 *            Map of character representation to item stack needed.
	 * @param experience
	 *            The amount of experience yielded by crafting the recipe.
	 */
	public PolycraftRecipe addShapedRecipe(final PolycraftRecipeFactory recipeFactory,
			final PolycraftContainerType containerType,
			final Iterable<ItemStack> resultItems, final String[] inputShape,
			final Map<Character, ItemStack> itemStackMap, double experience) {
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(resultItems);
		Preconditions.checkNotNull(inputShape);
		Preconditions.checkArgument(inputShape.length != 0, "No input shapes given!");
		Preconditions.checkNotNull(itemStackMap);
		Preconditions.checkArgument(itemStackMap.size() != 0, "No input items were specified");

		// Map letters to items and place them in a map
		Set<RecipeInput> recipeInputs = Sets.newLinkedHashSet();
		ContainerSlot[][] inputGrid = containerType.getContainerSlotGrid(SlotType.INPUT);
		Preconditions.checkArgument(inputGrid.length != 0, "Input Container type has not slots defined!");
		for (int y = 0; y < inputShape.length; ++y) {
			final String shapeRow = inputShape[y];
			for (int x = 0; x < shapeRow.length(); ++x) {
				Preconditions.checkArgument(x < inputGrid.length, "The item configuration specified is too big for the container!");
				Preconditions.checkArgument(y < inputGrid[x].length, "The item configuration specified is too big for the container!");
				Preconditions.checkNotNull(inputGrid[x][y], "Invalid container slot on row " + y + " column " + x);
				final char ch = shapeRow.charAt(x);
				if (ch != ' ') {
					Preconditions.checkNotNull(itemStackMap.get(ch), "No item specified for character '" + ch + "'");
					recipeInputs.add(RecipeInput.shapedInput(inputGrid[x][y], itemStackMap.get(ch)));
				}
			}
		}

		final List<ContainerSlot> outputSlots = ImmutableList.copyOf(containerType.getSlots(SlotType.OUTPUT));
		final List<RecipeComponent> recipeOutputs = Lists.newArrayList();
		int index = 0;
		for (final ItemStack stack : resultItems) {
			Preconditions.checkArgument(stack.stackSize > 0, "Result stack size must be > 0!");
			if (index >= outputSlots.size()) {
				throw new IllegalArgumentException("Too many outputs for this container.");
			}
			recipeOutputs.add(new RecipeComponent(outputSlots.get(index), stack));
			index++;
		}
		PolycraftRecipe newRecipe = recipeFactory.createRecipe(containerType, recipeInputs, recipeOutputs, experience);
		this.addRecipe(newRecipe);

		// Add to Forge's GameRegistry if necessary
		if (containerType.equals(PolycraftContainerType.FURNACE)) {
			Preconditions.checkArgument(newRecipe.getInputCount() == 1, "Furnace recipes may only have one input!");
			ItemStack singleInput = itemStackMap.values().iterator().next();
			GameRegistry.addSmelting(singleInput, resultItems.iterator().next(), (float) experience);
		}

		return newRecipe;
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onItemCraftedEvent(final PlayerEvent.ItemCraftedEvent event) {
		ItemStack craftedItem = event.crafting;
		//if (craftedItem.getTagCompound() != null) {
			// Item has been marked as being a Polycraft recipe, which allows recipes to require
			// itemstacks with any stackSize.  The generic crafting recipes only remove a single
			// item for each recipe item, so the rest may need to be removed.
			if (isItemStackFromPolycraftRecipe(craftedItem)) {
				//TODO figure out a way that we can remove this key, can't at the moment because net.minecraft.inventory.ContainerWorkbench.transferStackInSlot(EntityPlayer, int)
				//which is called when a player shift clicks, only hands us a copy, so removing the key results in a mismatch between shift click created items, and single click 
				//created items
				//if (craftedItem.stackTagCompound.hasKey("is-recipe-null-tag-compound")) {
				//if we had to create a tag compound just to store is-recipe (the tag compound didn't exist before we came along) then remove the whole thing
				//otherwise minecraft will think that two items are different, even if the tag compound on one is empty, and on the other it is null
				//see net.minecraft.item.ItemStack.areItemStackTagsEqual(ItemStack, ItemStack) for more info
				//	craftedItem.setTagCompound(null);
				//}
				//else
				//	craftedItem.stackTagCompound.removeTag("polycraft-recipe");
				Set<RecipeComponent> inputs = CustomGenericCraftingRecipe.getComponentsFromInventory(event.craftMatrix);
				PolycraftRecipe recipe = findRecipe(PolycraftContainerType.CRAFTING_TABLE, inputs);
				if (recipe == null) {
					//logger.warn("Couldn't find that recipe");
				} else {
					recipe.processGenericCrafting(inputs, event.craftMatrix);
				}
			}
		//}
	}
}
