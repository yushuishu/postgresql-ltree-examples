package com.shuishu.demo.ltree.controller;


import com.shuishu.demo.ltree.entity.Tree;
import com.shuishu.demo.ltree.service.TreeService;
import com.shuishu.demo.ltree.utils.TreeBuilder;
import com.shuishu.demo.ltree.utils.TreeNode;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 12:38
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@RestController
@RequestMapping("tree")
public class TreeController {
    @Resource
    private TreeService treeService;

    @GetMapping("add")
    public ResponseEntity<?> addTree(String parentTreeCode, String newTreeCode){
        treeService.addTree(parentTreeCode, newTreeCode);
        return ResponseEntity.ok("添加节点成功");
    }

    @GetMapping("delete")
    public ResponseEntity<?> deleteTree(String treePath, Boolean currentOrAll){
        treeService.deleteTree(treePath, currentOrAll);
        return ResponseEntity.ok("删除节点成功");
    }

    @GetMapping("/path")
    public ResponseEntity<List<TreeNode>> findAllBySingleCondition(String treePath) {
        return ResponseEntity.ok(treeService.findAllBySingleCondition(treePath));
    }

    @GetMapping("/path")
    public ResponseEntity<List<TreeNode>> findAllByMultipleCondition(String treePath, String treePath2) {
        return ResponseEntity.ok(treeService.findAllByMultipleCondition(treePath, treePath2));
    }

    @GetMapping("/moveTreeOneLevelUp")
    public ResponseEntity<String> moveTreeOneLevelUp(String pathToMove) {
        treeService.moveTreeOneLevelUp(pathToMove);
        return ResponseEntity.ok("将树向上移动一级");
    }

    @GetMapping("/moveRootTreeDown")
    public ResponseEntity<String> moveRootTreeDown(String destinationPath, String sourcePath) {
        treeService.moveRootTreeDown(destinationPath, sourcePath);
        return ResponseEntity.ok("移动根节点向下");
    }

    @GetMapping("/moveNonRootTreeDown")
    public ResponseEntity<String> moveNonRootTreeDown(String destinationPath, String sourcePath) {
        treeService.moveNonRootTreeDown(destinationPath, sourcePath);
        return ResponseEntity.ok("移动非根节点向下");
    }

    @GetMapping("/copyTree")
    public ResponseEntity<String> copyTree(String destinationPath, String sourcePath) {
        treeService.copyTree(destinationPath, sourcePath);
        return ResponseEntity.ok("复制节点到指定节点");
    }

}
