package com.shuishu.demo.ltree.service.impl;


import cn.hutool.core.util.StrUtil;
import com.shuishu.demo.ltree.dsl.TreeDsl;
import com.shuishu.demo.ltree.entity.LTree;
import com.shuishu.demo.ltree.entity.Tree;
import com.shuishu.demo.ltree.repository.TreeRepository;
import com.shuishu.demo.ltree.service.TreeService;
import com.shuishu.demo.ltree.utils.TreeBuilder;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public void addTree(String parentTreeCode, String newTreeCode) {
        if (!StringUtils.hasText(newTreeCode)){
            throw new RuntimeException("新增节点不能为空");
        }
        Tree tempTree = treeRepository.findTreeByTreeCode(newTreeCode);
        if (tempTree != null){
            throw new RuntimeException("节点已添加");
        }
        if (newTreeCode.contains(StrUtil.DOT)){
            throw new RuntimeException("新增节点不能包含英文点（.）");
        }

        Tree tree = new Tree();

        if (StringUtils.hasText(parentTreeCode)){
            // 查询父节点全路径
            String fullPath = treeRepository.findFullPathByCode(parentTreeCode);
            if (!StringUtils.hasText(fullPath)){
                throw new RuntimeException("父节点不存在");
            }
            String newTreePath = fullPath + "." + newTreeCode;
            // 查询父节点的子节点有多少了(排序号)
            long count = treeRepository.findCurrentChildrenNodeCountByTreeCode("*." + parentTreeCode + ".*{1}");
            tree.setTreeName(newTreeCode + "名称");
            tree.setTreeDesc(newTreeCode + "描述");
            tree.setTreeSort((int) (count + 1L));
            tree.setTreeCode(newTreeCode);
            tree.setTreePath(new LTree(newTreePath));
        }else {
            // 添加的节点是根节点
            // 排序号处理：查询所有根节点数量
            long rootNodeCount = treeRepository.findTreeCountByTreePathLevel(1);
            tree.setTreeName(newTreeCode + "名称");
            tree.setTreeDesc(newTreeCode + "描述");
            tree.setTreeSort((int) (rootNodeCount + 1L));
            tree.setTreeCode(newTreeCode);
            tree.setTreePath(new LTree(newTreeCode));
        }
        List<Tree> all = treeRepository.findAll();
        treeRepository.addTree((long) (all.size() + 1), tree.getTreeName(), tree.getTreeDesc(), tree.getTreeSort(), tree.getTreeCode(), tree.getTreePath().getValue());
    }

    @Override
    public void deleteTree(String treeCode, Boolean includedChild) {
        Tree tree = treeRepository.findTreeByTreeCode(treeCode);
        Objects.requireNonNull(tree, "删除的节点不存在");
        String treePath = tree.getTreePath().getValue();
        // 处理当前删除节点的 同级节点的排序号（所有子节点都删除，不需要考虑子节点的排序号）
        List<Tree> currentTierList = treeRepository.findBrotherNodeByTreePath(treePath);
        if (ObjectUtils.isEmpty(currentTierList)){
            Objects.requireNonNull(tree, "删除的节点不存在");
        }

        // currentOrAll删除当前节点，是否包含子孙节点 true：所有， false：当前
        if (Boolean.TRUE.equals(includedChild)){
            List<Tree> brotherNodeList = currentTierList.stream()
                    .filter(t -> !t.getTreeCode().equals(tree.getTreeCode()))
                    .sorted(Comparator.comparing(Tree::getTreeSort))
                    .toList();

            if (!ObjectUtils.isEmpty(brotherNodeList)){
                for (int i = 0; i < brotherNodeList.size(); i++) {
                    entityManager.unwrap(Session.class).evict(brotherNodeList.get(i));
                    brotherNodeList.get(i).setTreeSort(i + 1);
                }
                treeDsl.updateBatchTreeSortAndPathByTreeId(brotherNodeList);
            }
            // 删除当前节点 和 所有子孙节点
            treeRepository.deleteTree(treePath);
        }else {
            // 只删除当前节点，子孙节点所属父级向上移动到 删除节点的父级
            List<Tree> treeList = treeRepository.findIncludedChildByTreePath(treePath);
            if (ObjectUtils.isEmpty(treeList)){
                throw new RuntimeException("删除的节点不存在");
            }
            List<Tree> childrenList = treeList.stream().filter(t -> !treeCode.equals(t.getTreeCode())).toList();
            if (!ObjectUtils.isEmpty(childrenList)){
                String treeCodeAndDot = tree.getTreeCode() + StrUtil.DOT;
                // 删除节点的兄弟节点数量 (filter：将要删除的节点移除，不计算在内)
                AtomicLong count = new AtomicLong(currentTierList.stream()
                        .filter(t -> !t.getTreeCode().equals(tree.getTreeCode()))
                        .count());
                // 提升forEach性能，找到了直接子节点，就直接跳过对排序的处理，间接子节点（孙子节点）排序不需要处理
                childrenList.forEach(t -> {
                    entityManager.unwrap(Session.class).evict(t);
                    String childrenPath = t.getTreePath().getValue();
                    boolean isSonNode = childrenPath.substring(childrenPath.lastIndexOf(treeCodeAndDot) + treeCodeAndDot.length()).contains(StrUtil.DOT);
                    // 是删除节点的直接子节点
                    if (!isSonNode){
                        count.getAndIncrement();
                        t.setTreeSort((int) count.get());
                    }
                    // 路径中的删除节点code移除
                    t.setTreePath(new LTree(t.getTreePath().getValue().replace(treeCode + StrUtil.DOT, "")));
                });

                treeDsl.updateBatchTreeSortAndPathByTreeId(childrenList);
            }
            treeRepository.deleteCurrentTree(treePath);
        }
    }

    @Override
    public void moveTreeOneLevelUp(String pathToMove, Boolean currentOrAll) {
        if (currentOrAll){
            // 包括所有子节点都进行移动操作
            treeRepository.moveTreeOneLevelUp2(pathToMove);
        }else {
            // 只移动当前节点，所有的子孙节点的层级将向上移动一级
            treeRepository.moveTreeOneLevelUp(pathToMove);
        }
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
    public void copyTree(String destinationPath, String sourcePath, Boolean currentOrAll) {
        if (currentOrAll){
            // 拷贝当前节点，包括所有子孙节点
        }else {
            // 只拷贝当前层级节点
            treeRepository.copyTree(destinationPath, sourcePath);
        }
    }
}
