package com.mfizz.util;

/*
 * #%L
 * mfizz-util
 * %%
 * Copyright (C) 2012 mfizz
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

/**
 * Most recent items on front of linked-list and oldest at back.
 * 
 * @author joe@mfizz.com
 */
public class TimeSeries<V extends Timestamped> {
    
    static public class Entry<T> implements Timestamped {

        final private long timestamp;
        final private T value;

        public Entry(long timestamp, T value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        @Override
        public long getTimestamp() {
            return timestamp;
        }

        public T getValue() {
            return value;
        }
    }
    
    final private long retentionMillis;
    private ArrayDeque<V> series;           // youngest to oldest from front to back
    
    public TimeSeries(long retentionMillis, int initialCapacity) {
        this.retentionMillis = retentionMillis;
        this.series = new ArrayDeque<V>(initialCapacity);
    }

    public long getRetentionMillis() {
        return retentionMillis;
    }
    
    public int prune() {
        return pruneLessThan(retentionTimestamp(-1));
    }
    
    public int pruneLessThan(long timestamp) {
        // simple case where series is empty
        if (this.series.isEmpty()) {
            return 0;
        }
        
        // iterate thru series in reverse
        int size = series.size();
        int removed = 0;
        Iterator<V> li = series.descendingIterator();
        prune:
        while (li.hasNext()) {
            V next = li.next();
            if (next.getTimestamp() < timestamp) {
                li.remove();
                removed++;
            } else {
                break prune;
            }
        }
        
        return removed;
    }
    
    public Collection<V> getSeries() {
        return this.series;
    }
    
    /**
    public List<V> createSeriesList() {
        return Lists.newArrayList(this.series);
    }
    */
    
    public boolean add(V value) {
        return add(value, -1);
    }
    
    public boolean add(V value, long currentTimeMillis) {
        long retentionTimestamp = retentionTimestamp(currentTimeMillis);
        
        // regardless if any value is added, always prune first
        pruneLessThan(retentionTimestamp);
        
        // value too old to retain?
        if (value.getTimestamp() < retentionTimestamp) {
            return false;
        }
        
        // find where in the linked list the value should be inserted
        if (series.isEmpty()) {
            series.add(value);
        } else {
            if (value.getTimestamp() >= series.peek().getTimestamp()) {
                // optimized for pushing new values onto front
                series.addFirst(value);
            } else if (value.getTimestamp() < series.peekLast().getTimestamp()) {
                // second optimization for adding values onto back
                series.addLast(value);
            } else {
                // value needs inserted into middle of deque -- only way to do
                // this is unfortunately with a new Deque -- this is super innefficient
                // but is only implemented for edge cases rather than intended use
                ArrayDeque<V> newSeries = new ArrayDeque<V>(this.series.size() + 1);
                
                // iterate thru the list to find where to insert
                Iterator<V> li = series.iterator();
                boolean inserted = false;
                while (li.hasNext()) {
                    V next = li.next();
                    if (!inserted && value.getTimestamp() >= next.getTimestamp()) {
                        // add the new value first -- then the existing value
                        newSeries.add(value);
                        inserted = true;
                    }
                    // always add the existing value
                    newSeries.add(next);
                }
                
                this.series = newSeries;
            }
        }
        
        return true;
    }
    
    private long retentionTimestamp(long currentTimeMillis) {
        if (currentTimeMillis < 0) {
            return currentTimeMillis() - retentionMillis;
        } else {
            return currentTimeMillis - retentionMillis;
        }
    }
    
    static public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
}
