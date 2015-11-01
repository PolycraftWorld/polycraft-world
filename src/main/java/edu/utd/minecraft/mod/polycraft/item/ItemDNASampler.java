package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.DNASampler;

public class ItemDNASampler extends Item implements PolycraftItem {
	public final DNASampler dnaSampler;

	public ItemDNASampler(final DNASampler dnaSampler) {
		Preconditions.checkNotNull(dnaSampler);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.dnaSampler = dnaSampler;
		this.setMaxStackSize(this.dnaSampler.maxStackSize);
		if (this.dnaSampler.maxStackSize == 1)
			this.setTextureName(PolycraftMod.getAssetName("dna_sampler_used"));
		else
			this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(dnaSampler.name)));
		this.setUnlocalizedName(dnaSampler.name);

	}

	@Override
	public ItemCategory getCategory() {
		return ItemCategory.MATERIALS_CELL_CULTURE_DISH;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5)
	{
		PolycraftMod.setPolycraftStackCompoundTag(par1ItemStack);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (stack.stackSize > 1)
			return false;

		if (((ItemDNASampler) stack.getItem()).dnaSampler.level >= 1)
		{

			if (entity instanceof EntityCow) {
				return checkDNASampler(player, "DNA Sampler (Cow)");
			}
			if (entity instanceof EntitySheep) {
				return checkDNASampler(player, "DNA Sampler (Sheep)");
			}
			if (entity instanceof EntityPig) {
				return checkDNASampler(player, "DNA Sampler (Pig)");
			}
			if (entity instanceof EntityChicken) {
				return checkDNASampler(player, "DNA Sampler (Chicken)");
			}
		}
		if (((ItemDNASampler) stack.getItem()).dnaSampler.level >= 2)
		{

			if (entity instanceof EntitySquid) {
				return checkDNASampler(player, "DNA Sampler (Squid)");
			}
			if (entity instanceof EntityWolf) {
				return checkDNASampler(player, "DNA Sampler (Wolf)");
			}
			if (entity instanceof EntityOcelot) {
				return checkDNASampler(player, "DNA Sampler (Ocelot)");
			}
			if (entity instanceof EntityBat) {
				return checkDNASampler(player, "DNA Sampler (Bat)");
			}
		}
		if (((ItemDNASampler) stack.getItem()).dnaSampler.level >= 3)
		{

			if (entity instanceof EntitySpider) {
				return checkDNASampler(player, "DNA Sampler (Spider)");
			}
			if (entity instanceof EntityZombie) {
				return checkDNASampler(player, "DNA Sampler (Zombie)");
			}
			if (entity instanceof EntitySkeleton) {
				return checkDNASampler(player, "DNA Sampler (Skeleton)");
			}
			if (entity instanceof EntityHorse) {
				return checkDNASampler(player, "DNA Sampler (Horse)");
			}
			if (entity instanceof EntityPlayer) {
				return checkDNASampler(player, "DNA Sampler (Person)");
			}
		}
		if (((ItemDNASampler) stack.getItem()).dnaSampler.level >= 4)
		{

			if (entity instanceof EntityWitch) {
				return checkDNASampler(player, "DNA Sampler (Witch)");
			}
			if (entity instanceof EntityCreeper) {
				return checkDNASampler(player, "DNA Sampler (Creeper)");
			}
			if (entity instanceof EntitySilverfish) {
				return checkDNASampler(player, "DNA Sampler (Silverfish)");
			}
			if (entity instanceof EntityCaveSpider) {
				return checkDNASampler(player, "DNA Sampler (CaveSpider)");
			}
		}
		if (((ItemDNASampler) stack.getItem()).dnaSampler.level >= 5)
		{

			if (entity instanceof EntityGhast) {
				return checkDNASampler(player, "DNA Sampler (Witch)");
			}
			if (entity instanceof EntityBlaze) {
				return checkDNASampler(player, "DNA Sampler (Creeper)");
			}
			if (entity instanceof EntityPigZombie) {
				return checkDNASampler(player, "DNA Sampler (Zombie Pigman)");
			}
			if (entity instanceof EntityMagmaCube) {
				return checkDNASampler(player, "DNA Sampler (Magma Cube)");
			}
		}
		return false;
	}

	private boolean checkDNASampler(EntityPlayer player, String name)
	{
		DNASampler lookup = DNASampler.registry.get(name);
		if (lookup != null)
		{
			player.setCurrentItemOrArmor(0, lookup.getItemStack(1));
			return true;
		}
		return false;

	}

}
