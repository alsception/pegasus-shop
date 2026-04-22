package org.alsception.pegasus.core.utils;

import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.UUID;

@Component
public class CodeGenerator 
{
    private static final AtomicLong counter = new AtomicLong();    
    private static final Logger log = LoggerFactory.getLogger(CodeGenerator.class);
    static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final AtomicLong threeDigitCounter = new AtomicLong(0);
    
    private static EntityManager entityManager;

    // hack: Autowire na NE-STATIČKI setter u STATIČKO polje
    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        CodeGenerator.entityManager = em;
    }
    
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
        log.trace("Generated nano ID: " + uniqueID);
        return uniqueID;
    }
    
    public static String generateCompactNanoId() 
    {
        return toAlpha(System.nanoTime());
    }

    /**
     * Returns a 3-digit string, zero-padded, incrementing from "000" to "999".
     * Rolls over to "000" after "999".
     * Prefix meaning: R - restoran, T - take away, D - delivery
     */
    public static String generateOrderCode(String sufix) 
    {
        // Izvlači sledeći broj iz baze
        Query query = entityManager.createNativeQuery("SELECT nextval('order_code_seq')");
        Number nextVal = (Number) query.getSingleResult();
        
        long value = nextVal.longValue();
        String code = String.format("%03d", value)+sufix;
    
        log.trace("Generated 3-digit code: " + code);
        return code;
    }

    // Metod za reset, treba ga testirati i pozvati prilikom reset DailySessiona.TODO
    public static void resetSequence() {
        entityManager.createNativeQuery("ALTER SEQUENCE order_code_seq RESTART WITH 1").executeUpdate();
    }

    /**
     * Base36 will return Uppercase
     * @param value
     * @return
     */
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

    public static String generateUUID() 
    {
        return UUID.randomUUID().toString();
    }
}