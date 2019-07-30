package influencer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import util.GraphLoader;
import util.OutputPrinter;

/**
 * Class that is the simple UI for gleaning data on influential nodes in a
 * network of retweets and how their decision adoption (either individually or
 * concurrently) would cascade through the network with varying rewards for
 * change and inertia
 * 
 * @author Hillary
 */
public class InfluencerModelingApp {

	public static void main(String[] args) {
		InfluencerModelingApp app = new InfluencerModelingApp();
		Scanner scanner = new Scanner(System.in);
		// System.out.println("What is the file to build the network?");
		// String filePath = scanner.nextLine();
		String filePath = "data/twitter_higgs.txt";
		int lastIndex = filePath.lastIndexOf("/");
		String inputFileName = filePath.substring(lastIndex + 1, filePath.length() - 4);
		Graph graph = app.createGraph(filePath);
		List<Integer> influentialNodes = app.runFindInfluencersProgram(scanner, false, graph, inputFileName);
		app.runCascadeModelProgram(scanner, graph, influentialNodes, inputFileName);
		scanner.close();
		System.out.println("done");

	}

	/**
	 * Helper method to main() that creates the graph with the GraphLoader class and
	 * returns a deep copy for manipulation by the programs
	 */
	private Graph createGraph(String filePath) {
		Graph g = new Graph();
		GraphLoader.loadGraph(g, filePath);
		return g.deepCopy();
	}

	/**
	 * Method that finds the influencer graphs and prints info about them
	 * 
	 * @param scanner-- scanner of Sysin to get user input from console
	 * @param print--   whether detailed information will be saved in a file
	 * @param graph     -- network of retweets
	 * @return though it does not help with data visualization for this program,
	 *         returning a list of influential node numbers is essential for the
	 *         cascade model program to run
	 */
	private List<Integer> runFindInfluencersProgram(Scanner scanner, boolean print, Graph graph, String inputFileName) {
		OutputPrinter printer = new OutputPrinter();
		String outputFilePath = "data/output/InfluencerInfoFrom" + inputFileName + ".txt";
		String networkInfo = graph.toString();
		File outputFile = printer.fileCreationForInfluencerInfo(outputFilePath, networkInfo);
		int howMany = getInfluencerNumUserInput(scanner);
		List<Graph> influencerGraphs = getInfluencers(howMany, graph);
		List<Integer> influentialNodeNums = new ArrayList<Integer>(howMany);
		for (Graph influencerGraph : influencerGraphs) {
			String graphInfo = influencerGraph + " and influential node: "
					+ ((InfluencerGraph) influencerGraph).getInfluentialNodeNum();
			printer.printOutputInfluentialNodes(outputFile, graphInfo);
			influentialNodeNums.add(((InfluencerGraph) influencerGraph).getInfluentialNodeNum());
		}
		if (print) {
			printInfluencerGraphPaths(scanner, false, influencerGraphs, printer, outputFile);
		}
		return influentialNodeNums;
	}

	/**
	 * Helper to runFindInfluencerProgram that gets user input about number of
	 * influential nodes to find
	 */
	private int getInfluencerNumUserInput(Scanner scanner) {
		System.out.println("How many influencers do you want to find?");
		return scanner.nextInt();
	}

	/**
	 * Helper to runFindInfluencersProgram that uses the FindInfluencer class
	 * algorithm to make influential node graphs
	 */
	private List<Graph> getInfluencers(int howMany, Graph g) {
		return g.getInfluencerGraphs(howMany);
	}

	/**
	 * Optional helper method to runFindInfluencersProgram that allows user to
	 * decide which nodes to explore in more detail
	 */
	private Set<Integer> getNodesForPathsUserInput(Scanner scanner, Graph g) {
		scanner.nextLine();
		Set<Integer> nodes = g.getNodeNumbers();
		System.out.println(nodes);
		System.out.println("I'll find the shortest path between the " + "influencer and another node. What nodes? ");
		System.out.println("Press enter between each node.");
		System.out.println("Type 0 when all nodes entered.");
		Set<Integer> nodesForPaths = new HashSet<Integer>();
		while (scanner.hasNextInt()) {
			int next = scanner.nextInt();
			if (next != 0) {
				nodesForPaths.add(next);
			} else {
				break;
			}
		}
		return nodesForPaths;

	}

	/**
	 * Optional helper to runFindInfluencersProgram that prints paths from network
	 * nodes to the influential node. Essentially, this runs a breadth first search
	 * and will be time consuming on large data sets.
	 */
	private void printInfluencerGraphPaths(Scanner scanner, boolean userChooses, List<Graph> influencerGraphs,
			OutputPrinter printer, File outputFile) {
		List<Integer> influentialNodes = new LinkedList<Integer>();
		for (Graph infGraph : influencerGraphs) {
			int infNodeNum = ((InfluencerGraph) infGraph).getInfluentialNodeNum();
			influentialNodes.add(infNodeNum);
			Set<Integer> nodesForPaths;
			if (userChooses) {
				nodesForPaths = getNodesForPathsUserInput(scanner, infGraph);
			} else {
				nodesForPaths = infGraph.getNodeNumbers();
			}
			printPaths(outputFile, (InfluencerGraph) infGraph, nodesForPaths, printer);
		}
	}

