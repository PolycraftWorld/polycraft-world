package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * This adapter is used to parse a JSON file containing a list of ExperimentFeature objects
 * that are all part of a single Experiment. It uses {@link #delegateWrite(JsonWriter, Object)} and 
 * {@link #delegateRead(JsonReader, Class)} to automatically pick the proper TypeAdapter Class to handle
 * the reading and writing, based on what the Class or Object is. The Current Read format is as follows:
 * 
 * [ //open array
 * 		{ Experiment Feature Object 1}, {Experiment Feature Object 2}, ... , {Experiment Feature Object n}
 * ] //end array
 * 
 * Please note, within the JsonObject, order matters. Please place the "type": parameter first in every
 * object so that no parameters are skipped. Gson reads linearly so any parameters before type are
 * not passed to the Type Adapters to be read; they are skipped!
 * 
 * 
 * @author dxn140130
 *
 */
public class FeatureListAdapter extends AbstractFeatureAdapter<List<ExperimentFeature>> {

	 @Override
	  public void write(final JsonWriter out, final List<ExperimentFeature> object) throws IOException {
	    out.beginArray();
	    for (final ExperimentFeature a : object) {
	      delegateWrite(out, a);
	    }
	    out.endArray();
	  }
	 
	 /**
	  * This Class needs to be in the same package as all of the other Adapters, otherwise, modify the 
	  * @canonicalPackageExtension variable to the location where all of the adapters are located.
	  * 
	  * The endObject() reading is processed here, inside the try-catch
	  */
	 @Override
	 public List<ExperimentFeature> read(final JsonReader in) throws IOException {
		 
		 String canonicalPackageExtension = this.getClass().getPackage().getName();
		 
		 ArrayList<ExperimentFeature> expFeat = new ArrayList<>();
		 
		 in.beginArray(); //consume the begin array token
		 while(in.hasNext()) { //while in still has characters
			 in.beginObject(); //consume the begin object token ('{')
			 String name = in.nextName(); //get the first parameter name
			 if(name == null) continue;
			 switch(name) {
			 case ExperimentFeatureAdapter.KEY_TYPE: //if the name is type (Which it should be!), handle the case
				 Class<ExperimentFeatureAdapter> cls;
				try {
					String clsName = in.nextString();
					
					//get generic class from the package name and Type passed in
					cls = (Class<ExperimentFeatureAdapter>) Class.forName(canonicalPackageExtension + "." + clsName);
					
					//use that object's TypeAdapter to read in the JSON and add it to the ExperimentFeature List
					expFeat.add((ExperimentFeature) delegateRead(in, cls));
					
					in.endObject(); //consume the end object token ('}')
					
				} catch (ClassNotFoundException e1) {
					System.out.println("Uh-oh, not All Type Adapters have been Registered! Or, invalid JSON class passed in.");
					e1.printStackTrace();
				} 
				break;
			 default:
				 in.skipValue(); //skip any values that are not "type". note, this also skips its associated objects/arrays and is recursive
				 break;
			 }
		 }
		 
		 in.endArray(); //consume the end array token
		 		 
		 return expFeat;
	 }
}
