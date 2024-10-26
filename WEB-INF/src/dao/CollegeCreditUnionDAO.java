package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import model.CollegeCreditUnion;

public class CollegeCreditUnionDAO {
    protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("mydb");

    public CollegeCreditUnionDAO() {
        // TODO Auto-generated constructor stub
    }

    public void persist(CollegeCreditUnion collegeCreditUnion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(collegeCreditUnion);
        em.getTransaction().commit();
        em.close();
    }

    public void remove(CollegeCreditUnion collegeCreditUnion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.merge(collegeCreditUnion));
        em.getTransaction().commit();
        em.close();
    }

    public CollegeCreditUnion merge(CollegeCreditUnion collegeCreditUnion) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        CollegeCreditUnion updatedCollegeCreditUnion = em.merge(collegeCreditUnion);
        em.getTransaction().commit();
        em.close();
        return updatedCollegeCreditUnion;
    }
}
