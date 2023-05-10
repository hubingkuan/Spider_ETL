/*
package com.yeshen.appcenter.config.mysql.Test;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;



@Configuration
@MapperScan(basePackages = "com.nox.launcher.en.repository.mysql.appcenter",sqlSessionFactoryRef = "appcenterSqlSessionFactory")
public class DataSourceConfig2 {
    @Bean(name = "appcenterDataSourceProperties")
    @ConfigurationProperties("spring.datasource2")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "appcenterDataSource")
    @ConfigurationProperties("spring.datasource2.hikari")
    public HikariDataSource appcenterDataSource(@Qualifier("appcenterDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "appcenterSqlSessionFactory")
    public SqlSessionFactory appcenterSqlSessionFactory(@Qualifier("appcenterDataSource") DataSource datasource) throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(datasource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        sqlSessionFactory.setGlobalConfig(globalConfig);
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{paginationInterceptor});
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                getResources("classpath*:com/nox/appcenter/en/mapper/*.xml"));
        return sqlSessionFactory.getObject();
    }

    @Bean(name = "appcenterTransactionManager")
    public DataSourceTransactionManager appcenterTransactionManager(@Qualifier("appcenterDataSource") HikariDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("appcenterSqlSessionTemplate")
    public SqlSessionTemplate appcenterSqlSessionTemplate(
            @Qualifier("appcenterSqlSessionFactory") SqlSessionFactory sessionFactory) {
        return new SqlSessionTemplate(sessionFactory);
    }
}*/