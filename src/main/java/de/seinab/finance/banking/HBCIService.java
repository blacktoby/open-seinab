package de.seinab.finance.banking;

import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.manager.HBCIHandler;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIExecStatus;
import org.kapott.hbci.structures.Konto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class HBCIService {
    private static final Logger log = LoggerFactory.getLogger(HBCIService.class);
    private final HBCIVersion VERSION = HBCIVersion.HBCI_300;

    public List<GVRKUms.UmsLine> getUmsaetze(String iban, BankingHandles bankingHandles) {
        setProperties(new HBCICallback(iban, bankingHandles));
        HBCIHandler hbciHandler = buildHandler(iban);
        Konto konto = getKonto(iban);
        if(konto == null) {
            return null;
        }
        return executeUmsatzJob(hbciHandler, konto).getFlatData();
    }


    private GVRKUms executeUmsatzJob(HBCIHandler handler, Konto konto)
    {
        HBCIJob umsatzJob = handler.newJob("KUmsAll");
        umsatzJob.setParam("my",konto);
        umsatzJob.addToQueue();
        executeHandlerQueue(handler);
        GVRKUms result = (GVRKUms) umsatzJob.getJobResult();
        if(!result.isOK())
        {
            log.error("Umsatz konnte nicht abgerufen werden! - " +result.toString());
            throw new RuntimeException("Umsatz konnte nicht abgerufen werden! - " +result.toString());
        }
        return result;
    }

    private void executeHandlerQueue(HBCIHandler handler) {
        HBCIExecStatus status = handler.execute();

        if(!status.isOK()){
            log.error("Handler Queue konnte nicht ausgeführt werden! - " +status.toString());
            throw new RuntimeException("Handler Queue konnte nicht ausgeführt werden! - " +status.toString());
        }
    }


    private HBCIHandler buildHandler(String iban) {
        HBCIPassport passport = BankingUtils.buildPassport(iban);
        return new HBCIHandler(VERSION.getId(), passport);
    }

    private void setProperties(HBCICallback hbciCallback) {
        Properties properties = new Properties();
        final File passportFile = new File("/tmp", new Date().getTime() + ".dat");
        HBCIUtils.init(properties, hbciCallback);
        HBCIUtils.initThread(properties, hbciCallback);
        HBCIUtils.setParam("client.passport.default","PinTan");
        HBCIUtils.setParam("client.passport.PinTan.filename",passportFile.getAbsolutePath());
    }


    private Konto getKonto(String iban){
        Konto[] konten = BankingUtils.buildPassport(iban).getAccounts();
        if(konten == null || konten.length == 0)
        {
            log.error("No Konto found!");
            throw new RuntimeException("Keine Konten gefunden.");
        }
        String kontoNumber = BankingUtils.kontoNumberFromIBAN(iban);
        List<Konto> kontoList =
                Arrays.stream(konten)
                        .filter(konto -> StringUtils.contains(konto.number, kontoNumber))
                        .collect(Collectors.toList());
        if(kontoList.isEmpty())
        {
            log.warn("Konto with number " +kontoNumber +" could no be found.");
            return null;
        }
        return kontoList.get(0);
    }
}
