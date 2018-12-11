package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Reads and writes a {@link FeatureSchematic} object.
 * @author dxn140130
 *
 */
public class FeatureSchematicAdapter extends ExperimentFeatureAdapter {
	
	@Override
	public void write(final JsonWriter out, final ExperimentFeature value) throws IOException {
		super.write(out, value);
		this.endWrite(out);
		
		
	}

	@Override
	public ExperimentFeature read(JsonReader in) throws IOException {
		/* Example write output.
		 *   {
			    "type": "edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureSchematic",
			    "simpleType": "FeatureSchematic",
			    "name": "stoopUpdated.psm",
			    "x": 10000,
			    "y": 16,
			    "z": 16
			  },
		 */
		
		int x = 0, y= 0, z= 0;
		String name = "";
		
		boolean endOfObject = false;
		
		while(!endOfObject) {
			if(in.peek() == JsonToken.NULL) {
				in.nextNull();
				break;
				//end case
			}else if(in.peek() == JsonToken.END_OBJECT) {
				//end the object in the parent class;
				endOfObject = true;
				break;
			}
			//case: Property
			else if(in.peek() == JsonToken.NAME) {
				switch(in.nextName()) {
				case "x":
					x = in.nextInt();
					break;
				case "y":
					y = in.nextInt();
					break;
				case "z":
					z = in.nextInt();
					break;
				case "name":
					name = in.nextString();
					break;
				default:
					if(in.peek() != JsonToken.END_OBJECT && in.peek() != JsonToken.END_ARRAY)
						in.skipValue(); //ignore the next value
					break;
				}
			}
			
			else {
				throw new IOException("Error reading file - corrupt?");
			}
		}
		
		return new FeatureSchematic(name);
 
	}
}
