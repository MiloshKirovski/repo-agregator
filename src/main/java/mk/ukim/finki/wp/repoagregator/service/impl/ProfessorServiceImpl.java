package mk.ukim.finki.wp.repoagregator.service.impl;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import mk.ukim.finki.wp.repoagregator.model.Professor;
import mk.ukim.finki.wp.repoagregator.model.exceptions.ProfessorNotFoundException;
import mk.ukim.finki.wp.repoagregator.repository.ProfessorRepository;
import mk.ukim.finki.wp.repoagregator.service.ProfessorService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

import static mk.ukim.finki.wp.repoagregator.specifications.FieldFilterSpecification.filterContainsText;

@Service
@AllArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;

    @Override
    public Professor getProfessorById(String id) throws ProfessorNotFoundException {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ProfessorNotFoundException("Professor with id " + id + " doesn't exist"));
    }

    @Override
    public List<Professor> listAllProfessors(String professorName) {
        Specification<Professor> spec = Specification.where(null);
        if(professorName!=null && !professorName.isEmpty()){
            Specification<Professor> nameSpec = filterContainsText(Professor.class, "name", professorName);
            Specification<Professor> idSpec = filterContainsText(Professor.class, "id", professorName);

            spec = spec.and(nameSpec.or(idSpec));
        }

        Specification<Professor> excludeSpec = (root, query, criteriaBuilder) -> {
            Predicate notDemonstrator = criteriaBuilder.notLike(root.get("name"), "%Демонстратор%");
            Predicate notIndustry = criteriaBuilder.notLike(root.get("name"), "%Индустрија%");
            return criteriaBuilder.and(notDemonstrator, notIndustry);
        };

        spec = spec.and(excludeSpec);

        return professorRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }
}
