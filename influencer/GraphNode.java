package influencer;

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a single user in the social network graph. Vertices are
 * represented as an integer. Edges are stored in HashMap<Integer, Integer> with
 * node number of the other node keyed to the weight of the edge (how many times
 * this node and the other retweeted.
 * 
 * @author hillary
 * 
 *
 */
public class GraphNode implements Comparable<GraphNode> {

	private final int nodeNum;
	private Map<Integer, Integer> followerNodes;
	private Map<Integer, Integer> nodesIFollow;
	private int totalRetweetsMade;
	private int totalTimesRetweeted;

	/**
	 * @param nodeNum --value assigned to this node
	 */
	public GraphNode(int nodeNum) {
		this.nodeNum = nodeNum;
		this.followerNodes = new HashMap<Integer, Integer>();
		this.nodesIFollow = new HashMap<Integer, Integer>();
	}

	/**
	 * @param nodeNum --value of the other node that retweeted this node
	 */
	public void addFollowerNode(int nodeNum) {
		if (!followerNodes.containsKey(nodeNum)) {
			followerNodes.put(nodeNum, 1);
		} else {
			followerNodes.put(nodeNum, followerNodes.get(nodeNum) + 1);
		}
		totalTimesRetweeted++;
	}

	/**
	 * @param nodeNum --value of the other node that I retweeted
	 */
	public void addNodeIFollow(int nodeNum) {
		if (!nodesIFollow.containsKey(nodeNum)) {
			nodesIFollow.put(nodeNum, 1);
		} else {
			nodesIFollow.put(nodeNum, nodesIFollow.get(nodeNum) + 1);
		}
		totalRetweetsMade++;
	}

	public HashMap<Integer, Integer> getFollowers() {
		return new HashMap<Integer, Integer>(followerNodes);
	}

	public HashMap<Integer, Integer> getNodesIFollow() {
		return new HashMap<Integer, Integer>(nodesIFollow);
	}

	public int getNodeNum() {
		return nodeNum;
	}

	public int getTotalRetweetsMade() {
		return totalRetweetsMade;
	}

	public int getTotalTimesRetweeted() {
		return totalTimesRetweeted;
	}

	@Override
	public String toString() {
		return getNodeNum() + " following: " + nodesIFollow + " and followed by: " + followerNodes + " Made "
				+ totalRetweetsMade + " retweets. Was retweeted " + totalTimesRetweeted + " times.";
	}

	@Override
	/**
	 * Compares this node to others with total number of times retweeted for
	 * ordering
	 */
	public int compareTo(GraphNode other) {
		int thisNodeTot = this.getTotalTimesRetweeted();
		int otherNodeTot = other.getTotalTimesRetweeted();
		if (thisNodeTot < otherNodeTot) {
			return -1;
		} else {
			if (thisNodeTot > otherNodeTot) {
				return 1;
			}
			return 0;
		}
	}

}
