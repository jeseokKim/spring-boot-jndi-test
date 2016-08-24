package com.mybatis.config;

import java.io.IOException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(basePackages = {"com.mybatis.**.repository"}, annotationClass = Repository.class, sqlSessionFactoryRef = "sqlSessionFactory")
public class MyBatisConfiguration {

    public final String TYPE_ALIASES_PACKAGE = "com.mybatis";    // VO패키지 주소
    public final String TYPE_HANDLERS_PACKAGE = "com.mybatis.handler";    //
    public final String CONFIG_LOCATION_PATH = "classpath:sqlmap/mybatis-config.xml";   // mybatis-config.xml 경로
    public final String MAPPER_LOCATIONS_PATH = "classpath:sqlmap/mapper/**/*.xml";   // Mapper 폴더 경로

    @Bean(name = "dataSource", destroyMethod = "")
    public DataSource oracleJndiDataSource() throws IllegalArgumentException, NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/DataSource");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(false);
        bean.afterPropertiesSet();
        return (DataSource)bean.getObject();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        configureSqlSessionFactory(sessionFactoryBean, dataSource);
        return sessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource mssqlDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(mssqlDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        //transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }

    public void configureSqlSessionFactory(SqlSessionFactoryBean sessionFactoryBean, DataSource dataSource) throws IOException {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        sessionFactoryBean.setTypeHandlersPackage(TYPE_HANDLERS_PACKAGE);
        //sessionFactoryBean.setConfigLocation(pathResolver.getResource(CONFIG_LOCATION_PATH));
        sessionFactoryBean.setConfigLocation(new ClassPathResource("sqlmap/mybatis-config.xml"));
        sessionFactoryBean.setMapperLocations(pathResolver.getResources(MAPPER_LOCATIONS_PATH));
    }
}


/*
public abstract class DatabaseConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Bean
    public abstract DataSource dataSource();

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource, DatabaseProperties databaseProperties) {
        LOGGER.info("### configureDataSource = {}", databaseProperties);
        dataSource.setDriverClassName(databaseProperties.getDriverClassName());
        dataSource.setUrl(databaseProperties.getUrl());
        dataSource.setUsername(databaseProperties.getUsername());
        dataSource.setPassword(databaseProperties.getPassword());
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
    }

}

@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(MssqlDatabaseProperties.class)
class MssqlDatabaseConfiguration extends DatabaseConfiguration {

    @Autowired
    private MssqlDatabaseProperties mssqlDatabaseProperties;

    @Primary
    @Bean(name = "mssqlDataSource", destroyMethod = "close")
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource mssqlDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        configureDataSource(mssqlDataSource, mssqlDatabaseProperties);
        return mssqlDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("mssqlDataSource") DataSource mssqlDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(mssqlDataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false);
        //transactionManager.setRollbackOnCommitFailure(true);
        return transactionManager;
    }
}

@Configuration
@EnableConfigurationProperties(OracleDatabaseProperties.class)
class OracleDatabaseConfiguration extends DatabaseConfiguration {

    @Autowired
    private OracleDatabaseProperties oracleDatabaseProperties;

    @Bean(name = "oracleDataSource", destroyMethod = "close")
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource oracleDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        configureDataSource(oracleDataSource, oracleDatabaseProperties);
        return oracleDataSource;
    }
}
*/