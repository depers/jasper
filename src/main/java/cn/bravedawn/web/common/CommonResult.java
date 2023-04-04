package cn.bravedawn.web.common;


import cn.bravedawn.web.exception.BusinessException;

/**
 * 响应结果
 */
public class CommonResult<T> {

    private String code;
    private String msg;
    private T data;

    private CommonResult() {};

    public CommonResult<T> SUCCESS(T data) {
        this.code = ResultEnum.SUCCESS.getCode();
        this.msg = ResultEnum.SUCCESS.getMsg();
        this.data = data;
        return this;
    }


    public CommonResult<T> FAILURE(BusinessException exception) {
        this.code = exception.getResultEnum().getCode();
        this.msg = exception.getResultEnum().getMsg();
        return this;
    }


}
