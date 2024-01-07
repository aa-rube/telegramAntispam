package app.service;

import app.model.BotAdmin;
import app.repository.BotAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BotAdminService {
    @Autowired
    private BotAdminRepository repository;

    public void save(BotAdmin admin) {
        repository.save(admin);
    }

    public List<BotAdmin> getAllAdmins() {
        return repository.findAll();
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

}
