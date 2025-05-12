package tests.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Geolocation{

	@JsonProperty("lat")
	private String lat;

	@JsonProperty("long")
	private String jsonMemberLong;

	public String getLat(){
		return lat;
	}

	public String getJsonMemberLong(){
		return jsonMemberLong;
	}
}