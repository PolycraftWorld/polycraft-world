package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet;

public class GovernmentZone{
	public final int id;
	public final String name;
	public final int parentZoneID;
	public final boolean override;
	public final Map<Integer, GovPermissionSet> permissionSetsByRole;
	public final Set<Chunk> chunks = Sets.newHashSet();
	public final GovernmentProperty property; //wrapper to work with current enforcer
	
	public GovernmentZone(final int id,  
			final String name, 
			final JsonElement parentZoneID, 
			final boolean override, 
			final JsonElement permissionSets,
			final JsonElement chunksJson){
		this.id = id;
		this.name = name;
		if(parentZoneID.isJsonNull()){
			this.parentZoneID = -1;
		}else{
			this.parentZoneID = parentZoneID.getAsInt();
		}
		this.override = override;
		this.permissionSetsByRole = Maps.newHashMap();
		for(JsonElement govPermissionSet: permissionSets.getAsJsonArray()) {
			final GovPermissionSet permissionSet = new GovPermissionSet(govPermissionSet.getAsJsonObject());
			permissionSetsByRole.put(permissionSet.roleID, permissionSet);
		}
		for(JsonElement chunk: chunksJson.getAsJsonArray()) {
			this.chunks.add(new Chunk(chunk.getAsJsonObject().get("chunk_x").getAsInt(),
					chunk.getAsJsonObject().get("chunk_z").getAsInt()));
		}
		
		property = new GovernmentProperty(name, name+"message", this);
		
		//add property to enforcer
		for(Chunk chunk: chunks) {
			Enforcer.privatePropertiesByChunk.put(Enforcer.getChunkKey(chunk.x, chunk.z), property);
		}
		
	}
	
	public String toString(){
		return this.name;
	}
	
	public static class GovPermissionSet extends PermissionSet{
		
		public final int id;
		public final int roleID;
		
		public GovPermissionSet(final JsonObject jsonObject) {
			super(jsonObject, true);
			final JsonElement idElement = jsonObject.get("id");
			id = idElement.isJsonNull() ? null : idElement.getAsInt();
			final JsonElement roleElement = jsonObject.get("role");
			roleID = roleElement.isJsonNull() ? null : roleElement.getAsInt();
		}
		
		public String toString() {
			return "id: " + this.id + "| role: " + this.roleID + "| base permissions: " + super.toString();
		}
		
	}
}
