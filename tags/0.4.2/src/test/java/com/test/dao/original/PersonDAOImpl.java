package com.test.dao.original;

import org.springframework.stereotype.Repository;

import com.test.model.Person;

@Repository
public class PersonDAOImpl extends BaseGenericDAOImpl<Person, Long> implements PersonDAO {

}
