package gateway_microservice.gateway_microservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import com.netflix.discovery.DiscoveryClient;

import gateway_microservice.gateway_microservice.filter.AuthenticationFilter;


@EnableEurekaClient
@SpringBootApplication
public class GatewayMicroserviceApplication {
	 @Autowired
	    AuthenticationFilter filter;

	@Bean
	public RouteLocator routes (RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/auth/**").uri("lb://BALADE-SERVICE"))
		     	 .route("balade-service", r -> r.path("/balade/**")
	                    .filters(f -> f.filter(filter))
	                   .uri("lb://BALADE-SERVICE"))
	                .build();
				
	}
	//@Bean
	DiscoveryClientRouteDefinitionLocator dynamicRoutes (ReactiveDiscoveryClient rdc ,DiscoveryLocatorProperties dlp) {
		return new DiscoveryClientRouteDefinitionLocator(rdc, dlp);
		}
	
	
	
	

	public static void main(String[] args) {
		
		SpringApplication.run(GatewayMicroserviceApplication.class, args);
		
		
		
	}

}
