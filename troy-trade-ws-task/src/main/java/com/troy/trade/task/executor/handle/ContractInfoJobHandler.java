package com.troy.trade.task.executor.handle;

import com.troy.task.core.biz.model.ReturnT;
import com.troy.task.core.handler.IJobHandler;
import com.troy.task.core.handler.annotation.JobHandler;
import com.troy.trade.ws.scheduled.InstrumentsSyncUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 交易所合约信息同步
 *
 * @author yanping
 */
@Slf4j
@JobHandler(value="contractInfoJobHandler")
@Component
public class ContractInfoJobHandler extends IJobHandler {

    @Autowired
    private InstrumentsSyncUtil instrumentsSyncUtil;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        instrumentsSyncUtil.initInstruments();
        return SUCCESS;
    }
}
