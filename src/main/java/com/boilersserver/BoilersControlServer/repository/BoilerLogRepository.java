package com.boilersserver.BoilersControlServer.repository;

import com.boilersserver.BoilersControlServer.BoilerLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoilerLogRepository extends MongoRepository<BoilerLog, String> {

}
