package org.alsception.pegasus.core.utils;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniqueIdGenerator 
{
    private static final AtomicLong counter = new AtomicLong();    
    private static final Logger log = LoggerFactory.getLogger(UniqueIdGenerator.class);
    static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    
    /**
    Uses a thread-safe counter (AtomicLong) that starts from 0.
    Every call increments the counter by 1 and returns the new value.
    Very safe and reliable within a single JVM session.

    ⚠️ Limitations:
    When the app restarts, the counter resets to 0 unless persisted.
    Not suitable across multiple machines or clustered deployments.
    */
    public static long generateAtomicId() 
    {
        long uniqueID = counter.incrementAndGet(); 
        log.trace("Generated unique atomic ID: " + uniqueID);
        return uniqueID;
    }
    
    /* 
    High-resolution time-based unique ID
    returns the number of nanoseconds since the JVM started.
    Not guaranteed to be unique if called multiple times in rapid succession (especially on fast CPUs or in multithreaded environments).
    */
    public static long generateNanoId() 
    {
        long uniqueID = System.nanoTime(); 
        log.trace("Generated unique nano ID: " + uniqueID);
        return uniqueID;
    }
    
    public static String generateCompactNanoId() 
    {
        return toAlpha(System.nanoTime());
    }

    public static String toAlpha(long value) 
    {        
        StringBuilder sb = new StringBuilder();
        while (value > 0) 
        {
            sb.append(BASE36.charAt((int)(value % 36)));
            value /= 36;
        }        
        String id = sb.reverse().toString();
        log.trace("Generated alfa: "+id);
        return id;
    
    }
}
