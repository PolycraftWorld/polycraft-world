package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/***
 * Adapted from: https://github.com/javacreed/gson-typeadapterfactory-example
 * A generic typed adapter that returns objects of Type<T>, 
 * @see ExperimentFeatureAdapter
 * @author dxn140130
 *
 * @param <T> The Type of Objects that are being created/read/written ( for now, {@link ExperimentFeature})
 */
public abstract class AbstractFeatureAdapter<T> extends TypeAdapter<T> {
	
	private Gson gson;

	  protected <E> void delegateWrite(final JsonWriter out, final E object) throws IOException {
	    @SuppressWarnings("unchecked")
	    final Class<E> type = (Class<E>) object.getClass();
	    delegateWrite(out, object, type);
	  }

	  /**
	   * Get the proper TypeAdapter for writing a particular object
	   * @param out the JSONStream that is being written to
	   * @param object the Object that needs to be written
	   * @param type The Object's Type
	   * @throws IOException Any Write issues (i.e. not registering a type adapter)
	   */
	  protected <E> void delegateWrite(final JsonWriter out, final E object, final Class<E> type) throws IOException {
	    final TypeAdapter<E> typeAdapter = gson.getAdapter(type); //get the type adapter from the gson, if it's registered
	    typeAdapter.write(out, object);
	  }
	  
	  /**
	   * Get the proper type adapter to be used when reading an object
	   * @param in the JSONStream that is being read from
	   * @param cls the expected class to be read
	   * @return the {@link ExperimentFeature} object that is created by the TypeAdapter in question
	   * @throws IOException
	   */
	  protected <E extends ExperimentFeatureAdapter> T delegateRead(final JsonReader in, Class<E> cls) throws IOException {
		  final TypeAdapter<E> typeAdapter =  gson.getAdapter(cls);
		  return (T) typeAdapter.read(in);
	  }

	  @Override
	  public T read(final JsonReader in) throws IOException {
	    throw new UnsupportedOperationException("Method not implemented");
	  }

	  /**
	   * sets the {@link Gson} object that has the properly registered TypeAdapters to this class.
	   * @param gson 
	   */
	  public void setGson(final Gson gson) {
	    this.gson = gson;
	  }

	  @Override
	  public void write(final JsonWriter out, final T value) throws IOException {
	    throw new UnsupportedOperationException("Method not implemented");
	  }
	}