import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class GraphMap {

	private Map<String, List<String>> graphRelationMap = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> visitedGraphNodeMap = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> inconsistentNodeRelationMap = new LinkedHashMap<String, List<String>>();
	private Map<String, List<String>> inconsistentNodeMap = new LinkedHashMap<String, List<String>>();
	private Set<String> subGraphConsistentPaths = new LinkedHashSet<>();
	private String rootNode = null;

	/**
	 * Solution 1 : Traverse and form all subgraphs
	 * 
	 * @param node
	 */
	public void traverseGraph(String node) {

		List<String> nodeList = new LinkedList<>();
		if (visitedGraphNodeMap.isEmpty()) {
			List<String> rootNodePaths = new ArrayList<String>();
			rootNodePaths.add(node);
			visitedGraphNodeMap.put(node, rootNodePaths);
			subGraphConsistentPaths.add(node);
		} else {
			// Scan all the keys in the visited map to check for the node's
			// parent

			//System.out.println("visiting node..." + node);

			for (String mapKey : visitedGraphNodeMap.keySet()) {

				// checks if any keys in the visited map has the current
				// visited
				// node as its child.
				if (graphRelationMap.containsKey(mapKey) && graphRelationMap.get(mapKey).contains(node)) {

					// appends the current node to the paths which contains
					// parent key.
					for (List<String> mapValues : visitedGraphNodeMap.values()) {

					//	System.out.println("printing mapvalues ...." + mapValues + " of mapkey " + mapKey);
						//System.out.println("For mapKey "+ mapKey);
						for (String mapValue : mapValues) {
							if (mapValue.indexOf(mapKey) != -1) {

								//System.out.println("Entered if since makpkey " + mapKey + " found in mapvalue " + mapValue);
								
								StringBuffer buffer = new StringBuffer();
								buffer.append(mapValue+" ");
								//buffer.append(" ");
								buffer.append(node);
								nodeList.add(buffer.toString());
								subGraphConsistentPaths.add(buffer.toString());
								
							}
						}

					}

				}
			}

			if (!visitedGraphNodeMap.containsKey(node)) {
				//System.out.println("For node "+ node +"nodeList is "+nodeList);
				visitedGraphNodeMap.put(node, nodeList);
			}
		}

		if (graphRelationMap.get(node) != null && visitedGraphNodeMap.keySet().contains(node)) {
			for (String childKey : graphRelationMap.get(node)) {
				traverseGraph(childKey);
			}
		}
	}

	/**
	 * Soultion 2: Traverse and form all consistent subgraphs
	 * 
	 * @param node
	 */

	public void traverseConsistentGraphs(String node) {

		List<String> nodeList = new LinkedList<>();

		if (visitedGraphNodeMap.isEmpty()) {
			List<String> rootNodePaths = new ArrayList<String>();
			rootNodePaths.add(node);
			visitedGraphNodeMap.put(node, rootNodePaths);
			subGraphConsistentPaths.add(node);
		} else {
			// Scan all the keys in the visited map to check for the node's
			// parent
			for (String mapKey : visitedGraphNodeMap.keySet()) {

				// checks if any keys in the visited map has the current visited
				// node as its child.
				if (graphRelationMap.containsKey(mapKey) && graphRelationMap.get(mapKey).contains(node)) {

					//System.out.println("node visiting " + node);

					// appends the current node to the paths which contains
					// parent key.
					for (List<String> mapValues : visitedGraphNodeMap.values()) {

						for (String mapValue : mapValues) {
							//System.out.println("mapValue orig "+ mapValue);
							if (mapValue.indexOf(mapKey) != -1) {

								StringBuffer buffer = new StringBuffer();
								buffer.append(mapValue);
								
								//System.out.println("mapValue "+ mapValue);
								buffer.append(node);
								nodeList.add(buffer.toString());
								
								if (!inconsistentNodeRelationMap.containsKey(node)) {
									subGraphConsistentPaths.add(buffer.toString());
								}

							}
						}

					}
					
					//System.out.println("visitedGraphNodeMap.."+ visitedGraphNodeMap);

				}

			}

			if (!visitedGraphNodeMap.containsKey(node)) {
				//System.out.println(nodeList);
				Collections.sort(nodeList, new Comparator<String>() {
					@Override
					public int compare(String s1, String s2) {
						return s1.length() - s2.length();
					}
				});
				if (!inconsistentNodeRelationMap.containsKey(node)) {
					visitedGraphNodeMap.put(node, nodeList);
				} else {
					List<String> emptyList = new ArrayList<>();
					visitedGraphNodeMap.put(node, emptyList);
					inconsistentNodeMap.put(node, nodeList);
				}
				//System.out.println("inconsistentNodeMap.."+ inconsistentNodeMap);

			}
			Set<String> inconsitentChildrens = new LinkedHashSet<>();
			for (String ignoreKey : inconsistentNodeRelationMap.keySet()) {

				if (inconsistentNodeRelationMap.get(ignoreKey).contains(node)
						&& visitedGraphNodeMap.containsKey(ignoreKey)) {

					System.out.println("----------------------");

					System.out.println("correcting node" + ignoreKey);
					Set<String> consistentPaths = new LinkedHashSet<>();

					if (inconsistentNodeMap.get(ignoreKey).size() == 0) {
						List<String> ignoreKeyList = new ArrayList<>();
						for (String ignoredParent : inconsistentNodeRelationMap.get(ignoreKey)) {
							if (!visitedGraphNodeMap.get(ignoredParent).isEmpty() && !ignoredParent.equals(node)) {
								for (String mapValue : visitedGraphNodeMap.get(ignoredParent)) {

									StringBuffer buffer = new StringBuffer();
									buffer.append(mapValue);
									buffer.append(ignoreKey);
									ignoreKeyList.add(buffer.toString());
								}
							}
						}
						inconsistentNodeMap.get(ignoreKey).addAll(ignoreKeyList);
					}

					if (inconsitentChildrens.size() > 1) {
						for (String childPaths : inconsitentChildrens) {
							inconsistentNodeMap.get(ignoreKey).add(childPaths.concat(ignoreKey));
						}
					}
					for (String stringPath : inconsistentNodeMap.get(ignoreKey)) {
						StringBuilder builder = new StringBuilder(
								stringPath.concat(nodeList.size() > 0 ? nodeList.get(0) : ""));
						String nonDuplicated = new StringBuilder(
								builder.reverse().toString().replaceAll("(.)(?=.*\\1)", "")).reverse().toString();
						consistentPaths.add(nonDuplicated);
						inconsitentChildrens.addAll(consistentPaths);
						visitedGraphNodeMap.get(node).addAll(inconsitentChildrens);
						subGraphConsistentPaths.add(nonDuplicated);
					}
					visitedGraphNodeMap.put(ignoreKey,
							Arrays.asList(consistentPaths.toArray(new String[consistentPaths.size()])));
				}

			}
		}

		if (graphRelationMap.get(node) != null) {

			for (String childrens : graphRelationMap.get(node)) {
				if (!visitedGraphNodeMap.keySet().contains(childrens))
					traverseConsistentGraphs(childrens);
			}
		}

	}

	
	/**
	 * Read File For problem 2
	 * 
	 * @throws FileNotFoundException
	 */
	public void readFileProblem2() throws FileNotFoundException {
		String s;
		File filename = new File("123.txt");
		Scanner sc = new Scanner(filename);

		Set<String> allChildrens = new HashSet<>();
		
		
		Map<String,List<String>> graphChildParentMap = new HashMap<>();
		while (sc.hasNextLine()) {
			s = sc.nextLine();
			String[] split = s.split("\\s+");
			allChildrens.add(split[0]);
			
			
			if(graphChildParentMap.containsKey(split[0])){
				graphChildParentMap.get(split[0]).add(split[1]);
			}else{
				List<String> doubleParents= new ArrayList<>();
				doubleParents.add(split[1]);
				graphChildParentMap.put(split[0], doubleParents);
			}
			
			if (graphRelationMap.containsKey(split[1])) {
				List<String> list = graphRelationMap.get(split[1]);
				list.add(split[0]);
			}

			else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(split[0]);
				graphRelationMap.put(split[1], list);

			}
		}
		sc.close();

		for (String parentKey : graphRelationMap.keySet()) {

			if (!allChildrens.contains(parentKey)) {
				System.out.println("RootNode " + parentKey);
				rootNode = parentKey;
				break;
			}
		}
		
		for(String childKey :graphChildParentMap.keySet()){
			if(graphChildParentMap.get(childKey).size()>1){
				inconsistentNodeRelationMap.put(childKey, graphChildParentMap.get(childKey));
			}
		}
		System.out.println("dup children "+ inconsistentNodeRelationMap.size());
	}

	/**
	 * Read File Matrix for Problem 1
	 * 
	 * @throws FileNotFoundException
	 */
	public void readfileMatrix() throws FileNotFoundException {

		int lineCount = 0;
		File filename = new File("123.txt");

		Set<String> allChildrens = new HashSet<>();

		Scanner sc = new Scanner(filename);
		while (sc.hasNextLine()) {
			List<String> tempList = new ArrayList<String>();
			String s = sc.nextLine().trim();

			String temp = s.replaceAll("\\s+", "");

			lineCount++;

			graphRelationMap.put(String.valueOf(lineCount), tempList);

			for (int i = 0; i < temp.length(); i++) {
				if (temp.charAt(i) == '1') {

					if (graphRelationMap.containsKey(String.valueOf(lineCount))) {

						graphRelationMap.get(String.valueOf(lineCount)).add(i + 1 + "");
						allChildrens.add(i + 1 + "");
					}
				}

			}
		}
		sc.close();
		for (String parentKey : graphRelationMap.keySet()) {

			if (!allChildrens.contains(parentKey)) {
				rootNode = parentKey;
				break;
			}
		}
		System.out.println("RootNode " + rootNode);

	}

	public static void main(String args[]) throws FileNotFoundException {

		// Problem 1

		
		 GraphMap graphMapProblem1 = new GraphMap();
		  graphMapProblem1.readfileMatrix();
		  graphMapProblem1.traverseGraph(graphMapProblem1.rootNode);
		  
		  System.out.println(graphMapProblem1.subGraphConsistentPaths);
		  System.out.println("Subgraph Path Size " +
		  graphMapProblem1.subGraphConsistentPaths.size());
		 

		// Problem 2

		/*GraphMap graphMapProblem2 = new GraphMap();
		//graphMapProblem2.readFileProblem2();       // not needed since reading from matrix
		graphMapProblem2.readfileMatrix();
		
		//graphMapProblem2.checkDuplicateParentNodes();
		//System.out.println(graphMapProblem2.inconsistentNodeRelationMap);
		graphMapProblem2.traverseConsistentGraphs(graphMapProblem2.rootNode);

		System.out.println(graphMapProblem2.subGraphConsistentPaths);
		System.out.println("Subgraph Path Size " + graphMapProblem2.subGraphConsistentPaths.size());*/

	}

}
