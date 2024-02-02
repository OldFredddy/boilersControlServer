package com.boilersserver.BoilersControlServer;
import com.boilersserver.BoilersControlServer.repository.BoilerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoilerLoggingService {

    private final BoilerLogRepository logRepository;

    @Autowired
    public BoilerLoggingService(BoilerLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logBoilerStatus(Boiler boiler, String errorDesc) {
        BoilerLog log = new BoilerLog();
        log.setBoiler(boiler);
        log.setErrorDesc(errorDesc);
        log.setTimestamp(System.currentTimeMillis());
        logRepository.save(log);
    }
}
