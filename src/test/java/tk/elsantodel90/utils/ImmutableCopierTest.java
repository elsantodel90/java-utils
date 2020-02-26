/*
 * Copyright 2020 Agustín Santiago Gutiérrez
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package tk.elsantodel90.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tk.elsantodel90.utils.ImmutableCopier.ofInteger;
import static tk.elsantodel90.utils.ImmutableCopier.ofList;
import static tk.elsantodel90.utils.ImmutableCopier.ofListMultimap;
import static tk.elsantodel90.utils.ImmutableCopier.ofMap;
import static tk.elsantodel90.utils.ImmutableCopier.ofMultimap;
import static tk.elsantodel90.utils.ImmutableCopier.ofMultiset;
import static tk.elsantodel90.utils.ImmutableCopier.ofSetMultimap;
import static tk.elsantodel90.utils.ImmutableCopier.ofString;
import static tk.elsantodel90.utils.ImmutableCopier.ofValue;
import static tk.elsantodel90.utils.MapUtils.entriesToMap;
import static tk.elsantodel90.utils.MapUtils.overwritingDuplicates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

public class ImmutableCopierTest {

	@Test
	public void testValues() {
		Double o1 = 1.5;
		Integer o2 = 10;
		Long o3 = 20L;
		String o4 = "pepe";

		for (Object o : Arrays.asList(o1, o2, o3, o4)) {
			assertTrue(o == ImmutableCopier.ofValue().copy(o));
		}

		assertTrue(o1 == ImmutableCopier.ofValue(Double.class).copy(o1));
		assertTrue(o2 == ImmutableCopier.ofValue(Integer.class).copy(o2));
		assertTrue(o3 == ImmutableCopier.ofValue(Long.class).copy(o3));
		assertTrue(o4 == ImmutableCopier.ofValue(String.class).copy(o4));

		assertTrue(o1 == ImmutableCopier.ofValue(new TypeToken<Double>() {}).copy(o1));
		assertTrue(o2 == ImmutableCopier.ofValue(new TypeToken<Integer>() {}).copy(o2));
		assertTrue(o3 == ImmutableCopier.ofValue(new TypeToken<Long>() {}).copy(o3));
		assertTrue(o4 == ImmutableCopier.ofValue(new TypeToken<String>() {}).copy(o4));

		assertTrue(o1 == ImmutableCopier.ofDouble().copy(o1));
		assertTrue(o2 == ImmutableCopier.ofInteger().copy(o2));
		assertTrue(o3 == ImmutableCopier.ofLong().copy(o3));
		assertTrue(o4 == ImmutableCopier.ofString().copy(o4));
	}

	@Test
	public void testImmutableList() {
		testImmutable(testListMethodFactory);
	}

	@Test
	public void testImmutableSet() {
		testImmutable(testSetMethodFactory);
	}

	@Test
	public void testImmutableMultiset() {
		testImmutable(testMultisetMethodFactory);
	}

	@Test
	public void testImmutableMap() {
		testImmutable(testMapMethodFactory);
	}

	@Test
	public void testImmutableMultimap() {
		testImmutable(testMultimapMethodFactory);
	}

	@Test
	public void testImmutableListMultimap() {
		testImmutable(testListMultimapMethodFactory);
	}

	@Test
	public void testImmutableSetMultimap() {
		testImmutable(testSetMultimapMethodFactory);
	}

	@Test
	public void testDeep1() {
		Set<List<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> v =
				ImmutableSet.of(Arrays.asList(ImmutableMap.of(), ImmutableMap.of(
						10, ImmutableMultimap.of(15, "a", 15, "b", 20, "c", 15, "d", 20, ""),
						20, ImmutableMultimap.of(-10, "a", -11, "b", -11, "c"))),
						Collections.emptyList());
		Set<List<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> vcopy =
				ImmutableSet.of(Arrays.asList(ImmutableMap.of(), ImmutableMap.of(
						10, ImmutableMultimap.of(15, "a", 15, "b", 20, "c", 15, "d", 20, ""),
						20, ImmutableMultimap.of(-10, "a", -11, "b", -11, "c"))),
						Collections.emptyList());
		ImmutableSet<ImmutableList<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> actualCopy =
				ImmutableCopier.ofSet(ofList(ofMap(ofInteger(), ofMultimap(ofInteger(), ofString())))).copy(v);
		assertEquals(vcopy, actualCopy);
	}

	@Test
	public void testDeep2() {
		Set<Multiset<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> v =
				ImmutableSet.of(HashMultiset.create(Arrays.asList(ImmutableMap.of(), ImmutableMap.of(
						10, ImmutableMultimap.of(15, "a", 15, "b", 20, "c", 15, "d", 20, ""),
						20, ImmutableMultimap.of(-10, "a", -11, "b", -11, "c")))),
						HashMultiset.create());
		Set<Multiset<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> vcopy =
				ImmutableSet.of(HashMultiset.create(Arrays.asList(ImmutableMap.of(), ImmutableMap.of(
						10, ImmutableMultimap.of(15, "a", 15, "b", 20, "c", 15, "d", 20, ""),
						20, ImmutableMultimap.of(-10, "a", -11, "b", -11, "c")))),
						HashMultiset.create());
		ImmutableSet<ImmutableMultiset<ImmutableMap<Integer, ImmutableMultimap<Integer, String>>>> actualCopy =
				ImmutableCopier.ofSet(ofMultiset(ofMap(ofInteger(), ofMultimap(ofInteger(), ofString())))).copy(v);
		assertEquals(vcopy, actualCopy);
	}

	@Test
	public void testDeep3() {
		Multimap<Integer, SetMultimap<Integer, ListMultimap<String, List<String>>>> v =
				ImmutableMultimap.of(1, ImmutableSetMultimap.of(15, helperMultimap()),
						15, ImmutableSetMultimap.of());
		Multimap<Integer, SetMultimap<Integer, ListMultimap<String, List<String>>>> vcopy =
				ImmutableMultimap.of(1, ImmutableSetMultimap.of(15, helperMultimap()),
						15, ImmutableSetMultimap.of());
		ImmutableMultimap<Integer, ImmutableSetMultimap<Integer, ImmutableListMultimap<String, ImmutableList<String>>>> actualCopy =
				ImmutableCopier.ofMultimap(ofInteger(),
						ofSetMultimap(ofInteger(), ofListMultimap(ofString(), ofList(ofString())))).copy(v);
		assertEquals(vcopy, actualCopy);
	}

	private static ListMultimap<String, List<String>> helperMultimap() {
		ListMultimap<String, List<String>> ret = MultimapBuilder.hashKeys().arrayListValues().build();
		ret.put("pepe", Arrays.asList("juan", "dos", "dos"));
		ret.put("pepes", Arrays.asList("juana"));
		ret.put("", Arrays.asList("", "", "", ""));
		return ret;
	}

	private static void testImmutable(CollectionTestMethodFactory methodFactory) {
		fullyTestImmutable(methodFactory.getMethod(),
				Double.class, 37.3, Arrays.<Double> asList(10.10, 20.5, 30.1, 30.1));
		fullyTestImmutable(methodFactory.getMethod(),
				Integer.class, -11, Arrays.<Integer> asList(-10, 20, 0, 0, 10, -10, 10, 20, 50));
		fullyTestImmutable(methodFactory.getMethod(),
				Long.class, 1000000000000L, Arrays.<Long> asList(0L, 10L, 1000L, 99L, -1L, 1000L, 99L, 0L));
		fullyTestImmutable(methodFactory.getMethod(),
				String.class, "XXXXXXXaX", Arrays.<String> asList("", "a", "", "deeee", "a", "ra!"));
	}

	private static <T> void testImmutableList(Collection<? extends T> collection, Class<T> clazz) {
		ArrayList<T> l1 = new ArrayList<>(collection);
		ArrayList<T> original = new ArrayList<>(l1);
		ImmutableList<T> l2 = ImmutableCopier.ofList(ofValue(clazz)).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableList<T> l3 = ImmutableCopier.ofList(ofValue(clazz)).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void testImmutableSet(Collection<? extends T> collection, Class<T> clazz) {

		HashSet<T> l1 = new HashSet<>(collection);
		HashSet<T> original = new HashSet<>(collection);
		ImmutableSet<T> l2 = ImmutableCopier.ofSet(ofValue(clazz)).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableSet<T> l3 = ImmutableCopier.ofSet(ofValue(clazz)).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void testImmutableMultiset(Collection<? extends T> collection, Class<T> clazz) {

		HashMultiset<T> l1 = HashMultiset.create(collection);
		HashMultiset<T> original = HashMultiset.create(collection);
		ImmutableMultiset<T> l2 = ImmutableCopier.ofMultiset(ofValue(clazz)).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableMultiset<T> l3 = ImmutableCopier.ofMultiset(ofValue(clazz)).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void testImmutableMap(Collection<? extends T> collection, Class<T> clazz) {

		Map<? extends T, Integer> basemap = mapFromCollection(collection);

		HashMap<T, Integer> l1 = new HashMap<>(basemap);
		HashMap<T, Integer> original = new HashMap<>(basemap);
		ImmutableMap<T, Integer> l2 = ImmutableCopier.ofMap(ofValue(clazz), ofInteger()).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableMap<T, Integer> l3 = ImmutableCopier.ofMap(ofValue(clazz), ofInteger()).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void testImmutableMultimap(Collection<? extends T> collection, Class<T> clazz) {

		Multimap<? extends T, Integer> basemultimap = listMultimapFromCollection(collection);

		// ListMultimap
		{
			ListMultimap<T, Integer> l1 = MultimapBuilder.hashKeys().arrayListValues().build(basemultimap);
			ListMultimap<T, Integer> original = MultimapBuilder.hashKeys().arrayListValues().build(basemultimap);
			ImmutableMultimap<T, Integer> l2 = ImmutableCopier.ofMultimap(ofValue(clazz), ofInteger()).copy(l1);
			assertEquals(original, l2);
			assertEquals(original, l1);
			ImmutableMultimap<T, Integer> l3 = ImmutableCopier.ofMultimap(ofValue(clazz), ofInteger()).copy(l2);
			assertTrue(l2 == l3);
		}

		// SetMultimap
		{
			SetMultimap<T, Integer> l1 = MultimapBuilder.hashKeys().hashSetValues().build(basemultimap);
			SetMultimap<T, Integer> original = MultimapBuilder.hashKeys().hashSetValues().build(basemultimap);
			ImmutableMultimap<T, Integer> l2 = ImmutableCopier.ofMultimap(ofValue(clazz), ofInteger()).copy(l1);
			assertEquals(ImmutableListMultimap.copyOf(original), l2);
			assertEquals(original, l1);
			ImmutableMultimap<T, Integer> l3 = ImmutableCopier.ofMultimap(ofValue(clazz), ofInteger()).copy(l2);
			assertTrue(l2 == l3);
		}

		ImmutableSetMultimap<T, Integer> l4 = ImmutableSetMultimap.copyOf(basemultimap);
		ImmutableMultimap<T, Integer> l5 = ImmutableCopier.ofMultimap(ofValue(clazz), ofInteger()).copy(l4);
		assertTrue(l4 == l5);
	}

	private static <T> void testImmutableListMultimap(Collection<? extends T> collection, Class<T> clazz) {

		ListMultimap<? extends T, Integer> basemultimap = listMultimapFromCollection(collection);

		ListMultimap<T, Integer> l1 = MultimapBuilder.hashKeys().arrayListValues().build(basemultimap);
		ListMultimap<T, Integer> original = MultimapBuilder.hashKeys().arrayListValues().build(basemultimap);
		ImmutableListMultimap<T, Integer> l2 = ImmutableCopier.ofListMultimap(ofValue(clazz), ofInteger()).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableListMultimap<T, Integer> l3 = ImmutableCopier.ofListMultimap(ofValue(clazz), ofInteger()).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void testImmutableSetMultimap(Collection<? extends T> collection, Class<T> clazz) {

		SetMultimap<? extends T, Integer> basemultimap = setMultimapFromCollection(collection);

		SetMultimap<T, Integer> l1 = MultimapBuilder.hashKeys().hashSetValues().build(basemultimap);
		SetMultimap<T, Integer> original = MultimapBuilder.hashKeys().hashSetValues().build(basemultimap);
		ImmutableSetMultimap<T, Integer> l2 = ImmutableCopier.ofSetMultimap(ofValue(clazz), ofInteger()).copy(l1);
		assertEquals(original, l2);
		assertEquals(original, l1);
		ImmutableSetMultimap<T, Integer> l3 = ImmutableCopier.ofSetMultimap(ofValue(clazz), ofInteger()).copy(l2);
		assertTrue(l2 == l3);
	}

	private static <T> void fullyTestImmutable(CollectionTestMethod<T> testMethod, Class<T> clazz,
			T element, Collection<T> collection) {

		testMethod.test(ImmutableSet.of(), clazz);
		testMethod.test(ImmutableSet.of(element), clazz);
		testMethod.test(ImmutableSet.of(element, element), clazz);
		testMethod.test(collection, clazz);
	}

	private static <T> Map<T, Integer> mapFromCollection(Collection<T> collection) {
		return listMultimapFromCollection(collection).entries().stream()
				.collect(entriesToMap(overwritingDuplicates()));
	}

	private static <T> SetMultimap<T, Integer> setMultimapFromCollection(Collection<T> collection) {
		return MultimapBuilder.hashKeys().hashSetValues().build(listMultimapFromCollection(collection));
	}

	private static <T> ListMultimap<T, Integer> listMultimapFromCollection(Collection<T> collection) {
		ListMultimap<T, Integer> ret = MultimapBuilder.hashKeys().arrayListValues().build();
		int elementIndex = 0;
		for (T element : collection) {
			ret.put(element, elementIndex * elementIndex);
			elementIndex++;
		}
		return ret;
	}

	@FunctionalInterface
	private static interface CollectionTestMethod<T> {

		public void test(Collection<? extends T> collection, Class<T> clazz);
	}

	private static interface CollectionTestMethodFactory {

		public <T> CollectionTestMethod<T> getMethod();
	}

	private static CollectionTestMethodFactory testListMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableList;
		}
	};

	private static CollectionTestMethodFactory testSetMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableSet;
		}
	};

	private static CollectionTestMethodFactory testMultisetMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableMultiset;
		}
	};

	private static CollectionTestMethodFactory testMapMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableMap;
		}
	};

	private static CollectionTestMethodFactory testMultimapMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableMultimap;
		}
	};

	private static CollectionTestMethodFactory testListMultimapMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableListMultimap;
		}
	};

	private static CollectionTestMethodFactory testSetMultimapMethodFactory = new CollectionTestMethodFactory() {

		@Override
		public <T> CollectionTestMethod<T> getMethod() {
			return ImmutableCopierTest::testImmutableSetMultimap;
		}
	};
}
