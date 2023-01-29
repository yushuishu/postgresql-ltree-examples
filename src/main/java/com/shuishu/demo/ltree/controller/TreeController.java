package com.shuishu.demo.ltree.controller;


import com.shuishu.demo.ltree.service.TreeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
     * 新节点 X <a href="http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=&newTreeCode=X">...</a>
     * 父节点 X 新节点 Y <a href="http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=X&newTreeCode=Y">...</a>
     * 父节点 Y 新节点 Z <a href="http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=Y&newTreeCode=Z">...</a>
     * 父节点 X 新节点 T <a href="http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=X&newTreeCode=T">...</a>
     * 父节点 Z 新节点 S <a href="http://localhost:8080/api/shuishu/demo/tree/add?parentTreeCode=Z&newTreeCode=S">...</a>
     *
     * @param parentTreeCode 父节点 code
     * @param newTreeCode 新节点 code
     * @return -
     */
    @GetMapping("add")
    public ResponseEntity<?> addTree(String parentTreeCode, String newTreeCode){
        treeService.addTree(parentTreeCode, newTreeCode);
        return ResponseEntity.ok("添加节点成功");
    }

    /**
     * deleteTree 删除接口
     * addTree 添加接口，增加了5条节点数据：X、Y、Z、T、S，测试删除节点。（第一次测试完毕，重新添加数据）
     * 第一次测试，treeCode = Y  includedChild = true    <a href="http://localhost:8080/api/shuishu/demo/tree/delete?treeCode=Y&includedChild=true">...</a>
     * 第二次测试，treeCode = X  includedChild = false   <a href="http://localhost:8080/api/shuishu/demo/tree/delete?treeCode=X&includedChild=false">...</a>
     *
     * @param treeCode 删除节点code
     * @param includedChild true：包含， false：不包含（所有子孙节点向上一级移动）
     * @return -
     */
    @GetMapping("delete")
    public ResponseEntity<?> deleteTree(String treeCode, Boolean includedChild){
        treeService.deleteTree(treeCode, includedChild);
        return ResponseEntity.ok("删除节点成功");
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
