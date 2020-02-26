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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMultiset.toImmutableMultiset;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

/**
 * A utility class providing a type-safe "deep copy" of guava's Immutables. The main downside is having to manually
 * specify the full "type" being copied (quite unavoidable without resigning type-safety, due to java's type-erasure).
 * <p>
 * For example, to copy a {@code List<Set<Multiset<String>>>}, just do
 * {@code ImmutableCopier.ofList(ofSet(ofMultiset(ofString()))).copy(data)}
 * <p>
 * The resulting compile time type of this copy method is
 * {@code ImmutableList<ImmutableSet<ImmutableMultiset<String>>> copy(List<? extends Set<? extends Multiset<String>>> data);},
 * which is the correct most general appropriate type, allowing clients to pass any particular implementation like
 * {@code List<HashSet<ImmutableMultiset<String>>> }, while rejecting non-matching collections like
 * {@code List<Set<Set<String>>>}.
 * <p>
 * See {@code ImmutableCopierTest} for examples of use.
 * 
 * @author <a href=
 *         "https://github.com/elsantodel90/java-utils">https://github.com/elsantodel90/java-utils</a>
 *
 */
public abstract class ImmutableCopier<T, W> {

	// Public Interface

	public abstract W copy(T value);

	@SuppressWarnings("unchecked")
	public static <T> ImmutableCopier<T, T> ofValue() {
		return (ImmutableCopier<T, T>) identityCopier;
	}

	/**
	 * This method is provided to help with static import, because
	 * ofList(ofValue(MyThing.class)) looks better than
	 * ofList(ImmutableCopier.&lt;MyThing&gt;ofValue())
	 */
	public static <T> ImmutableCopier<T, T> ofValue(@SuppressWarnings("unused") Class<T> clazz) {
		return ofValue();
	}

	/**
	 * This method is provided to help with static import, because
	 * ofList(ofValue(new TypeToken&lt;Holder&lt;Flag&gt;&gt;(){})) looks better than
	 * ofList(ImmutableCopier.&lt;Holder&lt;Flag&gt;&gt;ofValue())
	 */
	public static <T> ImmutableCopier<T, T> ofValue(@SuppressWarnings("unused") TypeToken<T> typeToken) {
		return ofValue();
	}

	// These specializations are provided to help type inference and avoid having to manually specify type parameters

	public static ImmutableCopier<Double, Double> ofDouble() {
		return ofValue();
	}

	public static ImmutableCopier<Integer, Integer> ofInteger() {
		return ofValue();
	}

	public static ImmutableCopier<Long, Long> ofLong() {
		return ofValue();
	}

	public static ImmutableCopier<String, String> ofString() {
		return ofValue();
	}

	@SuppressWarnings({"unchecked"})
	public static <T, W> ImmutableCopier<List<? extends T>, ImmutableList<W>> ofList(ImmutableCopier<T, W> subcopier) {
		return subcopier == identityCopier
				? (ImmutableCopier<List<? extends T>, ImmutableList<W>>) simpleListCopier
				: new DelegatingCollectionCopier<>(subcopier, toImmutableList());
	}

	@SuppressWarnings({"unchecked"})
	public static <T, W> ImmutableCopier<Set<? extends T>, ImmutableSet<W>> ofSet(ImmutableCopier<T, W> subcopier) {
		return subcopier == identityCopier
				? (ImmutableCopier<Set<? extends T>, ImmutableSet<W>>) simpleSetCopier
				: new DelegatingCollectionCopier<>(subcopier, toImmutableSet());
	}

	@SuppressWarnings({"unchecked"})
	public static <T, W> ImmutableCopier<Multiset<? extends T>, ImmutableMultiset<W>> ofMultiset(
			ImmutableCopier<T, W> subcopier) {

		return subcopier == identityCopier
				? (ImmutableCopier<Multiset<? extends T>, ImmutableMultiset<W>>) simpleMultisetCopier
				: new DelegatingCollectionCopier<>(subcopier, toImmutableMultiset());
	}

