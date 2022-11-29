package uet.ktmt.myproject.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.stream.Collectors;

@Component
// @Slf4j
public class JwtTokenProvider {
    // Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    //Thời gian có hiệu lực của chuỗi jwt
    @Value("${jwt.access.token.validity}")
    private Long JWT_ACCESS_TOKEN_VALIDITY;

    // Tạo ra jwt từ thông tin user
    public String generateAccessToken(UserDetails userDetails, HttpServletRequest request) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JWT_ACCESS_TOKEN_VALIDITY))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .withClaim("type", "access")
                .sign(algorithm);
    }

    public DecodedJWT decodedJWT(String token) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT;
    }

//    private boolean isTokenExpired(DecodedJWT decodedJWT) {
//        return decodedJWT.getExpiresAt().after(new Date());
//    }
//
//    // Lấy thông tin user từ jwt
//    public String getUserNameFromJwtToken(String token) {
//
//        DecodedJWT decodedJWT = decodedJWT(token);
//        if (decodedJWT != null && isTokenExpired(decodedJWT)) {
//            return decodedJWT.getSubject();
//        }
//        return null;
//    }

//    public boolean validateJwtToken(String token) {
//        try {
//            DecodedJWT decodedJWT = decodedJWT(token);
//            return true;
//        } catch (TokenExpiredException e) {
//            log.error("JWT token is expired: {}", e.getMessage());
//        } catch (JWTDecodeException e) {
//            log.error("JWT cannot decode: {}", e.getMessage());
//        }
//
//        return false;
//    }
}