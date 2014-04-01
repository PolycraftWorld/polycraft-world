package edu.utd.minecraft.mod.polycraft.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

// A set map is a map of sets mapping to sets of values.
// A SetMap can find both exact sets, or sets that contain a subset of keys.
// Useful if you want to store and find sets based on a subset of that set.
public class SetMap<K extends Comparable<K>, V> {
	private final Map<K, Set<V>> values = Maps.newHashMap();	
	private final Set<V> fullSet = Sets.newHashSet();
	
	// Adds the set and value to the map.
	public void add(final Set<K> set, final V value) {
		fullSet.add(value);
		for (final K item : set) {
			Set<V> itemSet = (Set<V>) values.get(item);
			if (itemSet == null) {
				itemSet = Sets.newHashSet();
				values.put(item, itemSet);
			}
			itemSet.add(value);
		}		
	}
	
	// Returns true if the map contains the exact specified set.
	public boolean contains(final Set<K> set) {
		return get(set).size() != 0;
	}
	
	private Set<V> mergeSet(final Set<V> set, final Set<V> set2) {
		Set<V> resultSet = set;
		if (resultSet == null) {
			resultSet = Sets.newHashSet();
		}
		if (set2 == null) {
			return resultSet;
		}
		resultSet.retainAll(set2);		
		return resultSet;
	}
	
	// Returns the set of values stored by the exact specified set.
	public Set<V> get(final Set<K> set) {
		Set<V> result = fullSet;
		for (final K key : set) {
			Set<V> set2 = values.get(key);
			if (set2 != null) {
				result.retainAll(set2);
			} else {
				return Collections.EMPTY_SET;
			}
		}
		return result;
	}
	
	// Returns true if any set in the map is a subset of the specified set.
	public boolean containsSubset(final Set<K> set) {
		return getAnySubset(set).size() != 0;
	}
	
	// Gets the set of values from the SetMap of any subset of the input set.
	public Set<V> getAnySubset(final Set<K> set) {
		if (set.size() == 0) {
			return fullSet;
		}
		
		Set<V> result = Sets.newHashSet();
		for (final K key : set) {
			Set<V> keySet = values.get(key);
			if (keySet != null) {
				result.addAll(keySet);
			}
		}		
		return result;		
	}
}
