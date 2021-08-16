package com.jlee.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;

/**
 * 前端异常信息显示模型
 *
 * @author jlee
 */
public class ErrorViewModel {
    private HttpStatus status;
    private String message;
    private int code;
    private List<?> errors;

    ErrorViewModel(int code, String message, List<?> errors) {
        this.message = message;
        this.errors = errors;
        this.code = code;
    }

    public static ErrorViewModel.ApiErrorViewModelBuilder builder() {
        return new ErrorViewModel.ApiErrorViewModelBuilder();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<?> getErrors() {
        return this.errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorViewModel that = (ErrorViewModel) o;
        return code == that.code && Objects.equals(message, that.message) && Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, code, errors);
    }

    protected boolean canEqual(Object other) {
        return other instanceof ErrorViewModel;
    }

    @Override
    public String toString() {
        return "ApiErrorViewModel(" +
                "message='" + message + '\'' +
                ", status=" + code +
                ", errors=" + errors +
                ')';
    }

    public static class ApiErrorViewModelBuilder {
        private int status;
        private String message;
        private List<?> errors;

        ApiErrorViewModelBuilder() {
        }

        public ErrorViewModel.ApiErrorViewModelBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorViewModel.ApiErrorViewModelBuilder errors(List<?> errors) {
            this.errors = errors;
            return this;
        }

        public ErrorViewModel.ApiErrorViewModelBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorViewModel build() {
            return new ErrorViewModel(this.status, this.message, this.errors);
        }

        @Override
        public String toString() {
            return "ApiErrorViewModel.ApiErrorViewModelBuilder(message=" + this.message + ", errors=" + this.errors + ")";
        }
    }
}
