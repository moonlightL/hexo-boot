package com.light.hexo.business.admin.model;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: PostTag
 * @ProjectName hexo-boot
 * @Description: 文章标签
 * @DateTime 2020/9/17 15:36
 */
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"postId", "tagId"})
@ToString
public class PostTag {

    private Integer postId;

    private Integer tagId;

    public PostTag(Integer postId, Integer tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }
}
