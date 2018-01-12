package wqa;

import java.sql.ResultSet;
import java.sql.SQLException;

import dbconnect.DBDriver;

public class Test {
	public static final int times = 100;
	public static void main(String[] args) {

		BP bp = new BP(3, 1, 1, 2.8);// ��ʼ��BPģ��
		bp.printAll();
		//ֻ��ȡ idt.id < 4148531 �����ݣ�����������ΪԤ������ʹ��
		String sql = "select min(idt.id) as id,id.id as device_id,idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "from test.ik_device_tds idt " 
				+ "inner join test.ik_device id on idt.device_num = id.device_num "
				+ "where idt.tds_in >= idt.tds_out " 
				+ "and idt.device_num = \"I6-B51000012\" "
				//+ "and idt.id < 4148531 " //in
				+ "and idt.id < 4133099 " //out
				+ "group by id.id,idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "order by id.id,idt.record_time;";// ��ʼ������Դ
		
		DBDriver db1 = new DBDriver(sql);// ����DBDriver����
		ResultSet ret = null;
		int counts = 0;
		try {
			ret = db1.pst.executeQuery();// ִ����䣬�õ������
			double in_x1 = -1;
			double in_x2 = -1;
			double in_x3 = -1;
			double in_temp1 = -1;
			double in_y = -1;
			while (ret.next()) {
				// ����ȡ�����ݽ���ѵ��
				//String device_num_str = ret.getString(3);
				//double tds_in_double = ret.getDouble(5)/10000;//��ˮ�����������Ƶ�(0,1)
				double tds_in_double = ret.getDouble(6)/10000;//��ˮ�����������Ƶ�(0,1)

				in_x1 = in_x2;
				in_x2 = in_x3;
				in_x3 = in_temp1;
				in_temp1 = in_y;
				in_y = tds_in_double;
				
				
				double[] binary = {in_x1,in_x2,in_x3};
				double[] real = {in_y};
				
				if (in_x1 != -1){
					System.out.println("�� "+(++counts)+" ��ѵ��:"
				+"["+in_x1+" "+in_x2+" "+in_x3+"] "+"["+in_y+"]"
				+"*************************************************************************************");
				
				int m = Test.times;
				while(m-- != 0){
					bp.train(binary, real);// ѵ��ģ��
					//bp.printAll();
				}
				}
			}
			ret.close();
			db1.close();// �ر�����
		} catch (SQLException e) {
			e.printStackTrace();
		}

		System.out.println("Training Completed!");
		//���BP�������
		bp.printAll();
		
		//�������ѵ�������е����ֵ
		for(int i=0;i<bp.errors_index;i++){
			System.out.println(bp.errors[i]);
		}
		
		//��ˮ��������
//		double predict_x1 = 765;
//		double predict_x2 = 769;
//		double predict_x3 = 780;
//		double predict_target = 790;
		//��ˮ��������
		double predict_x1 = 93;
		double predict_x2 = 94;
		double predict_x3 = 91;
		double predict_target = 94;
		
		double[] binary = {predict_x1/10000,predict_x2/10000,predict_x3/10000};
		double[] result = new double[1];
		bp.predict(binary, result);
			
		System.out.println("===>["+binary[0]+" "+binary[1]+" "+binary[2]+"]"+" ["+result[0]+"]"
		+" Ŀ��ֵ:"+predict_target/10000
		+"\n===>��ʵ��ƫ��ֵ��"+(predict_target-result[0]*10000)+"��"
		+"\n===>��ƫ���ʣ�"+((predict_target-result[0]*10000)/predict_target)+"��");

	}

}
