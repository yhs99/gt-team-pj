package com.team.goott.infra;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public abstract class InterceptorConfigurer implements WebMvcConfigurer{
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AdminSessionInterceptor()).addPathPatterns("/admin/**");
	}
}
