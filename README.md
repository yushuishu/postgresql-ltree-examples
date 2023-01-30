# postgresql-ltree-examples


<br>

# 参考
官网文档 LTree（查询语法）：https://www.postgresql.org/docs/current/ltree.html

官网中文翻译文档（查询语法）：http://postgres.cn/docs/13/ltree.html

国外写作平台，作者发布了5篇文章（建议仔细研究和看评论）：https://patshaughnessy.net/2017/12/11/trying-to-represent-a-tree-structure-using-postgres

大佬的写作平台 和 GitHub：

- （构建树）：https://github.com/luistrigueiros/pg-ltree-micronaut-java-example

- （文章） ： https://luistrigueiros.medium.com/handling-tree-data-models-with-postgresql-and-java-3c440105dead

GitHub参考（jpa查询语句）：https://github.com/biniama/postgres-ltree-spring-boot-jpa

Blog参考（注解）：https://www.wimdeblauwe.com/blog/2021/03/01/attributeconverter-vs-embeddable-in-jpa

Blog参考（语法）：https://www.cnblogs.com/nickchou/p/9391904.html

GitHub参考（构建树）：https://github.com/zhoujiaping/path-test

Blog参考（嵌套集模型）：https://mikehillyer.com/articles/managing-hierarchical-data-in-mysql

Stackoverflow（SQL查询json）：https://stackoverflow.com/questions/26995326/postgresql-materialized-path-ltree-to-hierarchical-json-object


<br>

# 介绍

Tree 数据设计方案比较

<table><thead><tr><th>设计</th><th>表数量</th><th>查询子</th><th>查询树</th><th>插入</th><th>删除</th><th>引用完整性</th></tr></thead><tbody><tr><td>邻接表</td><td>1</td><td>简单</td><td>困难</td><td>简单</td><td>简单</td><td>是</td></tr><tr><td>递归查询</td><td>1</td><td>简单</td><td>简单</td><td>简单</td><td>简单</td><td>是</td></tr><tr><td>枚举路径</td><td>1</td><td>简单</td><td>简单</td><td>简单</td><td>简单</td><td>否</td></tr><tr><td>闭包表</td><td>2</td><td>简单</td><td>简单</td><td>简单</td><td>简单</td><td>是</td></tr></tbody></table>

这里是介绍关于枚举路径的方案实现。枚举路径也叫物化路径

使用的数据库是 `postgresql` 因为 `postgresql` 的扩展插件 `ltree` 就是专门解决这种树形节点的。

`ltree` 是俄罗斯Teodor Sigaev和Oleg Bartunov共同开发的PostgreSQL contrib扩展模块，它包含数据类型的实现、为树状结构组织的数据提供索引和查询。

`ltree` 使用到了两种索引为 `GIST` 和 `btree`

关于GIST索引的一些介绍：https://developer.aliyun.com/article/68244


ltree 路径定义：

- 标签是由一组字符数据（A-Za-z0-9_）组成的，每个标签最大256字节
- 标签的路径是由0个或多个点号分割(只能是英文点 . 号)，如 a.b.c.d
- 标签的总长度必须小于65kb，但最好维持在2kb以下

ltree提供两种数据类型：
- ltree：存储标签路径
- lquery：表示用于匹配ltree值的类似正则表达式的模式。 一个简单的单词与路径中的标签匹配。 星号（*）匹配零个或多个标签。 一个简单词匹配一个路径中的那个标签。 一个星号（\*）匹配零个或更多个标签。它们可以用点连接起来，以形成一个必须匹配整个标签路径的模式。例如：
  - foo 匹配确切的标签路径foo
  - \*.foo.* 匹配包含标签foo的任何标签路径
  - \*.foo 匹配最后一个标签为foo的任何标签路径

标识可以量化，类似正则表达式：

- \* {n}完全匹配n个标签
- \* {n，}匹配至少n个标签
- \* {n，m}匹配至少n个但不超过m个标签
- \* {，m}匹配最多m个标签 - 与* {0，m}相同

有几个修饰符可以放在lquery中非星形标签的末尾，以使其匹配的不仅仅是完全匹配：

- A.B@.C      @ 不区分大小写匹配，A.B.C 或 A.b.C
- A.B*.C      * 匹配带B前缀的任何标签，A.B.C 或 A.BD.C 或 A.BDE.C  或  A.BF.C  。。。 
- A.B_C%.H    % 匹配开头以下划线分隔的词，A.B_C_.H 。注意并不是匹配全部（B_C）

