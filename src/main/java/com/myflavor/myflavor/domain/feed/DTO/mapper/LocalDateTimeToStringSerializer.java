package com.myflavor.myflavor.domain.feed.DTO.mapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class LocalDateTimeToStringSerializer extends JsonSerializer<LocalDateTime> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(FORMATTER));  // LocalDateTime을 "yyyy-MM-dd HH:mm:ss" 형식의 String으로 변환
	}
}