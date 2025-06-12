package mk.ukim.finki.wp.repoagregator.service;

import mk.ukim.finki.wp.repoagregator.model.Student;

public interface StudentService {
    Student findByEmail(String email);
}
