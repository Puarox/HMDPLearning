package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    private static final int  COUNT_BIT = 32 ;
    private static final long BEGIN_TIMESTAMP = 1640995200 ;
    private StringRedisTemplate stringRedisTemplate;
    public RedisIdWorker(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public long nextId(String keyPrefix)
    {

        //generate a time stamp
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp = nowSecond - BEGIN_TIMESTAMP;

        // generate a token

        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MMM:dd"));

        long count = stringRedisTemplate.opsForValue().increment("icr:"+ keyPrefix  + ":"+date);

        // strcat
        return timeStamp  << COUNT_BIT |count ;
    }
    public static void main(String[] args)
    {
        LocalDateTime time  = LocalDateTime.of(2022, 1,1,0,0,0);
        long second = time.toEpochSecond(ZoneOffset.UTC);
        System.out.println(second);

    }

}
