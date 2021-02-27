package com.distributed.systems.dom_judge.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Optional<String> res;
        try {
            res = Optional.of(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        } catch ( Exception e) {
            return Optional.empty();
        }
        return res;
    }
}
