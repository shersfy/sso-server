package org.young.sso.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.young.sso.server.controller.BaseController;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
 
@EnableSwagger2
@Configuration
@ConditionalOnProperty(prefix=SwaggerConfigProperties.PREFIX, value="enabled", havingValue="true")
public class SwaggerConfig {
	
    @Bean
    public Docket createRestApi() {
    	
    	ApiInfo info =  new ApiInfoBuilder()
    			 //页面标题
                .title("Swagger")
                //版本号
                .version("1.0")
                //描述
                .description("API description")
                .build();
    	
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(info)
                .select()
                //为当前包路径
                .apis(RequestHandlerSelectors.basePackage(BaseController.class.getPackage().getName()))
                .paths(PathSelectors.any())
                .build();
    }
 
}