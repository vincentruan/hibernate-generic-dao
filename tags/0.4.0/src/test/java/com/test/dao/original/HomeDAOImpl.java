package com.test.dao.original;

import org.springframework.stereotype.Repository;

import com.test.model.Home;
import com.trg.dao.dao.original.GenericDAOImpl;

@Repository
public class HomeDAOImpl extends GenericDAOImpl<Home, Long> implements HomeDAO {

}
