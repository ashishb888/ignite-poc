package poc.ignite.domain;

import org.apache.ignite.cache.query.annotations.QuerySqlField;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class StockTrade {

	@QuerySqlField(index = true)
	@JsonProperty("SYMBOL")
	private String symbol;
	@QuerySqlField(index = true)
	@JsonProperty("SERIES")
	private String series;
	@QuerySqlField
	@JsonProperty("OPEN")
	private double open;
	@QuerySqlField
	@JsonProperty("HIGH")
	private double high;
	@QuerySqlField
	@JsonProperty("LOW")
	private double low;
	@QuerySqlField
	@JsonProperty("CLOSE")
	private double close;
	@QuerySqlField
	@JsonProperty("LAST")
	private double last;
	@QuerySqlField
	@JsonProperty("PREVCLOSE")
	private double prevClose;
	@QuerySqlField
	@JsonProperty("TOTTRDQTY")
	private long totTrdQty; // Used long for totTrdQty
	@QuerySqlField
	@JsonProperty("TOTTRDVAL")
	private double totTrdVal;
	@QuerySqlField
	@JsonProperty("TIMESTAMP")
	private String timestamp; // We can use Date/Timestamp for timestamp (07-JAN-2019) instead String
	@QuerySqlField
	@JsonProperty("TOTALTRADES")
	private double totalTrades;
	@QuerySqlField
	@JsonProperty("ISIN")
	private String isin;

}
