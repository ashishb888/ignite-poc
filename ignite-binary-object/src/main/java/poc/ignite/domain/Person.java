package poc.ignite.domain;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

public class Person {
	@AffinityKeyMapped
	@QuerySqlField
	private int id;
	@QuerySqlField
	private String t1;
	@QuerySqlField
	private String t2;

	public Person() {
		super();
	}

	public Person(int id, String t1, String t2) {
		super();
		this.id = id;
		this.t1 = t1;
		this.t2 = t2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getT1() {
		return t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public String getT2() {
		return t2;
	}

	public void setT2(String t2) {
		this.t2 = t2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (id != other.id)
			return false;
		if (t1 == null) {
			if (other.t1 != null)
				return false;
		} else if (!t1.equals(other.t1))
			return false;
		if (t2 == null) {
			if (other.t2 != null)
				return false;
		} else if (!t2.equals(other.t2))
			return false;
		return true;
	}

}
