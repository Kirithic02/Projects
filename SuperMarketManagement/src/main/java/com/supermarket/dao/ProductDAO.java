package com.supermarket.dao;

import java.util.Map;

import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductSalesFilterList;
import com.supermarket.model.custom.product.ProductDTO;
import com.supermarket.model.entity.Product;

public interface ProductDAO {

	int addProduct(Product product);

	Product getProductById(int productId);

	Map<String, Object> listProducts(ProductFilterList filterList);

//	Map<String, Object> listActiveProducts(Date date, Integer page, Integer limit);
//	
//	Map<String, Object> listInActiveProducts(Date date);

	ProductDTO getProductDTOById(int productId);

	boolean isUniqueProduct(Integer productId, String productName);

	Map<String, Object> listProductsSales(ProductSalesFilterList productSalesFilterList);
}
