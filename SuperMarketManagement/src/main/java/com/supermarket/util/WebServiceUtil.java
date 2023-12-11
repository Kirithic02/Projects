package com.supermarket.util;

public final class WebServiceUtil {

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";

	// *********************** FilteredResponse *****************************
	public static final String FILTEREDRESPONSE_FILTEREDCOUNT = "filteredCount";
	public static final String FILTEREDRESPONSE_TOTALCOUNT = "totalCount";
	public static final String FILTEREDRESPONSE_DATA = "data";

	// *********************** FilterList *****************************
	public static final String FILTERLIST_LENGTH = "length";
	public static final String FILTERLIST_START = "start";
	public static final String FILTERLIST_SEARCHCOLUMN = "searchColumn";
	public static final String FILTERLIST_ORDERBY_COLUMN = "column";
	public static final String FILTERLIST_ORDERBY_TYPE = "type";
	public static final String FILTERLIST_ORDERBY_TYPE_ASC = "asc";
	public static final String FILTERLIST_ORDERBY_TYPE_DESC = "desc";

	// *********************** order Status *****************************
	public static final String NEW = "NEW";
	public static final String PACKED = "PACKED";
	public static final String SHIPPED = "SHIPPED";
	public static final String DELIVERED = "DELIVERED";
	public static final String CANCELLED = "CANCELLED";

	// *********************** Product Status *****************************
	public static final String PRODUCT_STATUS = "status";
	public static final String PRODUCT_STATUS_ACTIVE = "ACTIVE";
	public static final String PRODUCT_STATUS_INACTIVE = "INACTIVE";
	public static final String PRODUCT_STATUS_UPCOMING = "UPCOMING";

	// *********************** Customer *****************************
	public static final String CUSTOMER_ID = "customerId";
	public static final String CUSTOMER_NAME = "customerName";
	public static final String CUSTOMER_MOBILE_NUMBER = "mobileNo";
	public static final String CUSTOMER_ADDRESS = "address";
	public static final String CUSTOMER_LOCATION = "location";
	public static final String CUSTOMER_CITY = "city";
	public static final String CUSTOMER_PINCODE = "pincode";
	public static final String CUSTOMER_MAIL = "mail";
	public static final String CUSTOMER_CREATEDDATE = "createdDate";

	// *********************** Product *****************************
	public static final String PRODUCT_ID = "productId";
	public static final String PRODUCT_NAME = "productName";
	public static final String PRODUCT_PACKQUANTITY = "packQuantity";
	public static final String PRODUCT_PRICE = "productPrice";
	public static final String PRODUCT_CURRENTSTOCKPACKAGECOUNT = "currentStockPackageCount";
	public static final String PRODUCT_EFFECTIVEDATE = "effectiveDate";
//	public static final String PRODUCT_ = "";

	// *********************** OrderDetails *****************************
	public static final String ORDERDETAILS_ID = "orderId";
	public static final String ORDERDETAILS_ORDEREDDATE = "orderedDate";
	public static final String ORDERDETAILS_ORDEREXPECTEDDATE = "orderExpectedDate";
	public static final String ORDERDETAILS_STATUS = "orderStatus";
	public static final String ORDERDETAILS_ORDERCREATEDDATE = "orderCreatedDate";

	// *********************** OrderLineItemDetails *****************************
	public static final String ORDERLINEITEMDETAILS_OLIDID = "olidId";
	public static final String ORDERLINEITEMDETAILS_QUANTITYINDIVIDUALUNIT = "quantityIndividualUnit";
	public static final String ORDERLINEITEMDETAILS_QUANTITYINPACKAGE = "quantityInPackage";
	public static final String ORDERLINEITEMDETAILS_STATUS = "olidStatus";
	public static final String ORDERLINEITEMDETAILS_CREATEDDATE = "createdDate";

	public static String formatFullName(String fullName) {

		if (fullName == null || fullName.trim().length() <= 0) {
			return null;
		}

		String[] fullNameList = fullName.split("\\s");
		String formattedFullName = "";

		for (String splittedName : fullNameList) {
			if (splittedName.length() > 0) {
				formattedFullName += splittedName.substring(0, 1).toUpperCase()
						+ splittedName.substring(1).toLowerCase().concat(" ");
			}
		}

		return formattedFullName;
	}

}
