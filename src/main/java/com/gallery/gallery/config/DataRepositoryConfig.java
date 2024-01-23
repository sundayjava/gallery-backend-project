package com.gallery.gallery.config;

import com.gallery.gallery.models.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class DataRepositoryConfig implements RepositoryRestConfigurer {
    private final String theAllowedOrigin = "http://localhost:5173/"; //https://localhost:3000

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        HttpMethod[] theunsupportedActions = {HttpMethod.POST, HttpMethod.PATCH,HttpMethod.DELETE, HttpMethod.PUT};

        config.exposeIdsFor(User.class);

        disableHttpMethod(User.class, config, theunsupportedActions);

        cors.addMapping(config.getBasePath() + "/**")
                .allowedOrigins(theAllowedOrigin);
    }

    private void disableHttpMethod(Class theClass, RepositoryRestConfiguration cofig, HttpMethod[] theUnsupportedActions){
        cofig.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }
}
