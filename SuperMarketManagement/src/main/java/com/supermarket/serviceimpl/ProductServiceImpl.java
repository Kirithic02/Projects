package com.supermarket.serviceimpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
import com.supermarket.model.custom.product.ProductSales;
import com.supermarket.model.custom.product.ProductSalesFilterList;
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
	public Response saveOrUpdate(List<ProductDTO> productDTOList) {

		Response response = new Response();

		List<ErrorResponse> duplicateResponseList = new ArrayList<ErrorResponse>();

		for (int i = 0; i < productDTOList.size(); i++) {

			ProductDTO productDTO = productDTOList.get(i);

			List<ErrorResponse> errorResponseList = productValidation(productDTO);

			if (!errorResponseList.isEmpty()) {

				response.setStatus(WebServiceUtil.FAILURE);
				response.setData(errorResponseList);
				return response;
			}

			for (int j = i + 1; j < productDTOList.size(); j++) {

				ProductDTO checkProductDTO = productDTOList.get(j);

				if (productDTO.getProductName().trim().equalsIgnoreCase(checkProductDTO.getProductName().trim())) {

					ErrorResponse duplicateResponse = new ErrorResponse();
					duplicateResponse.setFieldName(WebServiceUtil.PRODUCT_NAME);
					duplicateResponse.setErrorMessage("Product " + productDTO.getProductName() + " Is Duplicate");
					duplicateResponseList.add(duplicateResponse);
				}
			}

		}

		if (!duplicateResponseList.isEmpty()) {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(duplicateResponseList);
			return response;
		}

		for (ProductDTO productDTO : productDTOList) {

			LOGGER.info("Save or Update Product ID : " + productDTO.getProductId());
//			List<ErrorResponse> errorResponseList = productValidation(productDTO);

			if (productDTO.getProductId() == null) {
				Product product = new Product();
				product.setProductName(WebServiceUtil.formatFullName(productDTO.getProductName()).trim());
				product.setPackQuantity(productDTO.getPackQuantity());
				product.setProductPrice(productDTO.getProductPrice());
				product.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
				product.setProductCategory(productDTO.getProductCategory());
				product.setEffectiveDate(productDTO.getEffectiveDate());
				product.setCreatedDate(new Date());
				product.setUpdatedDate(new Date());

				productDAO.addProduct(product);

				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData("New Products Has Been Added");
			} else {
				Product oldProduct = productDAO.getProductById(productDTO.getProductId());

				if (oldProduct != null) {

					if (oldProduct.getEffectiveDate().after(new Date())) {

						oldProduct.setProductName(WebServiceUtil.formatFullName(productDTO.getProductName()).trim());
						oldProduct.setPackQuantity(productDTO.getPackQuantity());
						oldProduct.setProductPrice(productDTO.getProductPrice());
						oldProduct.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
						oldProduct.setProductCategory(productDTO.getProductCategory());
						oldProduct.setEffectiveDate(productDTO.getEffectiveDate());
						oldProduct.setUpdatedDate(new Date());

						response.setStatus(WebServiceUtil.SUCCESS);
						response.setData("Product details updated successfully.");
					} else {
						response.setStatus(WebServiceUtil.FAILURE);
						response.setData("Update Failed, " + productDTO.getProductName() + " is Effective For Sale.");
						return response;

//						ErrorResponse errorResponse = new ErrorResponse();
//						errorResponse.setFieldName(null);
					}
				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData(productDTO.getProductName() + " Not Found");
					return response;
				}
			}
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

					oldProduct.setLastEffectiveDate(currentDate);
					oldProduct.setUpdatedDate(new Date());

					Product newProduct = new Product();
					newProduct.setProductName(WebServiceUtil.formatFullName(productDTO.getProductName()).trim());
					newProduct.setPackQuantity(productDTO.getPackQuantity());
					newProduct.setProductPrice(productDTO.getProductPrice());
					newProduct.setCurrentStockPackageCount(productDTO.getCurrentStockPackageCount());
					newProduct.setProductCategory(productDTO.getProductCategory());
					newProduct.setEffectiveDate(productDTO.getEffectiveDate());
					newProduct.setOldProductId(oldProduct);
					newProduct.setCreatedDate(new Date());
					newProduct.setUpdatedDate(new Date());

					productDAO.addProduct(newProduct);

					response.setStatus(WebServiceUtil.SUCCESS);
					response.setData("New Product Details Are Updated");

				} else {
					response.setStatus(WebServiceUtil.FAILURE);
					response.setData("Product is not effective for sale");
				}
			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product Not Found");
			}
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		return response;
	}

	private List<ErrorResponse> productValidation(ProductDTO productDTO) {
		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()
				|| !ValidationUtil.isValidProductName(productDTO.getProductName())) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_NAME);
			errorResponse.setErrorMessage(
					"Product Name Should Only Contain Alphabets and Numbers for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);

		} else if (!productDAO.isUniqueProduct(productDTO.getProductId(), productDTO.getProductName())) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_NAME);
			errorResponse.setErrorMessage("Product " + productDTO.getProductName() + " Already Exist");
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getEffectiveDate() == null) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_EFFECTIVEDATE);
			errorResponse.setErrorMessage("Date is Invalid for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);

		} else if (productDTO.getEffectiveDate().before(new Date())) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_EFFECTIVEDATE);
			errorResponse.setErrorMessage("Date Should be after current date for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getPackQuantity() == null || productDTO.getPackQuantity() <= 0) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_PACKQUANTITY);
			errorResponse.setErrorMessage("Pack Quantity Should be greater than 0 for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getCurrentStockPackageCount() == null || productDTO.getCurrentStockPackageCount() <= 0) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_CURRENTSTOCKPACKAGECOUNT);
			errorResponse.setErrorMessage(
					"Current Stock Package Count Should be greater than 0 for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getProductCategory() == null || productDTO.getProductCategory().trim().isEmpty()
				|| !(WebServiceUtil.PRODUCT_CATEGORIES
						.contains(productDTO.getProductCategory().trim().toUpperCase()))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_CATEGORY);
			errorResponse.setErrorMessage("Invalid Product Category for " + productDTO.getProductName());
			errorResponseList.add(errorResponse);
		}

		if (productDTO.getProductPrice() == null || productDTO.getProductPrice() <= 0) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_PRICE);
			errorResponse.setErrorMessage("Product Price Should be greater than 0 for " + productDTO.getProductName());
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
				response.setData("Product Not Found");
			}
		} else {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData("productId Should not be null");
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

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();
		Response response = new Response();

		if (productId == null) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_ID);
			errorResponse.setErrorMessage("Product ID Should not be null");
			errorResponseList.add(errorResponse);
		} else {

			ProductDTO productDTO = productDAO.getProductDTOById(productId);
			if (productDTO != null) {
				response.setStatus(WebServiceUtil.SUCCESS);
				response.setData(productDTO);
			} else {
				response.setStatus(WebServiceUtil.FAILURE);
				response.setData("Product Not Found");
			}
		}

		if (!errorResponseList.isEmpty()) {
			response.setStatus(WebServiceUtil.FAILURE);
			response.setData(errorResponseList);
		}

		return response;
	}

	/**
	 * Retrieves Product List
	 * 
	 * @param productFilterList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public FilteredResponse listProduct(ProductFilterList productFilterList) {

		LOGGER.info("View Product List");

		List<ErrorResponse> errorResponseList = productFilterListValidation(productFilterList);
		FilteredResponse filterResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = productDAO.listProducts(productFilterList);

			List<ProductDTO> transactionDetails = (List<ProductDTO>) resultMap.get("data");

			if (productFilterList.getOrderBy().getColumn() != null && productFilterList.getOrderBy().getColumn().equalsIgnoreCase("serialNumber")
					&& productFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
				Collections.reverse(transactionDetails);
				for (Integer i = transactionDetails.size() - 1; i >= 0; i--) {
					transactionDetails.get(i)
							.setSerialNumber(productFilterList.getStart() + transactionDetails.size() - i);
				}
			} else {
				for (Integer i = 0; i < transactionDetails.size(); i++) {
					transactionDetails.get(i).setSerialNumber(productFilterList.getStart() + i + 1);
				}
			}

			filterResponse.setStatus(WebServiceUtil.SUCCESS);
			filterResponse.setRecordsTotal((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSTOTAL));
			filterResponse.setRecordsFiltered((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSFILTERED));
			filterResponse.setData(transactionDetails);

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
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_LENGTH);
			errorResponse.setErrorMessage("Length Should be greater than 0 and Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (productFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_START);
			errorResponse.setErrorMessage("Start Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (!(productFilterList.getFilter().getStatus() == null
				|| productFilterList.getFilter().getStatus().trim().isEmpty()
				|| productFilterList.getFilter().getStatus().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_ACTIVE)
				|| productFilterList.getFilter().getStatus().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_INACTIVE)
				|| productFilterList.getFilter().getStatus().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_UPCOMING)
				|| productFilterList.getFilter().getStatus().trim()
						.equalsIgnoreCase(WebServiceUtil.PRODUCT_STATUS_STOCKUNAVAILABLE))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_STATUS);
			errorResponse.setErrorMessage("Status Should Contain only active (or) inactive (or) upcoming");
			errorResponseList.add(errorResponse);
		}

		if (!(productFilterList.getFilter().getCategory() == null
				|| productFilterList.getFilter().getCategory().trim().isEmpty() || WebServiceUtil.PRODUCT_CATEGORIES
						.contains(productFilterList.getFilter().getCategory().trim().toUpperCase()))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_CATEGORY);
			errorResponse.setErrorMessage("Inavlid Category");
			errorResponseList.add(errorResponse);
		}

		if (!(productFilterList.getSearchColumn() == null || productFilterList.getSearchColumn().trim().isEmpty()
				|| productFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_ID)
				|| productFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_SEARCHCOLUMN);
			errorResponse.setErrorMessage("searchColumn Should Contain only productid (or) productname");
			errorResponseList.add(errorResponse);

		}

//		if (productFilterList.getOrderBy() != null
//				&& ValidationUtil.isNotEmpty(productFilterList.getOrderBy().getType())
//				&& ValidationUtil.isNotEmpty(productFilterList.getOrderBy().getColumn())) {
//			
//			if (!(productFilterList.getOrderBy().getColumn().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME)
//					|| productFilterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_PRICE)
//					|| productFilterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_CURRENTSTOCKPACKAGECOUNT)
//					|| productFilterList.getOrderBy().getColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_EFFECTIVEDATE))) {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_COLUMN);
//				errorResponse.setErrorMessage("column Should Contain Only productname (or) productprice (or) CurrentStockPackageCount (or) effectivedate (or) null");
//				errorResponseList.add(errorResponse);
//			}
//			
//			if( !(productFilterList.getOrderBy().getType().trim().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_ASC)
//					|| productFilterList.getOrderBy().getType().trim().equalsIgnoreCase(WebServiceUtil.FILTERLIST_ORDERBY_TYPE_DESC)) ) {
//				ErrorResponse errorResponse = new ErrorResponse();
//				errorResponse.setFieldName(WebServiceUtil.FILTERLIST_ORDERBY_TYPE);
//				errorResponse.setErrorMessage("type Should Contain Only asc (or) desc (or) null");
//				errorResponseList.add(errorResponse);
//			}
//		}

		return errorResponseList;
	}

	/**
	 * Retrieves Product Sales Report List
	 * 
	 * @param productSalesFilterList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public FilteredResponse listProductSales(ProductSalesFilterList productSalesFilterList) {

		List<ErrorResponse> errorResponseList = productSalesFilterListValidation(productSalesFilterList);
		FilteredResponse filterResponse = new FilteredResponse();

		if (errorResponseList.isEmpty()) {

			Map<String, Object> resultMap = productDAO.listProductsSales(productSalesFilterList);

			List<ProductSales> transactionDetails = (List<ProductSales>) resultMap.get("data");

			if (productSalesFilterList.getOrderBy().getColumn() != null && productSalesFilterList.getOrderBy().getColumn().equalsIgnoreCase("serialNumber")
					&& productSalesFilterList.getOrderBy().getType().equalsIgnoreCase("desc")) {
				Collections.reverse(transactionDetails);
				for (Integer i = transactionDetails.size() - 1; i >= 0; i--) {
					transactionDetails.get(i)
							.setSerialNumber(productSalesFilterList.getStart() + transactionDetails.size() - i);
				}
			} else {
				for (Integer i = 0; i < transactionDetails.size(); i++) {
					transactionDetails.get(i).setSerialNumber(productSalesFilterList.getStart() + i + 1);
				}
			}

			filterResponse.setStatus(WebServiceUtil.SUCCESS);
			filterResponse.setRecordsTotal((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSTOTAL));
			filterResponse.setRecordsFiltered((Long) resultMap.get(WebServiceUtil.FILTEREDRESPONSE_RECORDSFILTERED));
			filterResponse.setData(transactionDetails);

		} else {
			filterResponse.setStatus(WebServiceUtil.FAILURE);
			filterResponse.setData(errorResponseList);
		}

		return filterResponse;
	}

	private List<ErrorResponse> productSalesFilterListValidation(ProductSalesFilterList productSalesFilterList) {

		List<ErrorResponse> errorResponseList = new ArrayList<ErrorResponse>();

		if (productSalesFilterList.getLength() == null || productSalesFilterList.getLength() < 1) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_LENGTH);
			errorResponse.setErrorMessage("Length Should be greater than 0 and Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (productSalesFilterList.getStart() == null) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_START);
			errorResponse.setErrorMessage("Start Should not be null");
			errorResponseList.add(errorResponse);
		}

		if (!(productSalesFilterList.getFilter().getProductCategory() == null
				|| productSalesFilterList.getFilter().getProductCategory().trim().isEmpty()
				|| WebServiceUtil.PRODUCT_CATEGORIES
						.contains(productSalesFilterList.getFilter().getProductCategory().trim().toUpperCase()))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.PRODUCT_CATEGORY);
			errorResponse.setErrorMessage("Inavlid Category");
			errorResponseList.add(errorResponse);
		}

		if (!(productSalesFilterList.getSearchColumn() == null
				|| productSalesFilterList.getSearchColumn().trim().isEmpty()
				|| productSalesFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_ID)
				|| productSalesFilterList.getSearchColumn().trim().equalsIgnoreCase(WebServiceUtil.PRODUCT_NAME))) {

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setFieldName(WebServiceUtil.FILTERLIST_SEARCHCOLUMN);
			errorResponse.setErrorMessage("searchColumn Should Contain only productid (or) productname");
			errorResponseList.add(errorResponse);

		}

		return errorResponseList;
	}

}
