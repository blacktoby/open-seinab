package de.seinab.backend.banking;

import de.seinab.EventGroup;
import de.seinab.backend.banking.models.BankingJob;
import de.seinab.backend.banking.models.BankingStatus;
import de.seinab.backend.banking.models.BankingUtilities;
import de.seinab.backend.security.EventGroupAccessControl;
import de.seinab.backend.security.FormPermissionAccessControl;
import de.seinab.finance.FormFeeTurnoverService;
import de.seinab.finance.banking.BankingHandles;
import de.seinab.finance.banking.HBCIService;
import de.seinab.user.models.User;
import org.apache.commons.lang3.StringUtils;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

@Controller
public class BackendBankingController {
    private static final Logger log = LoggerFactory.getLogger(BackendBankingController.class);

    @Autowired
    private SimpMessagingTemplate webSocket;

    @Autowired
    private HBCIService hbciService;
    @Autowired
    private FormFeeTurnoverService formFeeTurnoverService;
    @Autowired
    private FormPermissionAccessControl permissionAccessControl;
    @Autowired
    private EventGroupAccessControl eventGroupAccessControl;

    Map<BankingJob, BankingUtilities> stateMap = new ConcurrentHashMap<>();

    @MessageMapping("/{eventGroupName}/{formName}/feematching")
    public synchronized void startFeeMatching(@DestinationVariable String eventGroupName,
                                                       @DestinationVariable String formName,
                                                        UsernamePasswordAuthenticationToken principal) {
        checkFormFeeAccess(eventGroupName, formName, principal);
        checkBankingAccess(eventGroupName, formName, principal);

        User user = (User) principal.getPrincipal();
        EventGroup eventGroup = user.getEventGroupList()
                .stream()
                .filter(e -> StringUtils.equals(eventGroupName, e.getName()))
                .findAny().orElse(null);
        if(eventGroup == null) {
            throw new IllegalStateException("Feematching without banking data!");
        }

        BankingJob bankingJob = buildBankingJob(principal, eventGroupName);
        BankingHandles bankingHandles = buildBankingHandles(bankingJob);
        Runnable runnable = () -> {
            log.info("Get Umsaetze for eventGroup {} and formName {}", eventGroupName, formName);
            List<GVRKUms.UmsLine> umsLines = hbciService.getUmsaetze(eventGroup.getBankData().getIban(), bankingHandles);
            log.info("Do FormFeeTurnoverMatching for eventGroup {} and formName {}", eventGroupName, formName);
            formFeeTurnoverService.doFormFeeTurnoverMatching(eventGroupName, formName, umsLines);
            log.info("FormFeeMatching complete for eventGroup {} and formName {}", eventGroupName, formName);
        };
        startBankingJob(bankingJob, runnable);
    }


    private void startBankingJob(BankingJob bankingJob, Runnable runnable) {
        BankingStatus status = getBankingUtilities(bankingJob).getBankingStatus();
        if(status != BankingStatus.IDLE) {
            sendStatus(bankingJob, status);
        }

        ThreadGroup group = new ThreadGroup(bankingJob.userName + "-" +bankingJob.eventGroup);
        Thread job = new Thread(group, () -> {
            try {
                runnable.run();
                sendStatus(bankingJob, BankingStatus.SUCCESS);
            }catch (Exception e) {
                log.debug(e.getMessage(), e);
            }
            sendStatus(bankingJob, BankingStatus.IDLE);
        });
        stateMap.get(bankingJob).setJobThread(job);
        sendStatus(bankingJob, BankingStatus.PROCESSING);
        job.start();
    }

    private BankingHandles buildBankingHandles(BankingJob bankingJob) {
        BankingHandles handles = new BankingHandles();
        handles.setRequestUserid(buildHandle(bankingJob, BankingStatus.USERID));
        handles.setRequestPin(buildHandle(bankingJob, BankingStatus.PIN));
        handles.setRequestTan(buildHandle(bankingJob, BankingStatus.TAN));
        return handles;
    }

