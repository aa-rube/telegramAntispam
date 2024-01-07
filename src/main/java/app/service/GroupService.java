package app.service;

import app.model.Group;
import app.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class GroupService {
    @Autowired
    private GroupRepository repository;
    public Group getNewObjectByCallbackUpdate(Update update) {
        Group group = new Group();
        group.setOwnerChatId(update.getCallbackQuery().getFrom().getId());
        group.setOwnerUserName(update.getCallbackQuery().getFrom().getUserName());
        return group;
    }

    public Group getNewObjectByTextUpdate(Update update) {
        Group group = new Group();
        group.setOwnerChatId(update.getMessage().getChatId());
        group.setOwnerUserName(update.getMessage().getFrom().getUserName());
        return group;
    }

    public boolean save(Group group, String text) {
        try {
            group.setGroupUserName(text);
            repository.save(group);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteById(String text) {
        try {
            int id = Integer.parseInt(text.split("_")[1]);
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Group> findAllGroupsByOwnerUserName(String groupOwner) {
        try {
            return repository.findAllByOwnerUserName(groupOwner);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Group> findAllGroupsByOwnerChatId(Long chatId) {
        try {
            return repository.findAllByOwnerChatId(chatId);
        } catch (Exception e) {
            return null;
        }
    }
}
