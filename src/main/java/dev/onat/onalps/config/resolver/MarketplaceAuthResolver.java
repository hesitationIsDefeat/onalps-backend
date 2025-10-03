package dev.onat.onalps.config.resolver;

import dev.onat.onalps.annotation.MarketplaceAuth;
import dev.onat.onalps.dto.MarketplaceAuthDto;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MarketplaceAuthResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(MarketplaceAuth.class) != null
                && parameter.getParameterType().equals(MarketplaceAuthDto.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof MarketplaceAuthDto dto)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Marketplace authentication required"
            );
        }

        return dto;
    }
}
