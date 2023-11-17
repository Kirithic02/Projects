package com.supermarket.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supermarket.dao.ProductDAO;
import com.supermarket.model.custom.ErrorResponse;
import com.supermarket.model.custom.FilteredResponse;
import com.supermarket.model.custom.Response;
import com.supermarket.model.custom.product.ProductFilterList;
import com.supermarket.model.custom.product.PriceHistoryDTO;
import com.supermarket.model.custom.product.ProductDTO;
import com.supermarket.model.entity.Product;
import com.supermarket.service.ProductService;
import com.supermarket.util.ValidationUtil;
import com.supermarket.util.WebServiceUtil;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

	/**
	 * Instance of {@link ProductDAOImpl}
	 */
	@Autowired
	private ProductDAO productDAO;

	/**
	 * save or update product
	 * 
	 * @param productDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response saveOrUpdate(ProductDTO productDTO) {

		LOGGER.info("Save or Update Product ID : " + productDTO.getProductId());

		Response response = new Response();

		List<ErrorResponse> errorResponseList = productValidation(productDTO);
		if (errorResponseList.isEmpty()) {

			if (productDAO.isUniqueProduct(productDTO.getProductId(), productDTO.getProductName())) {

				if (productDTO.getProductId() == null) {
					Product product = new Product();
					product.setProductName(productDTO.getProductName().trim());
					product.setPackQuantity(productDTO.getPackQuantity());
					product.setProductPrice(productDTO.getProductPrice());
					product.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
					product.setEffectiveDate(productDTO.getEffectiveDate());
					product.setCreatedDate(new Date());
					product.setUpdatedDate(new Date());

					response.setStatus(WebServiceUtil.SUCCESS);
					response.setData("New Product Has Been Added, ID: " + productDAO.addProduct(product));
				} else {
					Product oldProduct = productDAO.getProductById(productDTO.getProductId());

					if (oldProduct != null) {

						if (oldProduct.getEffectiveDate().after(new Date())) {
							oldProduct.setProductName(productDTO.getProductName().trim());
							oldProduct.setPackQuantity(productDTO.getPackQuantity());
							oldProduct.setProductPrice(productDTO.getProductPrice());
							oldProduct.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
							oldProduct.setEffectiveDate(productDTO.getEffectiveDate());
							oldProduct.setUpdatedDate(new Date());

							response.setStatus(WebServiceUtil.SUCCESS);
							response.setData("Product details updated successfully.");
						} else {
							response.setStatus(WebServiceUtil.FAILURE);
							response.setData("Update Failed, Product is Effective For Sale.");
						}
					} else {
						response.setStatus(WebServiceUtil.FAILURE);
						response.setData("Product ID " + productDTO.getProductId() + " Not Found");
					}
				}

			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product Name Already Exist");
			}

		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		return response;
	}

	/**
	 * deactivate And Update Product
	 * 
	 * @param productDTO
	 * @return
	 */
	@Override
	@Transactional
	public Response deactivateAndUpdate(ProductDTO productDTO) {

		LOGGER.info("Deactivate and Update details for Product ID : " + productDTO.getProductId());
		
		Response response = new Response();

		List<ErrorResponse> errorResponseList = productValidation(productDTO);
		if (errorResponseList.isEmpty()) {

			Date currentDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
			Product oldProduct = productDAO.getProductById(productDTO.getProductId());

			if (oldProduct != null) {
				if (currentDate.after(oldProduct.getEffectiveDate()) && (oldProduct.getLastEffectiveDate()) == null) {

					if (productDAO.isUniqueProduct(productDTO.getProductId(), productDTO.getProductName())) {

						oldProduct.setLastEffectiveDate(currentDate);
						oldProduct.setUpdatedDate(new Date());

						Product newProduct = new Product();
						newProduct.setProductName(productDTO.getProductName().trim());
						newProduct.setPackQuantity(productDTO.getPackQuantity());
						newProduct.setProductPrice(productDTO.getProductPrice());
						newProduct.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
						newProduct.setEffectiveDate(productDTO.getEffectiveDate());
						newProduct.setOldProductId(oldProduct);
						newProduct.setCreatedDate(new Date());
						newProduct.setUpdatedDate(new Date());

						response.setStatus(WebServiceUtil.SUCCESS);
						response.setData(
								"Product Details Are Updated, New Product ID: " + productDAO.addProduct(newProduct));
					} else {
						response.setStatus(WebServiceUtil.SUCCESS);
						response.setData("Product Name Already Exist");
					}
				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData("Product is not effective for sale");
				}
			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product ID " + productDTO.getProductId() + " Not Found");
			}
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		return response;
	}

	private List<ErrorResponse> productValidation(ProductDTO productDTO) {
		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (productDTO.getProductName() == null || productDTO.getProductName().isBlank()
				|| !ValidationUtil.isValidName(productDTO.getProductName())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("productName");
			errorResponse.setErrorMessage("Product Name Should Only Contain Alphabets and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getEffectiveDate() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("effectiveDate");
			errorResponse.setErrorMessage("Date is Invalid");
			errorResponseList.add(errorResponse);
		} else if (productDTO.getEffectiveDate().before(new Date())) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("effectiveDate");
			errorResponse.setErrorMessage("Date Should be after current date");
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getPackQuantity() == null || productDTO.getPackQuantity() <= 0) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("packQuantity");
			errorResponse.setErrorMessage("Pack Quantity Should be greater than 0 and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getCurrentStockPackageCount() == null || productDTO.getCurrentStockPackageCount() <= 0) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("currentStockPackageCount");
			errorResponse
					.setErrorMessage("Current Stock Package Count Should be greater than 0 and Should Not be Null");
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getProductPrice() == null || productDTO.getProductPrice() <= 0) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("productPrice");
			errorResponse.setErrorMessage("Product Price Should be greater than 0 and Should Not be Null");
			errorResponseList.add(errorResponse);
		}
		return errorResponseList;
	}

	/**
	 * Retrieves Price History of an Active Product
	 * 
	 * @param productId
	 * @return
	 */
	@Override
	@Transactional
	public Response priceHistory(Integer productId) {

		LOGGER.info("View Price History for Product ID : " + productId);

		Response response = new Response();

		if (productId != null) {

			Product product = productDAO.getProductById(productId);
			List<PriceHistoryDTO> priceList = new ArrayList<PriceHistoryDTO>();

			if (product != null) {
				if ((product.getLastEffectiveDate() == null)) {

					while (true) {
						PriceHistoryDTO priceHistoryDTO = new PriceHistoryDTO();
						priceHistoryDTO.setProductPrice(product.getProductPrice());
						priceHistoryDTO.setEffectiveDate(product.getEffectiveDate());
						priceList.add(priceHistoryDTO);

						if (product.getOldProductId() == null) {
							break;
						}
						product = product.getOldProductId();
					}

					response.setStatus(WebServiceUtil.SUCCESS);
					response.setData(priceList);
				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData("Product is not Effective for sale");
				}
			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product ID " + productId + " Not Found");
			}
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData("productId Should not be Null");
		}

		return response;
	}

	/**
	 * Retrieves Product Details using Product Id
	 * 
	 * @param productId
	 * @return
	 */
	@Override
	@Transactional
	public Response getProductDTOById(Integer productId) {

		LOGGER.info("View Details of Product ID : " + productId);

		Response response = new Response();

		if (productId == null) {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData("Product ID Should not be Null");
		} else {

			ProductDTO productDTO = productDAO.getProductDTOById(productId);
			if (productDTO != null) {
				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData(productDTO);
			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product ID " + productId + " Not Found");
			}
		}
		return response;
	}

	/**
	 * Retrieves Product List
	 * 
	 * @return
	 */
	@Override
	@Transactional
	public FilteredResponse listProduct(ProductFilterList productFilterList) {

		LOGGER.info("View Product List");

		List<ErrorResponse> errorResponseList = productFilterListValidation(productFilterList);
		FilteredResponse filterResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = productDAO.listProducts(productFilterList);

			if ((Long) resultMap.get("filteredCount") > 0) {
				filterResponse.setStatus(WebServiceUtil.SUCCESS);
				filterResponse.setTotalCount((Long) resultMap.get("totalCount"));
				filterResponse.setFilteredCount((Long) resultMap.get("filteredCount"));
				filterResponse.setData(resultMap.get("data"));
			} else {
				filterResponse.setStatus(WebServiceUtil.SUCCESS);
				filterResponse.setTotalCount((Long) resultMap.get("totalCount"));
				filterResponse.setFilteredCount((Long) resultMap.get("filteredCount"));
				filterResponse.setData(resultMap.get("No Matching Records Found"));
			}
		} else {
			filterResponse.setStatus(WebServiceUtil.FAILURE);
			filterResponse.setData(errorResponseList);
		}

		return filterResponse;
	}

	private List<ErrorResponse> productFilterListValidation(ProductFilterList productFilterList) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (productFilterList.getLength() == null || productFilterList.getLength() < 1) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("length");
			errorResponse.setErrorMessage("Length Should be greater than 0 and Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if (productFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("start");
			errorResponse.setErrorMessage("Start Should not be Null");
			errorResponseList.add(errorResponse);
		}

		if ( !(productFilterList.getFilter().getStatus() == null || productFilterList.getFilter().getStatus().isBlank()
				|| productFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.ACTIVE)
				|| productFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.INACTIVE)
				|| productFilterList.getFilter().getStatus().equalsIgnoreCase(WebServiceUtil.UPCOMING))) {
			
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("status");
			errorResponse.setErrorMessage("Status Should Contain only ACTIVE (or) INACTIVE (or) UPCOMING");
			errorResponseList.add(errorResponse);
		}

		if ( !(productFilterList.getSearchColumn() == null || productFilterList.getSearchColumn().isBlank()
				|| productFilterList.getSearchColumn().equals("productName")) ) {
			
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName("searchColumn");
			errorResponse.setErrorMessage("searchColumn Should Contain only PRODUCTNAME");
			errorResponseList.add(errorResponse);
		} 
		
		if (productFilterList.getOrderBy() != null
				&& ValidationUtil.isNotEmpty(productFilterList.getOrderBy().getType())
				&& ValidationUtil.isNotEmpty(productFilterList.getOrderBy().getColumn())) {
			
			if (!(productFilterList.getOrderBy().getColumn().equalsIgnoreCase("productname")
					|| productFilterList.getOrderBy().getColumn().equalsIgnoreCase("productprice")
					|| productFilterList.getOrderBy().getColumn().equalsIgnoreCase("availablestock")
					|| productFilterList.getOrderBy().getColumn().equalsIgnoreCase("effectivedate"))) {
				ErrorResponse errorResponse = new ErrorResponse();
				errorResponse.setFieldName("column");
				errorResponse.setErrorMessage("column Should Contain Only PRODUCTNAME (or) PRODUCTPRICE (or) AVAILABLESTOCK (or) EFFECTIVEDATE (or) NULL");
				errorResponseList.add(errorResponse);
			}
		}

		return errorResponseList;
	}

}
