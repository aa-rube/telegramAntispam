package app.handle;

import app.model.BotAdmin;
import app.service.BotAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminsHandler {
    @Autowired
    BotAdminService service;
    public boolean deleteTheAdmin(String text) {
        try {
            int id = Integer.parseInt(text.trim().split("_")[1]);
            service.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean saveTheAdmin(String trim) {
        try {
            BotAdmin admin = new BotAdmin();
            admin.setName(trim);
            service.save(admin);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
