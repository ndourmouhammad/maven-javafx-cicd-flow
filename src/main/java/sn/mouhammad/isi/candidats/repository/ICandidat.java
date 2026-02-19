package sn.mouhammad.isi.candidats.repository;

import sn.mouhammad.isi.candidats.entity.Candidat;

import java.util.List;

public interface ICandidat {

    public List<Candidat> findAll();
    public List<Candidat> searchByAll (String mot);
}
