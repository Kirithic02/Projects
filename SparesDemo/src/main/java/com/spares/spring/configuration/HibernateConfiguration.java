package com.spares.spring.configuration;


import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.spares.spring.daoimpl.AdminDAOImpl;
import com.spares.spring.daoimpl.UserDAOImpl;
import com.spares.spring.serviceimpl.AdminServiceImpl;
import com.spares.spring.serviceimpl.UserServiceImpl;

import org.springframework.transaction.annotation.EnableTransactionManagement;



@Configuration
@EnableTransactionManagement
@ComponentScan("com.spares.spring")
public class HibernateConfiguration 
{
	@Bean
	public InternalResourceViewResolver view()
	{
		InternalResourceViewResolver rs = new InternalResourceViewResolver();
		rs.setPrefix("/WEB-INF/views/");
		rs.setSuffix(".jsp");
		return rs;
	}
	
	@Bean
    public DataSource dataSource() 
    {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/spares"); 
        dataSource.setUsername("root"); 
        dataSource.setPassword("5595"); 
        return dataSource;
    }
	 
	@Bean
    public Properties hibernateProperties()
    {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update"); 
        return hibernateProperties;
    }
	
	@Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        return txManager;
    }
	
	@Autowired
    private LocalSessionFactoryBean sessionFactory;

	@Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource)
    {
	    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
	    sessionFactory.setDataSource(dataSource());
	    sessionFactory.setPackagesToScan("com.spares.spring.model");
	    sessionFactory.setHibernateProperties(hibernateProperties());
	    return sessionFactory;
    }
	
//    @Bean
//    public AdminDAOImpl adminDAOImpl()
//    {
//    	AdminDAOImpl adminDAO = new AdminDAOImpl();        
//    	adminDAO.setSessionFactory(sessionFactory.getObject());
//        return adminDAO;
//    }
//    
//    @Bean
//    public UserDAOImpl userDAOImpl()
//    {
//    	UserDAOImpl userDAO = new UserDAOImpl();        
//    	userDAO.setSessionFactory(sessionFactory.getObject());
//        return userDAO;
//    }
//
//    @Bean
//    public AdminServiceImpl adminServiceImpl() {
//    	AdminServiceImpl adminService = new AdminServiceImpl();
//    	adminService.setAdminDAO(adminDAOImpl());
//        return adminService;
//    }
//    
//    @Bean
//    public UserServiceImpl userServiceImpl() {
//    	UserServiceImpl userService = new UserServiceImpl();
//    	userService.setUserDao(userDAOImpl());
//        return userService;
//    }
}