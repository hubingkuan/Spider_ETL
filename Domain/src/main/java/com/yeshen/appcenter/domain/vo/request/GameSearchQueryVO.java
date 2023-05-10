package com.yeshen.appcenter.domain.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Date 2022/1/24 19:43
 * author by YangGuo
 */
@Data
public class GameSearchQueryVO {
    @Min(value = 1,message = "The start pageNum must not be less than 1")
    private Integer pageNum;

    @Range(min = 1,max = 24,message = "The pageSize of queries is between 1 and 24")
    private Integer pageSize;

    @Size(max = 50,message = "Please control the query keywords within 50")
    @NotBlank(message = "The gameName param not null")
    private String keywords;
}