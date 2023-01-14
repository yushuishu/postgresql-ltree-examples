package com.shuishu.demo.ltree.utils;


import lombok.*;

import java.util.*;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 13:15
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TreeNode {
    private Long treeId;

    private String treeName;

    private String treeDesc;

    private Integer treeSort;

    private String treeCode;

    private String treePath;

    private List<TreeNode> children;

    public TreeNode(Long treeId, String treeName, String treeDesc, Integer treeSort, String treeCode, String treePath) {
        this.treeId = treeId;
        this.treeName = treeName;
        this.treeDesc = treeDesc;
        this.treeSort = treeSort;
        this.treeCode = treeCode;
        this.treePath = treePath;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public TreeNode getChild(String data) {
        for (TreeNode n : children){
            if (n.treeCode.equals(data)) {
                return n;
            }
        }
        return null;
    }

}
