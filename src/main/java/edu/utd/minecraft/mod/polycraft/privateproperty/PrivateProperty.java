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
			OpenEnderChest, //5
			OpenPlasticChest,
			UseCraftingTable,
			UseFurnace, //8
			UseTreeTap,
			UseMachiningMill, //10
			UseInjectionMolder,
			UseExtruder,
			UseDistillationColumn,
			UseSteamCracker,
			UseMeroxTreatmentUnit, //15
			UseChemicalProcessor,
			UseFloodlight, //FIXME renamed from UseFueledLamp
			UseSpotlight,
			UsePump,
			UseOilDerrick, //20
			UseButton,
			UseLever,
			UsePressurePlate,
			UseFlowRegulator,
			UseCondenser, //25
			UseFlameThrower,
			UseBucket,
			UseFreezeRay,
			UseWaterCannon,
			UseGaslamp, //30
			SpawnEntity,
			MountEntity, //horse, donkey, mule, pig, minecart, boat, etc
			UseDoor,
			UseTrapDoor, //34
			UseFenceGate, //35
			UseFlintAndSteel,
			AddBlockTNT,
			UseSolarArray, //added by Walter 11.3.2015 from here down...
			UseContactPrinter,
			UseTradingHouse, //40
			UseMaskWriter,
			UseHopper,
			PlaceSign,
			PlaceTorch,
			UseDispenser, //45
			UsePipe,
			AccessCHEM2323,
			//FIXME figure out how to add this
			//UsePhaseShifter
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

		public PermissionSet(final int[] permissions) {
			enabled = new boolean[Action.values().length];
			user = null;
			for (int action : permissions)
			{
				enabled[action] = true;
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

	public final boolean master;
	public boolean keepMasterWorldSame;
	public final String owner;
	public final String name;
	public final String message;
	public final Chunk[] bounds;
	public final Chunk boundTopLeft;
	public final Chunk boundBottomRight;
	public final PermissionSet defaultPermissions;
	public final PermissionSet masterPermissions;
	public final Map<String, PermissionSet> permissionOverridesByUser;

	public PrivateProperty(
			final boolean master,
			final JsonElement owner,
			final JsonElement name,
			final JsonElement message,
			final JsonArray chunks,
			final JsonArray permissions) {
		this.master = master;
		this.keepMasterWorldSame = false;
		this.owner = owner.getAsString();
		this.name = name.getAsString();
		this.message = message.getAsString();
		this.bounds = new Chunk[chunks.size()];
		for (int i = 0; i < chunks.size(); i++)
			this.bounds[i] = new Chunk(chunks.get(i).getAsJsonArray());
		this.boundTopLeft = this.bounds[0];
		this.boundBottomRight = this.bounds[1];
		this.defaultPermissions = new PermissionSet(permissions.get(0).getAsJsonObject());
		this.masterPermissions = new PermissionSet(new int[] {
				0, //"Enter",
				5, //"OpenEnderChest"
				23, //"UsePressurePlate"
				33, //"UseDoor",			
				34, //"UseTrapDoor",
				35, //"UseFenceGate",
				7 //"UseCraftingTable",				
		});
		this.permissionOverridesByUser = Maps.newHashMap();
		for (int i = 1; i < permissions.size(); i++) {
			final PermissionSet overridePermissionSet = new PermissionSet(permissions.get(i).getAsJsonObject());
			this.permissionOverridesByUser.put(overridePermissionSet.user, overridePermissionSet);
		}
	}

	public static class Deserializer implements JsonDeserializer<PrivateProperty> {

		private final boolean master;

		public Deserializer(final boolean master) {
			this.master = master;
		}

		@Override
		public PrivateProperty deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new PrivateProperty(
					master,
					jobject.get("owner"),
					jobject.get("name"),
					jobject.get("message"),
					jobject.getAsJsonArray("bounds"),
					jobject.getAsJsonArray("permissions"));
		}
	}

	public boolean actionEnabled(final EntityPlayer player, final Action action) {
		if (master) {
			//if the player owns this property, they can do anything
			if (owner.equalsIgnoreCase(player.getDisplayName()))
				return true;

			final PermissionSet overridePermissions = permissionOverridesByUser.get(player.getDisplayName().toLowerCase());
			//if there is a specific permission set for this user, use it over the defaults
			if (overridePermissions != null)
				return overridePermissions.enabled[action.ordinal()];
			//otherwise just use the default permissions
			return defaultPermissions.enabled[action.ordinal()];
		}
		//return action == Action.Enter;
		return masterPermissions.enabled[action.ordinal()];
	}

	public boolean actionEnabled(final Action action) {
		return defaultPermissions.enabled[action.ordinal()];
	}
}
