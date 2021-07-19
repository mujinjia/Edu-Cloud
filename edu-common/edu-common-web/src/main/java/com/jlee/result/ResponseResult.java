package com.jlee.result;

import com.sun.istack.internal.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;


/**
 * 封装统一的返回结果集
 * <p>
 * 想要返回的结果集中为null的数据不显示:请在配置文件中配置:
 * spring.jackson.default-property-inclusion = non_null
 *
 * @author jlee
 */
public class ResponseResult<T> {

    private final String message;
    private final int code;
    private final T data;
    private final Object status;
    private HttpHeaders headers;


    public ResponseResult(ResultStatus status) {
        this(status, null);
    }

    public ResponseResult(ResultStatus status, @Nullable HttpHeaders headers) {
        this(null, status, headers);
    }

    public ResponseResult(HttpStatus status) {
        this(status, null);
    }

    public ResponseResult(HttpStatus status, @Nullable HttpHeaders headers) {
        this(null, status, headers);
    }

    public ResponseResult(@Nullable T data, ResultStatus status) {
        this(data, status, null);
    }

    public ResponseResult(@Nullable T data, ResultStatus status, @Nullable HttpHeaders headers) {
        Assert.notNull(status, "Status不能为null");
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
        this.status = status;
        this.headers = headers;
    }

    public ResponseResult(@Nullable T data, HttpStatus status) {
        this(data, status, null);
    }

    public ResponseResult(@Nullable T data, HttpStatus status, @Nullable HttpHeaders headers) {
        Assert.notNull(status, "Status不能为null");
        this.code = status.value();
        this.message = status.getReasonPhrase();
        this.data = data;
        this.status = status;
        this.headers = headers;
    }


    public ResponseResult(int status, String message, @Nullable T data, @Nullable HttpHeaders headers) {
        this.code = status;
        this.message = message;
        this.data = data;
        this.status = status;
        this.headers = headers;
    }

    /**
     * 根据ResultStatus获取无data的DataBuilder并设置status
     *
     * @param status 返回值状态
     * @return DataBuilder
     */
    public static DataBuilder status(ResultStatus status) {
        Assert.notNull(status, "Status不能为null");
        return new DefaultBuilder(status);
    }

    /**
     * 根据HttpStatus获取无data的DataBuilder并设置status
     *
     * @param status 返回值状态
     * @return DataBuilder
     */
    public static DataBuilder status(HttpStatus status) {
        Assert.notNull(status, "Status不能为null");
        return new DefaultBuilder(status);
    }

    /**
     * 获取状态为200（成功）的 DataBuilder
     *
     * @return DataBuilder
     */
    public static DataBuilder ok() {
        return status(CommonResultStatus.ok());
    }

    /**
     * 设置 data且  状态为200（成功）
     *
     * @param data data数据
     * @param <T>  data类型
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> ok(T data) {
        return ok().data(data);
    }

    /**
     * 获取失败状态的ResponseResult
     *
     * @param status 失败状态
     * @param <T>    data类型
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(ResultStatus status) {
        return status(status).build();
    }

    /**
     * 获取失败状态的ResponseResult
     *
     * @param status 失败状态
     * @param <T>    data类型
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(HttpStatus status) {
        return status(status).build();
    }

    /**
     * 根据data 获取结果集，如果data为空，返回状态404（未找到）的结果集，否则返回成功并设置data 的结构集
     *
     * @param data data数据
     * @param <T>  data 类型
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> of(T data) {
        if (data == null) {
            return ResponseResult.notFound().build();
        } else {
            return ok(data);
        }
    }


    public static <T> ResponseResult<T> of(int status, String message, T data) {
        return new ResponseResult<T>(status, message, data, null);
    }


    /**
     * 返回一个文件下载响应
     *
     * @param filename 文件名称
     * @param content  文件内容
     * @return 响应实体
     */
    public static ResponseResult<byte[]> file(String filename, String content) {
        return file(filename, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 返回一个文件下载响应
     *
     * @param filename 文件名称
     * @param data     文件数据
     * @return 响应实体
     */
    public static ResponseResult<byte[]> file(String filename, byte[] data) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(data.length);
        try {
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
            // "UTF-8" 在URLEncoder.encode内部是支持的，不会抛异常
            // 如果是JDK10以上，可以用另一个encode重载方法直接传入StandardCharsets.UTF_8常量
        }
        return new ResponseResult<>(data, HttpStatus.OK, headers);
    }


    /**
     * 获取notFound状态的DataBuilder
     *
     * @return DataBuilder
     */
    public static DataBuilder notFound() {
        return status(CommonResultStatus.notFound());
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public T getData() {
        return data;
    }

    /**
     * 获取自定义业务枚举，如果当前状态不在传入的枚举类型中会抛异常
     *
     * @param enumType 要获取的枚举类型对应class
     * @param <T>      要获取的枚举类型
     * @return 枚举状态
     */
    public <T extends Enum<T>> T getStatusCode(@NonNull Class<T> enumType) {
        if (this.status instanceof ResultStatus) {
            return Enum.valueOf(enumType, ((ResultStatus) this.status).name());
        } else if (this.status instanceof HttpStatus) {
            return Enum.valueOf(enumType, ((HttpStatus) this.status).name());
        } else {
            // status 保存的时int值
            Optional<?> resultStatus = Optional.empty();
            if (enumType.isAssignableFrom(ResultStatus.class)) {
                resultStatus =
                        EnumSet.allOf(enumType)
                                .stream()
                                .map((enumValue) -> (ResultStatus) enumValue)
                                .filter((enumValue) -> enumValue.getCode() == (Integer) this.status)
                                .findAny();


            } else if (enumType == HttpStatus.class) {
                resultStatus =
                        EnumSet.allOf(enumType)
                                .stream()
                                .map((enumValue) -> (HttpStatus) enumValue)
                                .filter((enumValue) -> enumValue.value() == (Integer) this.status)
                                .findAny();
            }
            if (resultStatus.isPresent()) {
                return enumType.cast(resultStatus.get());
            }
            throw new IllegalArgumentException(
                    String.format("在 %s 中没有枚举常量可以匹配 %s", enumType.getTypeName(), this.status));
        }
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResponseResult<?> that = (ResponseResult<?>) o;
        return code == that.code && Objects.equals(message, that.message) && Objects.equals(data, that.data) && Objects.equals(status, that.status) && Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code, data, status, headers);
    }

    public interface DataBuilder {
        /**
         * 构建 ResponseResult 对象
         *
         * @param <T> data 的类型
         * @return ResponseResult
         */
        <T> ResponseResult<T> build();

        /**
         * 构建 ResponseResult 对象 并设置data
         *
         * @param data data数据
         * @param <T>  data 的类型
         * @return ResponseResult
         */
        <T> ResponseResult<T> data(@Nullable T data);
    }

    private static class DefaultBuilder implements DataBuilder {

        private final Object statusCode;

        public DefaultBuilder(ResultStatus statusCode) {
            this.statusCode = statusCode;
        }

        public DefaultBuilder(HttpStatus statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public <T> ResponseResult<T> build() {
            return data(null);
        }

        @Override
        public <T> ResponseResult<T> data(T data) {

            if (this.statusCode instanceof HttpStatus) {
                return new ResponseResult<>(data, (HttpStatus) this.statusCode);
            } else {
                return new ResponseResult<>(data, (ResultStatus) this.statusCode);
            }
        }
    }
}
