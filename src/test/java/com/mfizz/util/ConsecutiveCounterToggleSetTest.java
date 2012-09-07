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

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author joe@mfizz.com
 */
public class ConsecutiveCounterToggleSetTest {
    
    @Test
    public void set() throws Exception {
        ConsecutiveCounterToggledSet toggledSet = new ConsecutiveCounterToggledSet();
        ConsecutiveCounter ok = toggledSet.createToggledCounter();
        ConsecutiveCounter temp = toggledSet.createToggledCounter();
        ConsecutiveCounter failed = toggledSet.createToggledCounter();
        
        Assert.assertEquals(0L, ok.get());
        Assert.assertEquals(0L, temp.get());
        Assert.assertEquals(0L, failed.get());
        Assert.assertTrue(ok.isZero());
        Assert.assertTrue(temp.isZero());
        Assert.assertTrue(failed.isZero());
        
        long okts = ok.getTimestamp();
        long tempts = temp.getTimestamp();
        long failedts = failed.getTimestamp();
        
        // incrementing ok should attempt toggle all the other counters, but
        // they are already zero so it shouldn't have an impact
        Thread.sleep(50);   // to let timestamps catch up with each other
        ok.incrementAndGet();
        
        // temp and failed should have been reset
        Assert.assertEquals(1L, ok.get());
        Assert.assertEquals(0L, temp.get());
        Assert.assertEquals(0L, failed.get());
        Assert.assertTrue(ok.getTimestamp() > okts);
        Assert.assertTrue(temp.getTimestamp() == tempts);
        Assert.assertTrue(failed.getTimestamp() == failedts);
        
        okts = ok.getTimestamp();
        tempts = temp.getTimestamp();
        failedts = failed.getTimestamp();
        
        // increment again -- but nothing should have changed w/ timestamps
        Thread.sleep(10);           // since we rely on timestamps
        ok.incrementAndGet();
        
        Assert.assertEquals(2L, ok.get());
        Assert.assertEquals(0L, temp.get());
        Assert.assertEquals(0L, failed.get());
        Assert.assertTrue(ok.getTimestamp() == okts);
        Assert.assertTrue(temp.getTimestamp() == tempts);
        Assert.assertTrue(failed.getTimestamp() == failedts);
        
        okts = ok.getTimestamp();
        tempts = temp.getTimestamp();
        failedts = failed.getTimestamp();
        
        // increment failed counter (should toggle ok counter)
        Thread.sleep(10);           // since we rely on timestamps
        failed.incrementAndGet();
        
        Assert.assertEquals(0L, ok.get());
        Assert.assertEquals(0L, temp.get());
        Assert.assertEquals(1L, failed.get());
        Assert.assertTrue(ok.getTimestamp() > okts);
        Assert.assertTrue(temp.getTimestamp() == tempts);
        Assert.assertTrue(failed.getTimestamp() > failedts);
        
        okts = ok.getTimestamp();
        tempts = temp.getTimestamp();
        failedts = failed.getTimestamp();
        
        // increment temp counter (should toggle failed back to zero)
        Thread.sleep(10);           // since we rely on timestamps
        temp.incrementAndGet();
        
        Assert.assertEquals(0L, ok.get());
        Assert.assertEquals(1L, temp.get());
        Assert.assertEquals(0L, failed.get());
        Assert.assertTrue(ok.getTimestamp() == okts);
        Assert.assertTrue(temp.getTimestamp() > tempts);
        Assert.assertTrue(failed.getTimestamp() > failedts);
    }
    
}
