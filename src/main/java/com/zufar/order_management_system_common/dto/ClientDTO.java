package com.zufar.order_management_system_common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private Long id;

    @NotEmpty(message = "Please provide a client's short name. It is empty.")
    @NotNull(message = "Please provide a client's short name. It is absent.")
    @Size(min = 1, max = 60, message = "A client's short name length should be from 1 to 60.")
    private String shortName;

    @NotEmpty(message = "Please provide a client's full name. It is empty.")
    @NotNull(message = "Please provide a client's full name. It is absent.")
    @Size(min = 5, max = 255, message = "A client's short name length should be from 5 to 255.")
    private String fullName;

    @NotNull(message = "Please provide a client's type id.")
    private Long clientTypeId;

    private ClientTypeDTO clientType;
    
    @NotEmpty(message = "Please provide a client's inn. It is empty.")
    @NotNull(message = "Please provide a client's inn. It is absent.")
    @Size(min = 2, max = 12, message = "A client's inn length should be from 2 to 12.")
    private String inn;

    @Size(min = 2, max = 10, message = "A client's okpo length should be from 2 to 10.")
    private String okpo;

    private List<OrderDTO> orders;

    private LocalDateTime creationDate;

    private LocalDateTime modificationDate;
}
