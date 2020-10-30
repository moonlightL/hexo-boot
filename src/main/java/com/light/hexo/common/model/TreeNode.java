package com.light.hexo.common.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: TreeNode
 * @ProjectName hexo-boot
 * @Description: 树节点
 * @DateTime 2020/10/30 15:55
 */
@Setter
@Getter
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @JsonProperty("pId")
    private Integer pId;

    private String name;

    @JsonProperty("isParent")
    private Boolean parent;

    private List<TreeNode> children;

    private String path;
}
