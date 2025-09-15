package com.omnia.orm.component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class JpaCommon {
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T saveInNewTransaction(JpaRepository<T, ?> repository, T entity) {
        return repository.save(entity);
    }

    @Transactional
    public void saveEntities(List<Object> entities, int batchSize) {
        int i = 0;
        for (Object entity : entities) {
            entityManager.persist(entity);

            i++;
            if (i % batchSize == 0) {
                entityManager.flush();  // flush batch to DB
                entityManager.clear();  // detach all entities from persistence context
            }
        }
        // Flush remaining entities that didn't fit into last batch
        entityManager.flush();
        entityManager.clear();
    }
}
