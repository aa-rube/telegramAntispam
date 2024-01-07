package app.service;

import app.model.Setting;
import app.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SettingService {
    @Autowired
    private SettingRepository repository;

    public void save(int length,int count, boolean check) {
        Setting setting = new Setting();
        setting.setId(1);

        setting.setCaptionLength(length);
        setting.setCheckPhoto(check);
        setting.setCountPhoto(count);
        repository.save(setting);
    }

    public Optional<Setting> getSetting() {
        int id = 1;
        return repository.findById(id);
    }
}
