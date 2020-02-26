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

import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static tk.elsantodel90.utils.MapUtils.entriesToImmutableMap;
import static tk.elsantodel90.utils.MapUtils.entriesToMap;
import static tk.elsantodel90.utils.MapUtils.entryFilter;
import static tk.elsantodel90.utils.MapUtils.entryMapper;
import static tk.elsantodel90.utils.MapUtils.keyBiTransformer;
import static tk.elsantodel90.utils.MapUtils.keyFilter;
import static tk.elsantodel90.utils.MapUtils.keyTransformer;
import static tk.elsantodel90.utils.MapUtils.overwritingDuplicates;
import static tk.elsantodel90.utils.MapUtils.valueBiTransformer;
import static tk.elsantodel90.utils.MapUtils.valueFilter;
import static tk.elsantodel90.utils.MapUtils.valueTransformer;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class MapUtilsTest {

	private static final int MAX = 100;

	@Test
	public void test() {
		assertTrue(MAX > 10);
		assertEquals(testMapKeyPlusFive(), squaredTestMap().entrySet().stream()
				.map(keyTransformer(key -> key + 5))
				.collect(entriesToMap()));
		assertEquals(testMapKeyPlusSquared(), squaredTestMap().entrySet().stream()
				.map(keyBiTransformer((key, value) -> key + value))
				.collect(entriesToImmutableMap()));
		assertEquals(testMapValueMinusOne(), squaredTestMap().entrySet().stream()
				.map(valueTransformer(value -> value - 1))
				.collect(entriesToMap()));
		assertEquals(testMapValueMinusX(), squaredTestMap().entrySet().stream()
				.map(valueBiTransformer((key, value) -> value - key))
				.collect(entriesToImmutableMap()));
		assertEquals(testMapKeyPlusSquaredValueReplacedByMinusX(), squaredTestMap().entrySet().stream()
				.map(keyBiTransformer((key, value) -> key + value))
				.map(valueBiTransformer((key, value) -> value - key))
				.collect(entriesToMap()));
		assertEquals(IntStream.range(MAX - 5, MAX).map(x -> 2 * x + x * x).sum(),
				squaredTestMap().entrySet().stream()
						.filter(keyFilter(key -> key >= MAX - 5))
						.map(entryMapper((key, value) -> 2 * key + value))
						.collect(summingInt(x -> x)).intValue());
		assertEquals(IntStream.range(0, MAX).map(x -> (x * x) % 8 == 0 ? 3 * x : 0).sum(),
				squaredTestMap().entrySet().stream()
						.filter(valueFilter(value -> value % 8 == 0))
						.map(entryMapper((key, value) -> 3 * key))
						.collect(summingInt(x -> x)).intValue());
		assertEquals(IntStream.range(0, MAX).map(x -> (x * x) % 4 == 0 && x % 3 == 0 ? 7 * x + x * x : 0).sum(),
				squaredTestMap().entrySet().stream()
						.filter(entryFilter((key, value) -> key % 3 == 0 && value % 4 == 0))
						.map(entryMapper((key, value) -> 7 * key + value))
						.collect(summingInt(x -> x)).intValue());
		List<Entry<Integer, Integer>> baseList = ImmutableList.of(
				Maps.immutableEntry(1, 10),
				Maps.immutableEntry(2, 20),
				Maps.immutableEntry(1, 15));
		assertEquals(ImmutableMap.of(2, 20, 1, 15),
				baseList.stream()
						.collect(entriesToMap(overwritingDuplicates())));
		assertEquals(ImmutableMap.of(2, 20, 1, 25),
				baseList.stream()
						.collect(entriesToImmutableMap(Integer::sum)));

	}

	private static Map<Integer, Integer> squaredTestMap() {
		return testMap(x -> x, x -> x * x);
	}

	private static Map<Integer, Integer> testMapKeyPlusFive() {
		return testMap(x -> x + 5, x -> x * x);
	}

	private static Map<Integer, Integer> testMapKeyPlusSquared() {
		return testMap(x -> x + x * x, x -> x * x);
	}

	private static Map<Integer, Integer> testMapValueMinusOne() {
		return testMap(x -> x, x -> x * x - 1);
	}

	private static Map<Integer, Integer> testMapValueMinusX() {
		return testMap(x -> x, x -> x * x - x);
	}

	private static Map<Integer, Integer> testMapKeyPlusSquaredValueReplacedByMinusX() {
		return testMap(x -> x + x * x, x -> -x);
	}

	private static Map<Integer, Integer> testMap(Function<Integer, Integer> keyFunction,
			Function<Integer, Integer> valueFunction) {

		return IntStream.range(0, MAX)
				.mapToObj(x -> x)
				.collect(toMap(keyFunction, valueFunction));
	}
}
