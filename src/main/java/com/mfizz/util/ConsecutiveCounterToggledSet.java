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

import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author joe@mfizz.com
 */
public class ConsecutiveCounterToggledSet {
    
    private CopyOnWriteArrayList<ConsecutiveCounter> counters;
    
    public ConsecutiveCounterToggledSet() {
        this.counters = new CopyOnWriteArrayList<ConsecutiveCounter>();
    }
    
    public ConsecutiveCounter createToggledCounter() {
        ConsecutiveCounter counter = new ConsecutiveCounter();
        // add any existing counters as a "sibling" to the new counter
        // as well as adding the new counter as a sibling to existing ones
        for (ConsecutiveCounter sibling : counters) {
            counter.addSibling(sibling);
            sibling.addSibling(counter);
        }
        this.counters.add(counter);
        return counter;
    }
    
    public void reset() {
        for (ConsecutiveCounter c : counters) {
            c.reset();
        }
    }
    
}
