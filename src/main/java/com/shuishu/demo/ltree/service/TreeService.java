package com.shuishu.demo.ltree.service;


import com.shuishu.demo.ltree.entity.Tree;
import com.shuishu.demo.ltree.utils.TreeNode;

import java.util.List;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 12:38
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
public interface TreeService {
    /**
     * 添加节点
     *
     * @param parentTreeCode 父节点
     * @param newTreeCode 新增的节点
     */
    void addTree(String parentTreeCode, String newTreeCode);

    /**
     * 删除节点
     *
     * @param treePath 删除节点路径
     * @param currentOrAll true：所有， false：当前
     */
    void deleteTree(String treePath, Boolean currentOrAll);

    /**
     * 单个路径条件查询
     *
     * @param treePath 条件路径
     * @return 树结构
     */
    List<TreeNode> findAllBySingleCondition(String treePath);

    /**
     * 多个路径条件查询
     *
     * @param treePath  条件路径1
     * @param treePath2 条件路径2
     * @return -树结构
     */
    List<TreeNode> findAllByMultipleCondition(String treePath, String treePath2);

    /**
     * 将树向上移动一级
     *
     * @param pathToMove 移动的节点路径
     */
    void moveTreeOneLevelUp(String pathToMove);

    /**
     * 移动根节点向下
     *
     * @param destinationPath 目标节点
     * @param sourcePath 根节点
     */
    void moveRootTreeDown(String destinationPath, String sourcePath);

    /**
     * 移动非根节点向下
     *
     * @param destinationPath 目标节点
     * @param sourcePath 根节点
     */
    void moveNonRootTreeDown(String destinationPath, String sourcePath);

    /**
     * 复制节点到指定节点
     *
     * @param destinationPath 节点
     * @param sourcePath 节点
     */
    void copyTree(String destinationPath, String sourcePath);

}