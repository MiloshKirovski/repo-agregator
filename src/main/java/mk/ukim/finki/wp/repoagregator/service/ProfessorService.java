package mk.ukim.finki.wp.repoagregator.service;


import mk.ukim.finki.wp.repoagregator.model.Professor;

import java.util.List;
import java.util.TreeMap;


public interface ProfessorService {

    Professor getProfessorById(String id);

    List<Professor> listAllProfessors(String professorName);

    TreeMap<Character, List<Professor>> findAllProfessorsSortedByFirstName(String professorName);
    List<Professor> findAll();
}
