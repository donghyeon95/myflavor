package com.myflavor.myflavor.domain.feed.DTO.mapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class StringToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return LocalDateTime.parse(p.getText(), FORMATTER); // "yyyy-MM-dd HH:mm:ss" 형태의 String을 LocalDateTime으로 변환
	}
}