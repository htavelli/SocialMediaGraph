package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OutputPrinter {

	 
	/**
	 * Creates the File for the influencer graph info. Since the printer will be
	 * called with a loop to write info to the file, file creation needed 
	 * its own method call
	 * @param 		outputFilePath-- where file will be written
	 * @param 		networkInfo --
	 * @return  	File for writing data
	 */
	public File fileCreationForInfluencerInfo(String outputFilePath, String networkInfo) {
		File newFile = new File(outputFilePath);
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)), true);) {
			newFile.createNewFile();
			out.println(networkInfo);
			out.println();
		} catch (IOException e) {
			System.out.println("Error when trying to create file for influencer graph info.");
		}
		return newFile;

	}

	
	/**
	 * Saves info about the nodes in the influencer graph
	 * @param outputFile where data is being written
	 * @param graphInfo toString value from the graph  object
	 */
	public void printOutputInfluentialNodes(File outputFile, String graphInfo) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)), true);) {
			out.println(graphInfo);
		} catch (IOException e) {
			System.out.println("Error when trying to write output about influencer graphs.");
		}
	}

	
	/**
	 * Saves shortest paths from influencer to other nodes
	 * @param outputFile 	where data is being written 
	 * @param pathInfo 		String that tells influential node and which node it is finding path to
	 * @param path 			list of nodes along the shortest path
	 */
	public void printOutputShortestPathsToInfluencer(File outputFile, String pathInfo, List<Integer> path) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)), true);) {
			out.print(pathInfo);
			out.println(path);

		} catch (IOException e) {
			System.out.println("Error when trying to write output about influencer graphs.");
		}
	}

	
	/**
	 * Saves info about cascade model-- how many nodes were active each generation
	 * @param outputFilePath	path for file creation
	 * @param resultMap			tells how many nodes adopted new behavior
	 */
	public void printOutputNumNodesPerGenerationCascade(String outputFilePath, Map<Integer, Set<Integer>> resultMap) {
		File newFile = new File(outputFilePath);
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true)), true);) {
			newFile.createNewFile();
			int generation = 0;
			out.println("Generation, numberActive");
			for (int howMany : resultMap.keySet()) {
				out.print(generation + ", ");
				out.print(howMany);
				out.println();
				generation++;
			}
		} catch (IOException e) {
			System.out.println("Error when trying to write output for number of activeNodes for cascade.");
		}
	}

	
	/**
	 * Saves cascade data-- which nodes became active each generation
	 * @param outputFilePath 	tells where file should be created
	 * @param resultMap			tells which node nums became active during the generation
	 */
	public void printOutputActiveNodesEachGenerationCascade(String outputFilePath,
			Map<Integer, Set<Integer>> resultMap) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath, true)), true);) {
			int generation = 0;
			out.println("Generation, newlyActiveNodeNums");
			for (Set<Integer> nowActiveNodes : resultMap.values()) {
				out.print(generation + ", ");
				for (int num : nowActiveNodes) {
					out.print(num);
					out.print(", ");
				}
				out.println();
				generation++;
			}
		} catch (IOException e) {
			System.out.println("No file found when trying to write output data to file.");
		}
	}

}
