package com.boilersserver.BoilersControlServer.services;
import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.entities.BoilerLog;
import com.boilersserver.BoilersControlServer.repository.BoilerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class BoilerLoggingService {

    private final BoilerLogRepository logRepository;

    @Autowired
    public BoilerLoggingService(BoilerLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public void logBoilerStatus(Boiler boiler, String errorDesc) {
        // Попытка найти существующий лог для данного котла
        BoilerLog log = logRepository.findByBoiler(boiler);
        if (log == null) {
            log = new BoilerLog();
            log.setBoiler(boiler);
            log.setErrorDescs(new ArrayList<>()); // Создание нового списка ошибок
            log.setTimestamp(System.currentTimeMillis());
        }

        // Добавление новой ошибки в список
        log.getErrorDescs().add(errorDesc);

        // Обновление временной метки
        log.setTimestamp(System.currentTimeMillis());

        // Сохранение обновленного лога в репозитории
        logRepository.save(log);
    }
}
