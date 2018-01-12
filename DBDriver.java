package dbconnect;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBDriver {
	public static final String url = "jdbc:mysql://127.0.0.1/test?useSSL=false";
	public static final String name = "com.mysql.jdbc.Driver";
	public static final String user = "root";
	public static final String password = "123456";

	public Connection conn = null;
	public PreparedStatement pst = null;

	public DBDriver(String sql) {
		try {
			Class.forName(name);// 指定连接类型
			conn = DriverManager.getConnection(url, user, password);// 获取连接
			pst = conn.prepareStatement(sql);// 准备执行语句
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.conn.close();
			this.pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String sql = "select min(idt.id) as id,idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "from test.ik_device_tds idt " + "where idt.tds_in >= idt.tds_out "
				+ "and idt.device_num = \"I6-B51000008\" "
				+ "group by idt.device_num,idt.record_time,idt.tds_in,idt.tds_out "
				+ "order by idt.device_num,idt.record_time";// SQL语句
		DBDriver db1 = new DBDriver(sql);// 创建DBDriver对象
		ResultSet ret = null;
		try {
			ret = db1.pst.executeQuery();// 执行语句，得到结果集
			while (ret.next()) {
				String uid = ret.getString(1);
				String udevice_num = ret.getString(2);
				String urecord_time = ret.getString(3);
				String utds_in = ret.getString(4);
				System.out.println(uid + "\t" + udevice_num + "\t" + urecord_time + "\t" + utds_in);
			} // 显示数据
			ret.close();
			db1.close();// 关闭连接
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String str = "I6-B51000008";
		char[] chr = str.toCharArray();
		for(int i=0;i<str.length();i++){System.out.print(" "+(double)chr[i]);}
		System.out.println();

	}
}
