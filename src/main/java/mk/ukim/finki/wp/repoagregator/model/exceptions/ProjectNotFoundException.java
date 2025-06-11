package mk.ukim.finki.wp.repoagregator.model.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("The project with the given ID was not found!");
    }

}
