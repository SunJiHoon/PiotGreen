package com.piotgreen.piotgreen.scheduler;

import com.piotgreen.piotgreen.service.ReserveCommandDataStorageService;
import kotlinx.serialization.Required;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ScheduledTaskRunner {
    private final ReserveCommandDataStorageService reserveCommandService;

    // 매 1분마다 실행
//    @Scheduled(fixedRate = 60000)
    @Scheduled(fixedRate = 5000) //5초
    public void runScheduledCommands() {
        reserveCommandService.executeScheduledCommands();
    }

}
