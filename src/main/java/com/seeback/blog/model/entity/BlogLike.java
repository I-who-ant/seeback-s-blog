package com.seeback.blog.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章点赞记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("blog_like")
public class BlogLike implements Serializable {

    @Serial
    private static final long serialVersionUID = 2221181689250157811L;

    @Id(keyType = KeyType.Generator, value = KeyGenerators.snowFlakeId)
    private Long id;

    @Column("blogId")
    private Long blogId;

    @Column("userId")
    private Long userId;

    @Column("createTime")
    private LocalDateTime createTime;
}
