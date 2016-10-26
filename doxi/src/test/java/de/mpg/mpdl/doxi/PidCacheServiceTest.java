package de.mpg.mpdl.doxi;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.mpdl.doxi.pidcache.PidCacheService;
import de.mpg.mpdl.doxi.pidcache.PidID;
import de.mpg.mpdl.doxi.rest.JerseyApplicationConfig;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PidCacheServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(PidCacheServiceTest.class);

  private EntityManager em;
  private PidCacheService pidCacheService;

  @Before
  public void setUp() throws Exception {
    this.em = JerseyApplicationConfig.emf.createEntityManager();
    this.pidCacheService = new PidCacheService(em);
  }

  @After
  public void tearDown() throws Exception {
    this.em.close();
  }

  @Test
  public void test_1_removeAll_empty() throws Exception {
    LOG.info("--------------------- STARTING test_1_removeAll_empty ---------------------");

    long size = this.pidCacheService.getSize();

    while (size > 0) {
      PidID pidID = this.pidCacheService.getFirst();
      if (pidID != null) {
        this.em.getTransaction().begin();
        this.pidCacheService.remove(pidID);
        this.em.getTransaction().commit();
        size = this.pidCacheService.getSize();
      }
    }

    Assert.assertEquals(size, 0);

    boolean empty = this.pidCacheService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_1_removeAll_empty ---------------------");
  }

  @Test
  public void test_2_add_getFirst() throws Exception {
    LOG.info("--------------------- STARTING test_2_add_getFirst ---------------------");

    PidID pidID1 = PidID.create("TEST1/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID1);
    this.em.getTransaction().commit();
    PidID _pidID1 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID1);

    PidID pidID2 = PidID.create("TEST2/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID2);
    this.em.getTransaction().commit();
    PidID _pidID2 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID2);

    PidID pidID3 = PidID.create("TEST3/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.add(pidID3);
    this.em.getTransaction().commit();
    PidID _pidID3 = this.pidCacheService.getFirst();

    Assert.assertEquals(pidID1, _pidID3);

    LOG.info("--------------------- FINISHED test_2_add_getFirst ---------------------");
  }

  @Test
  public void test_3_remove() throws Exception {
    LOG.info("--------------------- STARTING test_4_remove ---------------------");

    long size1 = this.pidCacheService.getSize();

    PidID pidID1 = PidID.create("TEST1/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID1);
    this.em.getTransaction().commit();

    long size2 = this.pidCacheService.getSize();

    Assert.assertEquals(size1, size2 + 1);

    PidID pidID2 = PidID.create("TEST2/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID2);
    this.em.getTransaction().commit();

    long size3 = this.pidCacheService.getSize();

    Assert.assertEquals(size2, size3 + 1);

    PidID pidID3 = PidID.create("TEST3/00-001Z-0000-002B-FC67-5");
    this.em.getTransaction().begin();
    this.pidCacheService.remove(pidID3);
    this.em.getTransaction().commit();

    long size4 = this.pidCacheService.getSize();

    Assert.assertEquals(size3, size4 + 1);

    LOG.info("--------------------- FINISHED test_3_remove ---------------------");
  }

  @Test
  public void test_4_full() throws Exception {
    LOG.info("--------------------- STARTING test_4_full ---------------------");

    long size = this.pidCacheService.getSize();

    int i = 0;
    while (size < this.pidCacheService.getSizeMax()) {
      PidID pidID = PidID.create("TEST/" + ++i);
      this.em.getTransaction().begin();
      this.pidCacheService.add(pidID);
      this.em.getTransaction().commit();
      size = this.pidCacheService.getSize();
    }

    boolean full = this.pidCacheService.isFull();

    Assert.assertTrue(full);

    LOG.info("--------------------- FINISHED test_4_full ---------------------");
  }

  @Test
  public void test_5_removeAll_empty() throws Exception {
    LOG.info("--------------------- STARTING test_5_removeAll_empty ---------------------");

    long size = this.pidCacheService.getSize();

    while (size > 0) {
      PidID pidID = this.pidCacheService.getFirst();
      if (pidID != null) {
        this.em.getTransaction().begin();
        this.pidCacheService.remove(pidID);
        this.em.getTransaction().commit();
        size = this.pidCacheService.getSize();
      }
    }

    Assert.assertEquals(size, 0);

    boolean empty = this.pidCacheService.isEmpty();

    Assert.assertTrue(empty);

    LOG.info("--------------------- FINISHED test_5_removeAll_empty ---------------------");
  }
}
