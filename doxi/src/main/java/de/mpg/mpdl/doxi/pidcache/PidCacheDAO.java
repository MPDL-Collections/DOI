package de.mpg.mpdl.doxi.pidcache;

import javax.persistence.EntityManager;

public class PidCacheDAO {
  private final EntityManager em;

  public PidCacheDAO(EntityManager em) {
    this.em = em;
  }

  public void create(PidCache pidCache) {
    this.em.persist(pidCache);
  }
  
  public void remove(PidCache pidCache) {
    this.em.remove(pidCache);
  }
  
  public PidCache find(PidID pidID) {
    return this.em.find(PidCache.class, pidID.getIdAsString());
  }
  
  public PidCache getFirst() {
    final String query = "SELECT c FROM pid_cache c ORDER BY c.created DESC";
    return this.em.createQuery(query, PidCache.class).getSingleResult();
  }
  
  public long getSize() {
    final String query = "SELECT count(c) FROM pid_cache c";
    return this.em.createQuery(query, Long.class).getSingleResult();
  }
}