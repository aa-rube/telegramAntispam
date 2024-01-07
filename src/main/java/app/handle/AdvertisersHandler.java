package app.handle;

import app.model.AdvertisersUser;
import app.service.AdvertisersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class AdvertisersHandler {

    public AdvertisersUser getNewObject(Update update, Long chatId) {
        AdvertisersUser user = new AdvertisersUser();
        user.setAdminUserNameOwner(update.getCallbackQuery().getMessage().getFrom().getUserName());
        user.setAdminChatIdOwner(chatId);
        return user;
    }
}
