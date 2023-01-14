package com.shuishu.demo.ltree.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.Comment;
import org.springframework.util.Assert;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 13:05
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Embeddable
public class LTree {
    @Column(name = "tree_path", nullable = false, columnDefinition = "ltree")
    @Comment("树形结构路径")
    private String value;

    public String[] pathSplit(){
        return value.split("\\.");
    }

    protected LTree() {
    }

    public LTree(String value) {
        Assert.hasText(value, "value should have text");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
