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
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;

public class Government {
	
	public final int id;
	public final String type;
	public final String name;
	public final Set<Integer> members = Sets.newHashSet();
	public final Set<GovernmentRole> roles = Sets.newHashSet();
	public final static Map<Integer, GovernmentZone> zonesByID = Maps.newHashMap();
	public final Set<GovernmentZone> zones = Sets.newHashSet();
	public final Set<SuperChunk> super_chunks = Sets.newHashSet();
	
	public Government(
			final JsonElement id,
			final JsonElement type,
			final JsonElement name, 
			final JsonElement members,
			final JsonElement roles,
			final JsonElement zones,
			final JsonElement properties) {
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
			this.roles.add(new GovernmentRole(jobject.get("id").getAsInt(), 
					jobject.get("type").getAsString(), 
					jobject.get("name").getAsString(), 
					jobject.get("parent_id"), //must check in case null 
					jobject.get("is_sub").getAsBoolean(), 
					jobject.get("members")));
		}
		for(JsonElement zone: zones.getAsJsonArray())
		{
			JsonObject jobject = zone.getAsJsonObject();
			this.zones.add(new GovernmentZone(jobject.get("id").getAsInt(), 
					jobject.get("name").getAsString(), 
					jobject.get("parent_id"),  //must check in case null
					jobject.get("override").getAsBoolean(), 
					jobject.get("permission_sets"),
					jobject.get("chunks")));
		}
		for(GovernmentZone zone: this.zones) { //put all zones into static for searching
			zonesByID.put(zone.id, zone);
		}
		for(JsonElement property: properties.getAsJsonArray()) 
		{
			JsonObject jobject = property.getAsJsonObject();
			this.super_chunks.add(new SuperChunk(jobject.get("super_chunk_x").getAsInt(),
					jobject.get("super_chunk_z").getAsInt()));
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
					jobject.get("zones"),
					jobject.get("properties"));
		}
	}	

}