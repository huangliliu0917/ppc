package com.cyq.wsserver.server;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.tio.core.intf.TioUuid;

@Slf4j
@Component
public class ImTioUuid implements TioUuid {

    @Override
    public String uuid() {
        return IdWorker.get32UUID();
    }
}
