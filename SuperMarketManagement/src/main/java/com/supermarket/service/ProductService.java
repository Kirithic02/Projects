package com.supermarket.service;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductDTO;

public interface ProductService {

	Response getProductDTOById(Integer productId);

	FilteredResponse listProduct(ProductFilterList filterList);

	Response saveOrUpdate(ProductDTO productDTO);

	Response deactivateAndUpdate(ProductDTO productDTO);

	Response priceHistory(Integer productId);

//	FilteredResponse listActiveProducts(Date date, Integer page, Integer limit);
//	
//	FilteredResponse listInActiveProducts(Date date, Integer page, Integer limit);
}
