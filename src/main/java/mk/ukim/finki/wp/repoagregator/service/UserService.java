package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.User;

public interface UserService {
    User findById(String username);
}
