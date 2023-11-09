package com.supermarket.util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampSerializer extends JsonSerializer<Timestamp> {
	@Override
	public void serialize(Timestamp timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException {
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		String formattedDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
		jsonGenerator.writeString(formattedDateTime);
	}
}

//MM-dd-yyyy hh:mm a