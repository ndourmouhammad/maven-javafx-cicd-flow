package sn.mouhammad.isi.candidats.repository.implement;

import jakarta.persistence.EntityManager;
import sn.mouhammad.isi.candidats.entity.Candidat;
import sn.mouhammad.isi.candidats.repository.ICrud;
import sn.mouhammad.isi.candidats.utils.JPAUtil;


import java.util.List;

public class CandidatRepository implements ICrud<Candidat> {

    private EntityManager em;

    public CandidatRepository() {
        this.em = JPAUtil.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public List<Candidat> findAll() {
        em.clear();
        return em.createQuery("from Candidat ", Candidat.class).getResultList();
    }


    @Override
    public List<Candidat> searchByAll(String mot) {
        try {
            String lowerMot = mot.toLowerCase();
            String query = "SELECT c FROM Candidat c WHERE LOWER(c.name) LIKE LOWER(:m) OR CAST(c.id AS string) LIKE :m";
            
            if ("junior".contains(lowerMot)) {
                query += " OR c.niveau < 20";
            }
            if ("senior".contains(lowerMot)) {
                query += " OR (c.niveau >= 20 AND c.niveau <= 50)";
            }
            if ("expert".contains(lowerMot)) {
                query += " OR c.niveau > 50";
            }
            

            query += " OR CAST(c.niveau AS string) LIKE :m";

            return em.createQuery(query, Candidat.class)
                    .setParameter("m", "%" + mot + "%")
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


}
