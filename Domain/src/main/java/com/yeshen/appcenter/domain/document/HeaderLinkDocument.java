package com.yeshen.appcenter.domain.document;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "headerlinks")
public class HeaderLinkDocument {
	@Id
	@JSONField(serialize = false)
	private String id;

	private String lang;

	private String href;

	private String title;

	@JSONField(serialize = false)
	private Integer weight;

	@JSONField(serialize = false)
	private Integer status;

	@JSONField(serialize = false)
	private String operator;

	@JSONField(serialize = false)
	private LocalDateTime createdAt;

	@JSONField(serialize = false)
	private LocalDateTime updatedAt;
}