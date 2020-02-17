package edu.utd.minecraft.mod.polycraft.aitools;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.google.gson.JsonObject;

import io.netty.buffer.ByteBuf;

public class APICommandResult {

	public enum Result{
		SUCCESS,
		FAIL,
		PARTIAL,
		ACTION_TIMEOUT
	}
	private String command;
	private String[] args;
	private Result result;
	private String message;
	
	/**
	 * Used if the command <b>IS</b> included in args. ex: decoding command result json
	 * @param args
	 * @param result
	 * @param message
	 */
	public APICommandResult(String args[], Result result, String message) {
		command = args[0];
		if(args.length > 1)
			this.args = Arrays.copyOfRange(args, 1, args.length);
		
		this.result = result;
		this.message = message;
	}
	
	/**
	 * Used if the command <b>IS NOT</b> included in args. ex: decoding command result json
	 * @param command
	 * @param args
	 * @param result
	 * @param message
	 */
	public APICommandResult(String command, String args[], Result result, String message) {
		this.command = command;
		if(args.length > 1)
			this.args = Arrays.copyOfRange(args, 1, args.length);
		
		this.result = result;
		this.message = message;
	}
	
	public JsonObject toJson() {
		JsonObject jobject = new JsonObject();
		jobject.addProperty("command", command);
		if(args != null)
			jobject.addProperty("argument", String.join(" ", args));
		else
			jobject.addProperty("argument", "");
		jobject.addProperty("result", result.name());
		jobject.addProperty("message", message);
		return jobject;
	}
	
    public static APICommandResult fromJson(JsonObject jobject)
    {
    	return new APICommandResult(jobject.get("command").getAsString(),
    			jobject.get("argument").getAsString().split(" "),
    			Result.valueOf(jobject.get("result").getAsString()),
    			jobject.get("message").getAsString());
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

	
}
