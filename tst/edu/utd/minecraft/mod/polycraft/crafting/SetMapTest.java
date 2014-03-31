package edu.utd.minecraft.mod.polycraft.crafting;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import edu.utd.minecraft.mod.polycraft.util.SetMap;

public class SetMapTest {

	@Test
	public void simpleSetTest() {
		SetMap<String, String> map = new SetMap<String, String>();
		map.add(ImmutableSet.of("1", "2"), "test");
		
		assertTrue(map.contains(Sets.newLinkedHashSet(ImmutableSet.of("1", "2"))));
		assertTrue(map.contains(ImmutableSet.of("2", "1")));
		assertFalse(map.contains(ImmutableSet.of("2", "1", "3")));
		assertTrue(map.containsSubset(ImmutableSet.of("2", "1", "3")));		
	}

	@Test
	public void overlappingTestSet() {
		SetMap<String, String> map = new SetMap<String, String>();
		map.add(ImmutableSet.of("1", "2"), "test");
		map.add(ImmutableSet.of("1"), "test2");
		
		assertEquals(ImmutableSet.of("test", "test2"), map.getAnySubset(Sets.newLinkedHashSet(ImmutableSet.of("1", "2"))));
		assertEquals(ImmutableSet.of("test", "test2"), map.getAnySubset(ImmutableSet.of("2", "1", "3")));
	}

}
