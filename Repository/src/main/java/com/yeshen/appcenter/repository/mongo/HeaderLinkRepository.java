package com.yeshen.appcenter.repository.mongo;

import com.yeshen.appcenter.domain.document.HeaderLinkDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HeaderLinkRepository extends MongoRepository<HeaderLinkDocument, String> {
}