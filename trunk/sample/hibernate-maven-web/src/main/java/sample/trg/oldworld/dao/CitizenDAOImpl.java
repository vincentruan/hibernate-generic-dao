package sample.trg.oldworld.dao;

import org.springframework.stereotype.Repository;

import sample.trg.oldworld.model.Citizen;

@Repository
public class CitizenDAOImpl extends BaseDAO<Citizen, Long> implements CitizenDAO {

}
