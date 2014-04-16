package edu.utd.minecraft.mod.polycraft.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A set map is a map of sets mapping to sets of values.
 * A SetMap can find both exact sets, or sets that contain a subset of keys.
 * Useful if you want to store and find sets based on a subset of that set.
 */
public class SetMap<K extends Comparable<K>, V> {
	private final Map<K, Set<V>> values = Maps.newHashMap();	
	private final Set<V> fullSet = Sets.newHashSet();
	
	/**
	 * Maps each value of K in the SetMap to V.
	 */
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
	
	/**
	 * @return True if the map contains exactly the specified set.
	 */
	public boolean contains(final Set<K> set) {
		return get(set).size() != 0;
	}
	
	// Returns the set of values stored by the exact specified set.
	/**
	 * Returns the set of values stored by exactly the specified set.
	 */
	@SuppressWarnings("unchecked")
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
	
	/**
	 * @return true if any set in the map is a subset of the specified set.
	 */
	public boolean containsSubset(final Set<K> set) {
		return getAnySubset(set).size() != 0;
	}
	
	/**
	 * @return the set of values from the SetMap of any subset of the input set.
	 */
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