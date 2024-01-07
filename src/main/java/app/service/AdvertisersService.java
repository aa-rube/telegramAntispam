package app.service;

import app.model.AdvertisersUser;
import app.repository.AdvertisersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class AdvertisersService {
    @Autowired
    private AdvertisersRepository repository;

    public boolean save(AdvertisersUser user) {
        try {
            repository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<AdvertisersUser> getAllUsers() {
        return repository.findAll();
    }

    public List<AdvertisersUser> getAllUsersByOwnerChatId(Long chatId) {
        return  repository.findAllByAdminChatIdOwner(chatId);
    }

    public boolean deleteByUserId(int id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public AdvertisersUser getUserById(Integer id) {
        return repository.findById(id).get();
    }

    public AdvertisersUser getNewObjectCallBackQuery(Update update, Long chatId) {
        AdvertisersUser user = new AdvertisersUser();
        user.setAdminUserNameOwner("@" + update.getCallbackQuery().getFrom().getUserName());
        user.setAdminChatIdOwner(chatId);
        user.setStarted(false);
        return user;
    }

    public List<AdvertisersUser> getAllUsersByUserName(String userName) {
        return repository.findAllByUserName(userName);
    }
}