	/**
	 * Helper to printInfluencerGraphPaths that takes care of the printing
	 */
	private void printPaths(File outputFile, InfluencerGraph g, Set<Integer> nodesForPaths, OutputPrinter printer) {
		for (int node : nodesForPaths) {
			List<Integer> path = g.getShortestPath(node);
			if (path != null) {
				String pathInfo = "Path from Node: " + node + " to Influencer node: " + g.getInfluentialNodeNum()
						+ "--> ";
				printer.printOutputShortestPathsToInfluencer(outputFile, pathInfo, path);
			}
		}
	}

	/**
	 * Method that gets user input about the cascade parameters and runs the cascade
	 * model
	 * 
	 * @param scanner          SysIn
	 * @param graph            network graph
	 * @param influentialNodes identifies which node (for each cascade test) is
	 *                         adopting the new behavior and modeling the cascade
	 *                         from that node
	 */
	private void runCascadeModelProgram(Scanner scanner, Graph graph, List<Integer> influentialNodes,
			String inputFileName) {
		System.out.print("Preparing to run the cascade model for ");
		System.out.println(graph);
		System.out.println("How many times do you want to run the model?");
		int iterations = scanner.nextInt();
		System.out.println("What is the reward for inertia (not adopting the new behavior)? Enter a positive integer.");
		int rewardForInertia = scanner.nextInt();
		System.out.println("What is the reward for changing behavior? Enter a positive integer.");
		int rewardForChange = scanner.nextInt();
		System.out.println("Should the cascade run with all influencers seeded concurrently or one at a time?");
		System.out.println("Enter \'one\' or \'all\' ");
		String seeding = scanner.nextLine();
		boolean seedingOne = validateAndSetSeeding(scanner, seeding);
		doCascade(graph, influentialNodes, seedingOne, iterations, rewardForInertia, rewardForChange, inputFileName);

	}

	/**
	 * Helper to runCascadeModelingProgram that validates user input regarding
	 * single or concurrent seeding of influencers
	 */
	private boolean validateAndSetSeeding(Scanner scanner, String seeding) {
		while (!seeding.toLowerCase().equals("one") && !seeding.toLowerCase().equals("all")) {
			System.out.println("Enter \'one\' or \'all\'");
			seeding = scanner.nextLine();
		}
		if (seeding.toLowerCase().equals("one")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper to runCascadeModelProgram that creates the
	 * cascadeModelingWithInfluencers object and creates the cascade
	 */
	private void doCascade(Graph graph, List<Integer> influentialNodes, boolean seedingOne, int iterations,
			int rewardForInertia, int rewardForChange, String inputFileName) {
		CascadeModelingWithInfluencers cascade = new CascadeModelingWithInfluencers(graph);
		if (seedingOne) {
			for (int influentialNode : influentialNodes) {
				Map<Integer, Set<Integer>> resultMap = cascade.model(iterations, influentialNode, rewardForInertia,
						rewardForChange);
				writeFileCascadeResultsOne(influentialNode, resultMap, inputFileName);
			}
		} else {
			Map<Integer, Set<Integer>> resultMap = cascade.model(iterations, influentialNodes, rewardForInertia,
					rewardForChange);
			writeFileCascadeResultsMany(influentialNodes, resultMap, inputFileName);
		}
	}

	/**
	 * Helper to runCascadeModel that writes to a file with multiple influencers
	 * seeded concurrently
	 * 
	 * @param influentialNodes --set of seeded nodes where cascade begins
	 * @param resultMap        --map with key to number of nodes that are active
	 *                         mapped to a set of those active node numbers added
	 *                         during each subsequent iteration
	 */
	private void writeFileCascadeResultsMany(List<Integer> influentialNodes, Map<Integer, Set<Integer>> resultMap,
			String inputFileName) {
		StringBuilder sb = new StringBuilder(influentialNodes.size() * 2);
		for (int i : influentialNodes) {
			sb.append(i + "_");
		}
		String nodeNums = sb.toString();
		String outputFilePath = "data/output/cascadeFromNodes" + nodeNums.substring(0, nodeNums.length() - 1)
				+ inputFileName + ".csv";
		printToFile(resultMap, outputFilePath);
	}

	/**
	 * Helper to runCascadeModel that writes to a file when one node seeded
	 * 
	 * @param influentialNode --where cascade begins
	 * @param resultMap       --map with key to number of nodes that are active
	 *                        mapped to a set of those active node numbers added
	 *                        during each subsequent iteration
	 */
	private void writeFileCascadeResultsOne(int influentialNode, Map<Integer, Set<Integer>> resultMap,
			String inputFileName) {
		String outputFilePath = "data/output/CascadeFromNode" + Integer.toString(influentialNode) + inputFileName
				+ ".csv";
		printToFile(resultMap, outputFilePath);
	}

	/**
	 * @param resultMap      cascade results
	 * @param outputFilePath file path to save results
	 */
	private void printToFile(Map<Integer, Set<Integer>> resultMap, String outputFilePath) {
		OutputPrinter outputPrinter = new OutputPrinter();
		outputPrinter.printOutputNumNodesPerGenerationCascade(outputFilePath, resultMap);
		outputPrinter.printOutputActiveNodesEachGenerationCascade(outputFilePath, resultMap);
	}

}
