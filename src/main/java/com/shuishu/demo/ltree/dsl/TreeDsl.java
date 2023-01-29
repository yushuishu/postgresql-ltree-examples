package com.shuishu.demo.ltree.dsl;


import com.shuishu.demo.ltree.config.jdbc.BaseDsl;
import com.shuishu.demo.ltree.entity.Tree;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @author ：谁书-ss
 * @date ：2023-01-03 23:00
 * @IDE ：IntelliJ IDEA
 * @Motto ：ABC(Always Be Coding)
 * <p></p>
 * @Description ：
 */
@Component
public class TreeDsl extends BaseDsl {
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void updateBatchTreeSortAndPathByTreeId(List<Tree> brotherNodeList) {
        if (!ObjectUtils.isEmpty(brotherNodeList)){
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE ss_tree SET tree_sort=?, tree_path=CAST(? AS ltree) WHERE tree_id=?");
            jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Tree tree = brotherNodeList.get(i);
                    ps.setInt(1, tree.getTreeSort());
                    ps.setString(2, tree.getTreePath().getValue());
                    ps.setLong(3, tree.getTreeId());
                }
                @Override
                public int getBatchSize() {
                    return brotherNodeList.size();
                }
            });
        }
    }
}
