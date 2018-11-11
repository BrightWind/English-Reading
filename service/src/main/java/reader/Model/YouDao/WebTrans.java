package reader.Model.YouDao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WebTrans {
    @JsonProperty("web-translation")
    List<WebTranslation> web_translation;
}
