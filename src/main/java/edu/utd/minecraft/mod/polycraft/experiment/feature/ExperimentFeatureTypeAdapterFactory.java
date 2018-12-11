package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This type adapter factory registers and builds a FactoryBuilder that maps all of the 
 * Classes with their proper Adapter Type. As of 12/11/18, this system is designed to read and 
 * write a single ExperimentFeature ArrayList from/to a JSON file.
 * 
 * Usage: call {@link #getExperimentFeatureGsonReader()} to get a Gson object that has the 
 * type adapters already registered. Use {@link Gson#fromJson(String, Type)} and pass in the String
 * containing the JSON and {@link #EXPERIMENT_LIST_TYPE} as the type parameter.
 * 
 * Developers: when creating new ExperimentFeature types, register the class and adapter in 
 * {@link #getExperimentFeatureBuilder()}, following the convention. If we need additional 
 * Input Stream adapters (i.e. reading a list of Experiments, each with their own List of 
 * ExperimentFeatures), use the format provided by {@link FeatureListAdapter} to create 
 * a new custom JSON reader. 
 * 
 * @author dxn140130
 *
 */
public class ExperimentFeatureTypeAdapterFactory implements TypeAdapterFactory {
	
	/**
	 * Currently, this reader/writer writes a list of ExperimentFeatures
	 */
	public static final Class<?> EXPERIMENT_LIST_TYPE = new TypeToken<List<ExperimentFeature>>() {}.getRawType();
	
	/**
	 * Add additional ExperimentFeature classes and adapters here so that the builder 
	 * knows what class to use to convert JSON to a Java object
	 * @return the Builder object containing all registered types
	 */
	private static Builder getExperimentFeatureBuilder() {
		
		Builder builder = new ExperimentFeatureTypeAdapterFactory.Builder();
		
		builder.add(FeatureSchematic.class, new FeatureSchematicAdapter());
		builder.add(FeatureBase.class, new FeatureBaseAdapter());
		builder.add(FeatureSpawn.class, new FeatureSpawnAdapter());
		
		//final Class<?> type = new TypeToken<List<ExperimentFeature>>() {}.getRawType();
		builder.add(EXPERIMENT_LIST_TYPE, new FeatureListAdapter());
		
		return builder;
		
	}
	
	/**
	 * Get the pre-registered Gson object that contains the typeAdapterFactory that
	 * can convert ExperimentFeature objects from/to JSON files.
	 * 
	 * Usage: Use {@link Gson#fromJson(String, Type)} and pass in the String containing 
	 * the JSON and {@link #EXPERIMENT_LIST_TYPE} as the type parameter.
	 * @return
	 */
	public static Gson getExperimentFeatureGsonReader() {
		GsonBuilder gbuild = new GsonBuilder();
		gbuild.registerTypeAdapterFactory(getExperimentFeatureBuilder().build());
		gbuild.setPrettyPrinting();
		
		return gbuild.create();

	}
	
	/**
	 * Maps the Type Adapters to Classes so that Gson knows which read() or write() function to run
	 * based on the JSON input or the Class input.
	 * 
	 * Adapted from: https://github.com/javacreed/gson-typeadapterfactory-example/blob/master/src/main/java/com/javacreed/examples/gson/part4/DataTypeAdapterFactory.java
	 * 
	 * @author dxn140130
	 *
	 */
	private static class Builder {
	    private final Map<Type, AbstractFeatureAdapter<?>> adapters = new HashMap<>();

	    public <T, E extends T> Builder add(final Class<T> type, final AbstractFeatureAdapter<E> adapter) {
	      return checkAndAdd(type, adapter);
	    }

	    public <T, E extends T> Builder add(final Type type, final AbstractFeatureAdapter<E> adapter) {
	      return checkAndAdd(type, adapter);
	    }

	    public ExperimentFeatureTypeAdapterFactory build() {
	      return new ExperimentFeatureTypeAdapterFactory(this);
	    }

	    private <T> Builder checkAndAdd(final Type type, final AbstractFeatureAdapter<T> adapter) {
	      Objects.requireNonNull(type);
	      Objects.requireNonNull(adapter);
	      adapters.put(type, adapter);
	      return this;
	    }
	  }

	  private final Map<Type, AbstractFeatureAdapter<?>> adapters = new HashMap<>();

	  private ExperimentFeatureTypeAdapterFactory(final Builder builder) {
	    adapters.putAll(builder.adapters);
	  }

	  @Override
	  public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> typeToken) {
	    final AbstractFeatureAdapter<T> typeAdapter = findTypeAdapter(typeToken);
	    if (typeAdapter != null) {
	      typeAdapter.setGson(gson);
	    }
	    return typeAdapter;
	  }

	  private <T> AbstractFeatureAdapter<T> findTypeAdapter(final TypeToken<T> typeToken) {
	    @SuppressWarnings("unchecked")
	    final Class<T> rawType = (Class<T>) typeToken.getRawType();

	    Class<?> selectedTypeAdapterType = null;
	    AbstractFeatureAdapter<?> selectedTypeAdapter = null;

	    for (final Entry<Type, AbstractFeatureAdapter<?>> entry : adapters.entrySet()) {
	      final Type typeAdapterType = entry.getKey();
	      if (rawType == typeAdapterType) {
	        selectedTypeAdapter = entry.getValue();
	        break;
	      }

	      if (typeAdapterType instanceof Class) {
	        final Class<?> typeAdapterClass = (Class<?>) typeAdapterType;
	        if (typeAdapterClass.isAssignableFrom(rawType)) {
	          if (selectedTypeAdapterType == null || selectedTypeAdapterType.isAssignableFrom(typeAdapterClass)) {
	            selectedTypeAdapterType = rawType;
	            selectedTypeAdapter = entry.getValue();
	          }
	        }
	      }
	    }

	    @SuppressWarnings("unchecked")
	    final AbstractFeatureAdapter<T> t = (AbstractFeatureAdapter<T>) selectedTypeAdapter;
	    return t;
	  }
}