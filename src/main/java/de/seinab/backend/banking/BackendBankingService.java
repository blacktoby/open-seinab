package de.seinab.backend.banking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BackendBankingService {

    @Autowired
    private SimpMessagingTemplate webSocket;


}
