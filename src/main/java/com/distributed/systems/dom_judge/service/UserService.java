package com.distributed.systems.dom_judge.service;

import com.distributed.systems.dom_judge.dto.UserDto;
import com.distributed.systems.dom_judge.model.User;
import com.distributed.systems.dom_judge.repository.RoleRepository;
import com.distributed.systems.dom_judge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User findByUsername(String username) throws DataAccessException {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void register(User user, UserDto authDto, String role) {
        if(user == null) {
            user = new User();
            user.setUsername(authDto.getUsername());
        }
        user.setPassword(bCryptPasswordEncoder.encode(authDto.getPassword()));
        user.setRole(roleRepository.findByName(role).orElse(null));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
