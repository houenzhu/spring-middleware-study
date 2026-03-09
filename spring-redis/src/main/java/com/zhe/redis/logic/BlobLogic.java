package com.zhe.redis.logic;

import com.zhe.redis.domain.BlobWatch;

import java.util.Objects;

public class BlobLogic {

    public static boolean existsBlob(BlobWatch blobWatch) {
        return Objects.nonNull(blobWatch);
    }
}
