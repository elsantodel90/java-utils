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

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * A utility class providing methods designed to be used with those of
 * {@link java.util.stream.Stream}, like map and filter, so that operations over
 * {@link java.util.Map} can be composed easily.
 * <p>
 * See {@code MapUtilsTest} for examples.
 * 
 * @author <a href=
 *         "https://github.com/elsantodel90/java-utils">https://github.com/elsantodel90/java-utils</a>
 *
 */
public class MapUtils {

	public static <K1, K2, V> Function<Entry<K1, V>, Entry<K2, V>> keyTransformer(Function<K1, K2> f) {
		return entry -> Maps.immutableEntry(f.apply(entry.getKey()), entry.getValue());
	}

	public static <K1, K2, V> Function<Entry<K1, V>, Entry<K2, V>> keyBiTransformer(BiFunction<K1, V, K2> f) {
		return entry -> Maps.immutableEntry(f.apply(entry.getKey(), entry.getValue()), entry.getValue());
	}

	public static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> valueTransformer(Function<V1, V2> f) {
		return entry -> Maps.immutableEntry(entry.getKey(), f.apply(entry.getValue()));
	}

	public static <K, V1, V2> Function<Entry<K, V1>, Entry<K, V2>> valueBiTransformer(BiFunction<K, V1, V2> f) {
		return entry -> Maps.immutableEntry(entry.getKey(), f.apply(entry.getKey(), entry.getValue()));
	}

	public static <K, V1, V2> Function<Entry<K, V1>, V2> entryMapper(BiFunction<K, V1, V2> f) {
		return entry -> f.apply(entry.getKey(), entry.getValue());
	}

	public static <K, V> Predicate<Entry<K, V>> keyFilter(Predicate<K> p) {
		return entry -> p.test(entry.getKey());
	}

	public static <K, V> Predicate<Entry<K, V>> valueFilter(Predicate<V> p) {
		return entry -> p.test(entry.getValue());
	}

	public static <K, V> Predicate<Entry<K, V>> entryFilter(BiPredicate<K, V> p) {
		return entry -> p.test(entry.getKey(), entry.getValue());
	}

	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> entriesToMap() {
		return Collectors.toMap(Entry::getKey, Entry::getValue);
	}

	public static <K, V> Collector<Entry<K, V>, ?, Map<K, V>> entriesToMap(BinaryOperator<V> mergeFunction) {
		return Collectors.toMap(Entry::getKey, Entry::getValue, mergeFunction);
	}

	public static <K, V> Collector<Entry<K, V>, ?, ImmutableMap<K, V>> entriesToImmutableMap() {
		return ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue);
	}

	public static <K, V> Collector<Entry<K, V>, ?, ImmutableMap<K, V>> entriesToImmutableMap(
			BinaryOperator<V> mergeFunction) {

		return ImmutableMap.toImmutableMap(Entry::getKey, Entry::getValue, mergeFunction);
	}

	public static <T> BinaryOperator<T> overwritingDuplicates() {
		return (oldValue, newValue) -> newValue;
	}

	private MapUtils() {
	}
}
