package ir.stts.bajet.core.setting.repository;

import ir.stts.bajet.core.setting.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {

    Optional<Setting> findByKey(String key);

    Optional<Setting> findByIdAndKey(Long id, String key);
}