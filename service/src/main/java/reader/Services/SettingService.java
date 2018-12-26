package reader.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reader.Model.StrangeWordLevel;
import reader.Model.StrangeWordLevelDao;

import java.util.List;

@Service
public class SettingService {
    @Autowired
    StrangeWordLevelDao strangeWordLevelDao;

    public StrangeWordLevel strangeWordLevel;

    public void init() {
        List<StrangeWordLevel> settings = strangeWordLevelDao.Get();
        if (settings == null || settings.size() == 0) {
            strangeWordLevel = new StrangeWordLevel();
            strangeWordLevel.LowLevel = 2000;
            strangeWordLevel.HighLevel = 2500;
            strangeWordLevelDao.Add(strangeWordLevel);
        }
        else
        {
            strangeWordLevel = settings.get(0);
        }
    }
}
