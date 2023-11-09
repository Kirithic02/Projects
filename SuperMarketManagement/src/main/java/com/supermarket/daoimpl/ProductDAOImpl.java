package com.supermarket.daoimpl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductDTO;
import com.supermarket.model.entity.Product;
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
				.add(Restrictions.ilike("productName", productName)).add(Restrictions.isNull("lastEffectiveDate"))
				.add(Restrictions.neOrIsNotNull("productId", productId));

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

		if (filterList.getSearch() != null && !filterList.getSearch().isEmpty()) {
			
			if(!filterList.getSearchColumn().isEmpty()) {
				
				if(filterList.getSearchColumn().equalsIgnoreCase("productname")) {
					criteria.add(Restrictions.ilike("productName", "%" + filterList.getSearch() + "%"));
				}
			} else {
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.ilike("productName", "%" + filterList.getSearch() + "%")));
			}
		}

		if (filterList.getFilter().getMinPrice() != null) {
			criteria.add(Restrictions.ge("productPrice", filterList.getFilter().getMinPrice()));
		}

		if (filterList.getFilter().getMaxPrice() != null) {
			criteria.add(Restrictions.le("productPrice", filterList.getFilter().getMaxPrice()));
		}

		if (filterList.getFilter().getStatus() != null) {

			if (filterList.getFilter().getStatus().equals(WebServiceUtil.ACTIVE)) {
				criteria.add(Restrictions.le("effectiveDate", new Date()))
						.add(Restrictions.disjunction().add(Restrictions.gt("lastEffectiveDate", new Date()))
								.add(Restrictions.isNull("lastEffectiveDate")));
			} else if (filterList.getFilter().getStatus().equals(WebServiceUtil.INACTIVE)) {
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.conjunction().add(Restrictions.gt("effectiveDate", new Date())))
						.add(Restrictions.conjunction().add(Restrictions.isNotNull("lastEffectiveDate"))
								.add(Restrictions.lt("lastEffectiveDate", new Date()))));
			} else if (filterList.getFilter().getStatus().equals(WebServiceUtil.UPCOMING)) {
				criteria.add(Restrictions.gt("effectiveDate", new Date()));
			}
		}

		criteria.setProjection(Projections.rowCount());
		resultMap.put("filteredCount", criteria.uniqueResult());

		criteria.setProjection(Projections.projectionList().add(Projections.property("productId"), "productId")
				.add(Projections.property("productName"), "productName")
				.add(Projections.property("packQuantity"), "packQuantity")
				.add(Projections.property("productPrice"), "productPrice")
				.add(Projections.property("currentStockPackageCount"), "currentStockPackageCount")
				.add(Projections.property("effectiveDate"), "effectiveDate")
				.add(Projections.property("lastEffectiveDate"), "lastEffectiveDate")
				.add(Projections.property("oldproduct.productId"), "oldProductId"))
				.setFirstResult(filterList.getStart()).setMaxResults(filterList.getLength())
				.setResultTransformer(Transformers.aliasToBean(ProductDTO.class));

		resultMap.put("data", criteria.list());

		return resultMap;
	}

//	@Override
//	public Map<String, Object> listActiveProducts(Date date, Integer page, Integer limit) {
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class)
//				.createAlias("oldProductId", "oldproduct", JoinType.LEFT_OUTER_JOIN)
//				.setProjection(Projections.rowCount());
//
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("totalCount", criteria.uniqueResult());
//
//		if (date == null) {
//			criteria.add(Restrictions.le("effectiveDate", new Date()))
//					.add(Restrictions.disjunction().add(Restrictions.gt("lastEffectiveDate", new Date()))
//							.add(Restrictions.isNull("lastEffectiveDate")));
//		} else {
//			criteria.add(Restrictions.le("effectiveDate", date))
//					.add(Restrictions.disjunction().add(Restrictions.gt("lastEffectiveDate", date))
//							.add(Restrictions.isNull("lastEffectiveDate")));
//		}
//
//		criteria.setProjection(Projections.rowCount());
//		resultMap.put("filteredCount", criteria.uniqueResult());
//
//		criteria.setProjection(Projections.projectionList().add(Projections.property("productId"), "productId")
//				.add(Projections.property("productName"), "productName")
//				.add(Projections.property("packQuantity"), "packQuantity")
//				.add(Projections.property("productPrice"), "productPrice")
//				.add(Projections.property("currentStockPackageCount"), "currentStockPackageCount")
//				.add(Projections.property("effectiveDate"), "effectiveDate")
//				.add(Projections.property("oldproduct.productId"), "oldProductId")).setFirstResult((page - 1) * limit)
//				.setMaxResults(limit).setResultTransformer(Transformers.aliasToBean(ProductDTO.class));
//
//		resultMap.put("data", criteria.list());
//
//		return resultMap;
//	}
//
//	@Override
//	@Transactional
//	public Map<String, Object> listInActiveProducts(Date date) {
//		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class)
//				.createAlias("oldProductId", "oldproduct", JoinType.LEFT_OUTER_JOIN)
//				.setProjection(Projections.rowCount());
//
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("totalCount", criteria.uniqueResult());
//
//		if (date == null) {
//			criteria.add(Restrictions.disjunction()
//					.add(Restrictions.conjunction().add(Restrictions.gt("effectiveDate", new Date())))
//					.add(Restrictions.conjunction().add(Restrictions.isNotNull("lastEffectiveDate"))
//							.add(Restrictions.lt("lastEffectiveDate", new Date()))));
//		} else {
//			criteria.add(Restrictions.disjunction()
//					.add(Restrictions.conjunction().add(Restrictions.gt("effectiveDate", date)))
//					.add(Restrictions.conjunction().add(Restrictions.isNotNull("lastEffectiveDate"))
//							.add(Restrictions.lt("lastEffectiveDate", date))));
//		}
//		
//		criteria.setProjection(Projections.rowCount());
//		resultMap.put("filteredCount", criteria.uniqueResult());
//
//		criteria.setProjection(Projections.projectionList().add(Projections.property("productId"), "productId")
//				.add(Projections.property("productName"), "productName")
//				.add(Projections.property("packQuantity"), "packQuantity")
//				.add(Projections.property("productPrice"), "productPrice")
//				.add(Projections.property("currentStockPackageCount"), "currentStockPackageCount")
//				.add(Projections.property("effectiveDate"), "effectiveDate")
//				.add(Projections.property("lastEffectiveDate"), "lastEffectiveDate")
//				.add(Projections.property("oldproduct.productId"), "oldProductId"))
//				.setResultTransformer(Transformers.aliasToBean(ProductDTO.class));
//
//		resultMap.put("data", criteria.list());
//
//		return resultMap;
//
//	}
}
