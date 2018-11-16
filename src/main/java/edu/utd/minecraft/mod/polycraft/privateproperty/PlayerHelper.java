package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PlayerHelper {
	public long id;
	public String minecraft_user_name;
	public String uuid;
	public boolean is_active;
	
	
	public PlayerHelper(JsonElement id, 
						JsonElement minecraft_user_name, 
						JsonElement uuid,
						JsonElement is_active) {
		this.id = id.getAsLong();
		this.minecraft_user_name = minecraft_user_name.getAsString();
		this.uuid = uuid.getAsString();
	}
	
	public PlayerHelper(long id, 
			String minecraft_user_name, 
			String uuid) {
		this.id = id;
		this.minecraft_user_name = minecraft_user_name;
		this.uuid = uuid;
		this.is_active = true;
	}

	public static class Deserializer implements JsonDeserializer<PlayerHelper> {

		@Override
		public PlayerHelper deserialize(final JsonElement json, final Type typeOfT,
				final JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new PlayerHelper(
					jobject.get("id"),
					jobject.get("minecraft_user_name"),
					jobject.get("uuid"),
					jobject.get("is_active"));
		}
	}

}
