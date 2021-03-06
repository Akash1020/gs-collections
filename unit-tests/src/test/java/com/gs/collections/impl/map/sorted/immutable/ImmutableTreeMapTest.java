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

package com.gs.collections.impl.map.sorted.immutable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.sorted.ImmutableSortedMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.factory.SortedMaps;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import org.junit.Assert;
import org.junit.Test;

public class ImmutableTreeMapTest extends ImmutableSortedMapTestCase
{
    @Override
    protected ImmutableSortedMap<Integer, String> classUnderTest()
    {
        return SortedMaps.immutable.of(1, "1", 2, "2", 3, "3", 4, "4");
    }

    @Override
    protected ImmutableSortedMap<Integer, String> classUnderTest(Comparator<? super Integer> comparator)
    {
        return SortedMaps.immutable.of(comparator, 1, "1", 2, "2", 3, "3", 4, "4");
    }

    @Override
    protected <K, V> MapIterable<K, V> newMap()
    {
        return new ImmutableTreeMap<K, V>(SortedMaps.mutable.of());
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeyValue(K key1, V value1)
    {
        return SortedMaps.immutable.of(key1, value1);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2, key3, value3);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    protected int size()
    {
        return 4;
    }

    @Override
    public void entrySet()
    {
        super.entrySet();

        Interval interval = Interval.oneTo(100);
        LazyIterable<Pair<String, Integer>> pairs = interval.collect(Object::toString).zip(interval);
        MutableSortedMap<String, Integer> mutableSortedMap = new TreeSortedMap<String, Integer>(pairs.toArray(new Pair[]{}));
        ImmutableSortedMap<String, Integer> immutableSortedMap = mutableSortedMap.toImmutable();
        MutableList<Map.Entry<String, Integer>> entries = FastList.newList(immutableSortedMap.castToSortedMap().entrySet());
        MutableList<Map.Entry<String, Integer>> sortedEntries = entries.toSortedListBy(Map.Entry::getKey);
        Assert.assertEquals(sortedEntries, entries);
    }

    @Override
    @Test
    public void testToString()
    {
        Assert.assertEquals("{1=1, 2=2, 3=3, 4=4}", this.classUnderTest().toString());
        Assert.assertEquals("{4=4, 3=3, 2=2, 1=1}", this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).toString());
        Assert.assertEquals("{}", new ImmutableTreeMap<Integer, Integer>(new TreeSortedMap<Integer, Integer>()).toString());
    }

    @Test(expected = NullPointerException.class)
    public void nullConstructor()
    {
        new ImmutableTreeMap<Integer, Integer>(null);
    }

    @Test
    public void firstKey()
    {
        Assert.assertEquals(Integer.valueOf(1), new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).firstKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void firstKey_throws()
    {
        new ImmutableTreeMap<Integer, Integer>(new TreeSortedMap<Integer, Integer>()).firstKey();
    }

    @Test
    public void lastKey()
    {
        Assert.assertEquals(Integer.valueOf(4), new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).lastKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void lastKey_throws()
    {
        new ImmutableTreeMap<Integer, Integer>(new TreeSortedMap<Integer, Integer>()).lastKey();
    }

    @Test
    public void keySet()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Verify.assertSetsEqual(Sets.mutable.of(1, 2, 3, 4), immutableSortedMap.keySet());
    }

    @Test
    public void keySetContains()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Set<Integer> keySet = immutableSortedMap.keySet();
        Assert.assertTrue(keySet.contains(1));
        Assert.assertTrue(keySet.contains(2));
        Assert.assertTrue(keySet.contains(4));
        Assert.assertFalse(keySet.contains(0));
        Assert.assertFalse(keySet.contains(5));
    }

