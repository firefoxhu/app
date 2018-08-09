package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Type;
import java.util.List;

public interface TypeRepository extends XyRepository<Type>{

    List<Type> findTypeByIdIn(List<Long> typeIds);

    List<Type> findTypeByCategoryCode(String code);
}
