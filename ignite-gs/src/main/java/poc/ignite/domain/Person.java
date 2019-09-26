package poc.ignite.domain;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person implements Serializable {

	private static final long serialVersionUID = 7491527457827685935L;

//	@QuerySqlField(index = true)
//	private long id;
//	@QuerySqlField(index = true)
//	private long orgId;
//	@QuerySqlField
//	private String firstName;
//	@QuerySqlField
//	private String lastName;
//	@QuerySqlField
//	private String resume;
//	@QuerySqlField
//	private double salary;

	private long id;
	private long orgId;
	private String firstName;
	private String lastName;
	private String resume;
	private double salary;

	public Person(long id, String firstName) {
		super();
		this.id = id;
		this.firstName = firstName;
	}

}
