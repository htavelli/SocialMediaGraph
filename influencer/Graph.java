
package influencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author Hillary
 * @class Graph data structure with GraphNode implementation to represent
 *        vertices and adjacency lists to represent edges
 */
public class Graph {

	private Map<Integer, GraphNode> nodes;

	public Graph() {
		nodes = new HashMap<Integer, GraphNode>();
	}

	/**
	 * @param num -- value to assign this node
	 */
	public void addVertex(int num) {
		if (!nodes.containsKey(num)) {
			GraphNode node = new GraphNode(num);
			nodes.put(num, node);
		}
	}

	/**
	 * @param from -- node number of node that was retweeted
	 * @param to   -- node number of node who retweeted the other's post
	 */
	public void addEdge(int from, int to) {
		if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
			throw new IllegalArgumentException("Can't add an edge yet. Add nodes first.");
		}
		GraphNode fromNode = nodes.get(from);
		GraphNode toNode = nodes.get(to);
		fromNode.addNodeIFollow(to);
		toNode.addFollowerNode(from);

	}

	/**
	 * Makes a copy of the graph for manipulation without affecting the original
	 * 
	 * @return -- copy of the graph object and all vertices and edges
	 */
	public Graph deepCopy() {
		Graph newCopy = new Graph();
		Set<Integer> visited = new HashSet<Integer>();
		for (int nodeNum : nodes.keySet()) {
			if (!visited.contains(nodeNum)) {
				newCopy.addVertex(nodeNum);
				GraphNode node = nodes.get(nodeNum);
				Map<Integer, Integer> nodesFollowed = node.getNodesIFollow();
				for (int followedNode : nodesFollowed.keySet()) {
					newCopy.addVertex(followedNode);
					int edgeWeight = nodesFollowed.get(followedNode);
					for (int i = 0; i < edgeWeight; i++) {
						newCopy.addEdge(nodeNum, followedNode);
					}
				}
			}
		}
		return newCopy;
	}

	/**
	 * @param howMany -- how many influential nodes to find
	 * @return List<Graph> subgraphs based on the influential node
	 */
	public List<Graph> getInfluencerGraphs(int howMany) {
		List<Integer> influencerNodeNums = findInfluencers(howMany);
		List<Graph> influencerGraphList = new LinkedList<Graph>();
		for (int nodeNum : influencerNodeNums) {
			influencerGraphList.add(buildOneInfluencerGraph(nodeNum));
		}
		return influencerGraphList;
	}

	// helper to buildInfluencerGraphs that sorts nodes by number of times
	// retweeted
	private List<Integer> findInfluencers(int howMany) {
		if (howMany > getNumNodes()) {
			throw new IllegalArgumentException("Parameter is greater " + "than number of users in this graph.");
		}
		List<Integer> influencers = new ArrayList<Integer>(howMany);
		List<GraphNode> allNodes = new ArrayList<GraphNode>(nodes.values());
		for (GraphNode node : nodes.values()) {
			allNodes.add(node);
		}
		Collections.sort(allNodes, (a, b) -> b.compareTo(a));
		for (int i = 0; i < howMany; i++) {
			influencers.add(allNodes.get(i).getNodeNum());
		}
		return influencers;
	}

	// helper to buildInfluencerGraph that builds a graph including all
	// nodes that are connected to the influencer node
	private Graph buildOneInfluencerGraph(int nodeNum) {
		Graph influencerGraph = new InfluencerGraph(nodeNum);
		Set<Integer> visited = new HashSet<Integer>();
		Stack<Integer> toVisit = new Stack<Integer>();
		toVisit.push(nodeNum);
		while (!toVisit.empty()) {
			int currNum = toVisit.pop();
			if (!visited.contains(currNum)) {
				visited.add(currNum);
				influencerGraph.addVertex(currNum);
				GraphNode curr = nodes.get(currNum);
				for (int follower : curr.getFollowers().keySet()) {
					addInfluencerVerticesAndEdges(influencerGraph, visited, toVisit, currNum, follower);
				}

			}
		}
		return influencerGraph;
	}

	// helper to buildInfluencerGraphs that takes care of adding the vertices
	// and edges of the influencer graph
	private void addInfluencerVerticesAndEdges(Graph influencerGraph, Set<Integer> visited, Stack<Integer> toVisit,
			int currNum, int nextNum) {
		if (!visited.contains(nextNum)) {
			influencerGraph.addVertex(nextNum);
			GraphNode nextNode = nodes.get(nextNum);
			int edgeWeight = nextNode.getNodesIFollow().get(currNum);
			for (int i = 0; i < edgeWeight; i++) {
				influencerGraph.addEdge(nextNum, currNum);
			}
			toVisit.push(nextNum);
		}
	}

	/**
	 * A more detailed view of the graph object than a typical toString()
	 * 
	 * @return -- string with details about each nodes in the total graph
	 */
	public String graphInfo() {
		StringBuilder sb = new StringBuilder();
		for (GraphNode node : nodes.values()) {
			sb.append(node.toString() + "\n");
		}
		return sb.toString();

	}

	/**
	 * @return -- how many nodes in the graph
	 */
	public int getNumNodes() {
		return nodes.size();
	}

	/**
	 * @return -- all the values for the nodes in the graph
	 */
	public Set<Integer> getNodeNumbers() {
		return nodes.keySet();
	}

	/**
	 * Makes a new hashmap of graph nodes but this is a shallow copy so the graph
	 * node references are to the graph's original nodes protected access so that
	 * package classes can access nodes as necessary for their functionality but
	 * protects mutable graph nodes outside of package
	 */
	protected Map<Integer, GraphNode> getAllNodes() {
		return new HashMap<Integer, GraphNode>(nodes);
	}

	/**
	 * Returns one graph node object protected access so that package classes can
	 * access nodes as necessary for their functionality but protects mutable graph
	 * nodes outside of package
	 */
	protected GraphNode getOneNode(int nodeNum) {
		return nodes.get(nodeNum);
	}

	/**
	 * Brief description of graph suitable for printing
	 */
	@Override
	public String toString() {
		return "Graph with " + getNumNodes() + " nodes";
	}

}
