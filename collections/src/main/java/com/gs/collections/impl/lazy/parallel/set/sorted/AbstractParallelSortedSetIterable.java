/*
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.lazy.parallel.set.sorted;

import com.gs.collections.api.annotation.Beta;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ParallelListIterable;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import com.gs.collections.api.multimap.sortedset.SortedSetMultimap;
import com.gs.collections.api.set.sorted.ParallelSortedSetIterable;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.lazy.parallel.AbstractParallelIterable;
import com.gs.collections.impl.lazy.parallel.list.ParallelCollectListIterable;
import com.gs.collections.impl.lazy.parallel.list.ParallelFlatCollectListIterable;
import com.gs.collections.impl.multimap.set.sorted.SynchronizedPutTreeSortedSetMultimap;

@Beta
public abstract class AbstractParallelSortedSetIterable<T, B extends SortedSetBatch<T>> extends AbstractParallelIterable<T, B> implements ParallelSortedSetIterable<T>
{
    @Override
    protected boolean isOrdered()
    {
        return true;
    }

    public ParallelSortedSetIterable<T> asUnique()
    {
        return this;
    }

    public ParallelSortedSetIterable<T> select(Predicate<? super T> predicate)
    {
        return new ParallelSelectSortedSetIterable<T>(this, predicate);
    }

    public <P> ParallelSortedSetIterable<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.select(Predicates.bind(predicate, parameter));
    }

    public <S> ParallelSortedSetIterable<S> selectInstancesOf(Class<S> clazz)
    {
        return (ParallelSortedSetIterable<S>) this.select(Predicates.instanceOf(clazz));
    }

    public ParallelSortedSetIterable<T> reject(Predicate<? super T> predicate)
    {
        return this.select(Predicates.not(predicate));
    }

    public <P> ParallelSortedSetIterable<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return this.reject(Predicates.bind(predicate, parameter));
    }

    public <V> ParallelListIterable<V> collect(Function<? super T, ? extends V> function)
    {
        return new ParallelCollectListIterable<T, V>(this, function);
    }

    public <P, V> ParallelListIterable<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    public <V> ParallelListIterable<V> collectIf(Predicate<? super T> predicate, Function<? super T, ? extends V> function)
    {
        return this.select(predicate).collect(function);
    }

    public <V> ParallelListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        return new ParallelFlatCollectListIterable<T, V>(this, function);
    }

    public <V> SortedSetMultimap<V, T> groupBy(final Function<? super T, ? extends V> function)
    {
        final MutableSortedSetMultimap<V, T> result = SynchronizedPutTreeSortedSetMultimap.newMultimap();
        this.forEach(new Procedure<T>()
        {
            public void value(T each)
            {
                V key = function.valueOf(each);
                result.put(key, each);
            }
        });
        return result;
    }

    public <V> SortedSetMultimap<V, T> groupByEach(final Function<? super T, ? extends Iterable<V>> function)
    {
        final MutableSortedSetMultimap<V, T> result = SynchronizedPutTreeSortedSetMultimap.newMultimap();
        this.forEach(new Procedure<T>()
        {
            public void value(T each)
            {
                Iterable<V> keys = function.valueOf(each);
                for (V key : keys)
                {
                    result.put(key, each);
                }
            }
        });
        return result;
    }

    @Override
    public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        // TODO: Implement in parallel
        return this.toList().groupByUniqueKey(function);
    }

    @Override
    public Object[] toArray()
    {
        // TODO: Implement in parallel
        return this.toList().toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        // TODO: Implement in parallel
        return this.toList().toArray(array);
    }
}
