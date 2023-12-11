package com.supermarket.daoimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.supermarket.dao.OrderDAO;
import com.supermarket.model.custom.order.OrderDetailsDTO;
import com.supermarket.model.custom.order.OrderFilterList;
import com.supermarket.model.custom.order.OrderLineItemDetailsDTO;
import com.supermarket.model.entity.OrderDetails;
import com.supermarket.model.entity.OrderLineItemDetails;
import com.supermarket.util.ValidationUtil;
import com.supermarket.util.WebServiceUtil;

@Repository
public class OrderDAOImpl implements OrderDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void createOrder(OrderDetails order) {
		sessionFactory.getCurrentSession().save(order);
	}

	@Override
	public void orderProduct(OrderLineItemDetails item) {
		sessionFactory.getCurrentSession().persist(item);
	}

	@Override
	public OrderDetails getOrderById(Integer orderId) {
		return (OrderDetails) sessionFactory.getCurrentSession().get(OrderDetails.class, orderId);
	}

	@Override
	public OrderLineItemDetails getOrderItemByOlidId(Integer orderId, Integer productId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("orderId", "order").createAlias("productId", "product")
				.add(Restrictions.eq("order.orderId", orderId)).add(Restrictions.eq("product.productId", productId));

		return (OrderLineItemDetails) criteria.uniqueResult();
	}

	@Override
	public Long getProductReservedPackageCount(Integer productID, Integer orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("productId", "product").createAlias("orderId", "order")
				.add(Restrictions.eq("product.productId", productID)).add(Restrictions.eq("olidStatus", "new"))
				.add(Restrictions.neOrIsNotNull("order.orderId", orderId)) // need to change(is not null)
				.setProjection(Projections.sum("quantityInPackage"));

		return (Long) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OrderLineItemDetails> getOrderItemListByOrderId(Integer orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("orderId", "order").add(Restrictions.eq("order.orderId", orderId));

		return criteria.list();
	}

	@Override
	public Map<String, Object> getOrderItemListDTOByOrderId(Integer orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("orderId", "order").createAlias("order.customerId", "customer")
				.createAlias("productId", "product").add(Restrictions.eq("order.orderId", orderId))
				.setProjection(Projections.property("order.customerId"));
//						.add(Projections.property("order.orderId"), "orderId"));

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("customer", criteria.uniqueResult());

		criteria.setProjection(Projections.property("orderId"));
		resultMap.put("order", criteria.uniqueResult());

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("olidId"), "olidId");
		projectionList.add(Projections.property("product.productId"), "productId");
		projectionList.add(Projections.property("product.productName"), "productName");
		projectionList.add(Projections.property("product.productPrice"), "productPrice");
		projectionList.add(Projections.property("quantityIndividualUnit"), "quantityIndividualUnit");
		projectionList.add(Projections.property("quantityInPackage"), "quantityInPackage");
		projectionList.add(Projections.property("olidStatus"), "olidStatus");
		criteria.setProjection(projectionList);

		resultMap.put("data",
				criteria.setResultTransformer(Transformers.aliasToBean(OrderLineItemDetailsDTO.class)).list());

		return resultMap;
	}

	@Override
	public void updateOrderStatus(Integer orderId, String orderStatus) {
		String rawQuery = "UPDATE OrderDetails SET orderStatus = :orderStatus WHERE orderId = :orderId";

		Query query = sessionFactory.getCurrentSession().createQuery(rawQuery);
		query.setParameter("orderStatus", orderStatus);
		query.setInteger("orderId", orderId);
		query.executeUpdate();
	}

	@Override
	public void updateOrderItemStatus(Integer orderId, String orderStatus) {
		String rawQuery = "UPDATE OrderLineItemDetails SET olidStatus = :olidStatus WHERE orderId = :orderId";

		Query query = sessionFactory.getCurrentSession().createQuery(rawQuery);
		query.setParameter("olidStatus", orderStatus);
		query.setInteger("orderId", orderId);
		query.executeUpdate();
	}

