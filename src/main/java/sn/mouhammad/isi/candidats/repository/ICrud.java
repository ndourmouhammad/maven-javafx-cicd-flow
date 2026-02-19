package sn.mouhammad.isi.candidats.repository;

import java.util.List;

public interface ICrud<T> {

    public List<T> findAll();
    public List<T> searchByAll (String mot);
}
