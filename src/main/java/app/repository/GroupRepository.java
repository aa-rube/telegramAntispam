package app.repository;

import app.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findAllByOwnerUserName(String ownerUserName);
    List<Group> findAllByOwnerChatId(Long chatId);

}