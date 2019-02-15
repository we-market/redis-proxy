package cn.wemarket.redisproxy.common.exception;

import org.springframework.core.NestedRuntimeException;

public class SysException extends NestedRuntimeException {
    public SysException(String msg) {
        super(msg);
    }

    public SysException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
