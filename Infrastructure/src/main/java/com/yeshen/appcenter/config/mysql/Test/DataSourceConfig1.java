/*
package com.yeshen.appcenter.config.mysql.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;


@Configuration
@MapperScan(basePackages = "com.nox.launcher.en.repository.mysql.launcher",sqlSessionFactoryRef = "launcherSqlSessionFactory")
public class DataSourceConfig1 {

    @Primary
    @Bean(name = "launcherDataSourceProperties")
    @ConfigurationProperties("spring.datasource1")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "launcherDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource1.hikari")
    public HikariDataSource launcherDataSource(@Qualifier("launcherDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "launcherSqlSessionFactory")
    @Primary
    public SqlSessionFactory launcherSqlSessionFactory(@Qualifier("launcherDataSource") DataSource datasource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(datasource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setConfiguration(configuration);
        // 设置分页插件
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor});
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:com/nox/launcher/en/mapper/*.xml"));
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "launcherTransactionManager")
    @Primary
    public DataSourceTransactionManager launcherTransactionManager(@Qualifier("launcherDataSource") HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("launcherSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate launcherSqlSessionTemplate(
            @Qualifier("launcherSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return new SqlSessionTemplate(sessionFactory);
    }
}*/