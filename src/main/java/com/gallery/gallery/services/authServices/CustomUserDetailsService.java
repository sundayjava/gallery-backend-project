package com.gallery.gallery.services.authServices;

import com.gallery.gallery.models.User;
import com.gallery.gallery.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> dbUser = userRepository.findByEmail(email);

        return dbUser.map(CustomUserDetails::new)
                .orElseThrow(() -> new  UsernameNotFoundException("Couldn't find a matching user email in the database for " + email));
    }
}
