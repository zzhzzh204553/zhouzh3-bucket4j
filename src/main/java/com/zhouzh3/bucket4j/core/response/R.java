package com.zhouzh3.bucket4j.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author haig
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> success(T data) {
        return new R<>(200, "操作成功", data);
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }
}
