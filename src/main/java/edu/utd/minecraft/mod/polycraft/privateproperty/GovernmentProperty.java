package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.entity.player.EntityPlayer;



public class GovernmentProperty extends PrivateProperty{
	public final GovernmentZone zone;

	public GovernmentProperty(String name, String message, GovernmentZone zone) {
		super(name, message);
		this.zone = zone;
	}
	
	public boolean actionEnabled(final EntityPlayer player, final Action action) {
		
		long playerID;
		//get user id
		playerID = Enforcer.playerID;
		
		return zone.actionEnabled(playerID, action);
	}

}