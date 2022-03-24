package de.seinab.finance.banking;

import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.passport.AbstractHBCIPassport;
import org.kapott.hbci.passport.HBCIPassport;

public class BankingUtils {

    public static String kontoNumberFromIBAN(String iban) {
        return StringUtils.substring(iban, -10, -1);
    }

    public static String blzFromIBAN(String iban) {
        return StringUtils.substring(iban, 4, 12);
    }

    public static String countryCodeFromIBAN(String iban) {
        return StringUtils.substring(iban, 0, 2);
    }

    public static HBCIPassport buildPassport(String iban) {
        HBCIPassport passport = AbstractHBCIPassport.getInstance();
        passport.setCountry(countryCodeFromIBAN(iban));

        BankInfo info = HBCIUtils.getBankInfo(blzFromIBAN(iban));
        passport.setHost(info.getPinTanAddress());
        passport.setPort(443);
        passport.setFilterType("Base64");
        return passport;
    }

}
