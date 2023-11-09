package com.supermarket.util.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.supermarket.util.ValidationUtil;

public class DateDeserializer extends JsonDeserializer<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm a"); // HH:mm:ss

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String date = p.getText();
		if (ValidationUtil.isValidDate(date)) {
			try {
				return dateFormat.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
