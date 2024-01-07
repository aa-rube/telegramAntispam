package app.service;

import app.model.VipUser;
import app.repository.VipUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VipService {
    @Autowired
    private VipUserRepository vipUserRepository;

    public void save(String userName) {
        VipUser user = new VipUser();
        user.setUserName(userName);
        vipUserRepository.save(user);
    }

    public boolean delete(String userName) {
        try {
            vipUserRepository.deleteById(userName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<VipUser> findAllVipUsers() {
        return vipUserRepository.findAll();
    }
}
