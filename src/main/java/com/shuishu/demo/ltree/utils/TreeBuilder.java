package com.shuishu.demo.ltree.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuishu.demo.ltree.entity.Tree;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 13:14
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Component
public class TreeBuilder {

    private TreeNode root;

    public TreeBuilder() {
        root = new TreeNode(null, "", "", null, "", "");
    }


    public TreeBuilder buildTree(List<Tree> treeList){
        TreeBuilder currentTreeBuilder = new TreeBuilder();
        treeList.forEach(t -> currentTreeBuilder.add(t.getTreeId(), t.getTreeName(), t.getTreeDesc(), t.getTreeSort(), t.getTreeCode(), t.getTreePath().getValue()));
        return currentTreeBuilder;
    }

    private void add(Long treeId, String treeName, String treeDesc, Integer treeSort, String treeCode, String treePath) {
        TreeNode current = root;
        StringTokenizer stringTokenizer = new StringTokenizer(treePath, "\\.");
        String treePathPart = "";
        while (stringTokenizer.hasMoreElements()) {
            treePathPart = (String) stringTokenizer.nextElement();
            TreeNode child = current.getChild(treePathPart);
            if (child == null) {
                current.addChild(new TreeNode(treeId, treeName, treeDesc, treeSort, treeCode, treePath));
                child = current.getChild(treePathPart);
            }
            if (child != null){
                current = child;
            }
        }
    }

    public List<TreeNode> toListForSingleCondition(String queryTreePath) {
        if (StringUtils.hasText(queryTreePath)){
            // 路径条件多个时，buildTree()操作，所有节点数据都会在最外层出现一次，直接获取下标0 的数据
            if (!ObjectUtils.isEmpty(this.root.getChildren())){
                return new ArrayList<>((Collections.singletonList(this.root.getChildren().get(0))));
            }
        }
        // 无条件时，buildTree()不会出现重复数据
        return this.root.getChildren();
    }

    public List<TreeNode> toListForMultipleCondition(List<String> queryTreePathList) {
        if (ObjectUtils.isEmpty(queryTreePathList)){
            // 无条件时，buildTree()不会出现重复数据
            return this.root.getChildren();
        }else {
            // 防止外部传递进来的List是用的 Arrays.asList()，导致 remove()异常
            List<String> recordPathList = new ArrayList<>(queryTreePathList);
            List<TreeNode> treeNodeList = new ArrayList<>();
            this.root.getChildren().forEach(t -> {
                if (recordPathList.contains(t.getTreePath())){
                    treeNodeList.add(t);
                    recordPathList.remove(t.getTreePath());
                }
            });
            return treeNodeList;
        }
    }

    private String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
