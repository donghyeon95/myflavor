package com.myflavor.myflavor.domain.restaurant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long pk;

	@Column(unique = true)
	String categoryName;

	@Version
	private Long version;
}
