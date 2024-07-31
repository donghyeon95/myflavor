package com.myflavor.myflavor.domain.restaurant.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
	Double lat; // 위도
	Double lon; // 경도
}