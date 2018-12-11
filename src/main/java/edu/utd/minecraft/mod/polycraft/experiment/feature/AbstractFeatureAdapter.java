package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public abstract class AbstractFeatureAdapter<T> extends TypeAdapter<T> {
	
	private Gson gson;

	  protected <E> void delegateWrite(final JsonWriter out, final E object) throws IOException {
	    @SuppressWarnings("unchecked")
	    final Class<E> type = (Class<E>) object.getClass();
	    delegateWrite(out, object, type);
	  }

	  protected <E> void delegateWrite(final JsonWriter out, final E object, final Class<E> type) throws IOException {
	    final TypeAdapter<E> typeAdapter = gson.getAdapter(type);
	    typeAdapter.write(out, object);
	  }
	  
	  protected <E extends ExperimentFeatureAdapter> T delegateRead(final JsonReader in, Class<E> cls) throws IOException {
		  final TypeAdapter<E> typeAdapter =  gson.getAdapter(cls);
		  return (T) typeAdapter.read(in);
	  }

	  @Override
	  public T read(final JsonReader in) throws IOException {
	    throw new UnsupportedOperationException("Method not implemented");
	  }

	  public void setGson(final Gson gson) {
	    this.gson = gson;
	  }

	  @Override
	  public void write(final JsonWriter out, final T value) throws IOException {
	    throw new UnsupportedOperationException("Method not implemented");
	  }
	}