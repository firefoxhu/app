/**
 * 
 */
package com.xinyang.app.core.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author zhailiang
 *
 */
@NoRepositoryBean
public interface XyRepository<T> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

}
