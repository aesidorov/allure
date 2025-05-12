package tests.users;

import com.fasterxml.jackson.annotation.JsonProperty;
 import lombok.*;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Address{

	@JsonProperty("zipcode")
	private String zipcode;

	@JsonProperty("number")
	private int number;

	@JsonProperty("city")
	private String city;

	@JsonProperty("street")
	private String street;

	@JsonProperty("geolocation")
	private Geolocation geolocation;

}