//	@Override
	public Map<String, Object> listOrders(OrderFilterList orderFilterList) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderDetails.class)
				.createAlias("customerId", "customer").setProjection(Projections.rowCount());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCount", criteria.uniqueResult());

		if (orderFilterList.getSearch() != null && !orderFilterList.getSearch().trim().isEmpty()) {

			if (!orderFilterList.getSearchColumn().trim().isEmpty()) {

				if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_ID)) {
					criteria.add(Restrictions.eq("customer.customerId", Integer.parseInt(orderFilterList.getSearch())));
				} else if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)) {
					criteria.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch(),
							MatchMode.ANYWHERE));
				}
			} else {
//				criteria.add(Restrictions.disjunction()
//						.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch(), MatchMode.ANYWHERE))
//						.add(Restrictions.eq("customer.customerId", Integer.parseInt(orderFilterList.getSearch()))));
				if (ValidationUtil.isValidNumber(orderFilterList.getSearch())) {
					criteria.add(Restrictions.eq("customer.customerId", Integer.parseInt(orderFilterList.getSearch())));
				} else {
					criteria.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch(),
							MatchMode.ANYWHERE));
				}
			}
		}

		if (orderFilterList.getFilter().getOrderStatus() != null
				&& !orderFilterList.getFilter().getOrderStatus().trim().isEmpty()) {

			criteria.add(Restrictions.eq("orderStatus", orderFilterList.getFilter().getOrderStatus()));
		}

		if (orderFilterList.getFilter().getFromDate() != null) {
			criteria.add(Restrictions.gt("orderedDate", orderFilterList.getFilter().getFromDate()));
		}

		if (orderFilterList.getFilter().getToDate() != null) {
			criteria.add(Restrictions.le("orderedDate", orderFilterList.getFilter().getToDate()));
		}

		if (orderFilterList.getOrderBy() != null) {

			if (ValidationUtil.isNotEmpty(orderFilterList.getOrderBy().getColumn())
					&& orderFilterList.getOrderBy().getType() != null) {

				if (orderFilterList.getOrderBy().getColumn().equalsIgnoreCase("ordereddate")) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("orderedDate"));
					} else {
						criteria.addOrder(Order.desc("orderedDate"));
					}
				} else if (orderFilterList.getOrderBy().getColumn().equalsIgnoreCase("expecteddate")) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("orderExpectedDate"));
					} else {
						criteria.addOrder(Order.desc("orderExpectedDate"));
					}
				} else if (orderFilterList.getOrderBy().getColumn().equalsIgnoreCase("orderstatus")) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("orderStatus"));
					} else {
						criteria.addOrder(Order.desc("orderStatus"));
					}
				}
			}
		}

		criteria.setProjection(Projections.rowCount());
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.property("orderId"), "orderId")
				.add(Projections.property("orderedDate"), "orderedDate")
				.add(Projections.property("customer.customerId"), "customerId")
				.add(Projections.property("customer.customerName"), "customerName")
				.add(Projections.property("orderExpectedDate"), "orderExpectedDate")
				.add(Projections.property("orderStatus"), "orderStatus")).setFirstResult(orderFilterList.getStart())
				.setMaxResults(orderFilterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(OrderDetailsDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}

	@Override
	public Map<String, Object> listOrder(OrderFilterList orderFilterList) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class, "olid")
				.createAlias("olid.orderId", "order").createAlias("order.customerId", "customer")
				.createAlias("olid.productId", "product").setProjection(Projections.countDistinct("orderId"));
//				.setProjection(Projections.projectionList()
//						.add(Projections.groupProperty("order.orderId"))
////						.add(Projections.count("order.orderId")));
//						.add(Projections.rowCount()));
////				criteria.setProjection(Projections.groupProperty("olid.orderId"));
//		criteria.setProjection(Projections.rowCount());

//				criteria.setProjection(Projections.count("order.orderId"));

