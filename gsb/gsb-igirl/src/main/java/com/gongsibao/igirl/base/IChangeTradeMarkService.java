package com.gongsibao.igirl.base;

import com.gongsibao.entity.igirl.ChangeTradeMark;
import com.gongsibao.igirl.dto.ChangeTradeMark.ChangeTradeMarkDto;
import org.netsharp.base.IPersistableService;

import java.util.List;

public interface IChangeTradeMarkService extends IPersistableService<ChangeTradeMark> {
    List<ChangeTradeMarkDto> ctmToRobot();

    ChangeTradeMark updateCtmState(String agentFileNum,Integer state);
}
