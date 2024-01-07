package app.repository;

import app.model.StopWordObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopWordRepository extends JpaRepository<StopWordObject, Integer> {

}