    @Test
    public void keySetContainsAll()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Set<Integer> keySet = immutableSortedMap.keySet();
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(1, 2, 3, 4)));
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(1, 4)));
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(2, 3)));
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(1, 2, 3)));
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(2, 3, 4)));
        Assert.assertTrue(keySet.containsAll(UnifiedSet.newSetWith(1)));
        Assert.assertTrue(keySet.containsAll(FastList.newListWith(1, 4, 1, 3, 4)));
        Assert.assertFalse(keySet.containsAll(UnifiedSet.newSetWith(1, 2, 3, 4, 5)));
        Assert.assertFalse(keySet.containsAll(UnifiedSet.newSetWith(0, 1, 2, 3, 4, 5)));
        Assert.assertFalse(keySet.containsAll(FastList.newListWith(0, 1, 4, 1, 3, 4, 0)));
    }

    @Test
    public void keySetIsEmpty()
    {
        Assert.assertFalse(new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().isEmpty());
        Assert.assertTrue(new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of()).keySet().isEmpty());
    }

    @Test
    public void keySetToString()
    {
        Assert.assertEquals("[1, 2, 3, 4]", new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().toString());
        Assert.assertEquals("[1, 2, 3, 4]", new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(4, "4", 3, "3", 2, "2", 1, "1")).keySet().toString());
        Assert.assertEquals("[4, 3, 2, 1]", new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(Comparators.reverseNaturalOrder(), 4, "4", 3, "3", 2, "2", 1, "1")).keySet().toString());
    }

    @Test
    public void keySetEqualsAndHashCode()
    {
        ImmutableTreeMap<Integer, String> map = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3"));
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith(1, 2, 3), map.keySet());
    }

    @Test
    public void keySetToArray()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        MutableList<Integer> expected = FastList.newListWith(1, 2, 3, 4).toSortedList();
        Set<Integer> keySet = immutableSortedMap.keySet();
        Object[] array = keySet.toArray();
        Assert.assertEquals(expected, FastList.newListWith(keySet.toArray()).toSortedList());
        Assert.assertNotSame(array, keySet.toArray());
        array[3] = 5;
        Assert.assertEquals(expected, FastList.newListWith(keySet.toArray()).toSortedList());
        Assert.assertEquals(FastList.newListWith(1, 2, 3, 5).toSortedList(), FastList.newListWith(array).toSortedList());

        Assert.assertEquals(expected, FastList.newListWith(keySet.toArray(new Integer[keySet.size()])).toSortedList());
    }

    @Test
    public void keySet_toArray_withSmallTarget()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Integer[] destination = new Integer[2]; // deliberately to small to force the method to allocate one of the correct size
        Integer[] result = immutableSortedMap.keySet().toArray(destination);
        Arrays.sort(result);
        Assert.assertArrayEquals(new Integer[]{1, 2, 3, 4}, result);
    }

    @Test
    public void keySet_ToArray_withLargeTarget()
    {
        ImmutableTreeMap<Integer, String> immutableSortedMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Integer[] target = new Integer[6]; // deliberately large to force the extra to be set to null
        target[4] = 42;
        target[5] = 42;
        Integer[] result = immutableSortedMap.keySet().toArray(target);
        ArrayIterate.sort(result, result.length, Comparators.safeNullsHigh(Integer::compareTo));
        Assert.assertArrayEquals(new Integer[]{1, 2, 3, 4, 42, null}, result);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addToKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().add(5);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void addAllToKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().addAll(FastList.newListWith(5, 6, 7));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeFromKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().remove(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeAllFromKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().removeAll(FastList.newListWith(1, 2, 3));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void retainAllFromKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().retainAll(FastList.newListWith(1, 2, 3, 4));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void clearFromKeySet()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).keySet().clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void subMap()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).subMap(0, 1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void headMap()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).headMap(1);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void tailMap()
    {
        new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4")).tailMap(0);
    }

    @Test
    public void ofSortedMap()
    {
        ImmutableTreeMap<Integer, String> immutableMap = new ImmutableTreeMap<Integer, String>(SortedMaps.mutable.of(1, "1", 2, "2", 3, "3", 4, "4"));
        Assert.assertSame(immutableMap, SortedMaps.immutable.ofSortedMap(immutableMap));
    }
}
