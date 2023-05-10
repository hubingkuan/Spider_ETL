package com.yeshen.appcenter.domain.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Date 2022/06/15  17:51
 * author  by HuBingKuan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XMLVO {
    private String location;
    private LocalDate localDate;
}