    private Function<String, Future<String>> buildHandle(BankingJob bankingJob, BankingStatus newStatus) {
        BankingUtilities bankingUtilities = getBankingUtilities(bankingJob);
        return s -> {
            CompletableFuture<String> userIdFuture = new CompletableFuture<>();
            bankingUtilities.setCurrentFuture(userIdFuture);
            sendStatus(bankingJob, newStatus);
            return userIdFuture;
        };
    }

    private void sendStatus(BankingJob bankingJob, BankingStatus bankingStatus) {
        setBankingStatus(bankingJob, bankingStatus);
        webSocket.convertAndSendToUser(bankingJob.userName,
                "/socket/banking/status/"+bankingJob.eventGroup+"/message",
                bankingStatus);
    }

    @MessageMapping("/{eventGroup}/fulfill")
    public synchronized void fulfill(@DestinationVariable String eventGroup,
                                              String payload,
                                              UsernamePasswordAuthenticationToken principal) {
        checkEventGroupPermission(eventGroup, principal);
        BankingJob bankingJob = buildBankingJob(principal, eventGroup);
        BankingUtilities bankingUtilities = stateMap.get(bankingJob);
        bankingUtilities.getCurrentFuture().complete(payload);
        sendStatus(bankingJob, BankingStatus.PROCESSING);
    }


    @MessageMapping("/{eventGroup}/status")
    public synchronized void sendStatus(@DestinationVariable String eventGroup,
                                                    UsernamePasswordAuthenticationToken principal) {
        checkEventGroupPermission(eventGroup, principal);
        User user = (User) principal.getPrincipal();
        BankingJob bankingJob = buildBankingJob(user, eventGroup);
        BankingUtilities bankingUtilities = getBankingUtilities(bankingJob);
        if(bankingUtilities.getBankingStatus() != BankingStatus.IDLE && !bankingUtilities.getJobThread().isAlive()) {
            sendStatus(bankingJob, BankingStatus.IDLE);
        }
        sendStatus(bankingJob, bankingUtilities.getBankingStatus());
    }

    private BankingUtilities getBankingUtilities(BankingJob bankingJob) {
        return stateMap.computeIfAbsent(bankingJob, k -> new BankingUtilities());
    }

    private void setBankingStatus(BankingJob bankingJob, BankingStatus bankingStatus) {
        BankingUtilities bankingUtilities = getBankingUtilities(bankingJob);
        bankingUtilities.setBankingStatus(bankingStatus);
        stateMap.put(bankingJob, bankingUtilities);
    }

    private BankingJob buildBankingJob(UsernamePasswordAuthenticationToken principal, String eventGroup) {
        return buildBankingJob((User) principal.getPrincipal(), eventGroup);
    }

    private BankingJob buildBankingJob(User user, String eventGroup) {
        String eventGroupId = user.getEventGroupList().stream().filter(eg -> StringUtils.equals(eg.getName(), eventGroup))
                .map(EventGroup::getName).findFirst().orElse(null);
        if(eventGroupId == null) {
            throw new IllegalArgumentException("EventGroup: " +eventGroup + " not found!");
        }
        return new BankingJob(user.getUsername(), eventGroupId);
    }

    private void checkFormFeeAccess(String eventGroupName, String formName, Authentication user) {
        if(!permissionAccessControl.checkingFormfeePermission(user, eventGroupName, formName)) {
            throw new AccessDeniedException("No FormFee Permission!");
        }
    }

    private void checkBankingAccess(String eventGroupName, String formName, Authentication user) {
        if(!permissionAccessControl.checkBankingPermission(user, eventGroupName, formName)) {
            throw new AccessDeniedException("No Banking Permission!");
        }
    }

    private void checkEventGroupPermission(String eventGroupName, Authentication user) {
        if(!eventGroupAccessControl.checkEventGroup(user, eventGroupName)) {
            throw new AccessDeniedException("No access to eventGroup!");
        }
    }
}