% 的行为有点复杂。它尝试匹配词而不是整个标签。例如，B_C% 匹配 B_C_C 但是不匹配 B_CC。

如果和 `*` 组合，前缀匹配可以单独应用于每一个词，例如 foo_bar%* 匹配 foo1_bar2_baz 但不匹配foo1_br2_baz。


<br>

# 版本

jdk：17

spring-boot：3.0.1

spring-boot-starter-data-jpa：3.0.1 （hibernate：6.1.6.Final）

postgresql：42.5.1

hibernate-validator：8.0.0

querydsl：5.0.0

jakarta.validation-api：3.0.2


<br>

# 工程

com.shuishu.demo.ltree.entity.LTree ：PO实体的数据类型，属性value 存放节点全路径也就是 Tree

com.shuishu.demo.ltree.entity.Tree ：数据库PO实体（节点实体）

<br>

映射：实体LTree 作为 Tree 的数据类型，映射到数据库

```java
// LTree：属性和数据库中数据类型的转换 columnDefinition定义为 ltree类型 （jakarta.persistence.Embeddable）
@Embeddable
@Column(name = "tree_path", nullable = false, columnDefinition = "ltree")

// 属性字段名和数据库字段名称的映射 （jakarta.persistence.Embedded）
@Embedded
@AttributeOverrides(@AttributeOverride(name = "value", column = @Column(name = "tree_path", nullable = false, columnDefinition = "ltree")))
```


<br>

# 数据库

- 创建数据库
- 启用扩展插件 Ltree
- 创建表，ss_tree，注意其中的字段 `tree_path` 类型是 `ltree`
- 建立两种索引 `GIST` 和 `btree`

```sql
-- 数据库启用扩展 LTree
CREATE EXTENSION ltree;
    
-- 检查是否启用成功
select * from pg_extension where extname = 'ltree';

-- 创建表 （工程搭建好之后，可以使用 hibernate 的自动更新特性，自动建表）
CREATE TABLE "public"."ss_tree" (
    "tree_id" int8 NOT NULL,
    "tree_code" varchar(255) COLLATE "pg_catalog"."default",
    "tree_desc" varchar(255) COLLATE "pg_catalog"."default",
    "tree_name" varchar(255) COLLATE "pg_catalog"."default",
    "tree_path" "public"."ltree" NOT NULL,
    "tree_sort" int4
)
;
COMMENT ON COLUMN "public"."ss_tree"."tree_id" IS 'tree id';
COMMENT ON COLUMN "public"."ss_tree"."tree_code" IS '树形结构，当前节点路径path ';
COMMENT ON COLUMN "public"."ss_tree"."tree_desc" IS '树形结构描述';
COMMENT ON COLUMN "public"."ss_tree"."tree_name" IS '树形结构名称';
COMMENT ON COLUMN "public"."ss_tree"."tree_path" IS '树形结构路径';
COMMENT ON COLUMN "public"."ss_tree"."tree_sort" IS '树形结构，同一层级排序';
COMMENT ON TABLE "public"."ss_tree" IS '用户表';

-- 索引
CREATE INDEX path_gist_tree_idx ON ss_tree USING GIST(tree_path);
CREATE INDEX path_tree_idx ON ss_tree USING btree(tree_path);

-- 插入数据
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (1, 'A名称', 'A描述', 1, 'A', 'A');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (2, 'B名称', 'B描述', 1, 'B', 'A.B');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (3, 'C名称', 'C描述', 1, 'C', 'A.B.C');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (4, 'D名称', 'D描述', 2, 'D', 'A.B.D');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (5, 'E名称', 'E描述', 1, 'E', 'A.B.D.E');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (6, 'F名称', 'F描述', 3, 'F', 'A.B.F');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (7, 'G名称', 'G描述', 1, 'G', 'A.B.F.G');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (8, 'H名称', 'H描述', 4, 'H', 'A.B.H');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (9, 'I名称', 'I描述', 2, 'I', 'I');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (10, 'J名称', 'J描述', 1, 'J', 'I.J');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (11, 'K名称', 'K描述', 1, 'K', 'I.J.K');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (12, 'L名称', 'L描述', 2, 'L', 'I.J.L');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (13, 'M名称', 'M描述', 2, 'M', 'I.M');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (14, 'N名称', 'N描述', 1, 'N', 'I.M.N');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (15, 'O名称', 'O描述', 2, 'O', 'I.M.O');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (16, 'P名称', 'P描述', 1, 'P', 'I.M.O.P');
INSERT INTO ss_tree (tree_id, tree_name, tree_desc, tree_sort, tree_code, tree_path) VALUES (17, 'Q名称', 'Q描述', 2, 'Q', 'I.M.O.Q');
```


