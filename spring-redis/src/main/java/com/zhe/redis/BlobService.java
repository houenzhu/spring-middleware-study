package com.zhe.redis;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhe.redis.domain.BlobWatch;
import com.zhe.redis.logic.BlobLogic;
import com.zhe.redis.mapper.BlobWatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@DS("master")
public class BlobService extends ServiceImpl<BlobWatchMapper, BlobWatch> {

    private final RedisTemplate<String, Object> redisTemplate;

    public void watch(String bid) {
        String key = "blob:watch:" + bid;
        Integer watch = (Integer) redisTemplate.opsForValue().get(key);
        if (Objects.isNull(watch)) {
            redisTemplate.opsForValue().set(key, 1);
            return;
        }
        redisTemplate.opsForValue().increment(key);
        Integer views = (Integer) redisTemplate.opsForValue().get(key);
        if (views % 10 == 0) {
            LambdaUpdateWrapper<BlobWatch> updateWrapper = Wrappers.lambdaUpdate(BlobWatch.class);
            LambdaQueryWrapper<BlobWatch> queryWrapper = Wrappers.lambdaQuery(BlobWatch.class);
            BlobWatch blobWatch = getOne(queryWrapper.eq(BlobWatch::getBlobId, bid));
            if (BlobLogic.existsBlob(blobWatch)) {
                updateWrapper.set(BlobWatch::getWatch, views).eq(BlobWatch::getBlobId, bid);
                update(updateWrapper);
            } else {
                BlobWatch blobWatch1 = new BlobWatch();
                blobWatch1.setBlobId(bid);
                blobWatch1.setWatch(views);
                save(blobWatch1);
            }
        }
    }

    /**
     * 从库读取数据
     */
    @DS("slave_1")
    public Integer count(String blobId) {
        LambdaQueryWrapper<BlobWatch> queryWrapper = Wrappers.lambdaQuery(BlobWatch.class);
        return super.getOne(queryWrapper.eq(BlobWatch::getBlobId, blobId)).getWatch();
    }
}
