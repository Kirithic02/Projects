package com.supermarket.service;

import java.util.List;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductSalesFilterList;
import com.supermarket.model.custom.product.ProductDTO;

public interface ProductService {

	Response getProductDTOById(Integer productId);

	FilteredResponse listProduct(ProductFilterList filterList);

	Response saveOrUpdate(List<ProductDTO> productDTOList);

	Response deactivateAndUpdate(ProductDTO productDTO);

	Response priceHistory(Integer productId);

	FilteredResponse listProductSales(ProductSalesFilterList productFilterList);

//	FilteredResponse listActiveProducts(Date date, Integer page, Integer limit);
//	
//	FilteredResponse listInActiveProducts(Date date, Integer page, Integer limit);
}
