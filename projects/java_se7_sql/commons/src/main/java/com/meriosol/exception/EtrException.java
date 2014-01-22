package com.meriosol.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class EtrException extends RuntimeException {
    public static final Integer DEFAULT_ERROR_CODE = 99;
    public static final Integer DEFAULT_RESULT_CODE = -1;
    private Integer resultCode = null;
    private List<Integer> errorCodes = null;
    private static final long serialVersionUID = 89099712143090L;

    public EtrException() {
    }

    public EtrException(String errorMessage) {
        this(errorMessage, DEFAULT_RESULT_CODE, buildErrorCodeList(DEFAULT_ERROR_CODE));
    }

    public EtrException(Exception ex) {
        this(ex, DEFAULT_RESULT_CODE, buildErrorCodeList(DEFAULT_ERROR_CODE));
    }

    public EtrException(String errorMessage, Exception ex) {
        this(errorMessage, ex, DEFAULT_RESULT_CODE, buildErrorCodeList(DEFAULT_ERROR_CODE));
    }

    public EtrException(Exception ex, Integer resultCode, Integer errorCode) {
        this(ex, resultCode, buildErrorCodeList(errorCode));
    }

    public EtrException(String errorMessage, Integer resultCode, Integer errorCode) {
        this(errorMessage, resultCode, buildErrorCodeList(errorCode));
    }

    public EtrException(Exception ex, Integer errorCode) {
        this(ex, DEFAULT_RESULT_CODE, buildErrorCodeList(errorCode));
    }

    public EtrException(String errorMessage, Integer errorCode) {
        this(errorMessage, DEFAULT_RESULT_CODE, buildErrorCodeList(errorCode));
    }

    public EtrException(String errorMessage, Exception ex, Integer errorCode) {
        this(errorMessage, ex, DEFAULT_RESULT_CODE, buildErrorCodeList(DEFAULT_ERROR_CODE));
    }

    public EtrException(Exception ex, Integer resultCode, List<Integer> errorCodes) {
        super(ex);
        this.setResultCode(resultCode);
        this.setErrorCodes(errorCodes);
    }

    public EtrException(String errorMessage, Integer resultCode, List<Integer> errorCodes) {
        super(errorMessage);
        this.setResultCode(resultCode);
        this.setErrorCodes(errorCodes);
    }

    public EtrException(String errorMessage, Exception ex, Integer resultCode, List<Integer> errorCodes) {
        super(errorMessage, ex);
        this.setResultCode(resultCode);
        this.setErrorCodes(errorCodes);
    }

    public Integer getResultCode() {
        return resultCode;
    }

    private void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public List<Integer> getErrorCodes() {
        return errorCodes;
    }

    private void setErrorCodes(List<Integer> errorCodes) {
        this.errorCodes = errorCodes;
    }

    private static List<Integer> buildErrorCodeList(Integer errorCode) {
        List<Integer> errorCodeList = new ArrayList<Integer>(1);
        errorCodeList.add(errorCode);
        return errorCodeList;
    }
}