//		criteria.add(Restrictions.)

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCount", criteria.uniqueResult());

		if (orderFilterList.getSearch() != null && !orderFilterList.getSearch().trim().isEmpty()) {

			if (orderFilterList.getSearchColumn() != null && !orderFilterList.getSearchColumn().trim().isEmpty()) {

				if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_ID)) {
					criteria.add(Restrictions.eq("customer.customerId",
							Integer.parseInt(orderFilterList.getSearch().trim())));
				} else if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.CUSTOMER_NAME)) {
					criteria.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch().trim(),
							MatchMode.ANYWHERE));
				} else if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.PRODUCT_ID)) {
					criteria.add(
							Restrictions.eq("product.productId", Integer.parseInt(orderFilterList.getSearch().trim())));
				} else if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ID)) {
					criteria.add(
							Restrictions.eq("order.orderId", Integer.parseInt(orderFilterList.getSearch().trim())));
				} else if (orderFilterList.getSearchColumn().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME)) {
					criteria.add(Restrictions.ilike("product.productName", orderFilterList.getSearch().trim(),
							MatchMode.ANYWHERE));
				}
			} else {
//				criteria.add(Restrictions.disjunction()
//						.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch(), MatchMode.ANYWHERE))
//						.add(Restrictions.eq("customer.customerId", Integer.parseInt(orderFilterList.getSearch()))));
				if (ValidationUtil.isValidNumber(orderFilterList.getSearch())) {
					criteria.add(Restrictions.disjunction()
							.add(Restrictions.eq("customer.customerId",
									Integer.parseInt(orderFilterList.getSearch().trim())))
							.add(Restrictions.eq("product.productId",
									Integer.parseInt(orderFilterList.getSearch().trim()))));
				} else {
					criteria.add(Restrictions.disjunction()
							.add(Restrictions.ilike("customer.customerName", orderFilterList.getSearch(),
									MatchMode.ANYWHERE))
							.add(Restrictions.ilike("product.productName", orderFilterList.getSearch(),
									MatchMode.ANYWHERE)));
				}
			}
		}

		if (orderFilterList.getFilter().getOrderStatus() != null
				&& !orderFilterList.getFilter().getOrderStatus().trim().isEmpty()) {

			criteria.add(Restrictions.eq("order.orderStatus", orderFilterList.getFilter().getOrderStatus()));
		}

//		if(orderFilterList.getFilter().getFromDate() != null && orderFilterList.getFilter().getToDate() != null) {
//			if(orderFilterList.getFilter().getFromDate().equals(orderFilterList.getFilter().getToDate()))
//			{
//				System.out.println(orderFilterList.getFilter().getFromDate());
////				criteria.add(Restrictions.eq("order.orderedDate", orderFilterList.getFilter().getFromDate()));
//				criteria.add(Restrictions.sqlRestriction("DATE(OLID_created_date) = ?",
//						orderFilterList.getFilter().getFromDate(), StandardBasicTypes.DATE));
//			} else {
//				criteria.add(Restrictions.ge("order.orderedDate", orderFilterList.getFilter().getFromDate()))
//						.add(Restrictions.le("order.orderedDate", orderFilterList.getFilter().getToDate()));
//			}
//		} else 

		if (orderFilterList.getFilter().getFromDate() != null) {
			criteria.add(Restrictions.ge("order.orderExpectedDate", orderFilterList.getFilter().getFromDate()));
		}
		if (orderFilterList.getFilter().getToDate() != null) {
			criteria.add(Restrictions.le("order.orderExpectedDate", orderFilterList.getFilter().getToDate()));
		}

		if (orderFilterList.getOrderBy() != null) {

			if (ValidationUtil.isNotEmpty(orderFilterList.getOrderBy().getColumn())
					&& orderFilterList.getOrderBy().getType() != null) {

				if (orderFilterList.getOrderBy().getColumn()
						.equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ORDEREDDATE)) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("order.orderedDate"));
					} else {
						criteria.addOrder(Order.desc("order.orderedDate"));
					}
				} else if (orderFilterList.getOrderBy().getColumn()
						.equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_ORDEREXPECTEDDATE)) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("order.orderExpectedDate"));
					} else {
						criteria.addOrder(Order.desc("order.orderExpectedDate"));
					}
				} else if (orderFilterList.getOrderBy().getColumn()
						.equalsIgnoreCase(WebServiceUtil.ORDERDETAILS_STATUS)) {

					if (orderFilterList.getOrderBy().getType() == null
							|| orderFilterList.getOrderBy().getType().trim().isEmpty()
							|| orderFilterList.getOrderBy().getType().equalsIgnoreCase("asc")) {
						criteria.addOrder(Order.asc("order.orderStatus"));
					} else {
						criteria.addOrder(Order.desc("order.orderStatus"));
					}
				}
			}
		}

		criteria.setProjection(Projections.countDistinct("orderId"));
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("order.orderId"), "orderId")
				.add(Projections.property("order.orderedDate"), "orderedDate")
				.add(Projections.property("customer.customerId"), "customerId")
				.add(Projections.property("customer.customerName"), "customerName")
				.add(Projections.property("order.orderExpectedDate"), "orderExpectedDate")
				.add(Projections.property("order.orderStatus"), "orderStatus"))
				.setFirstResult(orderFilterList.getStart()).setMaxResults(orderFilterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(OrderDetailsDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}
}
