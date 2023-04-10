package cn.bravedawn.web.exception;

import cn.bravedawn.web.common.ResultEnum;

public class BusinessException extends RuntimeException{

    private ResultEnum resultEnum;

    private String message;

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
        this.message = "[" + resultEnum.getCode() + "]" + resultEnum.getMsg();
    }


    public ResultEnum getResultEnum() {
        return resultEnum;
    }


    public void setResultEnum(ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
