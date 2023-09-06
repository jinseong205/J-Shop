package com.shop.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);			// 응답을 할 때 json을 javascript에서 처리 할 수 있게 할지를 설정하는것
      config.addAllowedOriginPattern("*"); 		// 모든 ip에 응답을 허용. 
      config.addAllowedHeader("*");				// 모든 header의 응답을 허용
      config.addAllowedMethod("*");				// 모든 post,get,put,delete 허용
      config.addExposedHeader("Authorization");
      
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }

}