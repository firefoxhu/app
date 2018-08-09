package com.xinyang.app.web.service.impl;
import com.google.common.collect.Maps;
import com.xinyang.app.core.model.Category;
import com.xinyang.app.core.properties.XyProperties;
import com.xinyang.app.core.repository.CategoryRepository;
import com.xinyang.app.core.repository.TypeRepository;
import com.xinyang.app.web.domain.dto.CategoryDTO;
import com.xinyang.app.web.domain.dto.TypeDTO;
import com.xinyang.app.web.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private XyProperties xyProperties;

    @Override
    public Map<String, Object> findAll() {

        Map<String,Object> map = Maps.newHashMap();
        map.put("list",categoryRepository.findAll());
        return map;
    }

    @Override
    public Map<String, Object> findCategoryByCode(String[] codes) {

        List<Category> page = categoryRepository.findAll((root, query, cb)->cb.and(root.get("code").in(codes)),Sort.by(Sort.Direction.ASC,"sort"));

        Map<String,Object> map = Maps.newHashMap();
        map.put("list",page.parallelStream().map(
                            c -> CategoryDTO.builder()
                            .code(c.getCode())
                            .name(c.getName())
                            .picture(xyProperties.getFileConfig().getImageServer()+c.getPicture())
                            .sort(c.getSort())
                            .build()
                            ).collect(Collectors.toList()));

        return map;
    }

    @Override
    public Map<String, Object> findCategoryWithType() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("list",
        categoryRepository.findAll(Sort.by(Sort.Direction.ASC,"sort")).parallelStream().map(category ->
            CategoryDTO.builder().code(category.getCode()).name(category.getName()).picture(category.getPicture()).sort(category.getSort())
                    .typeDTOS(
                        typeRepository.findAll((root,query,cb)->cb.and(cb.equal(root.get("categoryCode"),category.getCode())),Sort.by(Sort.Direction.ASC,"sort")).stream().map(type ->
                                TypeDTO.builder().typeId(type.getId()).name(type.getName()).picture(type.getPicture()).categoryCode(type.getCategoryCode()).sort(type.getSort()).build()
                        ).collect(Collectors.toList())
                    ).build()
        ).collect(Collectors.toList()));

        return map;
    }
}
