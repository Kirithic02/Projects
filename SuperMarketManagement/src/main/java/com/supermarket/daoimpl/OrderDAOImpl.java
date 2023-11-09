package com.supermarket.daoimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
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
	public OrderDetails getOrderById(int orderId) {
		return (OrderDetails) sessionFactory.getCurrentSession().get(OrderDetails.class, orderId);
	}

	@Override
	public OrderLineItemDetails getOrderItemByOlidId(int orderId, int productId) {
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
	public List<OrderLineItemDetails> getOrderItemListByOrderId(int orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("orderId", "order").add(Restrictions.eq("order.orderId", orderId));

		return criteria.list();
	}

	@Override
	public Map<String, Object> getOrderItemListDTOByOrderId(Integer orderId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetails.class)
				.createAlias("orderId", "order").createAlias("order.customerId", "customer")
				.createAlias("productId", "product").add(Restrictions.eq("order.orderId", orderId))
				.setProjection(Projections.property("customer.customerId"));
//						.add(Projections.property("order.orderId"), "orderId"));

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("customerId", (Integer) criteria.uniqueResult());

		criteria.setProjection(Projections.property("order.orderId"));
		resultMap.put("orderId", criteria.uniqueResult());

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("olidId"), "olidId");
		projectionList.add(Projections.property("product.productId"), "productId");
		projectionList.add(Projections.property("quantityIndividualUnit"), "quantityIndividualUnit");
		projectionList.add(Projections.property("quantityInPackage"), "quantityInPackage");
		projectionList.add(Projections.property("olidStatus"), "olidStatus");
		criteria.setProjection(projectionList);

		resultMap.put("data",
				criteria.setResultTransformer(Transformers.aliasToBean(OrderLineItemDetailsDTO.class)).list());

		return resultMap;
	}

	@Override
	public void updateOrderStatus(int orderId, String orderStatus) {
		String rawQuery = "UPDATE OrderDetails SET orderStatus = :orderStatus WHERE orderId = :orderId";

		org.hibernate.Query query = sessionFactory.getCurrentSession().createQuery(rawQuery);
		query.setParameter("orderStatus", orderStatus);
		query.setInteger("orderId", orderId);
		query.executeUpdate();
	}

	@Override
	public void updateOrderItemStatus(int orderId, String orderStatus) {
		String rawQuery = "UPDATE OrderLineItemDetails SET olidStatus = :olidStatus WHERE orderId = :orderId";

		org.hibernate.Query query = sessionFactory.getCurrentSession().createQuery(rawQuery);
		query.setParameter("olidStatus", orderStatus);
		query.setInteger("orderId", orderId);
		query.executeUpdate();
	}

	@Override
	public Map<String, Object> listOrder(OrderFilterList orderFilterList) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderDetails.class)
				.createAlias("customerId", "customer").setProjection(Projections.rowCount());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCount", criteria.uniqueResult());

		if (orderFilterList.getFilter().getStatus() != null && !orderFilterList.getFilter().getStatus().isEmpty()) {

			criteria.add(Restrictions.eq("orderStatus", orderFilterList.getFilter().getStatus()));
		}

		if (orderFilterList.getFilter().getFromDate() != null) {
			criteria.add(Restrictions.gt("orderedDate", orderFilterList.getFilter().getFromDate()));
		}

		if (orderFilterList.getFilter().getToDate() != null) {
			criteria.add(Restrictions.le("orderedDate", orderFilterList.getFilter().getToDate()));
		}

		criteria.setProjection(Projections.rowCount());
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.property("orderId"), "orderId")
				.add(Projections.property("orderedDate"), "orderedDate")
				.add(Projections.property("customer.customerId"), "customerId")
				.add(Projections.property("orderExpectedDate"), "orderExpectedDate")
				.add(Projections.property("orderStatus"), "orderStatus")).setFirstResult(orderFilterList.getStart())
				.setMaxResults(orderFilterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(OrderDetailsDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}
}
