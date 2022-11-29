package uet.ktmt.myproject.config;

import uet.ktmt.myproject.common.exception.BadRequestException;
import uet.ktmt.myproject.persistance.entity.User;
import uet.ktmt.myproject.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailServiceConfig implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username).orElse(null);
        if (Objects.isNull(userEntity)) {
            throw new BadRequestException(username + " not found in database.");
        } else {
            return new CustomUserDetails(userEntity);
        }
    }
}
