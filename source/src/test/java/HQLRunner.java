import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class HQLRunner extends JFrame {
	private static final long serialVersionUID = 7980173937104587551L;
	
	//private static final String SPRING_CONFIG_LOCATION = "C:\\workspace\\eclipse1\\hibernate-generic-dao\\src\\test\\resources\\jUnit-applicationContext.xml";
	private static final String SPRING_CONFIG_LOCATION = "jUnit-applicationContext.xml";
	private static final String SESSION_FACTORY_BEAN_ID = "sessionFactory";
	
	public static void main(String[] args) {
		HQLRunner instance = new HQLRunner(SPRING_CONFIG_LOCATION, SESSION_FACTORY_BEAN_ID);
		instance.setVisible(true);
	}
	
	private ApplicationContext appContext;
	private String sessionFactoryBeanId;
	
	private JTextArea hqlInput;
	private JTable resultsTable;
	private JButton goButton;
	private TheTableModel tableModel;
	
	private HQLRunner(String springConfigLocation, String sessionFactoryBeanId) {
		super("HQL Runner");
		initAppContext(springConfigLocation, sessionFactoryBeanId);
		buildGUI();
	}
	
	private void buildGUI() {
		hqlInput = new JTextArea(6, 70);
		hqlInput.setFont(new Font("Courier New", Font.BOLD, 14));
		hqlInput.setBorder(new EmptyBorder(4, 4, 4, 4));
		
		resultsTable = new JTable(tableModel = new TheTableModel());
		resultsTable.setPreferredSize(new Dimension(15,400));
		
		goButton = new JButton(new AbstractAction("Execute") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				execute(hqlInput.getText());
			}
		});
		
		this.setLayout(new BorderLayout());
		this.add(hqlInput, BorderLayout.NORTH);
		this.add(goButton, BorderLayout.CENTER);
		this.add(resultsTable, BorderLayout.SOUTH);
		this.pack();
		this.setLocation(200,150);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void initAppContext(String springConfigLocation, String sessionFactoryBeanId) {
		//appContext = new FileSystemXmlApplicationContext(springConfigLocation);
		appContext = new ClassPathXmlApplicationContext(springConfigLocation);
		this.sessionFactoryBeanId = sessionFactoryBeanId;
		initDB();
	}
	
	private SessionFactory getSessionFactory() {
		return (SessionFactory) appContext.getBean(sessionFactoryBeanId);
	}
	
	private void initDB() {
		Session session = getSessionFactory().openSession();
		Statement statement = null;
		try {
			statement = session.connection().createStatement();
			statement.executeUpdate("insert into `address` (`id`,`city`,`state`,`street`,`zip`) values (1,'Chicago','IL','940 N Fairfield','60610'),(2,'Chicago','IL','734 N Fairfield, Apt 3','60610'),(3,'Chicago','IL','3290 W Fulton','60610')");
			statement.executeUpdate("insert into `home` (`id`,`type`,`address_id`) values (1,'house',1),(2,'apartment',2),(3,'house',3)");
			statement.executeUpdate("insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (1,65,'1943-12-17 15:13:59','Grandpa','Alpha',100.65,NULL,3,NULL),(2,65,'1943-12-17 15:13:59','Grandma','Alpha',100.65,NULL,3,NULL),(3,39,'1969-12-17 15:13:59','Papa','Alpha',100.39,1,1,2),(4,40,'1968-12-17 15:13:59','Mama','Alpha',100.4,NULL,1,NULL),(5,39,'1969-12-17 15:13:59','Papa','Beta',100.39,NULL,2,NULL),(6,38,'1970-12-17 15:13:59','Mama','Beta',100.38,1,2,2),(7,10,'1998-12-17 15:13:59','Joe','Alpha',100.1,3,1,4),(8,9,'1999-12-17 15:13:59','Sally','Alpha',100.09,3,1,4),(9,10,'1998-12-17 15:13:59','Joe','Beta',100.1,5,2,6),(10,14,'1994-12-17 15:13:59','Margret','Beta',100.14,5,2,6)");
			statement.executeUpdate("insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (1,1,4444,'Jimmy',NULL,'spider','\0',1),(0,2,1111,'Mr','Wiggles','fish',NULL,1),(1,3,2222,'Miss','Prissy','cat','\0',2),(1,4,3333,'Norman',NULL,'cat','\0',1)");
			statement.executeUpdate("insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left front leg',0),(1,'right front leg',1),(1,'left frontish leg',2),(1,'right frontish leg',3),(1,'left hindish leg',4),(1,'right hindish leg',5),(1,'left hind leg',6),(1,'right hind leg',7),(3,'left front leg',0),(3,'right front leg',1),(3,'left hind leg',2),(3,'right hind leg',3),(4,'left front leg',0),(4,'right front leg',1),(4,'left hind leg',2),(4,'right hind leg',3)");
			statement.close();
		} catch (Throwable e) {
			tableModel.setError(e);
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			session.close();
		}
	}
	
	private void execute(String hql) {
		Session session = getSessionFactory().openSession();
		try {
			List<?> results = session.createQuery(hql).list();
			tableModel.setResults(results);
		} catch (Throwable e) {
			tableModel.setError(e);
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	private class TheTableModel extends AbstractTableModel {

		Object[][] data = new Object[0][0];
		
		public void setResults(List<?> results) {
			if (results == null || results.size() == 0) {
				data = new Object[1][1];
				data[0][0] = "-- No Results --";
			} else {
				Object example = results.get(0);
				if (example instanceof Object[]) {
					data = new Object[results.size()][((Object[]) example).length];
					for (int i = 0; i < results.size(); i++) {
						Object[] row = (Object[]) results.get(i);
						for (int j = 0; j < row.length; j++) {
							data[i][j] = row[j];
						}
					}
				} else if (example instanceof Collection) {
					data = new Object[results.size()][((Collection) example).size()];
					for (int i = 0; i < results.size(); i++) {
						Collection row = (Collection) results.get(i);
						int j = 0;
						for (Object o : row)
							data[i][j++] = o;
					}
				} else if (example instanceof Map) {
					Object[] keys = new Object[((Map) example).keySet().size()];
					int k = 0;
					for (Object key : ((Map) example).keySet()) {
						keys[k++] = key;
					}
					
					data = new Object[results.size()][keys.length];
					for (int i = 0; i < results.size(); i++) {
						Map row = (Map) results.get(i);
						for (int j = 0; j < keys.length; j++) {
							data[i][j] = row.get(keys[j]);
						}
					}
				} else {
					data = new Object[results.size()][1];
					for (int i = 0; i < results.size(); i++) {
						Object row = results.get(i);
						data[i][0] = row;
					}
				}
			}
			
			this.fireTableStructureChanged();
		}
		
		public void setError(Throwable e) {
			data = new Object[1][1];
			data[0][0] = e.toString();
			
			this.fireTableStructureChanged();
		}
		
		public int getColumnCount() {
			return data.length == 0 ? 0 : data[0].length;
		}

		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return data[rowIndex][columnIndex];
		}
		
	}

}