<br>

# 工具类构建树形结构

## 工具类构建树形结构
工具类构建的树形结构有一个要求是，传递进去的Tree集合数据，在查询的时候，必须是按照`TreePath`进行过升序操作的，否则工具构建出的数据将是错误的数据。
工具类`TreeBuilder`作为`Bean`注入，
实例如下：接口 `/path`

```java
@Resource
private TreeBuilder treeBuilder;

List<Tree> treeList = treeRepository.findAllByTreePath(treePath, null);
return treeBuilder.buildTree(treeList).toListForSingleCondition(treePath);
```

## postgresql 函数直接构建 json

使用数据库`postgresql`函数构建`json`数据，不推荐使用，建议使用代码工具类进行构建

SQL语句的最后 `WHERE lvl = 1;` 代表查询所有节点，也就是从一级开始查询，可以换成`ltree`语法函数

```sql
WITH RECURSIVE c AS (
  SELECT
    tree_name, tree_path, nlevel ( tree_path ) AS lvl
  FROM ss_tree
),
maxlvl AS (
  SELECT max(lvl) maxlvl FROM c
),
j AS (
  SELECT
    c.*,
    json '[]' AS children
  FROM c, maxlvl
  WHERE lvl = maxlvl
  UNION ALL
  SELECT
    (c).*,
    CASE
      WHEN COUNT(j) > 0 -- 检查返回的记录是否为空
        THEN json_agg(j) -- 如果不是null，则聚合
      ELSE json '[]' -- 如果为null，则为叶数组，因此返回空数组
    END AS children
  FROM (
    SELECT
      c,
      CASE
        WHEN c.tree_path = subpath(j.tree_path, 0, nlevel(j.tree_path) - 1) -- c 是一个父节点
          THEN j
        ELSE NULL -- 如果c不是父类，返回NULL以触发基本情况
      END AS j
    FROM j
    JOIN c ON c.lvl = j.lvl - 1
  ) AS v
  GROUP BY v.c
)
SELECT row_to_json(j)::text AS json_tree
FROM j
WHERE lvl = 1;
```

测试
```sql
-- 测试
WITH RECURSIVE c AS (
  SELECT
    tree_name, tree_path, nlevel ( tree_path ) AS lvl
  FROM ss_tree
),
maxlvl AS (
  SELECT max(lvl) maxlvl FROM c
),
j AS (
  SELECT
    c.*,
    json '[]' AS children
  FROM c, maxlvl
  WHERE lvl = maxlvl
  UNION ALL
  SELECT
    (c).*,
    CASE
      WHEN COUNT(j) > 0 -- 检查返回的记录是否为空
        THEN json_agg(j) -- 如果不是null，则聚合
      ELSE json '[]' -- 如果为null，则为叶数组，因此返回空数组
    END AS children
  FROM (
    SELECT
      c,
      CASE
        WHEN c.tree_path = subpath(j.tree_path, 0, nlevel(j.tree_path) - 1) -- c 是一个父节点
          THEN j
        ELSE NULL -- 如果c不是父类，返回NULL以触发基本情况
      END AS j
    FROM j
    JOIN c ON c.lvl = j.lvl - 1
  ) AS v
  GROUP BY v.c
)
SELECT row_to_json(j)::text AS json_tree
FROM j;
-- WHERE tree_path = CAST('I.M' AS ltree);
```


<br>

# 节点移动

如果只是对单个节点的移动，Ltree内置语法（nlevel）非常方便的对接进行操作。

实例如下：nlevel() 语法可以获取当前节点的层级，所以将移动的节点TreePath作为条件传入，移动一级就减一。更新语句的执行，`postgresql` 底层会自动的进行相关操作。

```sql
UPDATE ss_tree SET tree_path = subpath(tree_path, nlevel(:pathToMove) - 1) WHERE tree_path <@ CAST(:pathToMove AS ltree)
```


<br>
<hr>

<p><span style="float:right;">2023-01-14</span></p>


