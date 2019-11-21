package poc.ignite.filters;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FirstNodeFilter implements IgnitePredicate<ClusterNode> {

	private static final long serialVersionUID = 1L;
	public static String NAME = "FIRST";

	@Override
	public boolean apply(ClusterNode node) {
		log.info("NAME: " + node.attributes().get("NAME"));
		log.info("Node ID: " + node.id() + " toString : " + node.toString() + " Client: " + node.isClient());

		return NAME.equals(node.attributes().get("NAME"));
	}
}
