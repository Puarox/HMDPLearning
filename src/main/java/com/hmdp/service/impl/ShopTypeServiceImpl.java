package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import  com.hmdp.utils.RedisConstants;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    public ShopTypeServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Result queryList() {
        String key = "cache:type";
        String jsonList = stringRedisTemplate.opsForValue().get(key);
        List<ShopType> shopTypeList = JSONUtil.toList(jsonList, ShopType.class);

        if(shopTypeList != null && shopTypeList.size() > 0){
            return Result.ok(shopTypeList);
        }

        List<ShopType> shopTypeList1 = list() ;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypeList1));
        return Result.ok(shopTypeList1); 

    }
}
