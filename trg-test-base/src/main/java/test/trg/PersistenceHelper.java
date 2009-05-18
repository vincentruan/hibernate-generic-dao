package test.trg;

import java.io.Serializable;

public interface PersistenceHelper {
	public <T> T find(Class<T> type, Serializable id);

	public void persist(Object entity);
	
	public void flush();
	
	public void clear();
}
