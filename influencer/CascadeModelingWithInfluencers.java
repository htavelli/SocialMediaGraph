package influencer;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Hillary
 * @class Class that performs the cascade model of information flow using both
 *        influence (as measured by how often a node is retweeted) and rewards
 *        for inertia or change.
 */
public class CascadeModelingWithInfluencers {

	private Graph graph;

	public CascadeModelingWithInfluencers(Graph graph) {
		this.graph = graph;
	}

	/**
	 * Models information cascade through a network where the user can input varying
	 * rewards for inertia or changing behavior. User also controls how many
	 * generations are modeled
	 * 
	 * @param iterations       how many generations of cascade should be tested,
	 *                         must be a positive number
	 * @param influentialNode  the ONE influential node from which to cascade
	 * @param rewardForInertia reward given for remaining inactive (not adopting new
	 *                         behavior), must be a positive number
	 * @param rewardForChange  reward given for changing behavior, must be a
	 *                         positive number
	 * @return linkedhashmap with key representing how many nodes have changed
	 *         behavior and value representing a set of the active nodes who changed
	 *         behavior during each subsequent iteration.
	 */
	public Map<Integer, Set<Integer>> model(int iterations, int influentialNode, int rewardForInertia,
			int rewardForChange) {
		validateArguments(iterations, rewardForInertia, rewardForChange);
		Map<Integer, Set<Integer>> result = new LinkedHashMap<Integer, Set<Integer>>();
		Set<Integer> activeNodes = new HashSet<Integer>();
		activeNodes.add(influentialNode);
		return beginCascade(iterations, rewardForInertia, rewardForChange, result, activeNodes);
	}

	/**
	 * Overload method to complement model(int, int, int, int) where a list of
	 * influential nodes to seed concurrently is used instead
	 * 
	 * @param iterations       how many generations of cascade should be tested,
	 *                         must be a positive number
	 * @param influentialNodes list of nodes to seed concurrently
	 * @param rewardForInertia reward given for remaining inactive (not adopting new
	 *                         behavior), must be a positive number
	 * @param rewardForChange  reward given for changing behavior, must be a
	 *                         positive number
	 * @return linkedhashmap with key representing how many nodes have changed
	 *         behavior and value representing a set of the active nodes who changed
	 *         behavior during each subsequent iteration.
	 */
	public Map<Integer, Set<Integer>> model(int iterations, List<Integer> influentialNodes, int rewardForInertia,
			int rewardForChange) {
		validateArguments(iterations, rewardForInertia, rewardForChange);
		Map<Integer, Set<Integer>> result = new LinkedHashMap<Integer, Set<Integer>>();
		Set<Integer> activeNodes = new HashSet<Integer>(influentialNodes);
		return beginCascade(iterations, rewardForInertia, rewardForChange, result, activeNodes);
	}

	/**
	 * helper to model overload methods that starts cascade once influential nodes
	 * are set
	 */
	private Map<Integer, Set<Integer>> beginCascade(int iterations, int rewardForInertia, int rewardForChange,
			Map<Integer, Set<Integer>> result, Set<Integer> activeNodes) {
		result.put(activeNodes.size(), new HashSet<Integer>(activeNodes));
		float rewardProbability = calculateRewardProbability(rewardForInertia, rewardForChange);
		runGenerations(iterations, result, activeNodes, rewardProbability);
		return result;
	}

	/**
	 * helper to model that validates iterations, rewardForInertia, and
	 * rewardForChange
	 */
	private void validateArguments(int iterations, int rewardForInertia, int rewardForChange) {
		if (iterations <= 0 || rewardForInertia <= 0 || rewardForChange <= 0) {
			throw new IllegalArgumentException("Iterations and reward values must be positive integers.");
		}
	}

	/**
	 * helper to model that runs the specified number of iterations and saves the
	 * number who change behavior in the result map
	 */
	private void runGenerations(int iterations, Map<Integer, Set<Integer>> result, Set<Integer> activeNodes,
			float rewardProbability) {
		for (int i = 0; i < iterations; i++) {
			Set<Integer> visited = new HashSet<Integer>();
			Set<Integer> nodesChangedThisIteration = new HashSet<Integer>();
			for (int activeNodeNum : activeNodes) {
				GraphNode activeNode = graph.getOneNode(activeNodeNum);
				for (int followerNodeNum : activeNode.getFollowers().keySet()) {
					checkFollowerNodes(activeNodes, rewardProbability, visited, nodesChangedThisIteration,
							followerNodeNum);
				}
			}
			if (nodesChangedThisIteration.isEmpty()) {
				break;
			}
			for (int node : nodesChangedThisIteration) {
				activeNodes.add(node);
			}
			result.put(activeNodes.size(), new HashSet<Integer>(nodesChangedThisIteration));
		}
	}

	/**
	 * helper to model() that determines whether a node will change behavior based
	 * on percentage of nodes followed exhibit the behavior
	 */
	private void checkFollowerNodes(Set<Integer> activeNodes, float rewardProbability, Set<Integer> visited,
			Set<Integer> nodesChangedThisIteration, int followerNodeNum) {
		if (!visited.contains(followerNodeNum) && !activeNodes.contains(followerNodeNum)) {
			GraphNode followerNode = graph.getOneNode(followerNodeNum);
			float influencePercent = calculateInfluencePercent(followerNode, activeNodes);
			if (influencePercent > rewardProbability) {
				nodesChangedThisIteration.add(followerNodeNum);
			}
			visited.add(followerNodeNum);
		}
	}

	/**
	 * helper method to model() that uses edgeWeight of active nodes compared to all
	 * retweets made to calculate the influence percentage that connected active
	 * nodes have on this node
	 */
	private float calculateInfluencePercent(GraphNode node, Set<Integer> activeNodes) {
		Map<Integer, Integer> nodesFollowed = node.getNodesIFollow();
		float thoseActive = 0;
		for (int followedNode : nodesFollowed.keySet()) {
			if (activeNodes.contains(followedNode)) {
				thoseActive += nodesFollowed.get(followedNode);
			}
		}
		return thoseActive / (node.getTotalRetweetsMade());
	}

	/**
	 * helper to model() that calculates the ratio of rewards Inputed by the user
	 */
	private float calculateRewardProbability(int rewardForInertia, int rewardForChange) {
		return ((float) rewardForInertia) / (rewardForChange + rewardForInertia);
	}

}
