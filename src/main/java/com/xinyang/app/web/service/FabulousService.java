package com.xinyang.app.web.service;

import com.xinyang.app.core.model.Fabulous;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface FabulousService {


    Fabulous writeFabulous(HttpServletRequest request, Fabulous fabulous);

    void cancelFabulous(HttpServletRequest request,long fabulousId);

    List<Fabulous> findFabulousByArticleId(Pageable page,long articleId);

}
