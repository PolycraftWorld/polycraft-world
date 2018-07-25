package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.util.Set;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;

public class GovernmentRole{
	public final int id;
	public final String type;
	public final String name;
	public final int parentRoleID;
	public final boolean isSubGroup;
	public final Set<Integer> members = Sets.newHashSet();
	
	public GovernmentRole(int id, 
			final String type, 
			final String name, 
			final JsonElement parentRoleID, 
			final boolean isSubGroup, 
			final JsonElement members){
		this.id = id;
		this.type = type;
		this.name = name;
		if(parentRoleID.isJsonNull()){
			this.parentRoleID = -1;
		}else{
			this.parentRoleID = parentRoleID.getAsInt();
		}
		this.isSubGroup = isSubGroup;
		for(JsonElement member: members.getAsJsonArray())
		{
			this.members.add(member.getAsInt());
		}
	}
	
	public String toString(){
		return this.name;
	}
	
}
