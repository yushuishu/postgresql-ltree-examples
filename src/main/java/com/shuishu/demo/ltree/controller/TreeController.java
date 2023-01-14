package com.shuishu.demo.ltree.controller;


import com.shuishu.demo.ltree.service.TreeService;
import com.shuishu.demo.ltree.utils.TreeNode;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=S&newTreeCode=X
     * http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=&newTreeCode=Y
     *
     * @param parentTreeCode
     * @param newTreeCode
     * @return
     */
    @GetMapping("add")
    public ResponseEntity<?> addTree(String parentTreeCode, String newTreeCode){
        treeService.addTree(parentTreeCode, newTreeCode);
        return ResponseEntity.ok("添加节点成功");
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/delete?treePath=&currentOrAll=false
     *
     * @param treePath
     * @param currentOrAll
     * @return
     */
    @GetMapping("delete")
    public ResponseEntity<?> deleteTree(String treePath, Boolean currentOrAll){
        treeService.deleteTree(treePath, currentOrAll);
        return ResponseEntity.ok("删除节点成功");
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/path?treePath=
     *
     * @param treePath
     * @return
     */
    @GetMapping("/path")
    public ResponseEntity<List<TreeNode>> findAllBySingleCondition(String treePath) {
        return ResponseEntity.ok(treeService.findAllBySingleCondition(treePath));
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/more/path?treePath=&treePath2=
     *
     * @param treePath
     * @param treePath2
     * @return
     */
    @GetMapping("more/path")
    public ResponseEntity<List<TreeNode>> findAllByMultipleCondition(String treePath, String treePath2) {
        return ResponseEntity.ok(treeService.findAllByMultipleCondition(treePath, treePath2));
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/moveTreeOneLevelUp?pathToMove=&currentOrAll=false
     *
     * @param pathToMove
     * @param currentOrAll
     * @return
     */
    @GetMapping("/moveTreeOneLevelUp")
    public ResponseEntity<String> moveTreeOneLevelUp(String pathToMove, Boolean currentOrAll) {
        treeService.moveTreeOneLevelUp(pathToMove, currentOrAll);
        return ResponseEntity.ok("将树向上移动一级");
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/moveRootTreeDown?destinationPath=&sourcePath=
     *
     * @param destinationPath
     * @param sourcePath
     * @return
     */
    @GetMapping("/moveRootTreeDown")
    public ResponseEntity<String> moveRootTreeDown(String destinationPath, String sourcePath) {
        treeService.moveRootTreeDown(destinationPath, sourcePath);
        return ResponseEntity.ok("移动根节点向下");
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/moveNonRootTreeDown?destinationPath=&sourcePath=
     *
     * @param destinationPath
     * @param sourcePath
     * @return
     */
    @GetMapping("/moveNonRootTreeDown")
    public ResponseEntity<String> moveNonRootTreeDown(String destinationPath, String sourcePath) {
        treeService.moveNonRootTreeDown(destinationPath, sourcePath);
        return ResponseEntity.ok("移动非根节点向下");
    }

    /**
     * http://localhost:8080/api/shuishu/demo/tree/copyTree?destinationPath=&sourcePath=&currentOrAll=false
     *
     * @param destinationPath
     * @param sourcePath
     * @param currentOrAll
     * @return
     */
    @GetMapping("/copyTree")
    public ResponseEntity<String> copyTree(String destinationPath, String sourcePath, Boolean currentOrAll) {
        treeService.copyTree(destinationPath, sourcePath, currentOrAll);
        return ResponseEntity.ok("复制节点到指定节点");
    }

}
