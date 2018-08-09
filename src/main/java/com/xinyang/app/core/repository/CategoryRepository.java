package com.xinyang.app.core.repository;

import com.xinyang.app.core.model.Category;

public interface CategoryRepository extends XyRepository<Category>{

    Category findCategoryByCode(String code);
}
