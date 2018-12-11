package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class ExperimentFeatureTypeAdapterFactory implements TypeAdapterFactory {
	
	public static class Builder {
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