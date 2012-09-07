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

import java.util.concurrent.TimeUnit;

/**
 *
 * @author joe@mfizz.com
 */
public class PreciseTimeUnitConverter {
    
    static public double convertMillis(long millis, TimeUnit tu) {
        switch (tu) {
            case NANOSECONDS:
                return millis*1000000d;
            case MICROSECONDS:
                return millis*1000d;
            case MILLISECONDS:
                return millis;
            case SECONDS:
                return ((double)millis)/1000d;
            case MINUTES:
                return ((double)millis)/1000d/60d;
            case HOURS:
                return ((double)millis)/1000d/60d/60d;
            case DAYS:
                return ((double)millis)/1000d/60d/60d/24d;
            default:
                throw new IllegalArgumentException("Unsupported timeunit " + tu.name());
            
        }
    }
    
}
