package com.supermarket.daoimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductSales;
import com.supermarket.model.custom.product.ProductSalesFilterList;
import com.supermarket.model.custom.order.OrderLineItemDetailsDTO;
import com.supermarket.model.custom.product.ProductDTO;
import com.supermarket.model.entity.Product;
import com.supermarket.util.ValidationUtil;
import com.supermarket.util.WebServiceUtil;

@Repository
public class ProductDAOImpl implements ProductDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public int addProduct(Product product) {
		return (int) sessionFactory.getCurrentSession().save(product);
	}

	@Override
	public Product getProductById(int productId) {
		return (Product) sessionFactory.getCurrentSession().get(Product.class, productId);
	}

	@Override
	public boolean isUniqueProduct(Integer productId, String productName) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class)
				.add(Restrictions.eq("productName", productName.trim())).add(Restrictions.isNull("lastEffectiveDate"));
//				.add(Restrictions.ne("productId", productId));

		if (productId != null) {
			criteria.add(Restrictions.ne("productId", productId));
		}

		return criteria.uniqueResult() == null;
	}

	@Override
	public ProductDTO getProductDTOById(int productId) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class)
				.createAlias("oldProductId", "oldproduct", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.eq("productId", productId))
				.setProjection(Projections.projectionList().add(Projections.property("productId"), "productId")
						.add(Projections.property("productName"), "productName")
						.add(Projections.property("packQuantity"), "packQuantity")
						.add(Projections.property("productPrice"), "productPrice")
						.add(Projections.property("currentStockPackageCount"), "currentStockPackageCount")
						.add(Projections.property("productCategory"), "productCategory")
						.add(Projections.property("effectiveDate"), "effectiveDate")
						.add(Projections.property("lastEffectiveDate"), "lastEffectiveDate")
						.add(Projections.property("oldproduct.productId"), "oldProductId"))
				.setResultTransformer(Transformers.aliasToBean(ProductDTO.class));

		return (ProductDTO) criteria.uniqueResult();
	}

	@Override
	public Map<String, Object> listProducts(ProductFilterList filterList) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class)
				.createAlias("oldProductId", "oldproduct", JoinType.LEFT_OUTER_JOIN)
				.setProjection(Projections.rowCount());

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("totalCount", criteria.uniqueResult());

		if (filterList.getSearch() != null && !filterList.getSearch().trim().isEmpty()) {

			if (filterList.getSearchColumn() != null && !filterList.getSearchColumn().trim().isEmpty()) {

				if (filterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_ID)) {
					criteria.add(Restrictions.eq("productId", Integer.parseInt(filterList.getSearch())));
				} else if (filterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME)) {
					criteria.add(Restrictions.ilike("productName", filterList.getSearch().trim(), MatchMode.ANYWHERE));
				}
			} else {
//				criteria.add(Restrictions.disjunction()
//						.add(Restrictions.ilike("productName", "%" + filterList.getSearch() + "%")));
				if (ValidationUtil.isValidNumber(filterList.getSearch())) {
					criteria.add(Restrictions.eq("productId", Integer.parseInt(filterList.getSearch())));
				} else {
					criteria.add(Restrictions.ilike("productName", filterList.getSearch().trim(), MatchMode.ANYWHERE));
				}
			}
		}

		if (filterList.getFilter().getMinPrice() != null) {
			criteria.add(Restrictions.ge("productPrice", filterList.getFilter().getMinPrice()));
		}

		if (filterList.getFilter().getMaxPrice() != null) {
			criteria.add(Restrictions.le("productPrice", filterList.getFilter().getMaxPrice()));
		}

		if (filterList.getFilter().getStatus() != null) {

			if (filterList.getFilter().getStatus().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_ACTIVE)) {
				criteria.add(Restrictions.le("effectiveDate", new Date()))
						.add(Restrictions.disjunction().add(Restrictions.gt("lastEffectiveDate", new Date()))
								.add(Restrictions.isNull("lastEffectiveDate")))
						.add(Restrictions.gt("currentStockPackageCount", 0));
			} else if (filterList.getFilter().getStatus().trim()
					.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_INACTIVE)) {
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.conjunction().add(Restrictions.gt("effectiveDate", new Date())))
						.add(Restrictions.conjunction().add(Restrictions.isNotNull("lastEffectiveDate"))
								.add(Restrictions.lt("lastEffectiveDate", new Date()))));
			} else if (filterList.getFilter().getStatus().trim()
					.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_UPCOMING)) {
				criteria.add(Restrictions.gt("effectiveDate", new Date()));
			} else if (filterList.getFilter().getStatus().trim()
					.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_STOCKUNAVAILABLE)) {
				criteria.add(Restrictions.le("effectiveDate", new Date()))
						.add(Restrictions.disjunction().add(Restrictions.gt("lastEffectiveDate", new Date()))
								.add(Restrictions.isNull("lastEffectiveDate")))
						.add(Restrictions.le("currentStockPackageCount", 0));
			}
		}

		if (filterList.getFilter().getCategory() != null && !filterList.getFilter().getCategory().isBlank()) {

			criteria.add(Restrictions.eq("productCategory", filterList.getFilter().getCategory()));
		}

		if (filterList.getOrderBy() != null) {

			if (ValidationUtil.isNotEmpty(filterList.getOrderBy().getColumn())
					&& filterList.getOrderBy().getType() != null) {

				if (filterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME)) {

					if (filterList.getOrderBy().getType() == null || filterList.getOrderBy().getType().trim().isEmpty()
							|| filterList.getOrderBy().getType().trim()
									.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("productName"));
					} else {
						criteria.addOrder(Order.desc("productName"));
					}
				} else if (filterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_PRICE)) {

					if (filterList.getOrderBy().getType() == null || filterList.getOrderBy().getType().trim().isEmpty()
							|| filterList.getOrderBy().getType().trim()
									.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("productPrice"));
					} else {
						criteria.addOrder(Order.desc("productPrice"));
					}
				} else if (filterList.getOrderBy().getColumn().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_CURRENTSTOCKPACKAGECOUNT)) {

					if (filterList.getOrderBy().getType() == null || filterList.getOrderBy().getType().trim().isEmpty()
							|| filterList.getOrderBy().getType().trim()
									.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("currentStockPackageCount"));
					} else {
						criteria.addOrder(Order.desc("currentStockPackageCount"));
					}
				} else if (filterList.getOrderBy().getColumn().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_EFFECTIVEDATE)) {

					if (filterList.getOrderBy().getType() == null || filterList.getOrderBy().getType().trim().isEmpty()
							|| filterList.getOrderBy().getType().trim()
									.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
						criteria.addOrder(Order.asc("effectiveDate"));
					} else {
						criteria.addOrder(Order.desc("effectiveDate"));
					}
				}
