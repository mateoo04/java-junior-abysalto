package hr.abysalto.hiring.api.junior.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
	private static final String BASIC_AUTH = "basicAuth";

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("public").pathsToMatch("/**").build();
	}

	@Bean
	public OpenAPI restaurantOrdersApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Restaurant Orders API")
						.version("1.0")
						.description("API for managing restaurant orders, buyers, and menu items."))
				.addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH))
				.components(new Components().addSecuritySchemes(BASIC_AUTH,
						new SecurityScheme()
								.name(BASIC_AUTH)
								.type(SecurityScheme.Type.HTTP)
								.scheme("basic")));
	}
}
