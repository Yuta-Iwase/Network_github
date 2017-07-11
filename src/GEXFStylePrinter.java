import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * aaaaasssss
 * @author yuta
 *
 */
public class GEXFStylePrinter {
	int N;
	int[][] list;
	boolean directed;
	String filePath;
	PrintWriter pw;

	/*
	 * aaa
	 */
	public GEXFStylePrinter(int N, int[][] list, boolean directed, String filePath) throws FileNotFoundException{
		this.N = N;
		this.list = list;
		this.filePath = filePath;
		pw = new PrintWriter(new File(filePath));
	}

	/*
	 * ssssss
	 */
	public void init_1st(){
		String s;

		s = "<gexf>";
		pw.println(s);
		System.out.println(s);

		if(directed){
			s = "\t" + "<graph defaultedgetype=\"" + "directed" + "\">";
		}else{
			s = "\t" + "<graph defaultedgetype=\"" + "undirected" + "\">";
		}
		pw.println(s);
		System.out.println(s);
	}

	public void printNode_2nd(String[] label, String  attributeName, double[] attribute){
		boolean use_attribute = true;
		if(attribute==null){
			use_attribute = false;
		}else if(attribute.length==0){
			use_attribute = false;
		}

		boolean use_label = (label==null || label.length==0);

		String s;
		if(use_attribute){
			String attribute_type = "";
			if(attribute.getClass().getName().equals("[I") || attribute.getClass().getName().equals("[D")){
				attribute_type = "float";
			}else{
				attribute_type = "string";
			}

			// attributesの記述
			s = "\t" + "\t" + "<attributes class=\"node\">";
			pw.println(s);
			System.out.println(s);

			s = "\t" + "\t" + "\t" + "<attribute id=\"0\" title=\"" + attributeName + "\" type=\"" + attribute_type + "\" />";
			pw.println(s);
			System.out.println(s);

			s = "\t" + "\t" + "</attributes>";
			pw.println(s);
			System.out.println(s);
		}

		s = "\t" + "\t" + "<nodes>";
		pw.println(s);
		System.out.println(s);

		for(int i=0;i<N;i++){
			// 1/5の途中
			if(use_label){
				s = "\t" + "\t" + "\t" + "<node id=\"" + i + "\" label=\"" + i + "\"";
			}else{
				s = "\t" + "\t" + "\t" + "<node id=\"" + i + "\" label=\"" + label[i] + "\"";
			}
			pw.print(s);
			System.out.print(s);

			if(use_attribute){
				// 1/5の終わり
				s = ">";
				pw.println(s);
				System.out.println(s);

				// 2/5
				s = "\t" + "\t" + "\t" + "\t" + "<attvalues>";
				pw.println(s);
				System.out.println(s);

				// 3/5
				s = "\t" + "\t" + "\t" + "\t" + "\t" + "<attvalue for=\"0\" value=\"" + attribute[i] + "\" />";
				pw.println(s);
				System.out.println(s);

				// 4/5
				s = "\t" + "\t" + "\t" + "\t" + "</attvalues>";
				pw.println(s);
				System.out.println(s);

				// 5/5
				s = "\t" + "\t" + "\t" + "</node>";
				pw.println(s);
				System.out.println(s);

			}else{
				// 1/5の終わり
				s = " />";
				pw.println(s);
				System.out.println(s);
			}
		}

		s = "\t" + "\t" + "</nodes>";
		pw.println(s);
		System.out.println(s);
	}

	public void printNode_2nd(String[] label, String  attributeName, int[] attribute){
		double[] attribute_int = new double[attribute.length];
		for(int i=0;i<attribute.length;i++){
			attribute_int[i] = attribute[i];
		}
		printNode_2nd(label, attributeName, attribute_int);
	}

	public void printEdge_3rd(double[] weight, String  attributeName, double[] attribute){
		boolean use_weight = true;
		if(weight==null){
			use_weight = false;
		}else if(weight.length==0){
			use_weight = false;
		}

		boolean use_attribute = true;
		if(attribute==null){
			use_attribute = false;
		}else if(attribute.length==0){
			use_attribute = false;
		}

		String s;
		if(use_attribute){
			String attribute_type = "";
			if(attribute.getClass().getName().equals("[I") || attribute.getClass().getName().equals("[D")){
				attribute_type = "float";
			}else{
				attribute_type = "string";
			}

			// attributesの記述
			s = "\t" + "\t" + "<attributes class=\"edge\">";
			pw.println(s);
			System.out.println(s);

			s = "\t" + "\t" + "\t" + "<attribute id=\"0\" title=\"" + attributeName + "\" type=\"" + attribute_type + "\" />";
			pw.println(s);
			System.out.println(s);

			s = "\t" + "\t" + "</attributes>";
			pw.println(s);
			System.out.println(s);
		}

		s = "\t" + "\t" + "<edges>";
		pw.println(s);
		System.out.println(s);

		for(int i=0;i<list.length;i++){
			// 1/5の途中
			if(use_weight){
				s = "\t" + "\t" + "\t" + "<edge id=\"" + i + "\" source=\"" + list[i][0] + "\" target=\"" + list[i][1] + "\" weight=\"" + weight[i] + "\"";
			}else{
				s = "\t" + "\t" + "\t" + "<edge id=\"" + i + "\" source=\"" + list[i][0] + "\" target=\"" + list[i][1] + "\"";
			}
			pw.print(s);
			System.out.print(s);

			if(use_attribute){
				// 1/5の終わり
				s = ">";
				pw.println(s);
				System.out.println(s);

				// 2/5
				s = "\t" + "\t" + "\t" + "\t" + "<attvalues>";
				pw.println(s);
				System.out.println(s);

				// 3/5
				s = "\t" + "\t" + "\t" + "\t" + "\t" + "<attvalue for=\"0\" value=\"" + attribute[i] + "\" />";
				pw.println(s);
				System.out.println(s);

				// 4/5
				s = "\t" + "\t" + "\t" + "\t" + "</attvalues>";
				pw.println(s);
				System.out.println(s);

				// 5/5
				s = "\t" + "\t" + "\t" + "</edge>";
				pw.println(s);
				System.out.println(s);

			}else{
				// 1/5の終わり
				s = " />";
				pw.println(s);
				System.out.println(s);
			}
		}

		s = "\t" + "\t" + "</edges>";
		pw.println(s);
		System.out.println(s);
	}

	public void printEdge_3rd(double[] weight, String  attributeName, int[] attribute){
		double[] attribute_int = new double[attribute.length];
		for(int i=0;i<attribute.length;i++){
			attribute_int[i] = attribute[i];
		}
		printEdge_3rd(weight, attributeName, attribute_int);
	}

	public void terminal_4th(){
		String s;

		s = "\t" + "</graph>";
		pw.println(s);
		System.out.println(s);


		s = "</gexf>";
		pw.println(s);
		System.out.println(s);

		pw.close();
	}

}
