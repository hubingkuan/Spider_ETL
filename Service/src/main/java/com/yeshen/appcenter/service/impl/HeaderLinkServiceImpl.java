package com.yeshen.appcenter.service.impl;

import com.yeshen.appcenter.config.mysql.DataSourceContextHolder;
import com.yeshen.appcenter.domain.constants.SystemConstant;
import com.yeshen.appcenter.domain.document.HeaderLinkDocument;
import com.yeshen.appcenter.repository.mongo.HeaderLinkRepository;
import com.yeshen.appcenter.service.HeaderLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Date 2022/05/19  10:55
 * author  by HuBingKuan
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression("'${spring.application.name}'.equals('WebAppcenterService')")
public class HeaderLinkServiceImpl implements HeaderLinkService {
    private final MongoTemplate mongoTemplate;

    private final HeaderLinkRepository headerLinkRepository;


    @Override
    public List<HeaderLinkDocument> findHeaderLinks(String region) {
        if(!SystemConstant.SPECIAL_HEADER_LINK_REGIONS.contains(region)){
            String dataSourceName = DataSourceContextHolder.getDatabaseHolder();
            region = dataSourceName.split("_")[1].toLowerCase();
        }
        log.info("region:{}",region);
        Query query = new Query();
        query.addCriteria(new Criteria().andOperator(
                Criteria.where("lang").is(region),
                Criteria.where("status").is(SystemConstant.HEADER_RELEASE)
        ));
        query.with(Sort.by(
                Sort.Order.desc("weight"),
                Sort.Order.desc("updatedAt")));


        return mongoTemplate.find(query, HeaderLinkDocument.class);
/*        HeaderLinks headerLinks = new HeaderLinks();
        headerLinks.setLang(region);
        Example<HeaderLinks> example=Example.of(headerLinks);
        List<HeaderLinks> list = repository.findAll(example);*/

        /* 分页查询
         Sort sort = Sort.by("updateTime","thumbup").descending();
         PageRequest pageRequest = PageRequest.of(0, 5, sort);
         headerLinkRepository.findAll(pageRequest);*/
    }
}