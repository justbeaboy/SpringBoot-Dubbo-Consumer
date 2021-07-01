package com.nacos.test.controller.search.repository;

import com.nacos.test.controller.search.document.MrsoftrockDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

/**
 * @author Mr.SoftRock
 * @Date 2021/7/1 09:52
 **/
@Service
public interface MrsoftrockDocRepository extends ElasticsearchRepository<MrsoftrockDocument,String> {
}
