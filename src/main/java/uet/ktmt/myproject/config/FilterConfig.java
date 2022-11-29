package uet.ktmt.myproject.config;

import com.auth0.jwt.interfaces.DecodedJWT;
import uet.ktmt.myproject.common.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class FilterConfig extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String[] arr = request.getServletPath().split("/");
        if (arr.length >= 2
                && !arr[1].equals("user")
                && !arr[1].equals("admin")
                || request.getServletPath().equals("/")) {

            filterChain.doFilter(request, response);
        } else {
            response.setContentType(APPLICATION_JSON_VALUE);

            String token = getJwtFromRequest(request);

            if (token != null) {
                try {
                    DecodedJWT decodedJWT = tokenProvider.decodedJWT(token);

                    String type = decodedJWT.getClaim("type").asString();
                    if (type.equals("access")) {
                        String username = decodedJWT.getSubject();
                        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        filterChain.doFilter(request, response);
                    }
                } catch (Exception exception) {
                    response.setStatus(UNAUTHORIZED.value());
                    response.sendRedirect(
                            request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/login"
                    );
                }
            } else {
                response.setStatus(UNAUTHORIZED.value());
                response.sendRedirect(
                        request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/login"
                );
            }
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION);
//        // Kiểm tra xem header Authorization có chứa thông tin jwt không
//        if (Objects.nonNull(bearerToken) && StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring("Bearer ".length());
//        }
//        return null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("accessToken")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