//			} else {
//				criteria.addOrder(Order.asc("productName"));
			}
//		} else {
//			criteria.addOrder(Order.asc("productName"));
		}

		criteria.setProjection(Projections.rowCount());
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.property("productId"), "productId")
				.add(Projections.property("productName"), "productName")
				.add(Projections.property("packQuantity"), "packQuantity")
				.add(Projections.property("productPrice"), "productPrice")
				.add(Projections.property("currentStockPackageCount"), "currentStockPackageCount")
				.add(Projections.property("productCategory"), "productCategory")
				.add(Projections.property("effectiveDate"), "effectiveDate")
				.add(Projections.property("lastEffectiveDate"), "lastEffectiveDate")
				.add(Projections.property("oldproduct.productId"), "oldProductId"))
				.setFirstResult(filterList.getStart()).setMaxResults(filterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(ProductDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}
	

//	@Override
//	public Map<String, Object> listProductsSales(ProductSalesFilterList productSalesFilterList) {
//
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderLineItemDetailsDTO.class)
//				.createAlias("productId", "product").setProjection(Projections.countDistinct("productId"));
//
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("totalCount", criteria.uniqueResult());
//		
////		if(productSalesFilterList.getFilter().getFromDate() != null && productSalesFilterList.getFilter().getToDate() != null) {
////			
////			criteria.add(Restrictions.between("createdDate", productSalesFilterList.getFilter().getFromDate(), productSalesFilterList.getFilter().getToDate()));
////		}
//		
////		if (productSalesFilterList.getFilter().getFromDate() != null) {
////			criteria.add(Restrictions.ge("createdDate", productSalesFilterList.getFilter().getFromDate()));
////		}
////		if (productSalesFilterList.getFilter().getToDate() != null) {
////			criteria.add(Restrictions.le("createdDate", productSalesFilterList.getFilter().getToDate()));
////		}
//
////		if (productSalesFilterList.getOrderBy().getType() != null && productSalesFilterList.getOrderBy().getType()
////				.equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)) {
////			
////			criteria.addOrder(Order.asc(""))
////		}
//		
//		criteria.setProjection(Projections.countDistinct("productId"));
//		resultMap.put("filteredCount", criteria.uniqueResult());
//		
//		criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("productId"), "productId")
//				.add(Projections.property("productName"), "productName")
//				.add(Projections.sum("quantityInPackage"), "salesCount"))
//				.setFirstResult(productSalesFilterList.getStart()).setMaxResults(productSalesFilterList.getLength())
//				.setResultTransformer(Transformers.aliasToBean(ProductSales.class))
//				.addOrder(Order.asc("salesCount"));
//		
//		resultMap.put("data", criteria.list());
//
//		return resultMap;
//	}

}
