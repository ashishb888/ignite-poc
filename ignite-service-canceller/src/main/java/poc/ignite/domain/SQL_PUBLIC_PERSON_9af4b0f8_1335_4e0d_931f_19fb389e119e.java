package poc.ignite.domain;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SQL_PUBLIC_PERSON_9af4b0f8_1335_4e0d_931f_19fb389e119e {
	@AffinityKeyMapped
	@QuerySqlField
	private int id;
	@QuerySqlField
	private int city_id;
	@QuerySqlField
	private String name;
}