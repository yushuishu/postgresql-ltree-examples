package com.shuishu.demo.ltree.repository;


import com.shuishu.demo.ltree.config.jdbc.BaseRepository;
import com.shuishu.demo.ltree.entity.Tree;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 23:00
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
public interface TreeRepository extends BaseRepository<Tree, Long> {

    /**
     * 查询单个节点
     *
     * @param treeName 节点名称
     * @return 当前节点信息
     */
    Tree findTreeByTreeName(String treeName);

    /**
     * 查询单个节点
     *
     * @param treeCode 节点code
     * @return 当前节点信息
     */
    Tree findTreeByTreeCode(String treeCode);

    /**
     * 根据最后一个TreePath 查询完整 TreePath
     *
     * @param treeCode 最后一个TreePath
     * @return 完整全路径
     */
    @Query(value = "SELECT tree_path FROM ss_tree WHERE tree_path ~ CAST('*.?1' AS ltree);", nativeQuery = true)
    String findFullPathByLastPath(String treeCode);

    /**
     * 查询当前节点下子节点的数量，第一级子节点，不包含孙子节点
     *
     * @param parentTreeCode 当前节点
     * @return 子节点数量
     */
    @Query(value = "SELECT tree_path FROM ss_tree WHERE tree_path ~ CAST('*.?1.*{1}' AS ltree);", nativeQuery = true)
    long findCurrentChildrenNodeCountByTreeCode(String parentTreeCode);

    /**
     * 删除当前节点 和 所有子孙节点
     *
     * @param treePath 要删除的节点路径
     */
    @Modifying
    @Query(value = "DELETE FROM ss_tree WHERE tree_path <@ CAST(:treePath AS ltree)", nativeQuery = true)
    void deleteTree(@Param("treePath") String treePath);


    /**
     * 删除当前节点，不删除子孙节点
     *
     * @param treePath 要删除的节点路径
     */
    @Modifying
    @Query(value = "DELETE FROM ss_tree WHERE tree_path = CAST(:treePath AS ltree)", nativeQuery = true)
    void deleteCurrentTree(@Param("treePath") String treePath);

    /**
     * 查询 treePath 下的所有节点
     * ltree <@ ltree → boolean
     * 左边的 ltree 是 右边ltree 的子路径(包括右边ltree，相等)
     *
     * @param treePath 搜索路径
     * @return 所有
     */
    @Query(value = "SELECT * FROM ss_tree WHERE tree_path <@ CAST(:treePath AS ltree) OR tree_path <@ CAST(:treePath2 AS ltree) ORDER BY tree_path", nativeQuery = true)
    List<Tree> findAllByTreePath(@Param("treePath") String treePath, @Param("treePath2") String treePath2);

    /**
     * 将树向上移动一级
     *
     * @param pathToMove 移动的节点路径
     */
    @Modifying
    @Query(value = "UPDATE ss_tree SET tree_path = subpath(tree_path, nlevel(:pathToMove) - 1) WHERE tree_path <@ CAST(:pathToMove AS ltree)", nativeQuery = true)
    void moveTreeOneLevelUp(String pathToMove);

    /**
     * 根节点 向下 移动（子节点下面）
     *
     * @param destinationPath 子节点路径
     * @param sourcePath 根节点
     */
    @Modifying
    @Query(value = "UPDATE ss_tree SET tree_path = CAST(:destinationPath AS ltree) || tree_path WHERE tree_path <@ CAST(:sourcePath AS ltree)", nativeQuery = true)
    void moveRootTreeDown(@Param("destinationPath") String destinationPath, @Param("sourcePath") String sourcePath);

    /**
     * 移动非根树下来
     *
     * @param destinationPath 目标节点
     * @param sourcePath 非根节点
     */
    @Modifying
    @Query(value = "UPDATE ss_tree SET tree_path = CAST(:destinationPath AS ltree) || subpath(tree_path, 1) WHERE tree_path <@ CAST(:sourcePath AS ltree)", nativeQuery = true)
    void moveNonRootTreeDown(@Param("destinationPath") String destinationPath, @Param("sourcePath") String sourcePath);

    /**
     * 复制节点 到 目标节点
     *
     * @param destinationPath 节点
     * @param sourcePath 节点
     */
    @Modifying
    @Query(value = "INSERT INTO ss_tree (tree_name, tree_path) (SELECT tree_name, CAST(:destinationPath AS ltree) || subpath(tree_path, 1) FROM ss_tree WHERE CAST(:sourcePath AS ltree) @> path)", nativeQuery = true)
    void copyTree(@Param("destinationPath") String destinationPath, @Param("sourcePath") String sourcePath);

}
