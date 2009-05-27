package sample.trg;

import java.util.List;

import org.springframework.test.annotation.AbstractAnnotationAwareTransactionalTests;

import sample.trg.model.Citizen;
import sample.trg.service.CitizenService;


public class JpaTest extends AbstractAnnotationAwareTransactionalTests {
	private CitizenService citizenService;
	
	public void setCitizenService(CitizenService citizenService) {
		this.citizenService = citizenService;
	}
	
	protected String[] getConfigLocations()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
		return new String[]
		{ "classpath:applicationContext.xml" };
	}
	
	@Override
	public void setName(String name) {
		System.out.println(name);
		super.setName(name);
	}
	
	public void testThis() {
		Citizen joe = new Citizen();
		joe.setName("Joe");
		joe.setJob("Bar Tender");
		citizenService.persist(joe);
		
		citizenService.flush();
		
		List<String> results = getJdbcTemplate().queryForList("select name from citizen", String.class);
		assertEquals(1, results.size());
		assertEquals("Joe", results.get(0));
	}
	
	public void testThat() {
		GenericTest gt = new GenericTest();
		gt.testOne();
	}
}
