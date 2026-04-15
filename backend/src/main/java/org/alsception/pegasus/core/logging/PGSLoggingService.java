package org.alsception.pegasus.core.logging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor // Automatski ubacuje Repository preko konstruktora (Dependency Injection)
public class PGSLoggingService {

    private final PGSLogRepository logRepository;

    @Transactional
    public void logAction(String username, String action, String type) 
    {
        // Koristimo Builder za kreiranje objekta
        PGSLog log = PGSLog.builder()
                .username(username)
                .action(action)
                .type(type)
                .build();
        
        logRepository.save(log);

        //TODO: Ovo bi moglo da ide u try-catch da neblokira ostali kod
    }
    
    @Transactional
    public void logAction(String username, String action, String ip, String userAgent) 
    {
        // Koristimo Builder za kreiranje objekta
        PGSLog log = PGSLog.builder()
                .username(username)
                .action(action)
                .ip(ip)
                .userAgent(userAgent)
                .build();

        logRepository.save(log);
    }

    public List<PGSLog> getAllLogs() {
        return logRepository.findAll();
    }

    public List<PGSLog> getLogsByUser(String username) {
        return logRepository.findByUsername(username);
    }
}