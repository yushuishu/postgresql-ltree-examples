package com.shuishu.demo.ltree.service.impl;


import cn.hutool.core.util.StrUtil;
import com.shuishu.demo.ltree.dsl.TreeDsl;
import com.shuishu.demo.ltree.entity.LTree;
import com.shuishu.demo.ltree.entity.Tree;
import com.shuishu.demo.ltree.repository.TreeRepository;
import com.shuishu.demo.ltree.service.TreeService;
import com.shuishu.demo.ltree.utils.TreeBuilder;
import com.shuishu.demo.ltree.utils.TreeNode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 12:38
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class TreeServiceImpl implements TreeService {
    @Resource
    private TreeRepository treeRepository;
    @Resource
    private TreeDsl treeDsl;
    @Resource
    private TreeBuilder treeBuilder;


    @Override
    public void addTree(String parentTreeCode, String newTreeCode) {
        if (!StringUtils.hasText(parentTreeCode) || !StringUtils.hasText(newTreeCode)){
            throw new RuntimeException("父节点和新增节点都不能为空");
        }
        if (newTreeCode.contains(StrUtil.DOT)){
            throw new RuntimeException("新增节点不能包含英文点（.）");
        }
        // 验证是否已添加过
        Tree tempTree = treeRepository.findTreeByTreeCode(newTreeCode);
        if (tempTree != null){
            throw new RuntimeException("节点已添加");
        }
        // 查询最后一级是parentTreeName 的全路径：*.parentTreeName
        String fullPath = treeRepository.findFullPathByLastPath(parentTreeCode);
        if (!StringUtils.hasText(fullPath)){
            throw new RuntimeException("父节点不存在");
        }
        String newTreePath = fullPath + "." + newTreeCode;
        // 查询父节点的子节点有多少了(排序号)
        long count = treeRepository.findCurrentChildrenNodeCountByTreeCode(parentTreeCode);

        Tree tree = new Tree();
        tree.setTreeName(newTreeCode + "名称");
        tree.setTreeDesc(newTreeCode + "描述");
        tree.setTreeSort((int) (count + 1L));
        tree.setTreeCode(newTreeCode);
        tree.setTreePath(new LTree(newTreePath));
        treeRepository.save(tree);
    }

    @Override
    public void deleteTree(String treePath, Boolean currentOrAll) {
        // currentOrAll删除当前节点，是否包含子孙节点 true：所有， false：当前
        if (Boolean.TRUE.equals(currentOrAll)){
            // 删除当前节点 和 所有子孙节点
            treeRepository.deleteTree(treePath);
        }else {
            // 只删除当前节点，子孙节点所属父级向上移动到 删除节点的父级
            List<Tree> treeList = treeRepository.findAllByTreePath(treePath, null);
            List<TreeNode> treeNodeList = treeBuilder.buildTree(treeList).toListForSingleCondition(treePath);
            if (ObjectUtils.isEmpty(treeNodeList)){
                throw new RuntimeException("删除的节点不存在");
            }
            List<TreeNode> childrenList = treeNodeList.get(0).getChildren();
            if (!ObjectUtils.isEmpty(childrenList)){
                // 获取删除节点的父节点
                String parentTreePath = treePath.substring(0, treePath.lastIndexOf(StrUtil.DOT));

            }
            treeRepository.deleteCurrentTree(treePath);
        }
    }

    @Override
    public List<TreeNode> findAllBySingleCondition(String treePath) {
        List<Tree> treeList = treeRepository.findAllByTreePath(treePath, null);
        return treeBuilder.buildTree(treeList).toListForSingleCondition(treePath);
    }

    @Override
    public List<TreeNode> findAllByMultipleCondition(String treePath, String treePath2) {
        List<Tree> treeList = treeRepository.findAllByTreePath(treePath, treePath2);
        return treeBuilder.buildTree(treeList).toListForMultipleCondition(Arrays.asList(treePath, treePath2));
    }

    @Override
    public void moveTreeOneLevelUp(String pathToMove) {
        treeRepository.moveTreeOneLevelUp(pathToMove);
    }

    @Override
    public void moveRootTreeDown(String destinationPath, String sourcePath) {
        treeRepository.moveRootTreeDown(destinationPath, sourcePath);
    }

    @Override
    public void moveNonRootTreeDown(String destinationPath, String sourcePath) {
        treeRepository.moveNonRootTreeDown(destinationPath, sourcePath);
    }

    @Override
    public void copyTree(String destinationPath, String sourcePath) {
        treeRepository.copyTree(destinationPath, sourcePath);
    }
}
