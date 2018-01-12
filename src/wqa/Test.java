package wqa;

import java.sql.ResultSet;
import java.sql.SQLException;

import dbconnect.DBDriver;

public class Test {
	public static final int times = 100;
	public static void main(String[] args) {

		BP bp = new BP(3, 1, 1, 2.8);// 初始化BP模型
		bp.printAll();
		//只获取 idt.id < 4148531 的数据，其他数据作为预测数据使用
		String sql = "select min(idt.id) as id,id.id as device_id,idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "from test.ik_device_tds idt " 
				+ "inner join test.ik_device id on idt.device_num = id.device_num "
				+ "where idt.tds_in >= idt.tds_out " 
				+ "and idt.device_num = \"I6-B51000012\" "
				//+ "and idt.id < 4148531 " //in
				+ "and idt.id < 4133099 " //out
				+ "group by id.id,idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "order by id.id,idt.record_time;";// 初始化数据源
		
		DBDriver db1 = new DBDriver(sql);// 创建DBDriver对象
		ResultSet ret = null;
		int counts = 0;
		try {
			ret = db1.pst.executeQuery();// 执行语句，得到结果集
			double in_x1 = -1;
			double in_x2 = -1;
			double in_x3 = -1;
			double in_temp1 = -1;
			double in_y = -1;
			while (ret.next()) {
				// 轮流取出数据进行训练
				//String device_num_str = ret.getString(3);
				//double tds_in_double = ret.getDouble(5)/10000;//入水，将数据限制到(0,1)
				double tds_in_double = ret.getDouble(6)/10000;//出水，将数据限制到(0,1)

				in_x1 = in_x2;
				in_x2 = in_x3;
				in_x3 = in_temp1;
				in_temp1 = in_y;
				in_y = tds_in_double;
				
				
				double[] binary = {in_x1,in_x2,in_x3};
				double[] real = {in_y};
				
				if (in_x1 != -1){
					System.out.println("第 "+(++counts)+" 次训练:"
				+"["+in_x1+" "+in_x2+" "+in_x3+"] "+"["+in_y+"]"
				+"*************************************************************************************");
				
				int m = Test.times;
				while(m-- != 0){
					bp.train(binary, real);// 训练模型
					//bp.printAll();
				}
				}
			}
			ret.close();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Training Completed!");
		//输出BP相关数据
		bp.printAll();
		
		//输出所有训练过程中的误差值
		for(int i=0;i<bp.errors_index;i++){
			System.out.println(bp.errors[i]);
		}
		
		//入水测试数据
//		double predict_x1 = 765;
//		double predict_x2 = 769;
//		double predict_x3 = 780;
//		double predict_target = 790;
		//出水测试数据
		double predict_x1 = 93;
		double predict_x2 = 94;
		double predict_x3 = 91;
		double predict_target = 94;
		
		double[] binary = {predict_x1/10000,predict_x2/10000,predict_x3/10000};
		double[] result = new double[1];
		bp.predict(binary, result);
			
		System.out.println("===>["+binary[0]+" "+binary[1]+" "+binary[2]+"]"+" ["+result[0]+"]"
		+" 目标值:"+predict_target/10000
		+"\n===>【实际偏差值："+(predict_target-result[0]*10000)+"】"
		+"\n===>【偏差率："+((predict_target-result[0]*10000)/predict_target)+"】");

	}

}
