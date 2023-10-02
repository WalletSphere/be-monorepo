package com.khomishchak.cryptoportfolio.services.security;

import com.khomishchak.cryptoportfolio.model.User;
import com.khomishchak.cryptoportfolio.repositories.UserRepository;
import com.khomishchak.cryptoportfolio.security.UserDetailsImpl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with username: %s was not found!", username)));

        return new UserDetailsImpl(user);
    }
}
