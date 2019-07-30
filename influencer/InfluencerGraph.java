package influencer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Extends Graph and provides functionality for analyzing graph results centered
 * around an influential node in a network
 * 
 * @author hillary
 * 
 */
public class InfluencerGraph extends Graph {

	private final int influentialNodeNum;

	public InfluencerGraph(int nodeNum) {
		super();
		this.influentialNodeNum = nodeNum;
	}

	/**
	 * Main functionality of class. Returns shortest path between influencer node
	 * and another node within its sphere of reach
	 */
	public List<Integer> getShortestPath(int otherNode) {
		Map<Integer, GraphNode> nodes = getAllNodes();
		if (!nodes.containsKey(otherNode) || !nodes.containsKey(influentialNodeNum)) {
			System.out.println("Nodes are invalid.  No path.");
			return null;
		}
		if (influentialNodeNum == otherNode) {
			return null;
		}
		boolean pathFound = false;
		Map<Integer, Integer> parentMap = new HashMap<Integer, Integer>();
		pathFound = performBFS(otherNode, parentMap, nodes);
		if (pathFound) {
			return getPath(otherNode, parentMap);
		} else {
			return null;
		}
	}

	// helper to getShortestPath that finds the shortest path
	private boolean performBFS(int outerNode, Map<Integer, Integer> parentMap, Map<Integer, GraphNode> nodes) {
		Set<Integer> visited = new HashSet<Integer>();
		Queue<Integer> toExplore = new LinkedList<Integer>();
		boolean found = false;
		toExplore.add(influentialNodeNum);
		while (!toExplore.isEmpty()) {
			int currNum = toExplore.remove();
			if (currNum == outerNode) {
				found = true;
				break;
			}
			GraphNode currNode = nodes.get(currNum);
			Set<Integer> followers = currNode.getFollowers().keySet();
			for (int follower : followers) {
				if (!visited.contains(follower)) {
					toExplore.add(follower);
					visited.add(follower);
					parentMap.put(follower, currNum);
				}
			}
		}
		return found;
	}

	// helper to getShortestPath that recreates the path
	private List<Integer> getPath(int outerNode, Map<Integer, Integer> parentMap) {
		List<Integer> bestPath = new LinkedList<Integer>();
		bestPath.add(outerNode);
		int next = parentMap.get(outerNode);
		while (next != influentialNodeNum) {
			bestPath.add(next);
			next = parentMap.get(next);
		}
		bestPath.add(influentialNodeNum);
		return bestPath;
	}

	// returns number of nodes are within the influencer's sphere
	public int influencerReach() {
		return getNumNodes() - 1;
	}

	public int getInfluentialNodeNum() {
		return influentialNodeNum;
	}

}
