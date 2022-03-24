package de.seinab.finance.banking;

import org.kapott.hbci.manager.MatrixCode;

import java.util.concurrent.Future;
import java.util.function.Function;

public class BankingHandles {
    private Function<MatrixCode, Future<Integer>> requestPhotoTan;
    private Function<String, Future<String>> requestTan;
    private Function<String, Future<String>> requestUserid;
    private Function<String, Future<String>> requestPin;
    private Function<String, Future<String>> requestSecMech;

    public Function<MatrixCode, Future<Integer>> getRequestPhotoTan() {
        return requestPhotoTan;
    }

    public void setRequestPhotoTan(Function<MatrixCode, Future<Integer>> requestPhotoTan) {
        this.requestPhotoTan = requestPhotoTan;
    }

    public Function<String, Future<String>> getRequestTan() {
        return requestTan;
    }

    public void setRequestTan(Function<String, Future<String>> requestTan) {
        this.requestTan = requestTan;
    }

    public Function<String, Future<String>> getRequestUserid() {
        return requestUserid;
    }

    public void setRequestUserid(Function<String, Future<String>> requestUserid) {
        this.requestUserid = requestUserid;
    }

    public Function<String, Future<String>> getRequestPin() {
        return requestPin;
    }

    public void setRequestPin(Function<String, Future<String>> requestPin) {
        this.requestPin = requestPin;
    }

    public Function<String, Future<String>> getRequestSecMech() {
        return requestSecMech;
    }

    public void setRequestSecMech(Function<String, Future<String>> requestSecMech) {
        this.requestSecMech = requestSecMech;
    }
}
