package com.lyf.seexp.pojo;



import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
//@TableName("api_keys")
public class ApiKey {
    //@TableId(type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private String apiKey;
    private Date createdAt;
    private Date expiresAt;
    private Boolean isActive;
    private Boolean isPermanent;
}
