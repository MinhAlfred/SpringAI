package demo.springai.model;

import java.util.HashMap;
import java.util.Map;

public class TextbookMetadata {
    private Integer grade;          // 3-12
    private String subject;         // "Tin học" / "IT"
    private String chapter;
    private String topic;
    private String language;        // "vi"
    private String fileType;        // "pdf" or "text"
    private String fileName;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("grade", grade);
        map.put("subject", subject);
        map.put("chapter", chapter);
        map.put("topic", topic);
        map.put("language", language);
        map.put("fileType", fileType);
        map.put("fileName", fileName);
        return map;
    }
}