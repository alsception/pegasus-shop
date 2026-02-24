package org.alsception.pegasus.features.order;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PGSDailySessionService 
{

    @Autowired
    private PGSDailySessionRepository sessionRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(PGSDailySessionService.class);

    // Provera da li možemo da radimo
    public PGSDailySession getActiveSession() 
    {
        return sessionRepository.findByStatus(PGSSessionStatus.OPEN)
                .orElseGet(() -> {
                    // Logika za automatsko otvaranje
                    // Sledeci korak je da korisnik rucno potvrdi i unese pocetni kesh
                    logger.info("Otvoren nov dan.");
                    return openDay(BigDecimal.ZERO); 
                });
    }

    // Otvaranje dana
    @Transactional
    public PGSDailySession openDay(BigDecimal startingCash) 
    {
        // Prvo proveri da nije već otvoreno (zaštita od dupliranja)
        if (sessionRepository.existsByStatus(PGSSessionStatus.OPEN)) {
            throw new RuntimeException("Već postoji otvorena sesija!");
        }

        PGSDailySession session = new PGSDailySession();
        session.setOpenedAt(LocalDateTime.now());
        session.setStartingCash(startingCash);
        session.setStatus(PGSSessionStatus.OPEN);
        
        return sessionRepository.save(session);
    }
}