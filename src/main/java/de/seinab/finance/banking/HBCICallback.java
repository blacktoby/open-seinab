package de.seinab.finance.banking;

import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.callback.AbstractHBCICallback;
import org.kapott.hbci.manager.MatrixCode;
import org.kapott.hbci.passport.HBCIPassport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class HBCICallback extends AbstractHBCICallback {
    private static final Logger log = LoggerFactory.getLogger(HBCICallback.class);

    private BankingHandles bankingHandles;
    private String iban;
    private String pinCache;

    public HBCICallback(String iban, BankingHandles bankingHandles) {
        this.bankingHandles = bankingHandles;
        this.iban = iban;
    }

    @Override
    public void log(String msg, int i, Date date, StackTraceElement stackTraceElement) {
        log.debug(msg);
    }

    @Override
    public void callback(HBCIPassport passport, int reason, String msg, int datatype, StringBuffer retData) {
        switch (reason)
        {
            case NEED_PASSPHRASE_LOAD:
            case NEED_PASSPHRASE_SAVE:
            // PIN wird benoetigt
            case NEED_PT_PIN:
                log.debug("NEED_PIN");
                if(StringUtils.isEmpty(pinCache)) {
                    handleRequest(bankingHandles.getRequestPin(), retData);
                    pinCache = retData.toString();
                } else {
                    retData.replace(0,retData.length(),pinCache);
                }
                break;

            case NEED_PT_TAN:
                log.debug("NEED_TAN");
                handleRequest(bankingHandles.getRequestTan(), retData);
                break;

            // BLZ wird benoetigt
            case NEED_BLZ:
                log.debug("need blz");
                retData.replace(0, retData.length(), BankingUtils.blzFromIBAN(iban));
                break;

            // Die Benutzerkennung
            case NEED_USERID:
                log.debug("NEED_USERID");
                handleRequest(bankingHandles.getRequestUserid(), retData);
                break;

            // Die Kundenkennung. Meist identisch mit der Benutzerkennung.
            // Bei manchen Banken kann man die auch leer lassen
            case NEED_CUSTOMERID:
                log.debug("NEED_CUSTOMERID");
                break;

            // Manche Fehlermeldungen werden hier ausgegeben
            case HAVE_ERROR:
                log.error(msg);
                break;

            case NEED_HOST:
                log.debug("NEED_HOST");
                break;

            case NEED_PT_PHOTOTAN:
                log.debug("NEED_PT_PHOTOTAN");
                MatrixCode matrixCode = MatrixCode.tryParse(retData.toString());
                try {
                    Future<Integer> tanFuture = bankingHandles.getRequestPhotoTan().apply(matrixCode);
                    Integer tan = tanFuture.get(5, TimeUnit.MINUTES);
                    log.debug("Got PhotoTan: {}", tan);
                    retData.replace(0,retData.length(),String.valueOf(tan));
                } catch (Exception e) {
                    log.error("Couldn't get Tan: {}", e.getMessage());
                    throw new RuntimeException("Couldn't get Tan!");
                }
                break;

            case NEED_PT_SECMECH:
                log.debug("NEED_PT_SECMECH");
                log.debug(retData.toString());
                // 901:mobileTAN-Verfahren|902:photoTAN-Verfahren
                handleRequest(bankingHandles.getRequestSecMech(), retData);
                break;

            default:
                // log.debug("NEED SOMETHING ELSE!!");
                // log.debug("{}", reason);
                // Wir brauchen nicht alle der Callbacks
                break;

        }
    }

    private void handleRequest(Function<String, Future<String>> futureFunction, StringBuffer retData) {
        try {
            Future<String> future = futureFunction.apply(retData.toString());
            String pin = future.get(5, TimeUnit.MINUTES);
            retData.replace(0,retData.length(),pin);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }
    }

    @Override
    public void status(HBCIPassport hbciPassport, int i, Object[] objects) {

    }
}
