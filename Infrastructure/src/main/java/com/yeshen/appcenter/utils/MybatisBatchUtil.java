package com.yeshen.appcenter.utils;

import com.yeshen.appcenter.domain.common.BusinessException;
import com.yeshen.appcenter.domain.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.BiFunction;

/**
 * author by HuBingKuan
 * Date 2022/05/05  11:29
 * 批处理工具  支持多数据:DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.DEV_TW);
 * 思考为什么Batch比Simple模式快?
 * 此处需要注意的是SQL的执行过程(SQL->连接器->语法分析器->语义分析与优化器->执行引擎)
 * Simple模式下它为每个语句的执行创建一个【新】的预处理语句，单条提交SQL,每次都要重新经过语法分析器生成执行计划
 * Batch模式【重复使用】预处理的语句,预先提交带占位符的SQL到数据库进行预处理，提前生成执行计划，当给定占位符参数，真正执行SQL的时候，执行引擎可以直接执行
 * ,在JDBC客户端缓存多条SQL语句,然后在flush或缓存满的时候，将多条SQL语句打包发送到数据库执行
 * 但Batch模式在Insert事务没有提交之前，是没办法获取到自增ID
 * <p>
 * 另外prePareStatement防止了sql注入攻击,prePareStatement在程序第一次查询数据库以前sql语句就被数据库进行了分析、编译、优化以及具体的查询计划也都造成了
 * 当程序真正发起查询请求时，这时传递过来的参数会被认为是某个字段的值，而不会从新编译、优化，因此它不会被认为是一个sql指令
 * 若是传递进来的参数没法被看做是sql指令那么就没法造成sql注入了。如 or '1=1' 这样的参数不会被当作or指令，而是某个字段的字符串类型的值
 */
@Slf4j
@Component
public class MybatisBatchUtil {
    private static final int BATCH_SIZE = 1000;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 批量处理插入或删除
     * 使用案例:batchUtils.batchUpdateOrInsert(postMetas, PostMetaDAO.class, (item, postMetaDAO) -> postMetaDAO.insert(item))
     *
     * @param data        要更新的数据
     * @param mapperClass mapper对象的class
     * @param function    自定义处理逻辑
     * @param <T>         数据集合中的实例
     * @param <U>         Mapper的Class的实例
     * @param <R>         影响的总行数
     * @return
     */
    public <T, U, R> int batchUpdateOrInsert(List<T> data, Class<U> mapperClass, BiFunction<T, U, R> function) {
        int i = 1;
        SqlSession batchSqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            U mapper = batchSqlSession.getMapper(mapperClass);
            long startTime = System.currentTimeMillis();
            int size = data.size();
            for (T element : data) {
                function.apply(element, mapper);
                if ((i % BATCH_SIZE == 0) || i == size) {
                    // 清除(执行)缓存在JDBC驱动类中的批量更新语句
                    batchSqlSession.flushStatements();
                }
                i++;
            }
            // 非事务环境下强制commit,事务情况下该commit相当于无效
            batchSqlSession.commit(!TransactionSynchronizationManager.isSynchronizationActive());
            log.info("批量操作耗时:{}", System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            log.warn("批处理操作回滚,错误信息:{}", e.getMessage(), e);
            batchSqlSession.rollback();
            throw new BusinessException(ResultCode.SERVER_ERROR, e);
        } finally {
            batchSqlSession.close();
        }
        return i - 1;
    }
}