package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Subclass this class to convert an ExperimentFeature to a JSON object.
 * You will need to override the write() function and implement the endWrite() function. (TODO: create an interface for this)
 * 
 * @author dxn140130
 *
 */
public class ExperimentFeatureAdapter extends AbstractFeatureAdapter<ExperimentFeature> implements ExperimentAdapterInterface {
	
	public static final String KEY_TYPE = "type";
	
	/**
	 * Begins writing an ExperimentFeature object to a file.
	 * Note: this does NOT add out.endObject() 
	 * Please call {@link ExperimentFeatureAdapter#endWrite()} to add the out.endObject() to the write stream.
	 * 
	 * 
	 * (non-Javadoc)
	 * @see edu.utd.minecraft.mod.polycraft.experiment.feature.AbstractFeatureAdapter#write(com.google.gson.stream.JsonWriter, java.lang.Object)
	 */
	@Override
	public void write(final JsonWriter out, final ExperimentFeature expFeature) throws IOException {
		out.beginObject();
		//out.name("type").value(expFeature.getClass().getCanonicalName());
		out.name(this.KEY_TYPE).value(expFeature.getClass().getSimpleName());
		out.name("name").value(expFeature.getName());
		out.name("x").value(expFeature.getxPos());
		out.name("y").value(expFeature.getyPos());
		out.name("z").value(expFeature.getyPos());
		
	}
	
	/**
	 * Call this after the writing is done for an ExperimentFeature object
	 * this adds the endObject ('}') text to the object.
	 * 
	 * @param out the JSON file that is being written
	 * @throws IOException
	 */
	protected void endWrite(final JsonWriter out) throws IOException {
		out.endObject();
	}
	
	/**
	 * Since we're reading files, we must consider EVERY POSSIBLE Parameter, even if we don't care to
	 * store the value in an object by calling in.nextName() and in.nextString() on that property and its value(s)
	 * 
	 * We can only return once the in object has reached the {@link JsonToken.END_OBJECT} flag (use in.peek() to check)
	 */
	@Override
	public ExperimentFeature read(final JsonReader in) throws IOException {return null;};

}
