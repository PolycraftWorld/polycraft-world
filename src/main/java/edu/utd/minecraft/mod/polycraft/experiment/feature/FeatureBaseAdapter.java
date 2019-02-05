package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * Reads and writes a {@link FeatureBase} object to Json
 * @author dxn140130
 *
 */
public class FeatureBaseAdapter extends ExperimentFeatureAdapter {
	
	@Override
	public void write(final JsonWriter out, final ExperimentFeature value) throws IOException {
		super.write(out, value);
		FeatureBase fb = (FeatureBase) value;
		out.name("baseSize").value(fb.getBoundingBoxRadius());
		out.name("baseHeight").value(1); //TODO: make this variable!
		this.endWrite(out);
		
	}

	@Override
	public ExperimentFeature read(JsonReader in) throws IOException {
		/*
		 *   {
			    "type": "FeatureBase",
			    "name": "",
			    "x": 114,
			    "y": 21,
			    "z": 21,
			    "baseSize": 12.0,
			    "baseHeight": 1
			  },
		 */
		int x = 0, y= 0, z= 0, radius= 0, height= 0;
		
		boolean endOfObject = false;
		
		while(!endOfObject) {
			
			//Case: Null inputs (consume, and move on)
			if(in.peek() == JsonToken.NULL) {
				in.nextNull();
				continue;
			}
			
			//Case: Arrived at the end of the object.
			else if(in.peek() == JsonToken.END_OBJECT) {
				//end the object in the parent read function
				endOfObject = true;
				break;
			}
			
			//Case: Property
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
				case "baseSize":
					radius = (int) in.nextDouble(); //This currently truncates!
					break;
				case "baseHeight":
					height = in.nextInt();
					break;
				default:
					if(in.peek() != JsonToken.END_OBJECT && in.peek() != JsonToken.END_ARRAY)
						in.skipValue(); //ignore the next value
					break;
				}
			}
			
			//Case: all others - should not happen in normal flow, unless the input file is corrupt.
			else {
				throw new IOException("Error in Reading the Input");
				//in.nextString();
				//continue;
			}
		}
		
		return new FeatureBase(x,y,z,radius,height);
	}

}
