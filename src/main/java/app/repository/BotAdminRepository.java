package app.repository;


import app.model.BotAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotAdminRepository extends JpaRepository<BotAdmin, Integer> {
}
