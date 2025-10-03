package dev.onat.onalps.config.security;

import dev.onat.onalps.config.resolver.MarketplaceAuthResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MarketplaceAuthResolver marketplaceAuthResolver;

    public WebMvcConfig(MarketplaceAuthResolver marketplaceAuthArgumentResolver) {
        this.marketplaceAuthResolver = marketplaceAuthArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(marketplaceAuthResolver);
    }
}

