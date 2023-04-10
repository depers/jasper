package cn.bravedawn.web.common;


import cn.bravedawn.web.exception.BusinessException;

/**
 * 响应结果
 */
public class CommonResult<S> {

    private String code;
    private String msg;
    private S data;

    private CommonResult() {};

    private CommonResult(String code, String msg, S data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public CommonResult(String code, String msg) {
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

    public S getData() {
        return data;
    }

    public void setData(S data) {
        this.data = data;
    }

    public static <T> CommonResult<T> SUCCESS() {
        return new CommonResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg());
    }

    public static <T> CommonResult<T> SUCCESS(T data) {
        return new CommonResult<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    public static <T> CommonResult<T> FAILURE(BusinessException exception) {
        return new CommonResult<>(exception.getResultEnum().getCode(), exception.getResultEnum().getMsg());
    }


    public static <T> CommonResult<T> FAILURE() {
        return new CommonResult<>(ResultEnum.SYSTEM_ERROR.getCode(), ResultEnum.SYSTEM_ERROR.getMsg());
    }

}
