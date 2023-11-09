package com.supermarket.util.serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.JsonSerializer;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

	@Override
	public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializers)
			throws IOException {
		String formattedDate = value.format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a"));
		jsonGenerator.writeString(formattedDate);
	}
}
