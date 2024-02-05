package com.boilersserver.BoilersControlServer.entities;

import com.boilersserver.BoilersControlServer.entities.Boiler;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class BoilerLog {
    @Id
    private String id;
    private Boiler boiler;
    private String errorDesc;
    private long timestamp;
}