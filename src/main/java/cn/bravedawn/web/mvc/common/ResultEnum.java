package cn.bravedawn.web.mvc.common;


/**
 * 枚举码值
 */
public enum ResultEnum {


    // 成功
    SUCCESS("0000", "success"),
    SYSTEM_ERROR("0500", "系统异常"),

    ;

    private String code;
    private String msg;

    ResultEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
