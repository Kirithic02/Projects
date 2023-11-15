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

		if (customerFilterList.getSearch() != null && !customerFilterList.getSearch().isEmpty()) {
			
			if(!customerFilterList.getSearchColumn().isEmpty()) {
				
				if(customerFilterList.getSearchColumn().equalsIgnoreCase("customername")) {
					criteria.add(Restrictions.ilike("customerName", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				} else if(customerFilterList.getSearchColumn().equalsIgnoreCase("mobileno")) {
					criteria.add(Restrictions.ilike("mobileNo", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				} else {
					criteria.add(Restrictions.ilike("mail", customerFilterList.getSearch(), MatchMode.ANYWHERE));
				}
			} else {
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.ilike("customerName", customerFilterList.getSearch(), MatchMode.ANYWHERE))
						.add(Restrictions.ilike("mobileNo", customerFilterList.getSearch(), MatchMode.ANYWHERE))
						.add(Restrictions.ilike("mail", customerFilterList.getSearch(), MatchMode.ANYWHERE)));
			}
		}
		
		if(customerFilterList.getOrderBy() != null) {
			
			if(ValidationUtil.isNotEmpty(customerFilterList.getOrderBy().getColumn()) && customerFilterList.getOrderBy().getType() != null) {
				
				if(customerFilterList.getOrderBy().getColumn().equalsIgnoreCase("customername")) {
					
					if(customerFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
						criteria.addOrder(Order.desc("customerName"));
					} else {
						criteria.addOrder(Order.asc("customerName"));
					}
				} else if(customerFilterList.getOrderBy().getType().equalsIgnoreCase("createddate")) {
					
					if(customerFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
						criteria.addOrder(Order.desc("createdDate"));
					} else {
						criteria.addOrder(Order.asc("createdDate"));
					}
				}
			} else {
				criteria.addOrder(Order.asc("customerName"));
			}
		} else {
			criteria.addOrder(Order.asc("customerName"));
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
