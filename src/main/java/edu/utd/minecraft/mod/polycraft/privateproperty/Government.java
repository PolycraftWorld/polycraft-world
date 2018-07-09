package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet;

public class Government {
	
	public final int id;
	public final String type;
	public final String name;
	public final Set<Integer> members = Sets.newHashSet();
	public final Set<Role> roles = Sets.newHashSet();
	public final Set<Zone> zones = Sets.newHashSet();
	
	public Government(
			final JsonElement id,
			final JsonElement type,
			final JsonElement name, 
			final JsonElement members,
			final JsonElement roles,
			final JsonElement zones) {
		this.id = id.getAsInt();
		this.type = type.getAsString();
		this.name = name.getAsString();
		for(JsonElement member: members.getAsJsonArray())
		{
			this.members.add(member.getAsInt());
		}
		for(JsonElement role: roles.getAsJsonArray())
		{
			JsonObject jobject = role.getAsJsonObject();
			this.roles.add(new Role(jobject.get("id").getAsInt(), 
					jobject.get("type").getAsString(), 
					jobject.get("name").getAsString(), 
					jobject.get("parent_id"), //must check in case null 
					jobject.get("is_sub").getAsBoolean(), 
					jobject.get("members")));
		}
		for(JsonElement zone: zones.getAsJsonArray())
		{
			JsonObject jobject = zone.getAsJsonObject();
			this.zones.add(new Zone(jobject.get("id").getAsInt(), 
					jobject.get("name").getAsString(), 
					jobject.get("parent_id"),  //must check in case null
					jobject.get("override").getAsBoolean(), 
					jobject.get("permission_sets")));
		}
	}
	
	public Object[] getMembers()
	{
		return members.toArray();
	}
	
	public Object[] getRoles()
	{
		return roles.toArray();
	}
	
	public static class Deserializer implements JsonDeserializer<Government> {

		@Override
		public Government deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new Government(
					jobject.get("id"),
					jobject.get("type"),
					jobject.get("name"),
					jobject.get("members"),
					jobject.get("roles"),
					jobject.get("zones"));
		}
	}
	
	public static class Role{
		public final int id;
		public final String type;
		public final String name;
		public final int parentRoleID;
		public final boolean isSubGroup;
		public final Set<Integer> members = Sets.newHashSet();
		
		public Role(int id, 
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
	
	public static class Zone{
		public final int id;
		public final String name;
		public final int parentZoneID;
		public final boolean override;
		public final Set<Integer> permissionSets = Sets.newHashSet();
		
		public Zone(final int id,  
				final String name, 
				final JsonElement parentZoneID, 
				final boolean override, 
				final JsonElement permissionSets){
			this.id = id;
			this.name = name;
			if(parentZoneID.isJsonNull()){
				this.parentZoneID = -1;
			}else{
				this.parentZoneID = parentZoneID.getAsInt();
			}
			this.override = override;
			for(JsonElement permissionSet: permissionSets.getAsJsonArray())
			{
				this.permissionSets.add(permissionSets.getAsInt());
			}
		}
		
		public String toString(){
			return this.name;
		}
		
		public static class PermissionSet{
			public final int id;
			public final int roleID;
			public final boolean[] permissions= new boolean[9];
			
			public PermissionSet(final int id,
					final int roleID,
					final JsonArray permissions){
				this.id = id;
				this.roleID = roleID;
				
			}
			
		}
		
	}
	
	

}