package com.shuishu.demo.ltree.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：谁书-ss
 * @date ：2023-01-05 22:34
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：定义
 * lquery 表示一个用于匹配 ltree值的类正则表达式的模式
 * 一个简单词匹配一个路径中的那个标签。 一个星号（*）匹配零个或更多个标签。它们可以用点连接起来，以形成一个必须匹配整个标签路径的模式。
 * 例如：
 *     foo         正好匹配标签路径foo
 *     *.foo.*     匹配任何包含标签foo的标签路径
 *     *.foo       匹配任何最后一个标签是foo的标签路径
 *     A.B.C.*     匹配所有 A.B.C 开头的所有标签路径
 *
 * <p></p>
 * 以下修饰符可以放在一个非星号的 lquery项的末尾，使它能匹配除了精确匹配之外更多的匹配:
 *     A.B@.C      @ 不区分大小写匹配，A.B.C 或 A.b.C
 *     A.B*.C      * 匹配带B前缀的任何标签，A.B.C 或 A.BD.C 或 A.BDE.C  或  A.BF.C  。。。
 *     A.B_C%.H    % 匹配开头以下划线分隔的词，A.B_C_.H
 *
 */
@RestController
@RequestMapping("lquery")
public class LQueryController {

}
