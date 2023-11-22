package com.supermarket.daoimpl;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.supermarket.dao.CustomerDAO;
import com.supermarket.model.custom.customer.CustomerDTO;
import com.supermarket.model.custom.customer.CustomerFilterList;
import com.supermarket.model.entity.Customer;
import com.supermarket.util.ValidationUtil;
import com.supermarket.util.WebServiceUtil;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public int addCustomer(Customer customer) {
		return (int) sessionFactory.getCurrentSession().save(customer);
	}

	@Override
	public boolean isUniqueCustomer(Integer customerId, String mobileNo, String mail) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class)
				.add(Restrictions.neOrIsNotNull("customerId", customerId)).add(Restrictions.disjunction()
						.add(Restrictions.eq("mail", mail)).add(Restrictions.eq("mobileNo", mobileNo)));

		return criteria.uniqueResult() == null;
	}

	@Override
	public Customer getCustomerById(Integer customerId) {
		return (Customer) sessionFactory.getCurrentSession().get(Customer.class, customerId);
	}

	@Override
	public Customer getCustomerByName(String custName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class)
				.add(Restrictions.eq("customerName", custName));
		return (Customer) criteria;
	}

	@Override
	public CustomerDTO getCustomerDTOById(int customerId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class)
				.add(Restrictions.eq("customerId", customerId))
				.setProjection(Projections.projectionList().add(Projections.property("customerId"), "customerId")
						.add(Projections.property("customerName"), "customerName")
						.add(Projections.property("mobileNo"), "mobileNo")
						.add(Projections.property("address"), "address")
						.add(Projections.property("location"), "location").add(Projections.property("city"), "city")
						.add(Projections.property("pincode"), "pincode").add(Projections.property("mail"), "mail"))
				.setResultTransformer(Transformers.aliasToBean(CustomerDTO.class));

		return (CustomerDTO) criteria.uniqueResult();
	}

	@Override
	public Map<String, Object> listCustomer(CustomerFilterList customerFilterList) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Customer.class)
				.setProjection(Projections.rowCount());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCount", criteria.uniqueResult());

		if (customerFilterList.getSearch() != null && !customerFilterList.getSearch().trim().isEmpty()) {
			
			if(customerFilterList.getSearchColumn() != null && !customerFilterList.getSearchColumn().trim().isEmpty()) {
				
				if(customerFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.CUSTOMER_ID)) {
					criteria.add(Restrictions.eq("customerId", Integer.parseInt(customerFilterList.getSearch())));
				} else if(customerFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)) {
					criteria.add(Restrictions.ilike("customerName", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				} else if(customerFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.CUSTOMER_MOBILE_NUMBER)) {
					criteria.add(Restrictions.ilike("mobileNo", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				} else {
					criteria.add(Restrictions.ilike("mail", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				}
			} else {
				if(ValidationUtil.isValidNumber(customerFilterList.getSearch())) {
					criteria.add(Restrictions.disjunction()
							.add(Restrictions.eq("customerId", Integer.parseInt(customerFilterList.getSearch())))
							.add(Restrictions.ilike("mobileNo", customerFilterList.getSearch(), MatchMode.ANYWHERE)));
				} else {
					criteria.add(Restrictions.disjunction()
							.add(Restrictions.ilike("customerName", customerFilterList.getSearch(), MatchMode.ANYWHERE))
							.add(Restrictions.ilike("mail", customerFilterList.getSearch(), MatchMode.ANYWHERE)));
				}
			}
		}
		
		if(customerFilterList.getOrderBy() != null) {
			
//			 && customerFilterList.getOrderBy().getType() != null
			if(ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getColumn())) {
				
				if(customerFilterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)) {
					
					if (customerFilterList.getOrderBy().getType() == null
							|| customerFilterList.getOrderBy().getType().trim().isEmpty()
							|| customerFilterList.getOrderBy().getType().trim().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("customerName"));
					} else {
						criteria.addOrder(Order.desc("customerName"));
					}
				} else if(customerFilterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.CUSTOMER_CREATEDDATE)) {
					
					if(customerFilterList.getOrderBy().getType() == null
							|| customerFilterList.getOrderBy().getType().trim().isEmpty()
							|| customerFilterList.getOrderBy().getType().trim().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("createdDate"));
					} else {
						criteria.addOrder(Order.desc("createdDate"));
					}
				}
			}
		}

		criteria.setProjection(Projections.rowCount());
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.property("customerId"), "customerId")
				.add(Projections.property("customerName"), "customerName")
				.add(Projections.property("mobileNo"), "mobileNo").add(Projections.property("address"), "address")
				.add(Projections.property("location"), "location").add(Projections.property("city"), "city")
				.add(Projections.property("pincode"), "pincode").add(Projections.property("mail"), "mail"))
				.setFirstResult(customerFilterList.getStart()).setMaxResults(customerFilterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(CustomerDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}

}