	@SuppressWarnings({"unchecked"})
	public static <K, V, K2, V2> ImmutableCopier<Map<? extends K, ? extends V>, ImmutableMap<K2, V2>> ofMap(
			ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {

		return keySubcopier == identityCopier && valueSubcopier == identityCopier
				? (ImmutableCopier<Map<? extends K, ? extends V>, ImmutableMap<K2, V2>>) simpleMapCopier
				: new DelegatingMapCopier<>(keySubcopier, valueSubcopier);
	}

	@SuppressWarnings({"unchecked"})
	public static <K, V, K2, V2> ImmutableCopier<Multimap<? extends K, ? extends V>, ImmutableMultimap<K2, V2>> ofMultimap(
			ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {

		return keySubcopier == identityCopier && valueSubcopier == identityCopier
				? (ImmutableCopier<Multimap<? extends K, ? extends V>, ImmutableMultimap<K2, V2>>) simpleMultimapCopier
				: new DelegatingMultimapCopier<>(keySubcopier, valueSubcopier);
	}

	@SuppressWarnings({"unchecked"})
	public static <K, V, K2, V2> ImmutableCopier<ListMultimap<? extends K, ? extends V>, ImmutableListMultimap<K2, V2>> ofListMultimap(
			ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {

		return keySubcopier == identityCopier && valueSubcopier == identityCopier
				? (ImmutableCopier<ListMultimap<? extends K, ? extends V>, ImmutableListMultimap<K2, V2>>) simpleListMultimapCopier
				: new DelegatingListMultimapCopier<>(keySubcopier, valueSubcopier);
	}

	@SuppressWarnings({"unchecked"})
	public static <K, V, K2, V2> ImmutableCopier<SetMultimap<? extends K, ? extends V>, ImmutableSetMultimap<K2, V2>> ofSetMultimap(
			ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {

		return keySubcopier == identityCopier && valueSubcopier == identityCopier
				? (ImmutableCopier<SetMultimap<? extends K, ? extends V>, ImmutableSetMultimap<K2, V2>>) simpleSetMultimapCopier
				: new DelegatingSetMultimapCopier<>(keySubcopier, valueSubcopier);
	}

	// Internal Implementation
	private ImmutableCopier() {
	}

	private static final ImmutableCopier<?, ?> identityCopier = new ImmutableCopier<Object, Object>() {

		@Override
		public Object copy(Object value) {
			return value;
		}
	};

	private static class DelegatingCollectionCopier<A, B, C extends Collection<? extends A>, D extends ImmutableCollection<B>>
			extends ImmutableCopier<C, D> {

		private final ImmutableCopier<A, B> subcopier;
		private final Collector<B, ?, D> collector;

		private DelegatingCollectionCopier(ImmutableCopier<A, B> subcopier, Collector<B, ?, D> collector) {
			this.subcopier = subcopier;
			this.collector = collector;
		}

		@Override
		public D copy(C collection) {
			return collection.stream()
					.map(subcopier::copy)
					.collect(collector);
		}

	}

	private static abstract class AbstractDelegatingMaplikeCopier<K, V, K2, V2, C, C2>
			extends ImmutableCopier<C, C2> {

		private final ImmutableCopier<K, K2> keySubcopier;
		private final ImmutableCopier<V, V2> valueSubcopier;
		private final BiFunction<Function<? super Entry<? extends K, ? extends V>, K2>, Function<? super Entry<? extends K, ? extends V>, V2>, Collector<? super Entry<? extends K, ? extends V>, ?, ? extends C2>> collectorFunction;

		private AbstractDelegatingMaplikeCopier(ImmutableCopier<K, K2> keySubcopier,
				ImmutableCopier<V, V2> valueSubcopier,
				BiFunction<Function<? super Entry<? extends K, ? extends V>, K2>, Function<? super Entry<? extends K, ? extends V>, V2>, Collector<? super Entry<? extends K, ? extends V>, ?, ? extends C2>> collectorFunction) {

			this.keySubcopier = keySubcopier;
			this.valueSubcopier = valueSubcopier;
			this.collectorFunction = collectorFunction;
		}

		@Override
		public C2 copy(C map) {
			return entries(map)
					.collect(collectorFunction.apply(entry -> keySubcopier.copy(entry.getKey()),
							entry -> valueSubcopier.copy(entry.getValue())));
		}

		abstract Stream<? extends Entry<? extends K, ? extends V>> entries(C map);

	}

	private static class DelegatingMapCopier<K, V, K2, V2, C extends Map<? extends K, ? extends V>>
			extends AbstractDelegatingMaplikeCopier<K, V, K2, V2, C, ImmutableMap<K2, V2>> {

		private DelegatingMapCopier(ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {
			super(keySubcopier, valueSubcopier, ImmutableMap::toImmutableMap);
		}

		@Override
		Stream<? extends Entry<? extends K, ? extends V>> entries(C map) {
			return map.entrySet().stream();
		}

	}

	private static abstract class AbstractDelegatingMultimapCopier<K, V, K2, V2, C extends Multimap<? extends K, ? extends V>, C2 extends ImmutableMultimap<K2, V2>>
			extends AbstractDelegatingMaplikeCopier<K, V, K2, V2, C, C2> {

		private AbstractDelegatingMultimapCopier(ImmutableCopier<K, K2> keySubcopier,
				ImmutableCopier<V, V2> valueSubcopier,
				BiFunction<Function<? super Entry<? extends K, ? extends V>, K2>, Function<? super Entry<? extends K, ? extends V>, V2>, Collector<? super Entry<? extends K, ? extends V>, ?, ? extends C2>> collectorFunction) {
			super(keySubcopier, valueSubcopier, collectorFunction);
		}

		@Override
		Stream<? extends Entry<? extends K, ? extends V>> entries(C multimap) {
			return multimap.entries().stream();
		}

	}

	private static class DelegatingMultimapCopier<K, V, K2, V2, C extends Multimap<? extends K, ? extends V>>
			extends AbstractDelegatingMultimapCopier<K, V, K2, V2, C, ImmutableMultimap<K2, V2>> {

		private DelegatingMultimapCopier(ImmutableCopier<K, K2> keySubcopier, ImmutableCopier<V, V2> valueSubcopier) {
			super(keySubcopier, valueSubcopier, ImmutableListMultimap::toImmutableListMultimap);
		}

	}

	private static class DelegatingListMultimapCopier<K, V, K2, V2, C extends ListMultimap<? extends K, ? extends V>>
			extends AbstractDelegatingMultimapCopier<K, V, K2, V2, C, ImmutableListMultimap<K2, V2>> {

		private DelegatingListMultimapCopier(ImmutableCopier<K, K2> keySubcopier,
				ImmutableCopier<V, V2> valueSubcopier) {
			super(keySubcopier, valueSubcopier, ImmutableListMultimap::toImmutableListMultimap);
		}

	}

	private static class DelegatingSetMultimapCopier<K, V, K2, V2, C extends SetMultimap<? extends K, ? extends V>>
			extends AbstractDelegatingMultimapCopier<K, V, K2, V2, C, ImmutableSetMultimap<K2, V2>> {

		private DelegatingSetMultimapCopier(ImmutableCopier<K, K2> keySubcopier,
				ImmutableCopier<V, V2> valueSubcopier) {
			super(keySubcopier, valueSubcopier, ImmutableSetMultimap::toImmutableSetMultimap);
		}

	}

	private static final ImmutableCopier<? extends List<?>, ? extends ImmutableList<?>> simpleListCopier =
			new ImmutableCopier<List<?>, ImmutableList<?>>() {

				@Override
				public ImmutableList<?> copy(List<?> value) {
					return ImmutableList.copyOf(value);
				}
			};

	private static final ImmutableCopier<? extends Set<?>, ? extends ImmutableSet<?>> simpleSetCopier =
			new ImmutableCopier<Set<Object>, ImmutableSet<Object>>() {

				@Override
				public ImmutableSet<Object> copy(Set<Object> value) {
					return ImmutableSet.copyOf(value);
				}
			};

	private static final ImmutableCopier<? extends Multiset<?>, ? extends ImmutableMultiset<?>> simpleMultisetCopier =
			new ImmutableCopier<Multiset<Object>, ImmutableMultiset<Object>>() {

				@Override
				public ImmutableMultiset<Object> copy(Multiset<Object> value) {
					return ImmutableMultiset.copyOf(value);
				}
			};

	private static final ImmutableCopier<? extends Map<?, ?>, ? extends ImmutableMap<?, ?>> simpleMapCopier =
			new ImmutableCopier<Map<Object, Object>, ImmutableMap<Object, Object>>() {

				@Override
				public ImmutableMap<Object, Object> copy(Map<Object, Object> map) {
					return ImmutableMap.copyOf(map);
				}

			};

	private static final ImmutableCopier<? extends Multimap<?, ?>, ? extends ImmutableMultimap<?, ?>> simpleMultimapCopier =
			new ImmutableCopier<Multimap<Object, Object>, ImmutableMultimap<Object, Object>>() {

				@Override
				public ImmutableMultimap<Object, Object> copy(Multimap<Object, Object> multimap) {
					return ImmutableMultimap.copyOf(multimap);
				}

			};

	private static final ImmutableCopier<? extends ListMultimap<?, ?>, ? extends ImmutableListMultimap<?, ?>> simpleListMultimapCopier =
			new ImmutableCopier<ListMultimap<Object, Object>, ImmutableListMultimap<Object, Object>>() {

				@Override
				public ImmutableListMultimap<Object, Object> copy(ListMultimap<Object, Object> multimap) {
					return ImmutableListMultimap.copyOf(multimap);
				}

			};

	private static final ImmutableCopier<? extends SetMultimap<?, ?>, ? extends ImmutableSetMultimap<?, ?>> simpleSetMultimapCopier =
			new ImmutableCopier<SetMultimap<Object, Object>, ImmutableSetMultimap<Object, Object>>() {

				@Override
				public ImmutableSetMultimap<Object, Object> copy(SetMultimap<Object, Object> multimap) {
					return ImmutableSetMultimap.copyOf(multimap);
				}

			};
}
