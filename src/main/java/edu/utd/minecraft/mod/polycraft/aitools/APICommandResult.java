package edu.utd.minecraft.mod.polycraft.aitools;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;

public class APICommandResult {

	public enum Result{
		SUCCESS,
		FAIL,
		PARTIAL,
		ATTEMPT,
		ACTION_TIMEOUT
	}
	private String command;
	private String[] args;
	private Result result;
	private String message;
	private float stepCost;
	private float rewardScore = 0;
	private JsonObject jobject;
	
	/**
	 * Used if the command <b>IS NOT</b> included in args. ex: decoding command result json
	 * @param command
	 * @param args
	 * @param result
	 * @param message
	 * @param cost
	 */
	public APICommandResult(String command, String[] args, Result result, String message, float cost) {
		if(command == null) {	// old style stores command in args
			this.command = args[0];
			if(args.length > 1)
				this.args = Arrays.copyOfRange(args, 1, args.length);
			else
				this.args = new String[0];
		}else {
			this.command = command;
			this.args = args;
		}
		
		this.result = result;
		this.message = message;
		this.stepCost = cost;
		jobject = new JsonObject();
	}
	
	public APICommandResult(String[] args, Result result, String message, float cost) {
		this(null, args, result, message, cost);
	}
	
	public APICommandResult(String command, String[] args, Result result, String message) {
		this(command, args, result, message, 0);
	}
	
	public APICommandResult(String[] args, Result result, String message) {
		this(null, args, result, message);
	}
	
	public JsonObject toJson() {
		JsonObject jobj = new JsonObject();
		jobj.addProperty("command", command);
		if(args != null)
			jobj.addProperty("argument", String.join(" ", args));
		else
			jobj.addProperty("argument", "");
		jobj.addProperty("result", result.name());
		jobj.addProperty("message", message);
		jobj.addProperty("stepCost", stepCost);
		jobj.addProperty("rewardScore", rewardScore);
		return jobj;
	}
    
	// don't include reward score when printing
    public JsonObject getPrintJObject()
    {
    	JsonObject jobj = new JsonObject();
		jobj.addProperty("command", command);
		if(args != null)
			jobj.addProperty("argument", String.join(" ", args));
		else
			jobj.addProperty("argument", "");
		jobj.addProperty("result", result.name());
		jobj.addProperty("message", message);
		jobj.addProperty("stepCost", stepCost);
		return jobj;
    }
	
	/**
	 * Function to get Both command result and jobj in one json
	 * @param jobj
	 * @return
	 */
	public JsonObject getFullJson(JsonObject jobj) {
		for(Entry<String, JsonElement> entry: this.toJson().entrySet()) {
			jobj.add(entry.getKey(), entry.getValue());
		}
		
		return jobj;
	}
	
    public static APICommandResult fromJson(JsonObject jobject)
    {
    	APICommandResult result = new APICommandResult(jobject.get("command").getAsString(),
    			jobject.get("argument").getAsString().split(" "),
    			Result.valueOf(jobject.get("result").getAsString()),
    			jobject.get("message").getAsString(),
    			jobject.get("stepCost").getAsFloat());
    	result.setScore(jobject.get("rewardScore").getAsFloat());
    	
    	return result;
    }

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getArgs() {
		return args;
	}
	
	public float getCost() {
		return stepCost;
	}
	
	public void setScore(float score) {
		this.rewardScore = score;
	}
	
	public float getScore() {
		return rewardScore;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void addJObject(String label, JsonObject jobj) {
		this.jobject.add(label, jobj);
	}
	
	public void setJObject(JsonObject jobj) {
		this.jobject = jobj;
	}
	
	public JsonObject getJobject() {
		return this.jobject;
	}
}
