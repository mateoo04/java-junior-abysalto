package hr.abysalto.hiring.api.junior.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:order-controller-tests")
@AutoConfigureMockMvc
class OrderControllerTests {

	private static final String AUTH_HEADER = basicAuthHeader();

	@Autowired
	private MockMvc mockMvc;

	@Test
	void createOrderCalculatesTotalPrice() throws Exception {
		this.mockMvc.perform(post("/orders")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"buyerId": 1,
									"buyer": {
										"firstName": "Ivan",
										"lastName": "Horvat"
									},
									"paymentOption": "CASH",
									"deliveryAddress": {
										"city": "Zagreb",
										"street": "Ilica",
										"homeNumber": "15"
									},
									"contactNumber": "+385911111111",
									"note": "Test order",
									"currency": "EUR",
									"items": [
										{
											"menuItemId": 1,
											"quantity": 2
										},
										{
											"menuItemId": 2,
											"quantity": 1
										}
									]
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderStatus").value("WAITING_FOR_CONFIRMATION"))
				.andExpect(jsonPath("$.totalPrice").value(26.40))
				.andExpect(jsonPath("$.items.length()").value(2));
	}

	@Test
	void updateOrderStatus() throws Exception {
		this.mockMvc.perform(patch("/orders/1/status")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"orderStatus": "DONE"
								}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.orderStatus").value("DONE"));
	}

	@Test
	void listOrdersSortedByTotalPrice() throws Exception {
		this.mockMvc.perform(get("/orders")
						.param("sort", "totalPrice")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].totalPrice").value(15.20))
				.andExpect(jsonPath("$[1].totalPrice").value(23.40));
	}

	@Test
	void createOrderRejectsEmptyItems() throws Exception {
		this.mockMvc.perform(post("/orders")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"buyerId": 1,
									"buyer": {
										"firstName": "Ivan",
										"lastName": "Horvat"
									},
									"paymentOption": "CASH",
									"deliveryAddress": {
										"city": "Zagreb",
										"street": "Ilica",
										"homeNumber": "15"
									},
									"contactNumber": "+385911111111",
									"currency": "EUR",
									"items": []
								}
								"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createOrderRejectsMissingMenuItem() throws Exception {
		this.mockMvc.perform(post("/orders")
						.header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
									"buyerId": 1,
									"buyer": {
										"firstName": "Ivan",
										"lastName": "Horvat"
									},
									"paymentOption": "CASH",
									"deliveryAddress": {
										"city": "Zagreb",
										"street": "Ilica",
										"homeNumber": "15"
									},
									"contactNumber": "+385911111111",
									"currency": "EUR",
									"items": [
										{
											"menuItemId": 999999,
											"quantity": 1
										}
									]
								}
								"""))
				.andExpect(status().isNotFound());
	}

	private static String basicAuthHeader() {
		String credentials = "user:password";
		String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encoded;
	}
}
