package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.lang.reflect.Type;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;

public class PrivateProperty {
	public static class PermissionSet {
		public enum Action {
			Enter,
			AddBlock,
			DestroyBlock,
			AttackEntity,
			OpenChest,
			OpenEnderChest,
			OpenPlasticChest,
			UseCraftingTable,
			UseFurnace,
			UseTreeTap,
			UseMachiningMill,
			UseInjectionMolder,
			UseExtruder,
			UseDistillationColumn,
			UseSteamCracker,
			UseMeroxTreatmentUnit,
			UseChemicalProcessor,
			UseFueledLamp,
			UseSpotlight,
			UsePump,
			UseOilDerrick,
			UseButton,
			UseLever,
			UsePressurePlate,
			UseFlowRegulator,
			UseCondenser,
			UseFlameThrower,
			UseBucket,
			UseFreezeRay,
			UseWaterCannon
		}
		
		public final String user;
		public final boolean[] enabled;
		
		public PermissionSet(final JsonObject jsonObject) {
			final JsonElement userElement = jsonObject.get("user");
			user = userElement.isJsonNull() ? null : userElement.getAsString();
			enabled = new boolean[Action.values().length];
			for (final JsonElement action : jsonObject.get("enabled").getAsJsonArray()) {
				enabled[action.getAsInt()] = true;
			}
		}
	}
	
	public static class Chunk {
		public final int x;
		public final int z;
		
		public Chunk(final JsonArray chunk) {
			this.x = chunk.get(0).getAsInt();
			this.z = chunk.get(1).getAsInt();
		}
	}
	
	public final String owner;
	public final String name;
	public final String message;
	public final Chunk[] bounds;
	public final Chunk boundTopLeft;
	public final Chunk boundBottomRight;
	public final PermissionSet defaultPermissions;
	public final Map<String, PermissionSet> permissionOverridesByUser;
	
	public PrivateProperty(
			final JsonElement owner,
			final JsonElement name,
			final JsonElement message,
			final JsonArray chunks,
			final JsonArray permissions) {
		this.owner = owner.getAsString();
		this.name = name.getAsString();
 		this.message = message.getAsString();
		this.bounds = new Chunk[chunks.size()];
		for (int i = 0; i < chunks.size(); i++)
			this.bounds[i] = new Chunk(chunks.get(i).getAsJsonArray());
		this.boundTopLeft = this.bounds[0];
		this.boundBottomRight = this.bounds[1];
		this.defaultPermissions = new PermissionSet(permissions.get(0).getAsJsonObject());
		this.permissionOverridesByUser = Maps.newHashMap();
		for (int i = 1; i < permissions.size(); i++) {
			final PermissionSet overridePermissionSet = new PermissionSet(permissions.get(i).getAsJsonObject());
			this.permissionOverridesByUser.put(overridePermissionSet.user, overridePermissionSet);
		}
	}
	
	public static class Deserializer implements JsonDeserializer<PrivateProperty> {
		@Override
		public PrivateProperty deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
		    JsonObject jobject = (JsonObject) json;
			return new PrivateProperty(
					jobject.get("owner"),
					jobject.get("name"),
					jobject.get("message"),
					jobject.getAsJsonArray("bounds"),
					jobject.getAsJsonArray("permissions"));
		}
		
	}
	
	public boolean actionEnabled(final EntityPlayer player, final Action action) {
		//if the player owns this property, they can do anything
		if (owner.equals(player.getDisplayName()))
			return true;
		
		final PermissionSet overridePermissions = permissionOverridesByUser.get(player.getDisplayName());
		//if there is a specific permission set for this user, use it over the defaults
		if (overridePermissions != null)
			return overridePermissions.enabled[action.ordinal()];
		//otherwise just use the default permissions
		return defaultPermissions.enabled[action.ordinal()];
	}
}
