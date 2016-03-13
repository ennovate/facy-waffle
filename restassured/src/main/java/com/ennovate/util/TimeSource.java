package com.ennovate.util;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TimeSource {

    public static final String ZONE_ID_UTC = "UTC";

    public ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }
    public ZonedDateTime nowWithUTC()
    {
        return ZonedDateTime.now(ZoneId.of(ZONE_ID_UTC));
    }
}
