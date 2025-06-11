package com.omnia.storage.dto.resp;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.ZonedDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class MinioBucketRespDto {

    private String name;
    private ZonedDateTime creationDate;
}