package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class FeatureListAdapter extends AbstractFeatureAdapter<List<ExperimentFeature>> {

	 @Override
	  public void write(final JsonWriter out, final List<ExperimentFeature> object) throws IOException {
	    out.beginArray();
	    for (final ExperimentFeature a : object) {
	      delegateWrite(out, a);
	    }
	    out.endArray();
	  }
	 
	 @Override
	 public List<ExperimentFeature> read(final JsonReader in) throws IOException {
		 
		 String canonicalPackageExtension = this.getClass().getPackage().getName();
		 
		 ArrayList<ExperimentFeature> expFeat = new ArrayList<>();
		 
		 in.beginArray();
		 while(in.hasNext()) {
			 in.beginObject();
			 String name = in.nextName();
			 if(name == null) continue;
			 switch(name) {
			 case ExperimentFeatureAdapter.KEY_TYPE:
				 Class<ExperimentFeatureAdapter> cls;
				try {
					String clsName = in.nextString();
					cls = (Class<ExperimentFeatureAdapter>) Class.forName(canonicalPackageExtension + "." + clsName);
					
					expFeat.add((ExperimentFeature) delegateRead(in, cls));
					in.endObject();
					
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} 
				//TODO: remove this.
				catch(IOException e2) {
					System.out.println("Error in Reading!");
					e2.printStackTrace();
				}
				break;
			 default:
				 in.skipValue();
			 }
		 }
		 
		 in.endArray();
		 
		 
		 return expFeat;
	 }
}
