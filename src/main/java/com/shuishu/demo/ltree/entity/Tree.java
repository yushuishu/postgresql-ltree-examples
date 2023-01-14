package com.shuishu.demo.ltree.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 12:45
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ss_tree")
@Comment(value = "用户表")
public class Tree implements Comparable<Tree>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Comment(value = "tree id")
    private Long treeId;

    @Comment("树形结构名称")
    private String treeName;

    /**
     * 业务场景：前端页面，鼠标悬停到节点名称上时，出现一小段描述
     */
    @Comment("树形结构描述")
    private String treeDesc;

    /**
     * 业务场景：根据同一层级排序节点
     */
    @Comment("树形结构，同一层级排序")
    private Integer treeSort;

    /**
     * 业务场景：记录当天节点在路径中的位置， 比如节点 treePath为 A.B.C  treeCode 就是 B
     * 可以使用生成器来生成唯一treeCode ，不建议使用 treeName 和 treeId，因为字符长以及不唯一，
     * 并且treeName 中可能出现英文点 . postgresql中的物化路径类型 Ltree就是用点 . 来做处理的
     */
    @Column(unique = true)
    @Comment("树形结构，当前节点路径path ")
    private String treeCode;

    /**
     * 业务场景：节点全路径 比如节点 treePath为 A.B.C
     * 根据 treeCode ，然后加上 前部分父路径
     */
    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "value", column = @Column(name = "tree_path", nullable = false, columnDefinition = "ltree")))
    @Comment("树形结构，当前节点全路径path")
    private LTree treePath;


    public Tree(Long treeId, String treeName, String treeDesc, Integer treeSort, String treeCode, LTree treePath) {
        this.treeId = treeId;
        this.treeName = treeName;
        this.treeDesc = treeDesc;
        this.treeSort = treeSort;
        this.treeCode = treeCode;
        this.treePath = treePath;
    }

    @Override
    public int compareTo(Tree other) {
        if (treeSort > other.treeSort) {
            return 1;
        } else if (treeSort < other.treeSort) {
            return -1;
        }
        return 0;
    }

}
