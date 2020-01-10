package com.troy.trade.ws.api.model.dto.out;

import com.troy.commons.dto.out.ResData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 当前挂单DTO
 *
 * @author dp
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenOrderResDto extends ResData {

    boolean result;
}
