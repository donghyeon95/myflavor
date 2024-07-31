package com.myflavor.myflavor.domain.restaurant.DTO;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import org.locationtech.proj4j.*;

import java.io.IOException;
import java.util.Base64;

public class LocationDeserializer extends JsonDeserializer<RestaurantDTO> {

	@Override
	public RestaurantDTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws
			IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		String manageCode = node.get("MGTNO").asText() + "-" + node.get("OPNSFTEAMCODE").asText(); // 지자체 번호
		String restName = node.get("BPLCNM").asText(); // 레스토랑 이름
		Double Longitude = node.get("X").asDouble(); // 레스토랑 동부원점 X좌표
		Double Latitude = node.get("Y").asDouble(); // 레스토랑 동부원점 Y좌표
		ProjCoordinate wgs84Coord = transform(Longitude, Latitude);

		return RestaurantDTO.builder()
				.name(restName)
				.location(new Location(wgs84Coord.y, wgs84Coord.x))
				.street_address(node.get("RDNWHLADDR").asText())
				.street_number(node.get("RDNPOSTNO").asText())
				.status(node.get("TRDSTATENM").asText())
				.restaurant_category_id(new RestaurantCategoryId(node.get("UPTAENM").asText()))
				// 지자체 번호와 관리번호를 통해 고유값 생성
				.managementNumber(Base64.getEncoder().encodeToString(manageCode.getBytes()))
				.build();
	}

	public ProjCoordinate transform(Double Longitude, Double Latitude) {
		// 좌표계 변환
		CRSFactory crsFactory = new CRSFactory();

		// Bessel 좌표계를 파라미터로 정의합니다. (예: UTM Zone 52N)
		String tmParams = "+proj=tmerc +lat_0=38 +lon_0=127.0028902777778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +towgs84=-115.80,474.99,674.11,1.16,-2.31,-1.63,6.43 +units=m +no_defs";
		CoordinateReferenceSystem tmCrs = crsFactory.createFromParameters("TMEastOrigin", tmParams);

		// WGS 84 좌표계를 파라미터로 정의합니다.
		String wgs84Params = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
		CoordinateReferenceSystem wgs84Crs = crsFactory.createFromParameters("WGS84", wgs84Params);

		// CoordinateTransformFactory를 생성합니다.
		CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
		CoordinateTransform transform = ctFactory.createTransform(tmCrs, wgs84Crs);

		// TM 좌표 (예: x=123456.78, y=987654.32)
		ProjCoordinate tmCoord = new ProjCoordinate(Longitude, Latitude);
		ProjCoordinate wgs84Coord = new ProjCoordinate();

		transform.transform(tmCoord, wgs84Coord);

		return wgs84Coord;
	}
}
