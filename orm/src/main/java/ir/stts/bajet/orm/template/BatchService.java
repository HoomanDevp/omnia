//package ir.stts.bajet.orm.template;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BatchService {
//
//    private final MyEntityRepository repository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Transactional
//    public void saveEntities(List<MyEntity> entities) {
//        int batchSize = 50;
//
//        for (int i = 0; i < entities.size(); i++) {
//            repository.save(entities.get(i));
//
//            // هر 50 بار فلش و پاک کردن را انجام می‌دهیم
//            if (i % batchSize == 0 && i > 0) {
//                entityManager.flush();
//                entityManager.clear();
//            }
//        }
//    }
//
//    @Transactional
//    public void saveEntitiesInBatch(List<MyEntity> entities) {
//        // پردازش دسته‌ای در هر 50 موجودیت
//        int batchSize = 50;
//
//        for (int i = 0; i < entities.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, entities.size());
//            List<MyEntity> batchList = entities.subList(i, end);
//
//            // استفاده از saveAll برای ذخیره‌سازی دسته‌ای
//            repository.saveAll(batchList);
//            repository.flush();
//            repository.clear();
//        }
//    }
//}
