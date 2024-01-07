package app.repository;

import app.model.VipUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VipUserRepository extends JpaRepository<VipUser, String> {
}
