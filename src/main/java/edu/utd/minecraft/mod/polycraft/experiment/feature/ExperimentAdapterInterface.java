package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * All experiment feature adapters need a write and read function that use these function signatures.
 * 
 * @author dxn140130
 *
 */
public interface ExperimentAdapterInterface {
	
	/**
	 * This should call {@link JsonWriter#beginObject()} and {@link JsonWriter#endObject()} at the beginning 
	 * and end of each ExperimentFeature object that needs to be written to the JSON file.
	 *  
	 * @param out the JSON file
	 * @param object the Object that is being converted 
	 * @throws IOException
	 */
	public void write(final JsonWriter out, final ExperimentFeature object) throws IOException;
	
	/**
	 * This should NEVER call {@link JsonReader#endObject()} and that should instead be handled by {@link FeatureListAdapter} or 
	 * by {@link AbstractFeatureAdapter}. 
	 * 
	 * @param in the object that contains the JSON string
	 * @return an ExperimentFeature object that can be added to 
	 * the FeatureList ArrayList in {@link FeatureListAdapter} and 
	 * given to the function that asked for the json to be read
	 * @throws IOException
	 */
	public ExperimentFeature read(final JsonReader in) throws IOException;
	
	

}
