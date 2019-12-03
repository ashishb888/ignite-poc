package poc.ignite.filters;

import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.lang.IgnitePredicate;

public class DataNodeFilter implements IgnitePredicate<ClusterNode> {

	private static final long serialVersionUID = -1242415912828642283L;
	public static String NODE_NAME = "data-node";

	@Override
	public boolean apply(ClusterNode node) {
		return NODE_NAME.equals(node.attributes().get("nodeName"));
	}
}
