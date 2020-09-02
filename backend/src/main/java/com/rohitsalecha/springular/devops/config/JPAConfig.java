package com.rohitsalecha.springular.devops.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.rohitsalecha.springular.devops")
public class JPAConfig {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Environment env;
	private static String dbDriver = "com.mysql.cj.jdbc.Driver";
	private static String connectionUrl = System.getenv("MYSQL_JDBC_URL");
	private static String mysql_db_name = System.getenv("MYSQL_DB_NAME");
	private static String vault_addr = System.getenv("VAULT_ADDR");

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setPackagesToScan("com.rohitsalecha.springular.devops");
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());

		return em;
	}

	@Bean
	public DataSource dataSource() {
		String url = "jdbc:mysql://"+connectionUrl+"/"+mysql_db_name ;
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		Credentials creds = getDBCredentials();
		dataSource.setDriverClassName(dbDriver);
		dataSource.setUrl(url);
		dataSource.setUsername(creds.getUsername());
		dataSource.setPassword(creds.getPassword());
		return dataSource;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	private Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
		return properties;
	}

	private Credentials getDBCredentials() {
		
		String userName="";
		String password="";
		
		if (isNullOrEmpty(vault_addr)) {

			userName = System.getenv("MYSQL_DB_USER");
			password = System.getenv("MYSQL_DB_PASSWORD");
		} else {
			logger.info("Vault Address: "+vault_addr);
			VaultEndpoint endpoint = null;
			try {

				endpoint = VaultEndpoint.from(new URI(vault_addr));
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			String token=System.getenv("VAULT_TOKEN_MYSQL");
			String path =System.getenv("VAULT_PATH_MYSQL");
			VaultTemplate vaultTemplate = new VaultTemplate(endpoint,
					new TokenAuthentication(token));
			logger.info("Vault Token: "+token);
			VaultResponseSupport<Credentials> response = vaultTemplate.read(path,
					Credentials.class);
			logger.info("Vault Path: "+path);
			userName = response.getData().getUsername();
			password = response.getData().getPassword();

		}
		logger.debug("DB UserName: "+userName);
		return new Credentials(userName,password);
	}

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}
}
