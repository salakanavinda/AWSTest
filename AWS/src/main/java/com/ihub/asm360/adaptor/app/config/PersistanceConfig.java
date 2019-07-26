/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ihub.asm360.adaptor.app.Constants;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * The Class PersistanceConfig.
 */
@Configuration
@EnableTransactionManagement
public class PersistanceConfig {

	/** The log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistanceConfig.class);

	/** The env. */
	@Autowired
	private Environment env;

	/**
	 * Data source.
	 *
	 * @return the data source
	 */
	@Bean
	public DataSource dataSource() {
		LOGGER.debug("JNDI lookup for irDataSource");

		final HikariConfig config = new HikariConfig();
		config.setDataSourceClassName(env.getProperty(Constants.Database.DATABASE_DRIVER_DATASOURCE_CLAZZ));
		config.addDataSourceProperty("url", env.getProperty(Constants.Database.DATABASE_URL));
		config.addDataSourceProperty("user", env.getProperty(Constants.Database.DATABASE_USERNAME));
		config.addDataSourceProperty("password", env.getProperty(Constants.Database.DATABASE_PWD));
		config.setConnectionTestQuery(env.getProperty(Constants.Database.DATABASE_CONNECTION_TEST_QUERY));
		config.setMaximumPoolSize(Integer.valueOf(env.getProperty(Constants.Database.DATABASE_MAX_POOL_SIZE)));
		final HikariDataSource ds = new HikariDataSource(config);

		return ds;
	}

	/**
	 * Entity manager factory.
	 *
	 * @return the local container entity manager factory bean
	 */
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactory.setDataSource(dataSource());
		entityManagerFactory.setJpaDialect(new HibernateJpaDialect());
		entityManagerFactory.setPackagesToScan("com.ihub.asm360.adaptor.core", "com.ihub.asm360.adaptor.app");

		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

		// Define additional properties
		final Properties additionalProperties = new Properties();
		additionalProperties.put(Constants.Hibernate.HIBERNATE_DIALECT,
				env.getProperty(Constants.Hibernate.HIBERNATE_DIALECT));
		additionalProperties.put(Constants.Hibernate.HIBERNATE_NAMING_STRATEGY,
				env.getProperty(Constants.Hibernate.HIBERNATE_NAMING_STRATEGY));

		additionalProperties.put(Constants.Hibernate.HIBERNATE_SHOW_SQL,
				Boolean.getBoolean(env.getProperty(Constants.Hibernate.HIBERNATE_SHOW_SQL)));
		additionalProperties.put(Constants.Hibernate.HIBERNATE_FORMAT_SQL,
				Boolean.getBoolean(env.getProperty(Constants.Hibernate.HIBERNATE_FORMAT_SQL)));

		if (Constants.Strings.TRUE.equals(env.getProperty(Constants.Hibernate.HIBERNATE_SHOULD_VALIDATE))) {
			additionalProperties.put(Constants.Hibernate.HIBERNATE_HBM2DDL,
					env.getProperty(Constants.Hibernate.HIBERNATE_HBM2DDL));
		}

		entityManagerFactory.setJpaProperties(additionalProperties);
		return entityManagerFactory;
	}

	/**
	 * Transaction manager.
	 *
	 * @return the platform transaction manager
	 */
	@Bean
	public PlatformTransactionManager transactionManager() {
		final JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(entityManagerFactory().getObject());
		return tm;
	}

	/**
	 * Exception translation.
	 *
	 * @return the persistence exception translation post processor
	 */
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}
