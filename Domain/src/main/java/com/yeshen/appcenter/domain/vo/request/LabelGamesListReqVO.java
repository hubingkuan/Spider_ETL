package com.yeshen.appcenter.domain.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Date 2022/1/24/0024
 * author by HuBingKuan
 */
@Data
public class LabelGamesListReqVO {
    @NotBlank(message = "Label cannot be empty")
    @Size(min = 1,message = "Tag name length must be greater than 1")
    private String labelName;

    @Min(value = 1,message = "The start page must not be less than 1")
    private Integer pageNum;

    @Range(min = 1,max = 24,message = "The number of queries is between 1 and 24")
    private Integer pageSize;
}