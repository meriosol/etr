package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;
import org.apache.ibatis.annotations.*;

/**
 * Event Category POJO for dictionary select.
 *
 * @author meriosol
 * @version 0.1
 * @since 24/01/14
 */
@CacheNamespace(implementation = org.mybatis.caches.ehcache.EhcacheCache.class)
interface EventCategoryMapper {
    interface DmlCommands {
        String RETRIEVE_CATEGORY = "select code, name from event_categories where code = #{code}";
    }


    /**
     * @param code
     * @return Event Category.
     */
    @Select(DmlCommands.RETRIEVE_CATEGORY)
    @Options(useCache = true)
    @MapKey("code")
    Event.Category retrieveEventCategory(@Param("code") String code);


}
