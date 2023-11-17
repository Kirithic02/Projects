package com.supermarket.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.ProductDTO;
import com.supermarket.service.ProductService;

@RestController
@RequestMapping(value = "/product")
public class ProductRestController {

	/**
	 * Instance of {@link ProductServiceImpl}
	 */
	@Autowired
	private ProductService productService;

	/**
	 * save or update product
	 * 
	 * @param productDTO
	 * @return
	 */
	@RequestMapping(value = "/saveorupdate", method = RequestMethod.POST)
	public ResponseEntity<Response> saveOrUpdate(@RequestBody ProductDTO productDTO) {
		return new ResponseEntity<>(productService.saveOrUpdate(productDTO), HttpStatus.OK);
	}

	/**
	 * deactivate And Update Product
	 * 
	 * @param productDTO
	 * @return
	 */
	@RequestMapping(value = "/deactivateAndUpdate", method = RequestMethod.POST)
	public ResponseEntity<Response> deactivateAndUpdate(@RequestBody ProductDTO productDTO) {
		return new ResponseEntity<>(productService.deactivateAndUpdate(productDTO), HttpStatus.OK);
	}

	/**
	 * Retrieves Price History of an Active Product
	 * 
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/priceHistory", method = RequestMethod.GET)
	public ResponseEntity<Response> updateStatus(@RequestParam Integer productId) {
		return new ResponseEntity<Response>(productService.priceHistory(productId), HttpStatus.OK);
	}

	/**
	 * Retrieves Product Details using Product Id
	 * 
	 * @param productId
	 * @return
	 */
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ResponseEntity<Response> showProduct(@RequestParam Integer productId) {
		return new ResponseEntity<Response>(productService.getProductDTOById(productId), HttpStatus.OK);
	}

	/**
	 * Retrieves Product List
	 * 
	 * @param filterListDto
	 * @return
	 */
	@RequestMapping(value = "/listProducts", method = RequestMethod.POST)
	public ResponseEntity<FilteredResponse> listProduct(@RequestBody ProductFilterList filterListDto) {
		return new ResponseEntity<>(productService.listProduct(filterListDto), HttpStatus.OK);
	}

}
