package com.supermarket.util.deserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class DateWithoutTimeDeserializer extends JsonDeserializer<Date> 
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
	{
		String date = p.getText();
		try
		{
            return DATE_FORMAT.parse(date);
        } 
		catch (ParseException e) 
		{
            throw new IOException("Error parsing date: " + date, e);
        }
	}
}
