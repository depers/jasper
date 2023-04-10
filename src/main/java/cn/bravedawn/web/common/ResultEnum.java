package cn.bravedawn.web.common;


/**
 * 枚举码值
 */
public enum ResultEnum {


    // 成功
    SUCCESS("0000", "success"),
    SYSTEM_ERROR("0500", "系统异常"),
    REQUEST_PARAMS_ERROR("0501", "请求参数错误"),
    ARTICLE_NO_EXIST_ERROR("0502", "查无此文章"),
    COMMENT_NICKNAME_ERROR("0503", "显示昵称不能为空"),
    COMMENT_EMAIL_ERROR("0504", "电子邮箱不能为空"),
    COMMENT_EMAIL_FORMAT_ERROR("0505", "请输入正确的电子邮箱"),
    COMMENT_CONTENT_ERROR("0506", "评论内容不能为空"),
    COMMENT_CONTENT_TOO_LONG_ERROR("0507", "评论内容不能为空"),
    COMMENT_FREQUENT_ERROR("0508", "请勿频繁发表评论"),
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
