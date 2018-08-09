package com.xinyang.app.core.validator;

import com.xinyang.app.core.model.Article;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class ArticleValidator implements Validator{

    private  final String ARTICLE_CONTENT = "content";

    @Override
    public boolean supports(Class<?> aClass) {
        return Article.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Article article = (Article) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors,ARTICLE_CONTENT,"NotEmpty");

        if (article.getContent().length() < 6 || article.getContent().length() > 256) {
            errors.rejectValue("username", "Size.article.content");
        }
    }
}
