package mk.ukim.finki.wp.repoagregator.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfessorNotFoundException extends RuntimeException {
    public ProfessorNotFoundException() {
        super("Професорот не е пронајден.");
    }
    public ProfessorNotFoundException(String message) {
        super(message);
    }

}

