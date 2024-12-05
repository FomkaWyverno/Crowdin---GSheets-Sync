package ua.wyverno.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.sync.crowdin.SynchronizationCrowdin;

@Service
public class SynchronizationService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    private final SynchronizationCrowdin synchronizationCrowdin;

    @Autowired
    public SynchronizationService(SynchronizationCrowdin synchronizationCrowdin) {
        this.synchronizationCrowdin = synchronizationCrowdin;
    }

    public void synchronizeTranslations() {
        this.synchronizationCrowdin.synchronizeToCrowdin();
    }
}
