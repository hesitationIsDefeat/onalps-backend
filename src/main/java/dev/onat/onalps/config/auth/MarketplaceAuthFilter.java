package dev.onat.onalps.config.auth;

import dev.onat.onalps.dto.MarketplaceAuthDto;
import dev.onat.onalps.repository.MarketplaceRepository;
import dev.onat.onalps.service.MarketplaceService;
import dev.onat.onalps.utils.Hasher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@Component
public class MarketplaceAuthFilter extends OncePerRequestFilter {
    private final MarketplaceService marketplaceService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public MarketplaceAuthFilter(MarketplaceService marketplaceService) {
        this.marketplaceService = marketplaceService;
    }

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v0/marketplace/get/user/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filter if path matches excluded ones

        boolean excluded = EXCLUDED_PATHS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (excluded) {
            filterChain.doFilter(request, response);
            return;
        }

        String adminUsername = request.getHeader("X-Admin-Username");
        String adminPassword = request.getHeader("X-Admin-Password");

        if (adminUsername != null && adminPassword != null && adminUsername.equals("admin-username") && adminPassword.equals("admin-password")) {
            filterChain.doFilter(request, response);
            return;
        }

        String marketplacePublicId = request.getHeader("X-Marketplace-Id");
        String brandPublicId = request.getHeader("X-Brand-Id");
        String secretKey = request.getHeader("X-Marketplace-Secret");

        if (marketplacePublicId == null || brandPublicId == null || secretKey == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing marketplace credentials");
            return;
        }

        String hashedSecretKey = Hasher.hashIdCRC(secretKey);

        var marketplace = this.marketplaceService.findBy(marketplacePublicId, hashedSecretKey);
        if (marketplace == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid marketplace credentials");
            return;
        }

        var brand = this.marketplaceService.findBrandBy(marketplace, brandPublicId);
        if (brand == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid marketplace credentials");
            return;
        }

        MarketplaceAuthDto authDto = new MarketplaceAuthDto(marketplacePublicId, hashedSecretKey, brandPublicId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(authDto, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
