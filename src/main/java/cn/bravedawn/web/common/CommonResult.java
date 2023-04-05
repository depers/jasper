package cn.bravedawn.web.common;


import cn.bravedawn.web.exception.BusinessException;

/**
 * 响应结果
 */
public class CommonResult {

    private String code;
    private String msg;
    private Object data;

    private CommonResult() {};

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static CommonResult SUCCESS(Object data) {
        CommonResult result = new CommonResult();
        result.code = ResultEnum.SUCCESS.getCode();
        result.msg = ResultEnum.SUCCESS.getMsg();
        result.data = data;
        return result;
    }


    public static CommonResult FAILURE(BusinessException exception) {
        CommonResult result = new CommonResult();
        result.code = exception.getResultEnum().getCode();
        result.msg = exception.getResultEnum().getMsg();
        return result;
    }


}
