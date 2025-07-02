package mk.ukim.finki.wp.repoagregator.service.impl;

import mk.ukim.finki.wp.repoagregator.model.Student;
import mk.ukim.finki.wp.repoagregator.repository.StudentRepository;
import mk.ukim.finki.wp.repoagregator.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email).orElseThrow(RuntimeException::new);
    }
}
