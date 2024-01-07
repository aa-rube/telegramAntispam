package app.handle;

import app.model.StopWordObject;
import app.service.StopWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopWordHandle {
    @Autowired
    private StopWordService stopWordService;

    public boolean saveTheWord(String text) {
        try {
            if (text.split(",").length > 1) {
                for (String word : text.split(",")) {
                    StopWordObject o = new StopWordObject();
                    o.setWord(word.trim());
                    stopWordService.save(o);
                }
            } else {
                StopWordObject o = new StopWordObject();
                o.setWord(text.trim());
                stopWordService.save(o);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTheWord(String text) {
        try {
            int id = Integer.parseInt(text.split("_")[1]);
            stopWordService.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
