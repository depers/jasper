package cn.bravedawn.scheduled.dto;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/13 16:51
 */
public class KeyNodeInfo {

    private String keyWord;
    private String intro;

    public KeyNodeInfo(String keyWord, String intro) {
        this.keyWord = keyWord;
        this.intro = intro;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public String toString() {
        return "KeyNodeInfo{" +
                "keyWord='" + keyWord + '\'' +
                ", intro='" + intro + '\'' +
                '}';
    }
}
