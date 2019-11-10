package com.zufar.order_management_system_common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;

    @NotEmpty(message = "Please provide a goods name in an order. It is empty.")
    @NotNull(message = "Please provide a goods name in an order. It is absent.")
    @Size(min = 5, max = 160, message = "Goods name length in an order should be from 5 to 160.")
    private String goodsName;

    @NotNull(message = "Please provide a category id in a order. It is absent.")
    private Category category;

    @NotNull(message = "Please provide a client id in a order. It is absent.")
    private Long clientId;
}
