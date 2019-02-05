package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class FeatureSpawnAdapter extends ExperimentFeatureAdapter {

	@Override
	public void write(final JsonWriter out, final ExperimentFeature value) throws IOException {
		super.write(out, value);
		FeatureSpawn fs = (FeatureSpawn) value;
		out.name("xMax").value(fs.getxMax());
		out.name("yMax").value(fs.getyMax());
		out.name("zMax").value(fs.getzMax());
		this.endWrite(out);
		
	}

	@Override
	public ExperimentFeature read(JsonReader in) throws IOException {
//		// {
//	    "type": "edu.utd.minecraft.mod.polycraft.experiment.feature.FeatureSpawn",
//	    "simpleType": "FeatureSpawn",
//	    "name": "",
//	    "x": 100,
//	    "y": 21,
//	    "z": 21,
//	    "xMax": 110,
//	    "yMax": 22,
//	    "zMax": 110
//	  },
		int x = 0, y= 0, z= 0, xMax= 0, zMax= 0;
		
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
				case "xMax":
					xMax = in.nextInt();
					break;
				case "zMax":
					zMax = in.nextInt();
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
		
		return new FeatureSpawn(x,y,z,xMax,zMax);
	}
}
