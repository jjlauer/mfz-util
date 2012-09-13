
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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory that can name and set as a daemon.
 */
public class NamingThreadFactory implements ThreadFactory {
    
    private String name;
    private boolean daemon;
    private AtomicInteger sequencer;
    
    /**
     * Creates a new NamingThreadFactory.  All threads will be non-daemon threads.
     * @param name The name (prefix) of any threads created by this factory.
     */
    public NamingThreadFactory(String name) {
        this(name, false);
    }

    /**
     * Creates a new NamingThreadFactory.
     * @param name The name (prefix) of any threads created by this factory.
     * @param daemon Whether to create a daemon thread.
     */
    public NamingThreadFactory(String name, boolean daemon) {
        this.name = name;
        this.daemon = daemon;
        this.sequencer = new AtomicInteger();
    }
    
    @Override
    public Thread newThread(Runnable r) {
        long id = sequencer.getAndIncrement();
        String threadName = name + "-" + id;
        Thread thread = new Thread(r, threadName);
        thread.setDaemon(daemon);
        return thread;
    }
    
}
