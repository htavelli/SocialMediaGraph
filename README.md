# SocialMediaGraph

## Overview
Purpose: Determine the most influential users in a graph (based on whose tweets are retweeted most often) and model information flow through the network from influential users when decision adoption has differing rewards.

Questions: : Which nodes are the most influential in this social network? How far is their reach within the network? If influential nodes are the seeds for information flow, how will the information cascade through the network?  How many other users will make a decision based on the cascade? Given differing rewards for change or inertia and a differing number of influential nodes seeded at the outset, how will the cascade change?

## Data Structures
The total graph is represented with an adjacency hashmap of node integers to node objects. 
The influencer subgraphs are represented in a subclass of the total graph.  
The node class represents multiple edges between nodes as a hashmap and stores both nodes followed (outEdges) and nodes following (inEdges).  It implements Comparable so nodes can be sorted.
The cascade model is represented as its own class that stores the graph as a member variable.  
It has a series of methods to run the information flow model.  Each generation of the cascade is stored in a linkedHashMap to record how many nodes are now active mapped to a Set of the node numbers that became active during that iteration.  A linkedHashMap was used to ensure ordering of the map and thereby “storing” the generation ordering without explicitly using it as a key or value in the hashMap.

## Data Set


