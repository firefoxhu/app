package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Mchnt;
public interface MchntRepository extends XyRepository<Mchnt>{

    Mchnt findMchntByPhone(String mobile);

    Mchnt findMchntByUserId(Long userId);
}
