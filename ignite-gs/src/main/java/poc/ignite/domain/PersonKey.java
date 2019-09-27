package poc.ignite.domain;

import java.io.Serializable;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

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
public class PersonKey implements Serializable {

	private static final long serialVersionUID = -170700890808617562L;

	@AffinityKeyMapped
	private long id;
	private String firstName;

}
