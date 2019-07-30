# SocialMediaGraph

## Overview
### Purpose
Use retweet data from Twitter users to determine the most influential users in a graph (based on whose tweets are retweeted most often) and model information flow through the network from influential users when decision adoption has differing rewards.

### Questions
Which nodes are the most influential in this social network? How far is their reach within the network? If influential nodes are the seeds for information flow, how will the information cascade through the network?  How many other users will make a decision based on the cascade? Given differing rewards for change or inertia and a differing number of influential nodes seeded at the outset, how will the cascade change?

## Data Structures
The total graph is represented with an adjacency hashmap of node integers to node objects. 
The influencer subgraphs are represented in a subclass of the total graph.  
The node class represents multiple edges between nodes as a hashmap and stores both nodes followed (outEdges) and nodes following (inEdges).  It implements Comparable so nodes can be sorted.
The cascade model is represented as its own class that stores the graph as a member variable.  
It has a series of methods to run the information flow model.  Each generation of the cascade is stored in a linkedHashMap to record how many nodes are now active mapped to a Set of the node numbers that became active during that iteration.  A linkedHashMap was used to ensure ordering of the map and thereby “storing” the generation ordering without explicitly using it as a key or value in the hashMap.

## Data Set
twitter-higgs.txt

## Classes
### Graph
The graph class contains the hashmap of nodes and methods: addVertex, addEdge, buildInfluencerGraphs, along with getters.  A deepCopy method recreates the entire graph to pass to another class without compromising the original graph or graph node objects.  Of note, I decided on two protected methods, getAllNodes and getOneNode, so that InfluencerGraph subclass and CascadeModelingWithInfluencers could access the nodes without storing them as member variables in their own classes.  I decided that while protected access is undesirable in most cases, here it was necessary to allow large data sets and minimize memory usage.

### GraphNode
GraphNode class contains hashMaps of inEdges(followers) and outEdges(nodesFollowed) with integer variables to store total retweets and total times retweeted.  I included these two int variables so that compareTo could quickly get the total without iterating through the hashmap.  This saved time complexity with sorting the nodes.

### InfluencerGraph
InfluencerGraph is a subclass of Graph and contains an additional member variable to store the influential node number upon which the graph was built.  Methods include those to find shortest paths and calculate the influencer’s reach along with getters.

### CascadeModelingWithInfluencers
CascadeModelingWithInfluencers compares reward values against the percentage of following nodes that have adopted the behavior and determines if, and when, each node will change behavior.  It has two public methods.  One runs the cascade model with one influencer node seeded at the beginning.  The other seeds all identified influencers concurrently.  I included both implementations to give program users more options to analyze data differently.  Other methods are private helpers to the main class functionality.  Program user choices are passed in as parameters to customize functionality.  
Class Name: OutputPrinter
Purpose and Description of Class: This is a utility class that creates two kinds of files for the program.  First, it saves shortest paths between an influencer node and other nodes in the influencer graph.  It also creates a csv file that saves the cascade modeling results.  

### CascadeModelingApp
This class represents the user interface for the program.  It contains the main method and many private methods to get program user input and run the algorithms to build the graphs, find influential nodes, build influencer graphs, and create the cascade model of information flow.

### GraphLoader
This class was provided by UCSD MOOC Team and was provided as part of the <a href = "coursera.org">Coursera.org</a> Specialization <a href = "https://www.coursera.org/specializations/java-object-oriented"> OO Java Programming: Data Structures and Beyond</a> by UCSD to read in the provided text file of twitter users.
  
### OutPutPrinter
A util class to save results of the influencer program in a file for analysis.

