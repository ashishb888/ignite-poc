package poc.ignite.filters;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;

public class WorkerNodeFilter implements IgnitePredicate<ClusterNode> {

	private static final long serialVersionUID = 4761356058637069151L;
	public static String NODE_NAME = "worker-node";

	@Override
	public boolean apply(ClusterNode node) {
		return NODE_NAME.equals(node.attributes().get("nodeName"));
	}
}
