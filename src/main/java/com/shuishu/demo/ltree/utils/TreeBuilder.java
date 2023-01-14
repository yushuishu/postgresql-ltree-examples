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
 * @Description ：构建树形结构工具类
 */
@Component
public class TreeBuilder {

    private final TreeNode root;

    public TreeBuilder() {
        root = new TreeNode(null, "", "", null, "", "");
    }


    /**
     * 构建树入口
     * 遍历数据，将每一个节点数据添加到 TreeBuilder
     *
     * @param treeList -使用SQL查询出来的数据
     * @return -扁平化数据封装成java对象树形结构（返回前端还需进一步处理）
     */
    public TreeBuilder buildTree(List<Tree> treeList){
        TreeBuilder currentTreeBuilder = new TreeBuilder();
        treeList.forEach(t -> currentTreeBuilder.add(t.getTreeId(), t.getTreeName(), t.getTreeDesc(), t.getTreeSort(), t.getTreeCode(), t.getTreePath().getValue()));
        return currentTreeBuilder;
    }

    /**
     * 对每一个节点的数据添加到 TreeBuilder
     *
     * @param treeId 节点id
     * @param treeName 节点名称
     * @param treeDesc 节点描述
     * @param treeSort 节点所在层级结构排序
     * @param treeCode 节点code
     * @param treePath 节点 tree路径
     */
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

    /**
     * 将 TreeBuilder 构建的树形结构数据（根节点有重复的所有扁平化节点数据），根据查询的 单个路径条件（一个条件），去重根节点的重复数据
     *
     * @param queryTreePath 前端传递过来的 查询一个路径treePath 条件
     * @return 返回最终正确的树形结构数据集合
     */
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

    /**
     * 将 TreeBuilder 构建的树形结构数据（根节点有重复的所有扁平化节点数据），根据查询的 多个路径条件（多个条件），去重根节点的重复数据
     *
     * @param queryTreePathList 前端传递过来的 查询多个路径treePath 条件
     * @return 返回最终正确的树形结构数据集合
     */
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

    /**
     * 树形结构转 json 字符串
     * 调用 toListForSingleCondition() 或  toListForMultipleCondition() 之后（完整的树形结构数据），
     * 再调用此方法，将集合转为 json字符串
     *
     * @return json字符串
     */
    private String toJson() {
        try {
            return new ObjectMapper().writeValueAsString(root);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
