package cn.bravedawn.web.exception;

import cn.bravedawn.web.common.ResultEnum;

public class BusinessException extends RuntimeException{

    private ResultEnum resultEnum;

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Throwable cause) {
        super(msg, cause);
    }


    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(ResultEnum resultEnum) {
        super("[" + resultEnum.getCode() + "]" + resultEnum.getMsg());
        this.resultEnum = resultEnum;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }
}
