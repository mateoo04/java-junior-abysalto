package hr.abysalto.hiring.api.junior.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:buyer-controller-tests")
@AutoConfigureMockMvc
class BuyerControllerTests {

	private static final String AUTH_HEADER = basicAuthHeader();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getOrderDefaultsReturnsLatestBuyerOrderDetails() throws Exception {
		this.mockMvc.perform(get("/api/buyers/1/order-defaults")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.contactNumber").value("+385911111111"))
				.andExpect(jsonPath("$.paymentOption").value("CASH"))
				.andExpect(jsonPath("$.currency").value("EUR"))
				.andExpect(jsonPath("$.deliveryAddress.city").value("Zagreb"))
				.andExpect(jsonPath("$.deliveryAddress.street").value("Ilica"))
				.andExpect(jsonPath("$.deliveryAddress.homeNumber").value("15"));
	}

	@Test
	void getOrderDefaultsReturnsNoContentWhenBuyerHasNoOrders() throws Exception {
		this.mockMvc.perform(get("/api/buyers/3/order-defaults")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
				.andExpect(status().isNoContent());
	}

	private static String basicAuthHeader() {
		String credentials = "user:password";
		String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encoded;
	}
}
