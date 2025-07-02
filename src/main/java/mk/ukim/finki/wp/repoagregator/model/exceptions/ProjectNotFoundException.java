package mk.ukim.finki.wp.repoagregator.model.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException() {
        super("ГРЕШКА! Не пронајдовме проект со тоа ID...");
    }

}
