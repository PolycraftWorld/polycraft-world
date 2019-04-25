package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment;
import edu.utd.minecraft.mod.polycraft.experiment.ExperimentManager;
import edu.utd.minecraft.mod.polycraft.experiment.Experiment.State;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemFreezingKnockbackBomb  extends ItemKnockbackBomb{
	
	
	public ItemFreezingKnockbackBomb(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("freezing_knockback_bomb"));
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.color = Color.CYAN;
	}
	
	
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		// TODO Auto-generated method stub
		if (!world.isRemote) {
			for (Experiment Exp : ExperimentManager.INSTANCE.getExperiments().values()) {
				if (Exp.isPlayerInExperiment(player.getDisplayName()) && Exp.currentState == State.Halftime) {
					return itemstack;
				}
			}
			List list = knockback(world, player);
			if(list != null)
				freezePlayers(new LinkedList(list));
			return super.onItemRightClick(itemstack, world, player);
		}
		
		return itemstack;
		
	}
	
	private void freezePlayers(LinkedList list) {
		for(Object obj: list) {
			if(obj instanceof EntityPlayerMP) {
				ServerEnforcer.INSTANCE.freezePlayerForTicks(200, (EntityPlayerMP)obj);
			}
		}
		
	}

}