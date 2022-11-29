package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Role;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {
    void create(Role role);
}
