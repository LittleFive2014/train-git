package jp.co.wesoft.autocalc.web.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseData<T> {
    private Integer code;
    private String message;
    private T data;
}
