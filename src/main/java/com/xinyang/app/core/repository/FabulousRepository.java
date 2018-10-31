package com.xinyang.app.core.repository;
import com.xinyang.app.core.model.Fabulous;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FabulousRepository extends XyRepository<Fabulous>{

    List<Fabulous> findFabulousByArticleId(Pageable page, long  articleId);

}
