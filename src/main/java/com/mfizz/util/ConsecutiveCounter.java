package com.mfizz.util;

/*
 * #%L
 * mfz-util
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

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An AtomicLong that stores a timestamp marking when its value changed from/to
 * zero. Useful for finding out the timestamp of when a "consecutive" counter
 * is reset back to zero.
 * 
 * @author joe@mfizz.com
 */
public class ConsecutiveCounter {
    
    private AtomicLong timestamp;
    private AtomicLong counter;
    private CopyOnWriteArrayList<ConsecutiveCounter> siblings;
    
    public ConsecutiveCounter() {
        this.timestamp = new AtomicLong(System.currentTimeMillis());
        this.counter = new AtomicLong(0);
    }

    void addSibling(ConsecutiveCounter sibling) {
        if (this.siblings == null) {
            this.siblings = new CopyOnWriteArrayList<ConsecutiveCounter>();
        }
        this.siblings.add(sibling);
    }
    
    public void reset() {
        long v = this.counter.getAndSet(0);
        // only reset the timestamp if the value was non-zero
        if (v != 0) {
            this.timestamp.set(System.currentTimeMillis());
        }
    }
    
    public boolean isZero() {
        return this.counter.get() == 0;
    }
    
    public boolean isNonZero() {
        return this.counter.get() != 0;
    }
    
    public long getTimestamp() {
        return this.timestamp.get();
    }
    
    public long get() {
        return this.counter.get();
    }
    
    public long incrementAndGet() {
        long v = this.counter.incrementAndGet();
        if (v == 1) {
            // previous value was zero -- start of "consecutive" count
            this.timestamp.set(System.currentTimeMillis());
            // marks an important event (any sibling counters need reset)
            if (this.siblings != null) {
                for (ConsecutiveCounter sibling : siblings) {
                    sibling.reset();
                }
            }
        }
        return v;
    }
    
}
