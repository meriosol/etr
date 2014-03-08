package com.meriosol.etr.dao.mapper;

import com.meriosol.etr.domain.Event;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/01/14
 */
public class EventCategoryMapperTest {
    private static Logger LOG = null;
    private static final String MYBATIS_RESOURCE_CONFIG = "com/meriosol/etr/dao/mybatis-config.xml";
    private SqlSessionFactory sessionFactory;

    @BeforeClass
    public static void init() {
        LOG = LoggerFactory.getLogger(EventCategoryMapperTest.class);
    }

    @Before
    public void setUp() {
        initSessionFactory();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test @Ignore
    public void testEventCategoryRetrieveByCode() {
        final String module = "testEventCategoryRetrieveByCode";
        assertNotNull("sessionFactory should not be null!", this.sessionFactory);
        // CAUTION: DB should be initialized with events starting from 1000001.
        String sampleEventCategoryCode = "MESSAGE";

        LOG.info("BEFORE Retrieve event category by code='{}'...", sampleEventCategoryCode);

        try (SqlSession session = this.sessionFactory.openSession(TransactionIsolationLevel.READ_COMMITTED)) {
            EventCategoryMapper eventCategoryMapper = session.getMapper(EventCategoryMapper.class);
            Event.Category category = eventCategoryMapper.retrieveEventCategory(sampleEventCategoryCode);
            session.commit();
            assertNotNull(String.format("Event Category for code '%s' should not be null!", sampleEventCategoryCode), category);
            assertEquals(String.format("Event Category code '%s' should be the same as sampleEventCategoryCode='%s'!"
                    , category.getCode(), sampleEventCategoryCode), category.getCode(), sampleEventCategoryCode);
            LOG.info("AFTER Retrieve event category by code='{}': {}", sampleEventCategoryCode, category);
        }

    }

    //--------------------------------
    // Utils:
    private void initSessionFactory() {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(MYBATIS_RESOURCE_CONFIG);
            this.sessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            LOG.error(String.format("Error while opening inputStream for resource '%s'!", MYBATIS_RESOURCE_CONFIG), e);
        }
    }


}
