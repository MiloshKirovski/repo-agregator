package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.model.User;
import mk.ukim.finki.wp.repoagregator.repository.UserRepository;
import mk.ukim.finki.wp.repoagregator.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findById(String username) {
        return userRepository.findByid(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
