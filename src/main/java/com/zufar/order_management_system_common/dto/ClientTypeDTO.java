package com.zufar.order_management_system_common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientTypeDTO {

    private Long id;

    private String shortName;

    private String fullName;

    private String typeCode;
}
