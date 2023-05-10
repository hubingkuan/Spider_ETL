package com.yeshen.appcenter.config.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.enums.SupportDatasourceEnum;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Date 2022/1/18/0018
 * author by HuBingKuan
 * 除了使用AOP/注解的方法来配置多数据源的方法 还有一种方法通过mapperLocation来配置多数据源
 */
@Slf4j
@Configuration
public class DynamicDataSourceConfiguration {
    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        log.info("开始初始化数据源");
        Set<SupportDatasourceEnum> supportDatasourceEnums;
        if (SystemConstant.ENV_DEV.equals(activeProfile)) {
            if("WebAppcenterETL".equals(environment.getProperty("spring.application.name"))){
                supportDatasourceEnums=SupportDatasourceEnum.getETLDevSupportDatasource();
            }else{
                supportDatasourceEnums=SupportDatasourceEnum.getServiceDevSupportDatasource();
            }
            for (SupportDatasourceEnum datasourceEnum : supportDatasourceEnums) {
                DataSourceContextHolder.addDatasource(datasourceEnum);
            }
        } else {
            if("WebAppcenterETL".equals(environment.getProperty("spring.application.name"))){
                supportDatasourceEnums=SupportDatasourceEnum.getETLPrdSupportDatasource();
            }else{
                supportDatasourceEnums=SupportDatasourceEnum.getServicePrdSupportDatasource();
            }
            for (SupportDatasourceEnum datasourceEnum : supportDatasourceEnums) {
                DataSourceContextHolder.addDatasource(datasourceEnum);
            }
        }
    }


    @Bean
    @Profile(SystemConstant.ENV_DEV)
    @ConditionalOnMissingBean
    public DataSource dataSourceDev() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        HashMap<Object, Object> dataSourcesMap = new HashMap<>(4);
        List<SupportDatasourceEnum> dataSet = DataSourceContextHolder.getDataSourceList();
        for (SupportDatasourceEnum supportDatasourceEnum : dataSet) {
            DataSource dataSource = this.createDataSourceProperties(supportDatasourceEnum);
            dataSourcesMap.put(supportDatasourceEnum.name(), dataSource);
            log.info("初始化数据库连接池:{}完成", supportDatasourceEnum);
        }
        dynamicDataSource.setTargetDataSources(dataSourcesMap);
        dynamicDataSource.setDefaultTargetDataSource(createDataSourceProperties(SupportDatasourceEnum.DEV_CN));
        return dynamicDataSource;
    }

    @Bean
    @Profile(SystemConstant.ENV_PRD)
    @ConditionalOnMissingBean
    public DataSource dataSourcePrd() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        HashMap<Object, Object> dataSourcesMap = new HashMap<>(16);
        List<SupportDatasourceEnum> dataSet = DataSourceContextHolder.getDataSourceList();
        for (SupportDatasourceEnum supportDatasourceEnum : dataSet) {
            DataSource dataSource = this.createDataSourceProperties(supportDatasourceEnum);
            dataSourcesMap.put(supportDatasourceEnum.name(), dataSource);
            log.info("初始化数据库连接池:{}完成", supportDatasourceEnum);
        }
        dynamicDataSource.setTargetDataSources(dataSourcesMap);
        dynamicDataSource.setDefaultTargetDataSource(createDataSourceProperties(SupportDatasourceEnum.PRD_CN));
        return dynamicDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactoryPrd() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        if (SystemConstant.ENV_DEV.equals(activeProfile)) {
            sqlSessionFactory.setDataSource(dataSourceDev());
        }else{
            sqlSessionFactory.setDataSource(dataSourcePrd());
        }
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml"));
        /*sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:com/nox/yeshen/appcenter/mysql/appcenter/*.xml"));*/
        MybatisConfiguration configuration = new MybatisConfiguration();
        // 允许设置字段为null
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        // 设置字段驼峰
        configuration.setMapUnderscoreToCamelCase(true);
        // 修改 mybatis 使用的 EnumTypeHandler  实体类使用枚举类型  在枚举类型字段上使用注解@EnumValue
        configuration.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);
        // 添加分页功能
        sqlSessionFactory.setPlugins(paginationInterceptor());
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        // 字段自动填充
        globalConfig.setMetaObjectHandler(new MyMetaObjectHandler());
        sqlSessionFactory.setGlobalConfig(globalConfig);
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "mysqlTransactionManager")
    public PlatformTransactionManager transactionManager(){
        if (SystemConstant.ENV_DEV.equals(activeProfile)) {
            return new DataSourceTransactionManager(dataSourceDev());
        }else{
            return new DataSourceTransactionManager(dataSourcePrd());
        }
    }

    @Bean(name = "mysqlTransactionTemplate")
    public TransactionTemplate transactionTemplate(@Qualifier("mysqlTransactionManager") PlatformTransactionManager transactionManager){
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        // 设置事务隔离级别, 默认使用ISOLATION_READ_UNCOMMITTED
        //transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // 设置事务传播方式  默认PROPAGATION_REQUIRED
		// transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 设置事务超时时间  默认-1
		// transactionTemplate.setTimeout(TransactionDefinition.TIMEOUT_DEFAULT);
        return transactionTemplate;
    }



    private synchronized HikariDataSource createDataSourceProperties(SupportDatasourceEnum supportDatasourceEnum) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(supportDatasourceEnum.getUrl());
        dataSource.setUsername(supportDatasourceEnum.getUsername());
        dataSource.setPassword(supportDatasourceEnum.getPassword());
        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(20);
        // 允许连接在池中的空闲状态的最长时间 默认值10分钟
        dataSource.setIdleTimeout(600000);
        // 控制池中连接的最大生存期 正在使用的连接不会停用，只有在关闭的时候才会删除连接
        dataSource.setMaxLifetime(1200000);
        dataSource.setConnectionTimeout(10000);
        // 是否开启ps cache(只对sql语句解析一次) 默认false
        dataSource.addDataSourceProperty("cachePrepStmts", true);
        // 可以缓存的sql数量  默认25
        dataSource.addDataSourceProperty("prepStmtCacheSize",250);
        // sql的字符数限制  某个sql超过这个字符数，则不会被缓存
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", true);
        dataSource.addDataSourceProperty("useLocalSessionState", true);
        dataSource.addDataSourceProperty("rewriteBatchedStatements", true);
        dataSource.addDataSourceProperty("cacheResultSetMetadata", true);
        dataSource.addDataSourceProperty("cacheServerConfiguration", true);
        dataSource.addDataSourceProperty("elideSetAutoCommits", true);
        dataSource.addDataSourceProperty("maintainTimeStats", false);
        return dataSource;
    }


    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 向MyBatis-Plus的过滤器链中添加分页拦截器，需要设置数据库类型（主要用于分页方言）
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }
}