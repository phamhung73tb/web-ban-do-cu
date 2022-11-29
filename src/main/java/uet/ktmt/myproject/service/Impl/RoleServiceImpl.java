package uet.ktmt.myproject.service.Impl;

import uet.ktmt.myproject.persistance.entity.Role;
import uet.ktmt.myproject.persistance.repository.RoleRepository;
import uet.ktmt.myproject.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public void create(Role role) {
        roleRepository.save(role);
    }
}
