package com.zhe.redis.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@TableName("blob_watch")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BlobWatch implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String blobId;
    private Integer watch;
}
