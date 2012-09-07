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

import com.mfizz.util.TimeSeries.Entry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author joe@mfizz.com
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( { TimeSeries.class })
@PowerMockIgnore({ "javax.management.*", "javax.xml.parsers.*",
        "com.sun.org.apache.xerces.internal.jaxp.*", "ch.qos.logback.*",
        "org.slf4j.*" })
public class TimeSeriesTest {
    //private static final Logger logger = LoggerFactory.getLogger(TimeSeriesTest.class);

    static public List<Entry<String>> createList(TimeSeries<Entry<String>> ts) {
        List<Entry<String>> list = new ArrayList<Entry<String>>();
        Iterator<Entry<String>> iterator = ts.getSeries().iterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    
    @Test
    public void usage() throws Exception {
        // TimeSeries relies heavily on System.currentTimeMillis() to decide
        // which values are retained/pruned.  No easy way to unit test for this
        // without using mocking
        //PowerMock.mockStatic(TimeSeries.class);
        PowerMock.mockStaticPartial(TimeSeries.class, "currentTimeMillis");
        EasyMock.expect(TimeSeries.currentTimeMillis()).andReturn(70000L).anyTimes();
        PowerMock.replay(TimeSeries.class);
        
        TimeSeries<Entry<String>> ts = new TimeSeries<Entry<String>>(60000L, 1);
        
        // current time is mocked to "70000"
        // retention millis is 60K -- mock adding an old item that isn't actually added
        boolean added = ts.add(new Entry(9999L, "item not added"));
        Assert.assertFalse(added);
        
        // add initial value in middle
        //logger.debug("adding item1");
        ts.add(new Entry(50000L, "item 1"));
        Assert.assertEquals(1, ts.getSeries().size());
        Assert.assertEquals(50000L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item 1", createList(ts).get(0).getValue());
        
        // add new value coming before 
        //logger.debug("adding item2");
        ts.add(new Entry(60000L, "item 2"));
        Assert.assertEquals(2, ts.getSeries().size());
        Assert.assertEquals(60000L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item 2", createList(ts).get(0).getValue());
        
        // add a new value that goes at end
        //logger.debug("adding item3");
        ts.add(new Entry(40000L, "item 3"));
        Assert.assertEquals(3, ts.getSeries().size());
        Assert.assertEquals(40000L, createList(ts).get(2).getTimestamp());
        Assert.assertEquals("item 3", createList(ts).get(2).getValue());
        
        // add a new value that goes between end and before
        //logger.debug("adding item4");
        ts.add(new Entry(45000L, "item 4"));     
        Assert.assertEquals(4, ts.getSeries().size());
        Assert.assertEquals(45000L, createList(ts).get(2).getTimestamp());
        Assert.assertEquals("item 4", createList(ts).get(2).getValue());
        
        //logger.debug("adding item5");
        ts.add(new Entry(70000L, "item 5"));
        Assert.assertEquals(5, ts.getSeries().size());
        Assert.assertEquals(70000L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item 5", createList(ts).get(0).getValue());
        
        //logger.debug("adding item6");
        ts.add(new Entry(10000L, "item 6"));    // oldest possible entry
        Assert.assertEquals(6, ts.getSeries().size());
        Assert.assertEquals(10000L, createList(ts).get(5).getTimestamp());
        Assert.assertEquals("item 6", createList(ts).get(5).getValue());
        
        // bump up current time so last item is trimmed off on next add
        PowerMock.reset(TimeSeries.class);
        EasyMock.expect(TimeSeries.currentTimeMillis()).andReturn(70001L).anyTimes();
        PowerMock.replay(TimeSeries.class);
        
        //logger.debug("adding item7");
        ts.add(new Entry(70001L, "item 7"));
        Assert.assertEquals(6, ts.getSeries().size());      // last item at 10000L should have been dropped
        Assert.assertEquals(70001L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item 7", createList(ts).get(0).getValue());
        
        // debug printout series
        for (Entry<String> entry : ts.getSeries()) {
            //logger.debug(entry.getTimestamp() + ": " + entry.getValue());
        }
        
        PowerMock.verify(TimeSeries.class);
    }
    
    @Test
    public void addWithCurrentTimeMillis() throws Exception {
        // timeseries technically retains items for only 0ms
        TimeSeries<Entry<String>> ts = new TimeSeries<Entry<String>>(0L, 5);
        
        // try to add item at "now" of 1
        boolean added = ts.add(new Entry<String>(0L, "item not added"), 1L);
        Assert.assertFalse(added);
        Assert.assertEquals(0, ts.getSeries().size());
        
        // this should work
        added = ts.add(new Entry<String>(0L, "item1"), 0L);
        Assert.assertTrue(added);
        Assert.assertEquals(1, ts.getSeries().size());
        Assert.assertEquals(0L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item1", createList(ts).get(0).getValue());
        
        // this should work and prune off previous
        added = ts.add(new Entry<String>(1L, "item2"), 1L);
        Assert.assertTrue(added);
        Assert.assertEquals(1, ts.getSeries().size());
        Assert.assertEquals(1L, createList(ts).get(0).getTimestamp());
        Assert.assertEquals("item2", createList(ts).get(0).getValue());
    }
}
