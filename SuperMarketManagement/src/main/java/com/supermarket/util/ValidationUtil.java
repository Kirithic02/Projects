package com.supermarket.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
	
	public static boolean isNotEmpty(String field) {
		return ((field != null) && (field.trim().length() > 0));
	}
	
	public static boolean isValidName(String name) {
		Pattern pattern = Pattern.compile("^[A-Za-z\\s]+$");
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	public static boolean isValidEmail(String email) {
//		Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
//		Pattern pattern = Pattern.compile("^[A-Za-z0-9]+[_.-]*[A-Za-z0-9]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
		Pattern pattern = Pattern.compile("^[A-Za-z]+[_.-]{0,1}[A-Za-z0-9]*@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$");
//		public static final String REGEX_EMAIL = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		Pattern pattern = Pattern.compile("^\\d{10}$");
		Matcher matcher = pattern.matcher(phoneNumber);
		return matcher.matches();
	}
	
	public static boolean isValidNumber(String Number) {
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(Number);
		return matcher.matches();
	}

	public static boolean isValidAddressLine(String addressLine) {
		if (addressLine == null || addressLine.isEmpty()) {
			return false;
		}
		return addressLine.matches("^[a-zA-Z0-9\\s,.\\/-]+$");
	}

	public static boolean isValidPincode(String pincode) {
		Pattern pattern = Pattern.compile("^\\d{6}$");
		Matcher matcher = pattern.matcher(pincode);
		return matcher.matches();
	}
	
	public static boolean isValidProductName(String productName) {
		Pattern pattern = Pattern.compile("^[A-Za-z\\s]+[\\d\\s]*[A-Za-z]*$");
		Matcher matcher = pattern.matcher(productName);
		return matcher.matches();
	}

	public static boolean isValidStockQuantity(String quantity) {
		Pattern pattern = Pattern.compile("^[1-9]+$");
		Matcher matcher = pattern.matcher(quantity);
		return matcher.matches();
	}

	public static boolean isValidDate(String date) {
		Pattern pattern = Pattern
				.compile("^(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])-[0-9]{4} (0[1-9]|1[0-2]):([0-5][0-9]) (am|pm)$");
		Matcher matcher = pattern.matcher(date);

		if (matcher.matches()) {
			int month = Integer.parseInt(date.substring(0, 2));
			int day = Integer.parseInt(date.substring(3, 5));
			int year = Integer.parseInt(date.substring(6, 10));

			if (month == 2) { // February
				if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
					// Leap year, February - 29 days
					if (day >= 1 && day <= 29) {
						return true;
					}
				} else {
					// Not a leap year, February - 28 days
					if (day >= 1 && day <= 28) {
						return true;
					}
				}
			} else if ((month == 4 || month == 6 || month == 9 || month == 11) && day <= 30) {
				// Months with 30 days
				return true;
			} else if (day >= 1 && day <= 31) {
				// All other months with 31 days
				return true;
			}
		}

		return false;
	}

}
