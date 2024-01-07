package app.repository;

import app.model.AdvertisersUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisersRepository extends JpaRepository<AdvertisersUser, Integer> {
    List<AdvertisersUser> findAllByAdminChatIdOwner(Long chatId);

    List<AdvertisersUser> findAllByUserName(String userName);
}
