package com.hmdp.utils;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.KSQLJoinWindow;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.*;

@Component
@Slf4j
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;
    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    public void set(String key,Object value,Long time , TimeUnit timeUnit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, timeUnit);
    }

    public void setWithLogicalExpire(String key,Object value,Long time , TimeUnit timeUnit){
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));

        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value));

    }
    public <R,ID> R queryShopWithThrough(String keyPrefix, ID id, Class<R> type, Function<ID,R> dbFallback,
                                                Long time , TimeUnit timeUnit)
    {

        String key = keyPrefix+id ;
        String Json = stringRedisTemplate.opsForValue().get(key);

        if(StrUtil.isNotBlank(Json)){

            return JSONUtil.toBean(Json, type);
        }

        if(Json != null) {
            return null ;
        }
        R r = dbFallback.apply(id);

        if(r == null){
            //将空值写入redis
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES );

            return null;
        }
        this.set(key,r,time,timeUnit);

        return r;
    }

    public <R,ID> R queryShopWithMutex(String keyPrefix,ID id,Class<R> type,Function<ID,R> dbFallback,
                                       Long time , TimeUnit timeUnit,String lockPrefix)
    {
        String key = keyPrefix + id ;
        String Json = stringRedisTemplate.opsForValue().get(key);

        if(StrUtil.isNotBlank(Json)){

            return JSONUtil.toBean(Json, type);

        }
        if(Json != null) {
            return null ;
        }

        R r = null;
        String lockKey = lockPrefix+id ;
        try {

            boolean isLock = tryLock(lockKey);
            if(!isLock){
                Thread.sleep(50);
                return queryShopWithMutex(keyPrefix,id,type,dbFallback,time,timeUnit,lockPrefix);
            }
            r = dbFallback.apply(id);

            if(r == null){
                //将空值写入redis
                this.set(key,r,time,timeUnit);
            }
            this.set(key,r,time,timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            unlock(lockKey);
        }

        return r;
    }

    private boolean tryLock(String key)
    {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key,"1",10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    private void  unlock(String key){
        stringRedisTemplate.delete(key);
    }
}
