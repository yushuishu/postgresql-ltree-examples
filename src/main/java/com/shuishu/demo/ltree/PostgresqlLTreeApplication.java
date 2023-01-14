package com.shuishu.demo.ltree;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


/**
 * @author ：谁书-ss
 * @date   ： 2023-01-03 12:31
 * @IDE    ：IntelliJ IDEA
 * @Motto  ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：Tree 树形结构方案用例
 *
 * <p></p>
 * -------------------------- 参考：
 * 官网文档 LTree（查询语法）：{@link <a href="https://www.postgresql.org/docs/current/ltree.html">...</a>}
 * 官网中文翻译文档（查询语法）：{@link <a href="http://postgres.cn/docs/13/ltree.html">...</a>}
 * 国外写作平台作者发布了5篇文章（建议仔细研究和看评论）：{@link <a href="https://patshaughnessy.net/2017/12/11/trying-to-represent-a-tree-structure-using-postgres">...</a>}
 * 大佬的写作平台 和 GitHub：
 *          （构建树）：{@link <a href="https://github.com/luistrigueiros/pg-ltree-micronaut-java-example">...</a>}
 *          （文章） ： {@link <a href="https://luistrigueiros.medium.com/handling-tree-data-models-with-postgresql-and-java-3c440105dead">...</a>}
 * GitHub参考（jpa查询语句）：{@link <a href="https://github.com/biniama/postgres-ltree-spring-boot-jpa">...</a>}
 * Blog参考（注解）：{@link <a href="https://www.wimdeblauwe.com/blog/2021/03/01/attributeconverter-vs-embeddable-in-jpa/">...</a>}
 * Blog参考（语法）：{@link <a href="https://www.cnblogs.com/nickchou/p/9391904.html">...</a>}
 * GitHub参考（构建树）：{@link <a href="https://github.com/zhoujiaping/path-test">...</a>}
 * Blog参考（）：{@link <a href="https://mikehillyer.com/articles/managing-hierarchical-data-in-mysql/">...</a>}
 * Stackoverflow（SQL查询json）：{@link <a href="https://stackoverflow.com/questions/26995326/postgresql-materialized-path-ltree-to-hierarchical-json-object">...</a>}
 * <p></p>
 * 参考以上网站各位大佬的思路和源码，加以总结和优化，结合实际业务场景，编写此用例。
 */
@EnableJpaRepositories
@SpringBootApplication
public class PostgresqlLTreeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostgresqlLTreeApplication.class, args);
    }

}
