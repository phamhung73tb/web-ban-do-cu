package uet.ktmt.myproject.presentation.controller.basic;

import uet.ktmt.myproject.common.jwt.JwtTokenProvider;
import uet.ktmt.myproject.config.UserDetailServiceConfig;
import uet.ktmt.myproject.presentation.request.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@Controller
public class AuthController {

    @Autowired
    private UserDetailServiceConfig userDetailServiceConfig;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String home(Model model, @ModelAttribute LoginRequest loginRequest
            , HttpServletRequest request, HttpServletResponse response) {
        try {
            final UserDetails userDetails = userDetailServiceConfig.loadUserByUsername(loginRequest.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername()
                    , loginRequest.getPassword())
            );
            String accessToken = jwtTokenProvider.generateAccessToken(userDetails, request);
            Cookie cookie = new Cookie("accessToken", accessToken);
            response.addCookie(cookie);
            if (userDetails.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()).contains("ROLE_ADMIN")) {
                return "redirect:/admin/home";
            }
        } catch (LockedException e) {
            model.addAttribute("statusLogin", "Tài khoản của bạn đã bị khóa !!!");
            return "login";
        } catch (Exception e) {
            model.addAttribute("statusLogin", "Kiểm tra lại tài khoản và mật khẩu !!!");
            return "login";
        }
        return "redirect:/home";
    }
}

