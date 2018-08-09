package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Type;
import com.xinyang.app.core.repository.TypeRepository;
import com.xinyang.app.web.domain.dto.TypeDTO;
import com.xinyang.app.web.domain.support.ResultMap;
import com.xinyang.app.web.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;

    @Override
    public Map<String, Object> findTypeByCategoryCode(String[] codes, Pageable pageable) {
        log.info("当前类别参数：{}",codes);
        // cb.and(cb.in(root.get("categoryCode")).in(codes) wrong
        // cb.and(root.get("code").in(codes) right
        Page<Type> page = typeRepository.findAll((root, query, cb)->cb.and(root.get("categoryCode").in(codes)),pageable);

        Map<String,Object> map = Maps.newHashMap();
        map.put("hasNext",page.hasNext());
        map.put("map",page.getContent().parallelStream()
                .map(t -> TypeDTO.builder().typeId(t.getId()).categoryCode(t.getCategoryCode()).name(t.getName()).picture(t.getPicture()).sort(t.getSort()).build())
                .collect(
                        Collectors.groupingBy(
                                TypeDTO::getCategoryCode
                        )
                ));

        return map;
    }

    @Override
    public Map<String, Object> findTypeByCategory(String code) {
        return ResultMap.getInstance().put("list",typeRepository.findTypeByCategoryCode(code).stream() .map(t ->
                TypeDTO.builder().typeId(t.getId()).name(t.getName()).build())
                .collect(Collectors.toList())).toMap();
    }
}
