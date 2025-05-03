package mk.ukim.finki.wp.repoagregator.model.enumerations;

public enum AppRole {
    PROFESSOR, ADMIN, STUDENT, GUEST;

    public String roleName() {
        return "ROLE_" + this.name();
    }
}
