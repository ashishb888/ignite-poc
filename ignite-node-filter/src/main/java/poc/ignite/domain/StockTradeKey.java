package poc.ignite.domain;

import org.apache.ignite.cache.affinity.AffinityKeyMapped;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class StockTradeKey {
	@AffinityKeyMapped
	private String symbol;
	private String series;
	private String timestamp;
}
