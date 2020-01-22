package poc.ignite.domain;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PersonKey {
	private int id;
	@AffinityKeyMapped
	private int cityId;
}
