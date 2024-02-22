package com.boilersserver.BoilersControlServer.repository;

import com.boilersserver.BoilersControlServer.entities.Boiler;
import com.boilersserver.BoilersControlServer.entities.BoilerLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoilerLogRepository extends MongoRepository<BoilerLog, String> {
    BoilerLog findByBoiler(Boiler boiler);
}
