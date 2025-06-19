package mk.ukim.finki.wp.repoagregator.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;


public class FieldFilterSpecification {

    public static <T> Specification<T> filterEquals(Class<T> entityClass, String attribute, Object value) {
        return (root, query, criteriaBuilder) ->
                value == null ? null : criteriaBuilder.equal(root.get(attribute), value);
    }



    public static <T, V extends Comparable> Specification<T> greaterThanOrEqualTo(Class<T> clazz, String field, V value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(fieldToPath(field, root), value);
    }



    private static <T> Path fieldToPath(String field, Root<T> root) {
        String[] parts = field.split("\\.");
        Path res = root;
        for (String p : parts) {
            res = res.get(p);
        }
        return res;
    }

    public static <T, V extends Comparable> Specification<T> lessThan(Class<T> clazz, String field, V value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(fieldToPath(field, root), value);
    }

    public static <T> Specification<T> isUpcoming(Class<T> clazz, String dateField, String timeField, LocalDateTime now) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.greaterThan(fieldToPath(dateField, root), now),

                    criteriaBuilder.and(
                            criteriaBuilder.equal(fieldToPath(dateField, root), now),
                            criteriaBuilder.greaterThan(fieldToPath(timeField, root), now)
                    )
            );
        };
    }

    public static <T> Specification<T> isPast(Class<T> clazz, String dateField, String timeField, LocalDateTime now) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.or(
                    criteriaBuilder.lessThan(fieldToPath(dateField, root), now),

                    criteriaBuilder.and(
                            criteriaBuilder.equal(fieldToPath(dateField, root), now),
                            criteriaBuilder.lessThan(fieldToPath(timeField, root), now)
                    )
            );
        };
    }

    public static <T> Specification<T> filterEquals(Class<T> clazz, String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Path<String> fieldPath = getFieldPath(field, root);
            return criteriaBuilder.equal(fieldPath, value);
        };
    }

    public static <T, V> Specification<T> filterEqualsV(Class<T> clazz, String field, V value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Path<V> fieldPath = getFieldPath(field, root);
            return criteriaBuilder.equal(fieldPath, value);
        };
    }

    public static <T, V extends Comparable<? super V>> Specification<T> greaterThan(Class<T> clazz, String field, V value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Path<V> fieldPath = getFieldPath(field, root);
            return criteriaBuilder.greaterThan(fieldPath, value);
        };
    }

    public static <T> Specification<T> filterEquals(Class<T> clazz, String field, Long value) {
        if (value == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Path<Long> fieldPath = getFieldPath(field, root);
            return criteriaBuilder.equal(fieldPath, value);
        };
    }

    public static <T> Specification<T> filterContainsText(Class<T> clazz, String field, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Path<String> fieldPath = getFieldPath(field, root);
            return criteriaBuilder.like(
                    criteriaBuilder.lower(fieldPath),
                    "%" + value.toLowerCase() + "%"
            );
        };
    }

    @SuppressWarnings("unchecked")
    private static <T, R> Path<R> getFieldPath(String field, Root<T> root) {
        String[] parts = field.split("\\.");
        Path<?> res = root;
        for (String p : parts) {
            res = res.get(p);
        }
        return (Path<R>) res;
    }

    public static <T> Specification<T> filterEqualsInCollection(String field, Object value) {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            return null;
        }

        return (root, query, criteriaBuilder) -> {
            String[] parts = field.split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Field format must be 'collection.field'");
            }
            Join<T, ?> join = root.join(parts[0]);
            return criteriaBuilder.equal(join.get(parts[1]), value);
        };
    }

}