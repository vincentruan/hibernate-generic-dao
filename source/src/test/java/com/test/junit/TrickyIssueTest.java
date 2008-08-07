package com.test.junit;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.test.model.Home;
import com.test.model.Person;
import com.trg.dao.GeneralDAO;
import com.trg.search.Search;

public class TrickyIssueTest extends TestBase {
	private GeneralDAO generalDAO;
	
	public void setGeneralDAO(GeneralDAO generalDAO) {
		this.generalDAO = generalDAO;
	}
	
	public static void main(String[] args) {
//		BeanFactoryLocator bfl = ContextSingletonBeanFactoryLocator.getInstance("classpath:beanRefContext.xml");
//		BeanFactoryReference bfRef = bfl.useBeanFactory("e2e.application.context");
//		BeanFactory beanFactory = bfRef.getFactory();
		
//		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("jUnit-applicationContext.xml"));
		
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("jUnit-applicationContext.xml");
		BeanFactory beanFactory = appContext;
		
		
		TrickyIssueTest instance = new TrickyIssueTest();
		
		instance.setGeneralDAO((GeneralDAO) beanFactory.getBean("generalDAO"));
		instance.setJoeA((Person) beanFactory.getBean("joeA"));
		instance.setSallyA((Person) beanFactory.getBean("sallyA"));
		instance.setMamaA((Person) beanFactory.getBean("mamaA"));
		instance.setPapaA((Person) beanFactory.getBean("papaA"));
		instance.setJoeB((Person) beanFactory.getBean("joeB"));
		instance.setMargretB((Person) beanFactory.getBean("margretB"));
		instance.setMamaB((Person) beanFactory.getBean("mamaB"));
		instance.setPapaB((Person) beanFactory.getBean("papaB"));
		instance.setGrandmaA((Person) beanFactory.getBean("grandmaA"));
		instance.setGrandpaA((Person) beanFactory.getBean("grandpaA"));
	}
	
	/**
	 * The alias error occurs when using fetch mode FETCH_MAP. It occurs when
	 * there is a fetch that has a key with no "." in it and is the same as a
	 * property that is used in a filter.
	 */
	public void testAliasError() {
		initDB();
		
		List<Map<String, Object>> resultMap;
		
		Search s = new Search(Person.class);
		s.addFilterEqual("firstName", "Joe");
		s.addFilterEqual("age", 10);
		s.addSort("lastName");
		s.setFetchMode(Search.FETCH_MAP);
		
		s.addFetch("firstName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		
		
		s.addFetch("lastName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals("Alpha", resultMap.get(0).get("lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals("Beta", resultMap.get(1).get("lastName"));
		
		
		s.clearFetch();
		s.addFetch("firstName", "firstName");
		s.addFetch("age"); //this uses age for the property and key
		s.addFetch("lastName", "Last Name");
		s.addFetch("mother.lastName");
		
		resultMap = generalDAO.search(s);
		assertEquals(2, resultMap.size());
		assertEquals("Joe", resultMap.get(0).get("firstName"));
		assertEquals(10, resultMap.get(0).get("age"));
		assertEquals("Alpha", resultMap.get(0).get("Last Name"));
		assertEquals("Alpha", resultMap.get(0).get("mother.lastName"));
		assertEquals("Joe", resultMap.get(1).get("firstName"));
		assertEquals(10, resultMap.get(1).get("age"));
		assertEquals("Beta", resultMap.get(1).get("Last Name"));
		assertEquals("Beta", resultMap.get(1).get("mother.lastName"));
	}

	public void testEagerFetchingPagingError() {
		initDB();
		
		Search s = new Search(Home.class);
		
		assertEquals(3, generalDAO.search(s).size());
		
		s.setMaxResults(3);
		assertEquals(3, generalDAO.search(s).size());
		
		s.setMaxResults(2);
		assertEquals(2, generalDAO.search(s).size());
		
		s.setMaxResults(1);
		assertEquals(1, generalDAO.search(s).size());
		
		s.setMaxResults(2);
		s.setPage(1);
		assertEquals(1, generalDAO.search(s).size());
	}
}
