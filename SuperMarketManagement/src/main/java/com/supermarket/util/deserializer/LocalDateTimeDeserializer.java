package com.supermarket.util.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
	@Override
	public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
			throws IOException {
		String date = jsonParser.getText();
		return LocalDateTime.parse(date, DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));
	}
}
