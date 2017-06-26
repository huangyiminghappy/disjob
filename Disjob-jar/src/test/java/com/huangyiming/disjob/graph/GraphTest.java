package com.huangyiming.disjob.graph;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.huangyiming.disjob.java.ExecutorBuilder;
import com.huangyiming.disjob.java.job.DependDisJob;

public class GraphTest {

	private static Node<DependDisJob> startNode = null ;
	public static void main(String[] args) throws Exception {
		Graph<DependDisJob> graph = new Graph<DependDisJob>();

		Node<DependDisJob> JOB_A = new Node<DependDisJob>(new JobA("A"));
		Node<DependDisJob> JOB_B = new Node<DependDisJob>(new JobB("B"));
		Node<DependDisJob> JOB_C = new Node<DependDisJob>(new JobC("C"));
		Node<DependDisJob> JOB_D = new Node<DependDisJob>(new JobD("D"));
		Node<DependDisJob> JOB_E = new Node<DependDisJob>(new JobD("E"));
		Node<DependDisJob> JOB_F = new Node<DependDisJob>(new JobD("F"));

		graph.addNode(JOB_A, JOB_B);
		graph.addNode(JOB_B, JOB_E);
		graph.addNode(JOB_B, JOB_C);
		graph.addNode(JOB_E, JOB_D);
		graph.addNode(JOB_E, JOB_F);
		graph.addNode(JOB_C, JOB_F);
		graph.addNode(JOB_D, JOB_F);
		

		startNode = JOB_A;
		
		Iterator<Node<DependDisJob>> iter = graph.getVertexSet().iterator();
		while (iter.hasNext()) {
			Node<DependDisJob> node = iter.next();
			Set<Node<DependDisJob>> sets = graph.getAdjaNode().get(node);
			if (sets != null) {
				System.out.print(node.getVal().getKey() + "->");
				for (Node<DependDisJob> n : sets) {
					System.out.print(n.getVal().getKey() + ",");
				}
				System.out.println();
			}
		}
		
		
		System.out.println("===getReverseAdjaNode==");

		for(Map.Entry<Node<DependDisJob>, Set<Node<DependDisJob>>> entry : graph.getReverseAdjaNode().entrySet()){
			System.out.print(entry.getKey().getVal().getKey() + "->");
			Set<Node<DependDisJob>> sets = entry.getValue();
			for (Node<DependDisJob> n : sets) {
				System.out.print(n.getVal().getKey() + ",");
			}
			System.out.println();
		}
		
		
		Scheduler scheduler = new Scheduler(graph);
		//3s 钟后触发A节点
		while(true){
			Thread.sleep(10000);
			ExecutorBuilder.getJobExecutor().execute(new JobAction(startNode, scheduler));
		}
	}
}
