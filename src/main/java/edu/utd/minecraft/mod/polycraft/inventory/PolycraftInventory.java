package edu.utd.minecraft.mod.polycraft.inventory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftMod.RegistryNamespace;
import edu.utd.minecraft.mod.polycraft.crafting.ContainerSlot;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftBasicTileEntityContainer;
import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import edu.utd.minecraft.mod.polycraft.crafting.SlotType;

public abstract class PolycraftInventory extends PolycraftBasicTileEntityContainer {
	private final PolycraftContainerType containerType;
	private static final Logger logger = LogManager.getLogger();

	private boolean isCreated = false;
	
	private Material blockMaterial = Material.rock;
	private String name = "Unnamed Inventory";
	private Map<BlockFace, String> blockFaceAssets = Maps.newHashMap();
	private Set<String> iconAssets = Sets.newHashSet();
	private String iconAsset = null;
	private ResourceLocation guiResourceLocation = null;
	private int containerGuiId;
	
	private List<InventoryBehavior> behaviors = Lists.newArrayList();
	private PolycraftInventoryBlock block;
	
	public PolycraftInventory(final PolycraftContainerType containerType, String containerKey) {
		super(containerType, containerKey);
		Preconditions.checkNotNull(containerType);
		Preconditions.checkNotNull(containerKey);
		this.name = containerKey;
		this.containerType = containerType;
	}
	
	/**
	 * @param material the material of the standard block for this inventory.
	 */
	protected void setBlockMaterial(final Material material) {
		Preconditions.checkState(!isCreated);
		Preconditions.checkNotNull(material);
		this.blockMaterial = material;
	}
	
	/**
	 * @param face the block face to set
	 * @param assetName the asset name to set the block face to
	 */
	protected void setBlockFace(final BlockFace face, final String assetName) {
		Preconditions.checkState(!isCreated);
		Preconditions.checkNotNull(face);
		Preconditions.checkArgument(!Strings.isNullOrEmpty(assetName));
		final String polycraftAsset = PolycraftMod.getAssetName(assetName);
		this.blockFaceAssets.put(face, polycraftAsset);
		this.iconAssets.add(polycraftAsset);
	}
	
	/**
	 * @param assetName the asset name of the inventory icon to use.
	 */
	protected void setInventoryIcon(final String assetName) {
		Preconditions.checkState(!isCreated);
		Preconditions.checkArgument(!Strings.isNullOrEmpty(assetName));
		final String polycraftAsset = PolycraftMod.getAssetName(assetName);
		this.iconAssets.add(polycraftAsset);
		this.iconAsset = polycraftAsset;
	}
	
	/**
	 * @param assetName the asset name of the gui texture to use.
	 */
	protected void setGuiTexture(final String assetName) {
		Preconditions.checkState(!isCreated);
		Preconditions.checkArgument(!Strings.isNullOrEmpty(assetName));
		this.guiResourceLocation = new ResourceLocation(PolycraftMod.getAssetName(assetName));		
	}
	
	/**
	 * @param guiId The gui ID of the container.
	 */
	protected void setContainerGuiId(final int guiId) {
		this.containerGuiId = guiId;
	}
	
	/**
	 * @param behavior The behavior to add to this container.
	 */
	protected void addBehavior(final InventoryBehavior behavior) {
		Preconditions.checkState(!isCreated);
		Preconditions.checkNotNull(behavior);
		this.behaviors.add(behavior);
	}
	
	/**
	 * @return resource location of the container gui, or none if not specified.
	 */
	public ResourceLocation getGuiResourceLocation() {
		return this.guiResourceLocation;
	}
	
	/**
	 * @return Icon asset of the inventory icon, or null if none was specified.
	 */
	public String getInventoryIcon() {
		return this.iconAsset;
	}
	
	/**
	 * @return Icon asset of the specified face, or null if nothing was specified.
	 */
	public String getBlockFaceIcon(BlockFace face) {
		return this.blockFaceAssets.get(face);
	}
	
	/**
	 * @return iterable of inventory behaviors
	 */
	public Iterable<InventoryBehavior> getBehaviors() {
		return this.behaviors;
	}
	
	/**
	 * @return the container gui id.
	 */
	public int getGuiId() {
		return this.containerGuiId;
	}
	
	/**
	 * @return name of the inventory
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return the material of the inventory block.
	 */
	public Material getBlockMaterial() {
		return this.blockMaterial;
	}
	
	/**
	 * @return the regular block for this inventory.
	 */
	public Block getBlock() {
		return this.block;
	}
	
	/**
	 * @return the container type this inventory belongs to.
	 */
	public PolycraftContainerType getContainerType() {
		return this.containerType;
	}
	
	/**
	 * Creates the necessary pieces for the inventory.
	 */
	public PolycraftInventory create() {
		Preconditions.checkState(!isCreated);
		
		// TODO: Assert that it can be created with the provided arguments?
		this.block = new PolycraftInventoryBlock(this);
		logger.info("Registering block " + this.block.getUnlocalizedName() + ", " + this.block.getIdFromBlock(this.block) + " -- " + this.getName());
		Block finalBlock = PolycraftMod.registerBlock(RegistryNamespace.Inventory, this.getName(), this.getBlock());		
		logger.info("Registering block " + finalBlock.getUnlocalizedName() + ", " + finalBlock.getIdFromBlock(finalBlock) + " -- " + this.getName());
		
		GameRegistry.registerTileEntity(this.getClass(), "tile_entity_" + this.getName());
		PolycraftMod.addRegistryName(RegistryNamespace.Inventory, this.getName(), this.getName());
		
		this.isCreated = true;
		return this;
	}
	
	/**
	 * @return a new TileEntity for the inventory.
	 */
	public PolycraftInventory createTileEntity(final World world) {
		Preconditions.checkState(isCreated);
		try {
			return (PolycraftInventory)this.getClass().newInstance();
		} catch (InstantiationException e) {
			logger.error("Can't create an instance of your tile entity! Does it have a parameterless public constructor?");
		} catch (IllegalAccessException e) {
			logger.error("Can't create an instance of your tile entity! Does it have a parameterless public constructor?");
		}
		return null;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	// TileEntity Methods
	@Override
	public void updateEntity() {
		for (InventoryBehavior behavior : this.getBehaviors()) {
			Boolean result = behavior.updateEntity(this);
			if (result != null) {
				return;
			}
		}
	}
	
	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		for (InventoryBehavior behavior : this.getBehaviors()) {
			Boolean result = behavior.isItemValidForSlot(var1, var2, this);
			if (result != null) {
				return result;
			}
		}
		ContainerSlot containerSlotByIndex = this.getContainerType().getContainerSlotByIndex(var1);
		if (containerSlotByIndex.getSlotType() == SlotType.INPUT) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item, side
	 */
	@Override
	public boolean canExtractItem(int slotIndex, ItemStack itemStack, int side) {
		return true;
	}
}
