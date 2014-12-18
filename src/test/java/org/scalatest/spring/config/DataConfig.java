package org.scalatest.spring.config;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.H2Dialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
import org.h2.jdbcx.JdbcConnectionPool;


@Configuration
@EnableTransactionManagement
public class DataConfig {

    @Bean public AnnotationSessionFactoryBean sessionFactory() {
        AnnotationSessionFactoryBean sessionFactoryBean = new AnnotationSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
        sessionFactoryBean.setPackagesToScan(new String[]{"org.scalatest.spring.model"});
        Properties p = new Properties();
        p.put("hibernate.dialect", H2Dialect.class.getCanonicalName());
        p.put("hibernate.show_sql", "true");
        p.put("hibernate.hbm2ddl.auto", "update");
        sessionFactoryBean.setHibernateProperties(p);
        return sessionFactoryBean;
    }

    private DataSource dataSource() {
        return JdbcConnectionPool.create("jdbc:h2:mem:scalatest;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager();
        hibernateTransactionManager.setSessionFactory(sessionFactory().getObject());
        return hibernateTransactionManager;
    }

}
