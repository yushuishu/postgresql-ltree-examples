package com.shuishu.demo.ltree.utils;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.shuishu.demo.ltree.entity.LTree;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.*;
import java.util.stream.Collectors;

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


    //Optional<TreeNode> firstNodeHavingValue(String valueToFind){
    //    if (this.treeCode.equals(valueToFind)){
    //        return Optional.of(this);
    //    }
    //    return this.children.stream()
    //            .filter(treeNode -> treeNode.treeCode.equals(valueToFind))
    //            .findFirst();
    //}

    public List<TreeNode> renderAsJson() {
        List<TreeNode> nodeList = new ArrayList<>();
        TreeNode treeNode = new TreeNode();
        treeNode.setTreeId(this.treeId);
        treeNode.setTreeName(this.treeName);
        treeNode.setTreeDesc(this.treeDesc);
        treeNode.setTreeSort(this.treeSort);
        treeNode.setTreeCode(this.treeCode);
        treeNode.setTreePath(this.treePath);
        nodeList.add(treeNode);
        return nodeList;
    }

    //private String renderChildrenAsJson() {
    //    return this.children
    //            .stream()
    //            .map(TreeNode::renderAsJson)
    //            .collect(Collectors.joining(","));
    //}
}
