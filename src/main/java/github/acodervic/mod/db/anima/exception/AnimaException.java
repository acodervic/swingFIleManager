
package github.acodervic.mod.db.anima.exception;

import github.acodervic.mod.db.anima.enums.ErrorCode;

public class AnimaException extends RuntimeException {

    private static final long serialVersionUID = 3030374277105375809L;

	private Integer code;
    private String  message;

    public AnimaException() {
        super();
    }

    public AnimaException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public AnimaException(String message, Throwable cause) {
        super(message, cause);
    }

    public AnimaException(String message) {
        super(message);
    }

    public AnimaException(Throwable cause) {
        super(cause);
    }

}
