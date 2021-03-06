import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class AirportTest35_2_some_samples_exam_for_WorldAir{

	public static void main(String[] args) throws Exception{
		//// 変動するパラメータ
		// メタ・パラメータ
		int times = 100;
		int bins = 50;
		final boolean useAlphaList = true;
		final boolean useRandomWalk = false;
		// ネットワークパラメータ
		int N = 3237; //世界航空網の頂点数
		// 出力オプション
		String markerColor = "purple";


		// アルファ周りの設定
		final double ALPHA_CONTROLED_TIMES;
		ArrayList<BigDecimal> alphaList = new ArrayList<>();
		BigDecimal ALPHA_MIN = null;
		BigDecimal ALPHA_MAX = null;
		BigDecimal ALPHA_WIDTH = null;
		if(useAlphaList){
			// [true]使用したいalphaの値を明記
//			String[] alpha_strList = {"-3.0", "-1.0", "-0.4", "0.0" , "1.0", "3.0"};
//			String[] alpha_strList = {"-3.0","-1.0","1.0"};
//			String[] alpha_strList = {
//					"-2.0", "-1.9", "-1.8", "-1.7", "-1.6", "-1.5", "-1.4", "-1.3", "-1.2", "-1.1",
//					"1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0"
//			};
			String[] alpha_strList = {"-2.0"};
			for(int i=0;i<alpha_strList.length;i++){
				alphaList.add(new BigDecimal(alpha_strList[i]));
			}
			ALPHA_CONTROLED_TIMES = alpha_strList.length;
		}else{
			// [false]初期値、最終値、刻み幅でalphaを定義
			ALPHA_MIN = new BigDecimal("1.1");
			ALPHA_MAX = new BigDecimal("2.0");
			ALPHA_WIDTH = new BigDecimal("0.1");
			ALPHA_CONTROLED_TIMES = ALPHA_MAX.subtract(ALPHA_MIN).divide(ALPHA_WIDTH).add(BigDecimal.ONE).doubleValue();
		}

		//// 定数定義
		// ステップ数
		int num_step = 100*N;
		// オブジェクト定義
		HistogramGenerator hist = new HistogramGenerator();
		py_PointPlot py = new py_PointPlot();

		//// 処理

		// ④
		File f41 = null;
		PrintWriter pw41 = null;
		f41 = new File("high_salience_edges.csv");
		pw41 = new PrintWriter(f41);
		double HS_frac = 0;
		double current_HS_frac = 0.0;


		// 開始時のタイムスタンプ
		long start_time;

		// [ループ、level1]alpha回数処理
		BigDecimal dec_current_alpha = null;
		if(useAlphaList) dec_current_alpha = alphaList.get(0);
		else dec_current_alpha = ALPHA_MIN;
		for(int alpha_factor=0;alpha_factor<ALPHA_CONTROLED_TIMES;alpha_factor++) {
			if(useAlphaList) dec_current_alpha = alphaList.get(alpha_factor);
			double alpha = dec_current_alpha.doubleValue();
			String alphaString = dec_current_alpha.toString();
			String temp_fileName = "alpha=" + alphaString;
			new File(temp_fileName).mkdirs();
			if(!useAlphaList) dec_current_alpha = dec_current_alpha.add(ALPHA_WIDTH);
			start_time = System.currentTimeMillis();
			System.out.println("start: alpha=" + alphaString);
			System.out.println(new Date().toString());

			HS_frac = 0.0;


			// ①weight
			String s11 = "w_LinearScale.csv";
			String s12 = "w_LogScale.csv";
			String s13 = "w_RawList.csv";
			File f11 = new File(temp_fileName + "/" + s11);
			File f12 = new File(temp_fileName + "/" + s12);
			File f13 = new File(temp_fileName + "/" + s13);
			PrintWriter pw11 = new PrintWriter(f11);
			PrintWriter pw12 = new PrintWriter(f12);
			PrintWriter pw13 = new PrintWriter(f13);
			double[][] w_linear = new double[bins][2];
			double[][] w_log = new double[bins][2];
			for(int i=0;i<bins;i++) {
				w_linear[i][1] = 0.0;
				w_log[i][1] = 0.0;
			}
			double[][] current_w_linear = new double[bins][2];
			double[][] current_w_log = new double[bins][2];
			// ②edge_BC
			String s21 = "edgeBC_LinearScale.csv";
			String s22 = "edgeBC_LogScale.csv";
			String s23 = "edgeBC_RawFreq.csv";
			String s24 = "edgeBC_TotalFreq.csv";
			File f21 = new File(temp_fileName + "/" + s21);
			File f22 = new File(temp_fileName + "/" + s22);
			File f23 = new File(temp_fileName + "/" + s23);
			File f24 = new File(temp_fileName + "/" + s24);
			PrintWriter pw21 = new PrintWriter(f21);
			PrintWriter pw22 = new PrintWriter(f22);
			PrintWriter pw23 = new PrintWriter(f23);
			PrintWriter pw24 = new PrintWriter(f24);
			double[][] edgeBC_linear = new double[bins][2];
			double[][] edgeBC_log = new double[bins][2];
			int[] edgeBC_freq = new int[N*N];
			int[] total_edgeBC_freq = new int[N*N];
			for(int i=0;i<bins;i++) {
				edgeBC_linear[i][1] = 0.0;
				edgeBC_log[i][1] = 0.0;
			}
			for(int i=0;i<total_edgeBC_freq.length;i++) total_edgeBC_freq[i]=0;
			double[][] current_edgeBC_linear = new double[bins][2];
			double[][] current_edgeBC_log = new double[bins][2];
			// ③link salience
			String s31 = "LinkSalience_LinearScale.csv";
			String s32 = "LinkSalience_RawList.csv";
			File f31 = new File(temp_fileName + "/" + s31);
			File f32 = new File(temp_fileName + "/" + s32);
			PrintWriter pw31 = new PrintWriter(f31);
			PrintWriter pw32 = new PrintWriter(f32);
			double[][] s_linear = new double[bins][2];
			for(int i=0;i<bins;i++) s_linear[i][1]=0.0;
			double[][] current_s_linear = new double[bins][2];
			// ⑤degree distribution(high salience)
			String s51 = "degree(HS)_dist_LinearScale.csv";
			String s52 = "degree(HS)_dist_LogScale.csv";
			String s53 = "degree(HS)_dist_RawList_doubleCount.csv";
			File f51 = new File(temp_fileName + "/" + s51);
			File f52 = new File(temp_fileName + "/" + s52);
			File f53 = new File(temp_fileName + "/" + s53);
			PrintWriter pw51 = new PrintWriter(f51);
			PrintWriter pw52 = new PrintWriter(f52);
			PrintWriter pw53 = new PrintWriter(f53);
			double[][] d_hs_linear = new double[bins][2];
			double[][] d_hs_log = new double[bins][2];
			ArrayList<Double> d_hs_RawList = new ArrayList<>();
			// ⑥strength
			String s61 = "strength_LinearScale.csv";
			String s62 = "strength_LogScale.csv";
			String s63 = "strength_RawList.csv";
			File f61 = new File(temp_fileName + "/" + s61);
			File f62 = new File(temp_fileName + "/" + s62);
			File f63 = new File(temp_fileName + "/" + s63);
			PrintWriter pw61 = new PrintWriter(f61);
			PrintWriter pw62 = new PrintWriter(f62);
			PrintWriter pw63 = new PrintWriter(f63);
			double[][] str_linear = new double[bins][2];
			double[][] str_log = new double[bins][2];
			for(int i=0;i<bins;i++) {
				str_linear[i][1] = 0.0;
				str_log[i][1] = 0.0;
			}
			double[][] current_str_linear = new double[bins][2];
			double[][] current_str_log = new double[bins][2];
			// ⑦node_BC
			String s71 = "nodeBC_LinearScale.csv";
			String s72 = "nodeBC_LogScale.csv";
			String s73 = "nodeBC_RawFreq.csv";
			String s74 = "nodeBC_TotalFreq.csv";
			File f71 = new File(temp_fileName + "/" + s71);
			File f72 = new File(temp_fileName + "/" + s72);
			File f73 = new File(temp_fileName + "/" + s73);
			File f74 = new File(temp_fileName + "/" + s74);
			PrintWriter pw71 = new PrintWriter(f71);
			PrintWriter pw72 = new PrintWriter(f72);
			PrintWriter pw73 = new PrintWriter(f73);
			PrintWriter pw74 = new PrintWriter(f74);
			double[][] nodeBC_linear = new double[bins][2];
			double[][] nodeBC_log = new double[bins][2];
			int[] nodeBC_freq = new int[N*N];
			int[] total_nodeBC_freq = new int[N*N];
			for(int i=0;i<bins;i++) {
				nodeBC_linear[i][1] = 0.0;
				nodeBC_log[i][1] = 0.0;
			}
			for(int i=0;i<total_nodeBC_freq.length;i++) total_nodeBC_freq[i]=0;
			double[][] current_nodeBC_linear = new double[bins][2];
			double[][] current_nodeBC_log = new double[bins][2];
			// ⑧visited_nodes
			String s81 = "visited_nodes.csv";
			String s82 = "visited_nodes_once.csv";
			File f81 = new File(temp_fileName + "/" + s81);
			File f82 = new File(temp_fileName + "/" + s82);
			PrintWriter pw81 = new PrintWriter(f81);
			PrintWriter pw82 = new PrintWriter(f82);
			int[] visited_nodes = new int[num_step];
			int[] current_visited_nodes = new int[num_step];
			for(int i=0;i<num_step;i++) visited_nodes[i]=0;


			// [ループ、level2]times処理
			for(int t=0;t<times;t++) {
				// ネットワーク構築
				Network net;
				net = new NetworkForCSVFile("WorldAir_w.csv", false, true);
				net.setNode(false);
				net.setEdge();
				final double INV_M = 1.0/net.M;
				final double INV_N = 1.0/net.N;
				final double INV_SQUARE_N = 1.0/(net.N*net.N);

				// random walkの実行
				// ⑧用の準備も
				if(useRandomWalk) current_visited_nodes = net.BiasedRandomWalk_checkVisitedNodes(num_step, 1.0, alpha, (int)(System.currentTimeMillis()&Integer.MAX_VALUE), 0.0, true);
				else{
					net.SetWeight_to_Alpha(alpha, num_step);
					net.disturb();
				}

				// ①
				double[] w_round_list = new double[net.weight.length];
				for(int i=0;i<net.weight.length;i++) w_round_list[i]=Math.round(net.weight[i]);
				current_w_linear = hist.binPlot(w_round_list, bins, false, 1, num_step);
				current_w_log = hist.binPlot(w_round_list, bins, true, 1, num_step);
				for(int i=0;i<bins;i++) {
					w_linear[i][1] += current_w_linear[i][1]*INV_M;
					w_log[i][1] += current_w_log[i][1]*INV_M;
				}
				for(int i=0;i<w_round_list.length;i++) {
					pw13.println(w_round_list[i]);
				}
				// ②
				net.EdgeBetweenness();
				double[] edge_BC_list = new double[net.edgeList.size()];
				for(int i=0;i<N*N;i++) edgeBC_freq[i]=0;
				for(int i=0;i<edge_BC_list.length;i++) {
					edge_BC_list[i]=net.edgeList.get(i).betweenCentrality*INV_SQUARE_N;
					int intBC = (int)Math.round(net.edgeList.get(i).betweenCentrality);
					edgeBC_freq[intBC]++;
					total_edgeBC_freq[intBC]++;
				}
				current_edgeBC_linear = hist.binPlot(edge_BC_list, bins, false,0,1);
				current_edgeBC_log = hist.binPlot(edge_BC_list, bins, true,0,1);
				for(int i=0;i<current_edgeBC_linear.length;i++) {
					edgeBC_linear[i][1] += current_edgeBC_linear[i][1]*INV_M;
					edgeBC_log[i][1] += current_edgeBC_log[i][1]*INV_M;
				}
				pw23.println("node," + net.N);
				pw23.println("edge," + net.M);
				for(int i=0;i<edgeBC_freq.length;i++) {
					if(edgeBC_freq[i]>0) {
						pw23.println(i + "," + edgeBC_freq[i]);
					}
				}
				pw23.println("end");
				// ③
				net.LinkSalience();
				double[] s_list = new double[net.edgeList.size()];
				for(int i=0;i<s_list.length;i++) s_list[i]=net.edgeList.get(i).linkSalience*INV_N;
				current_s_linear = hist.binPlot(s_list, bins, false, 0, 1);
				for(int i=0;i<s_linear.length;i++) {
					s_linear[i][1] += current_s_linear[i][1]*INV_M;
				}
				for(int i=0;i<s_list.length;i++) {
					pw32.println(s_list[i]);
				}
				// ④,⑤
				current_HS_frac = 0.0;
				int[] HS_degree = new int[N];
				for(int i=0;i<HS_degree.length;i++) HS_degree[i]=0;
				for(int i=0;i<s_list.length;i++) {
					if(s_list[i]>=0.9) {
						// ④
						current_HS_frac++;
						// ⑤
						HS_degree[net.edgeList.get(i).node[0]]++;
						HS_degree[net.edgeList.get(i).node[1]]++;
					}
				}
				// ⑤
				for(int i=0;i<HS_degree.length;i++){
					if(HS_degree[i]>0){
						d_hs_RawList.add((double)HS_degree[i]);
						pw53.println(HS_degree[i]);
					}
				}
				// ④
				current_HS_frac *= INV_N;
				HS_frac += current_HS_frac;
				// ⑥
				double[] str_list = new double[net.nodeList.size()];
				for(int i=0;i<net.nodeList.size();i++) {
					str_list[i] = 0.0;
					for(int e=0;e<net.nodeList.get(i).eList.size();e++) {
						str_list[i] += w_round_list[net.nodeList.get(i).eList.get(e).index];
					}
				}
				current_str_linear = hist.binPlot(str_list, bins, false, 1, num_step);
				current_str_log = hist.binPlot(str_list, bins, true, 1, num_step);
				for(int i=0;i<bins;i++) {
					str_linear[i][1] += current_str_linear[i][1]*INV_N;
					str_log[i][1] += current_str_log[i][1]*INV_N;
				}
				for(int i=0;i<str_list.length;i++) {
					pw63.println(str_list[i]);
				}
				// ⑦
				net.nodeBetweenness_for_WeightedNet();
				double[] node_BC_list = new double[net.nodeList.size()];
				for(int i=0;i<N*N;i++) nodeBC_freq[i]=0;
				for(int i=0;i<node_BC_list.length;i++) {
					node_BC_list[i]=net.nodeList.get(i).betweenCentrality*INV_SQUARE_N;
					int intBC = (int)Math.round(net.nodeList.get(i).betweenCentrality);
					nodeBC_freq[intBC]++;
					total_nodeBC_freq[intBC]++;
				}
				current_nodeBC_linear = hist.binPlot(node_BC_list, bins, false,0,1);
				current_nodeBC_log = hist.binPlot(node_BC_list, bins, true,0,1);
				for(int i=0;i<current_nodeBC_linear.length;i++) {
					nodeBC_linear[i][1] += current_nodeBC_linear[i][1]*INV_N;
					nodeBC_log[i][1] += current_nodeBC_log[i][1]*INV_N;
				}
				pw73.println("node," + net.N);
				pw73.println("edge," + net.M);
				for(int i=0;i<node_BC_list.length;i++) {
					if(node_BC_list[i]>0) {
						pw73.println(i + "," + node_BC_list[i]);
					}
				}
				pw73.println("end");
				// ⑧
				for(int i=0;i<num_step;i++){
					visited_nodes[i] += current_visited_nodes[i];
				}


			}

			// ⑤
			if(d_hs_RawList.size()>0) {
				d_hs_linear = hist.binPlot(d_hs_RawList,bins,false);
				d_hs_log = hist.binPlot(d_hs_RawList, bins, true);
			}else {
				for(int i=0;i<bins;i++) {
					d_hs_linear[i][0] = i;
					d_hs_linear[i][1] = -123;
					d_hs_log[i][0] = i;
					d_hs_log[i][1] = -123;
				}
			}
			double d_divider = 1.0/(2*N*times);

			// bin関係print
			for(int i=0;i<bins;i++) {
				w_linear[i][0] = current_w_linear[i][0];
				w_linear[i][1] /= times;
				w_log[i][0] = current_w_log[i][0];
				w_log[i][1] /= times;
				edgeBC_linear[i][0] = current_edgeBC_linear[i][0];
				edgeBC_linear[i][1] /= times;
				edgeBC_log[i][0] = current_edgeBC_log[i][0];
				edgeBC_log[i][1] /= times;
				s_linear[i][0] = current_s_linear[i][0];
				s_linear[i][1] /= times;
				d_hs_linear[i][1] *= d_divider;
				d_hs_log[i][1] *= d_divider;
				str_linear[i][0] = current_str_linear[i][0];
				str_linear[i][1] /= times;
				str_log[i][0] = current_str_log[i][0];
				str_log[i][1] /= times;
				nodeBC_linear[i][0] = current_edgeBC_linear[i][0];
				nodeBC_linear[i][1] /= times;
				nodeBC_log[i][0] = current_edgeBC_log[i][0];
				nodeBC_log[i][1] /= times;

				if(w_linear[i][1]>0) pw11.println(w_linear[i][0]+","+w_linear[i][1]);
				if(w_log[i][1]>0) pw12.println(w_log[i][0]+","+w_log[i][1]);
				if(edgeBC_linear[i][1]>0) pw21.println(edgeBC_linear[i][0] + "," + edgeBC_linear[i][1]);
				if(edgeBC_log[i][1]>0) pw22.println(edgeBC_log[i][0] + "," + edgeBC_log[i][1]);
				if(s_linear[i][1]>0) pw31.println(s_linear[i][0] + "," + s_linear[i][1]);
				if(d_hs_linear[i][1]>0) pw51.println(d_hs_linear[i][0] + "," + d_hs_linear[i][1]);
				if(d_hs_log[i][1]>0) pw52.println(d_hs_log[i][0] + "," + d_hs_log[i][1]);
				if(str_linear[i][1]>0) pw61.println(str_linear[i][0] + ","  + str_linear[i][1]);
				if(str_log[i][1]>0) pw62.println(str_log[i][0] + ","  + str_log[i][1]);
				if(nodeBC_linear[i][1]>0) pw71.println(nodeBC_linear[i][0] + "," + nodeBC_linear[i][1]);
				if(nodeBC_log[i][1]>0) pw72.println(nodeBC_log[i][0] + "," + nodeBC_log[i][1]);
			}

			HS_frac /= times;
			pw41.println(alphaString + "," + HS_frac);

			double INV_SQUARE_N = 1.0/(N*N);
			for(int i=0;i<N*N;i++) {
				if(total_edgeBC_freq[i]>0) pw24.println(i*INV_SQUARE_N + "," + total_edgeBC_freq[i]);
				if(total_nodeBC_freq[i]>0) pw74.println(i*INV_SQUARE_N + "," + total_nodeBC_freq[i]);
			}
			for(int i=0;i<num_step;i++){
				pw81.println(i + "," + ((double)visited_nodes[i]/N)/times);
				pw82.println(i + "," + ((double)current_visited_nodes[i]/N));
			}


			pw11.close();
			pw12.close();
			pw13.close();
			pw21.close();
			pw22.close();
			pw23.close();
			pw24.close();
			pw31.close();
			pw32.close();
			pw51.close();
			pw52.close();
			pw53.close();
			pw61.close();
			pw62.close();
			pw63.close();
			pw71.close();
			pw72.close();
			pw73.close();
			pw74.close();
			pw81.close();
			pw82.close();

			py.plot(temp_fileName+"/plot_w_linear.py", f11.getAbsolutePath().replace("\\", "/"), "w_linear", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "weight dist. linear", "$w$", "$p(w)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_w_log.py", f12.getAbsolutePath().replace("\\", "/"), "w_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "weight dist. log", "$w$", "$p(w)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_edgeBC_linear.py", f21.getAbsolutePath().replace("\\", "/"), "edgeBC_linear", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "edgeBC dist. linear", "$BC_{\\rm edge}$", "$p(BC_{\\rm edge})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_edgeBC_log.py", f22.getAbsolutePath().replace("\\", "/"), "edgeBC_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "edgeBC dist. log", "$BC_{\\rm edge}$", "$p(BC_{\\rm edge})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");

			py.plot(temp_fileName+"/plot_edgeBC_freq_log.py", f24.getAbsolutePath().replace("\\", "/"), "edgeBC_freq_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "edgeBC dist. log", "$BC_{\\rm edge}$", "$p(BC_{\\rm edge})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_edgeBC_freq_log_ac.py", f24.getAbsolutePath().replace("\\", "/"), "edgeBC_freq_log_ac", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 2, true, true, "edgeBC dist. log, cumulative", "$BC_{\\rm edge}$", "$p(BC_{\\rm edge})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "lower left");

			py.plot(temp_fileName+"/plot_salience.py", f31.getAbsolutePath().replace("\\", "/"), "salience", 0, 1, 0, 1, true, "black", false, true, markerColor, 4, 0, false, false, "salience dist.", "$salience$", "$p(salience)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper center");
			py.plot(temp_fileName+"/plot_degree(HS)_linear.py", f51.getAbsolutePath().replace("\\", "/"), "degree(HS)_linear", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "degree${\\in}$(high salience links) dist. linear", "$k$", "$p(k)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_degree(HS)_log.py", f52.getAbsolutePath().replace("\\", "/"), "degree(HS)_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "degree${\\in}$(high salience links) dist. log", "$k$", "$p(k)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_strengh_linear.py", f61.getAbsolutePath().replace("\\", "/"), "str_linear", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "strength dist. linear", "$strength$", "$p(strength)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_strengh_log.py", f62.getAbsolutePath().replace("\\", "/"), "str_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "strength dist. log", "$strength$", "$p(strength)$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_nodeBC_linear.py", f71.getAbsolutePath().replace("\\", "/"), "nodeBC_linear", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "nodeBC dist. linear", "$BC_{\\rm node}$", "$p(BC_{\\rm node})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_nodeBC_log.py", f72.getAbsolutePath().replace("\\", "/"), "nodeBC_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "nodeBC dist. log", "$BC_{\\rm node}$", "$p(BC_{\\rm node})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");

			py.plot(temp_fileName+"/plot_nodeBC_freq_log.py", f74.getAbsolutePath().replace("\\", "/"), "nodeBC_freq_log", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, true, true, "nodeBC dist. log", "$BC_{\\rm node}$", "$p(BC_{\\rm node})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "upper right");
			py.plot(temp_fileName+"/plot_nodeBC_freq_log_ac.py", f74.getAbsolutePath().replace("\\", "/"), "nodeBC_freq_log_ac", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 2, true, true, "nodeBC dist. log, cumulative", "$BC_{\\rm node}$", "$p(BC_{\\rm node})$", true, "world air"+" ${\\alpha}="+alphaString+"$", "lower left");

			py.plot(temp_fileName+"/plot_visitedNodes.py", f81.getAbsolutePath().replace("\\", "/"), "visited_nodes", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "visited nodes", "step", "#visited nodes", true, "world air"+" ${\\alpha}="+alphaString+"$", "lower right");
			py.plot(temp_fileName+"/plot_visitedNodes(single).py", f81.getAbsolutePath().replace("\\", "/"), "visited_nodes(single)", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "visited nodes(a single try only)", "step", "#visited nodes", true, "world air"+" ${\\alpha}="+alphaString+"$", "lower right");


			int rap = (int)((System.currentTimeMillis()-start_time)/1000);
			System.out.println("finish: rap=" + rap + "[s]");
			System.out.println();

		}


		pw41.close();
		py.plot("plot_highSalience_edges.py", f41.getAbsolutePath().replace("\\", "/"), "high_salience_edges", 0, 0, 0, 0, true, "black", false, true, markerColor, 4, 0, false, false, "high salience edges", "${\\alpha}$", "$\\#HS(\\alpha) / N$", true, "world air", "lower right");


	}
}
