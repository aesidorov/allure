package tests.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.testng.annotations.DataProvider;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class StructResponseUser {

	@JsonProperty("password")
	private String password;

	@JsonProperty("address")
	private Address address;

	@JsonProperty("phone")
	private String phone;

	@JsonProperty("__v")
	private int v;

	@JsonProperty("name")
	private Name name;

	@JsonProperty("id")
	private int id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("username")
	private String username;

}