package test.trg.dao.dao.original;

import org.springframework.stereotype.Repository;

import test.trg.shared.model.Person;


@Repository
public class PersonDAOImpl extends BaseGenericDAOImpl<Person, Long> implements PersonDAO {

}
