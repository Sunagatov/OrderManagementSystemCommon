package com.zufar.order_management_system_common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OperationResult {

    private String timestamp;
    private String message;
    private String status;
}
