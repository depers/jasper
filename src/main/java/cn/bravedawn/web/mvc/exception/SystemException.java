package cn.bravedawn.web.mvc.exception;

import cn.bravedawn.web.mvc.common.ResultEnum;

public class SystemException extends Exception{

    private ResultEnum resultEnum;

    public SystemException() {
        super();
        this.resultEnum = ResultEnum.SYSTEM_ERROR;
    }

    public SystemException(String msg) {
        super(msg);
    }

    public SystemException(String msg, Throwable cause) {
        super(msg, cause);
    }


    public SystemException(Throwable cause) {
        super(cause);
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }
}

