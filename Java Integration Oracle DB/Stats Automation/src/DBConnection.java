import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

public class DBConnection {

	public static final String SITEP_DB = "sitep";
	public static final String REPALDA_DB = "repalda";
	
	public static final int YAD_IAV_FLAG = 0;
	public static final int REFRESH_STATS = 1;
	
	public static boolean IS_IAV = true;
	public static boolean IS_YCOM = false;
	
	//Query Column Headers
	public static final String CLASS_NAME = "class_name";
	public static final String TOTAL_REQUEST = "total_request";
	public static final String SUCCESS = "success";
	public static final String AGENT_ERROR = "agent_errors";
	public static final String SITE_ERROR = "site_errors";
	public static final String UAR_ERROR = "uar_errors";
	public static final String INFRA_ERROR = "infra_errors";
	public static final String LATENCY = "script_latency";
	public static final String IS_IAV_ENABLED = "IS_IAV_FAST_SUPPRTD";
	
	private  Connection getConnect(String db) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
        
        Connection DBConn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.84.20:1521:"+db ,"read", "read");
        return DBConn; 
	}
	
	ResultSet getResultData(String coBrandID, int queryIdentifier, String dbIdentifier) throws Exception{
		TreeMap<String, Object[]> data = new TreeMap<String, Object[]>();
				
		Connection con = this.getConnect(dbIdentifier);
		
		String serverTypeCheck = "";
		if(IS_IAV){
			serverTypeCheck = "and ss.server_type in ('V') ";
		}
		String cobrandIDCheck = "";
		if(IS_YCOM){
			cobrandIDCheck = "AND ss.cobrand_id IN ('10000004') ";
		}
		
		String query = "select si.CLASS_NAME,sss.SUM_INFO_ID,"
				+" SUM(sss.num_errors) AS Total_Req,"
				+" SUM(DECODE(sss.TYPE_OF_ERROR,0,1,0)) AS SUCCESS, "
				+" SUM(DECODE(sss.TYPE_OF_ERROR,811,1,0)) AS PARTIAL,"
				+" SUM(DECODE(sss.TYPE_OF_ERROR,570,1,0)) AS NO_errors,"
				+" SUM(DECODE(sss.TYPE_OF_ERROR,403,1,408,1,413,1,419,1,439,1,404,1,444,1,449,1,450,1,453,1,475,1,476,1,477,1,"
				+"478,1,479,1,491,1,492,1,493,1,494,1,495,1,496,1,497,1,498,1,507,1,513,1,514,1,707,1,"
				+"708,1,709,1,517,1,0)) as agent_errors,"
				+" sum(decode(sss.TYPE_OF_ERROR,401,1,409,1,410,1,412,1,415,1,416,1,418,1,424,1,425,1,426,1,431,1,432,1,"
				+"447,1,448,1,1012,1,571,1,0)) as site_errors,"
				+" sum(decode(sss.TYPE_OF_ERROR,402,1,406,1,407,1,411,1,414,1,417,1,420,1,421,1,422,1,423,1,427,1,428,1,"
				+"429,1,430,1,434,1,435,1,436,1,437,1,438,1,440,1,441,1,442,1,443,1,445,1,446,1,451,1,452,1,454,1,"
				+"455,1,456,1,457,1,458,1,459,1,460,1,461,1,462,1,463,1,464,1,465,1,466,1,467,1,468,1,469,1,470,1,"
				+"471,1,472,1,480,1,481,1,482,1,483,1,484,1,485,1,486,1,510,1,511,1,512,1,515,1,516,1,604,1,605,1,"
				+"701,1,702,1,703,1,704,1,705,1,706,1,1000,1,1001,1,1002,1,1003,1,1004,1,1005,1,1006,1,1007,1,1008,1,"
				+"1009,1,1010,1,1011,1,1013,1,505,1,506,1,574,1,509,1,518,1,519,1,520,1,521,1,522,1,523,1,524,1,526,1,0)) as uar_errors,"
				+"round (sum(sss.num_errors*AVG_SCRIPT_LATENCY)/sum(sss.num_errors))/1000 as script_latency "
				+" FROM site_stats_suminfo sss, sum_info si"
				+" where sss.COBRAND_ID ="+coBrandID 
				+" and sss.RESPONSE_TYPE='documentDownload'"
				+" and sss.SUM_INFO_ID=si.SUM_INFO_ID"
				//+" and sss.GATHERER_IP in ('10.10.23.100','10.10.23.101','10.10.23.102','10.10.23.107','10.10.23.109','10.10.23.111','10.10.23.98','10.10.23.99','10.22.25.12','10.22.25.14','10.22.25.15','10.22.25.16','10.22.25.17','10.22.25.18','10.22.25.19','10.22.25.21','10.22.25.22','10.22.25.23','10.22.25.24','10.22.25.25','10.22.25.26','10.22.25.3','10.22.25.32','10.22.25.37','10.22.25.39','10.22.25.4','10.22.25.40','10.22.25.43','10.22.25.45','10.22.25.46','10.22.25.50','10.22.25.53','10.22.25.56','10.22.25.6','10.22.25.65','10.22.25.68','10.22.25.69','10.22.25.70','10.22.25.71','10.22.25.72','10.22.25.8','10.22.25.82','10.22.25.83','10.22.25.90','10.22.25.91','10.22.25.92','10.26.25.10','10.26.25.13','10.26.25.14','10.26.25.16','10.26.25.17','10.26.25.18','10.26.25.19','10.26.25.20','10.26.25.21','10.26.25.22','10.26.25.25','10.26.25.46','10.26.25.48','10.26.25.54','10.26.25.55','10.26.25.56','10.26.25.57','10.30.1.20','10.30.1.21','10.30.1.22','10.30.1.23','10.30.1.24','10.30.1.25','10.30.1.26','10.30.1.27','10.30.1.28','10.30.1.29','10.30.1.31','10.30.1.34','10.30.1.40','10.30.1.43','10.30.1.45','10.30.1.63','10.30.1.64','10.30.1.65','10.30.1.66','10.30.1.67','10.30.1.68','10.30.2.21','10.30.2.22','10.30.2.23','10.30.2.24','10.30.2.26','10.30.2.27','10.30.2.31','10.30.2.41','10.30.2.44','10.30.2.45','10.30.2.46','10.8.0.131','10.8.0.132','10.8.0.134','10.8.0.137','10.8.0.141','10.8.0.142','10.8.0.144','10.8.0.147','10.8.0.150','10.8.0.151','10.8.0.153','10.8.0.155','10.8.0.156','10.8.0.157','10.8.0.160','10.8.0.162','10.8.0.165','10.8.0.174','10.8.0.176','172.17.6.10','172.17.6.100','172.17.6.101','172.17.6.103','172.17.6.105','172.17.6.106','172.17.6.107','172.17.6.109','172.17.6.114','172.17.6.115','172.17.6.116','172.17.6.117','172.17.6.118','172.17.6.119','172.17.6.12','172.17.6.120','172.17.6.121','172.17.6.122','172.17.6.123','172.17.6.124','172.17.6.125','172.17.6.13','172.17.6.137','172.17.6.14','172.17.6.143','172.17.6.144','172.17.6.145','172.17.6.148','172.17.6.149','172.17.6.15','172.17.6.150','172.17.6.152','172.17.6.154','172.17.6.156','172.17.6.158','172.17.6.160','172.17.6.18','172.17.6.180','172.17.6.195','172.17.6.196','172.17.6.197','172.17.6.20','172.17.6.206','172.17.6.208','172.17.6.211','172.17.6.216','172.17.6.221','172.17.6.23','172.17.6.237','172.17.6.238','172.17.6.239','172.17.6.24','172.17.6.241','172.17.6.242','172.17.6.243','172.17.6.245','172.17.6.246','172.17.6.25','172.17.6.253','172.17.6.27','172.17.6.28','172.17.6.31','172.17.6.32','172.17.6.34','172.17.6.39','172.17.6.42','172.17.6.43','172.17.6.47','172.17.6.49','172.17.6.52','172.17.6.56','172.17.6.59','172.17.6.62','172.17.6.64','172.17.6.75','172.17.6.79','172.17.6.8','172.17.6.82','172.17.6.91','172.17.6.92','172.17.6.98','172.17.6.99','172.17.7.100','172.17.7.101','172.17.7.102','172.17.7.103','172.17.7.104','172.17.7.107','172.17.7.108','172.17.7.110','172.17.7.111','172.17.7.113','172.17.7.114','172.17.7.115','172.17.7.117','172.17.7.121','172.17.7.122','172.17.7.124','172.17.7.125','172.17.7.126','172.17.7.127','172.17.7.128','172.17.7.129','172.17.7.131','172.17.7.132','172.17.7.133','172.17.7.134','172.17.7.138','172.17.7.143','172.17.7.146','172.17.7.148','172.17.7.149','172.17.7.150','172.17.7.151','172.17.7.152','172.17.7.154','172.17.7.159','172.17.7.171','172.17.7.181','172.17.7.190','172.17.7.22','172.17.7.24','172.17.7.242','172.17.7.245','172.17.7.246','172.17.7.25','172.17.7.27','172.17.7.28','172.17.7.29','172.17.7.32','172.17.7.33','172.17.7.39','172.17.7.40','172.17.7.41','172.17.7.42','172.17.7.43','172.17.7.44','172.17.7.46','172.17.7.47','172.17.7.5','172.17.7.53','172.17.7.54','172.17.7.55','172.17.7.56','172.17.7.57','172.17.7.58','172.17.7.59','172.17.7.6','172.17.7.60','172.17.7.61','172.17.7.62','172.17.7.64','172.17.7.65','172.17.7.66','172.17.7.68','172.17.7.70','172.17.7.71','172.17.7.73','172.17.7.79','172.17.7.82','172.17.7.83','172.17.7.84','172.17.7.85','172.17.7.86','172.17.7.89','172.17.7.91','172.17.7.92','172.17.7.93','172.17.7.96','172.17.7.97','172.17.7.98','172.17.7.99','172.17.8.100','172.17.8.102','172.17.8.106','172.17.8.108','172.17.8.109','172.17.8.11','172.17.8.113','172.17.8.114','172.17.8.117','172.17.8.12','172.17.8.121','172.17.8.13','172.17.8.14','172.17.8.15','172.17.8.16','172.17.8.167','172.17.8.17','172.17.8.178','172.17.8.18','172.17.8.19','172.17.8.197','172.17.8.20','172.17.8.206','172.17.8.21','172.17.8.210','172.17.8.214','172.17.8.216','172.17.8.217','172.17.8.22','172.17.8.223','172.17.8.224','172.17.8.225','172.17.8.226','172.17.8.23','172.17.8.234','172.17.8.235','172.17.8.24','172.17.8.25','172.17.8.26','172.17.8.28','172.17.8.29','172.17.8.30','172.17.8.31','172.17.8.32','172.17.8.33','172.17.8.35','172.17.8.36','172.17.8.37','172.17.8.38','172.17.8.39','172.17.8.40','172.17.8.41','172.17.8.42','172.17.8.46','172.17.8.50','172.17.8.54','172.17.8.56','172.17.8.57','172.17.8.58','172.17.8.62','172.17.8.63','172.17.8.66','172.17.8.67','172.17.8.69','172.17.8.70','172.17.8.71','172.17.8.73','172.17.8.77','172.17.8.78','172.17.8.82','172.17.8.83','172.17.8.84','172.17.8.93','172.17.9.100','172.17.9.101','172.17.9.111','172.17.9.112','172.17.9.113','172.17.9.114','172.17.9.115','172.17.9.117','172.17.9.118','172.17.9.119','172.17.9.120','172.17.9.121','172.17.9.122','172.17.9.123','172.17.9.166','172.17.9.168','172.17.9.169','172.17.9.17','172.17.9.170','172.17.9.171','172.17.9.173','172.17.9.174','172.17.9.176','172.17.9.177','172.17.9.181','172.17.9.182','172.17.9.184','172.17.9.185','172.17.9.189','172.17.9.193','172.17.9.194','172.17.9.20','172.17.9.211','172.17.9.218','172.17.9.219','172.17.9.22','172.17.9.220','172.17.9.222','172.17.9.223','172.17.9.224','172.17.9.226','172.17.9.227','172.17.9.229','172.17.9.230','172.17.9.233','172.17.9.236','172.17.9.237','172.17.9.239','172.17.9.246','172.17.9.247','172.17.9.250','172.17.9.251','172.17.9.252','172.17.9.253','172.17.9.40','172.17.9.47','172.17.9.49','172.17.9.52','172.17.9.53','172.17.9.54','172.17.9.98','172.25.19.15','172.25.2.207','172.25.2.209','172.25.2.212','172.25.2.214','172.25.35.12','172.25.35.15','172.25.35.17','172.25.35.18','172.25.35.19','172.25.35.25','172.25.35.26','172.25.35.28','172.25.35.9')"
				+" and TIMESTAMP>sysdate-1"
				+" GROUP By sss.SUM_INFO_ID, CLASS_NAME";
		ResultSet rs=null;
		try {
			Statement st = con.createStatement();
			System.out.println("^^^^^Query: "+query);
			 rs= st.executeQuery(query);
			/*while(rs.next()){
				if(queryIdentifier == REFRESH_STATS){
				System.out.println(rs.getString(1)+" \t"+rs.getInt(2)+" \t"+rs.getInt(3)+" \t"+rs.getInt(4)+" \t"+rs.getInt(5)+" \t"+rs.getInt(6)+" \t"+rs.getInt(7)+" \t"+rs.getDouble(8)+" \t"+rs.getInt(9));
				int total = rs.getInt(DBConnection.TOTAL_REQUEST);
				System.out.println("total: "+total);
				data.put(rs.getString(DBConnection.CLASS_NAME), 
						new Object[]{rs.getString(DBConnection.CLASS_NAME), 
							rs.getInt(DBConnection.TOTAL_REQUEST), 
							(double) rs.getInt(DBConnection.SUCCESS) *100 /total, 
							(double) rs.getInt(DBConnection.AGENT_ERROR)*100 /total, 
							(double) rs.getInt(DBConnection.SITE_ERROR)*100 /total, 
							(double) rs.getInt(DBConnection.UAR_ERROR)*100 /total, 
							(double) rs.getInt(DBConnection.INFRA_ERROR)*100 /total, 
							rs.getDouble(DBConnection.LATENCY), 
							rs.getInt(DBConnection.IS_IAV_ENABLED)});
				} else if (queryIdentifier == YAD_IAV_FLAG){
					System.out.println(rs.getString(1)+" \t"+rs.getInt(2));
					data.put(rs.getString(DBConnection.CLASS_NAME), new Object[]{rs.getInt(2)});
				}
			}*/
			/*if(DBConnection.IS_IAV){
				rs = st.executeQuery(queryArray[YAD_IAV_FLAG]);
				while(rs.next()){
					if(!data.containsKey(rs.getString(DBConnection.CLASS_NAME))){
						data.put(rs.getString(DBConnection.CLASS_NAME), 
								new Object[]{rs.getString(DBConnection.CLASS_NAME), 
									"No request(s)", 
									(double) 0.0, 
									(double) 0.0, 
									(double) 0.0, 
									(double) 0.0, 
									(double) 0.0, 
									"N/A", 
									rs.getInt(DBConnection.IS_IAV_ENABLED)});
					}
				}
			}*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	public static void main(String [] args) throws Exception{

//		ExcelHelper lt = new ExcelHelper();
//		String queryInput = lt.getQueryInput();
//		queryInput = "("+queryInput+")";
//		System.out.println("queryInput :"+queryInput);

		String fDate = "";
		String tDate = "";
		
		String sql = "select to_date('10/22/2016', 'MM/dd/YYYY') from dual";	
//				"select sum_info_id, class_name,is_ready,"
//				+" (select nvl(sum(num_errors),0) as total_request from site_stats_suminfo pikv  where  pikv.sum_info_id=s.sum_info_id and server_type not in ('B') and TIMESTAMP>="+fDate+" and TIMESTAMP<= "+tDate+") as total,"
//				+" (select nvl(sum(num_errors),0) as total_request from site_stats_suminfo pikv  where  pikv.sum_info_id=s.sum_info_id and server_type in('C','I') and TIMESTAMP>="+fDate+"  and TIMESTAMP<="+tDate+" ) as PFM,"
//				+" (select nvl(sum(num_errors),0) as total_request from site_stats_suminfo pikv  where  pikv.sum_info_id=s.sum_info_id  and server_type in('V') and TIMESTAMP>="+fDate+" and TIMESTAMP<="+tDate+") as IAV,"
//				+" tag_id"
//				+" from sum_info s";
////				+" where sum_info_id = 105";

		
		DBConnection obj = new DBConnection();
		Connection con = obj.getConnect(DBConnection.SITEP_DB);
		if(con == null){
			System.out.println("2^^^^no connection");
		}else {
			System.out.println("connected");
		}
		
//		String query = "Select class_name, IS_IAV_FAST_SUPPRTD from sum_info where class_name IN "+queryInput;
		try {
			System.out.println("SQL: "+sql);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				System.out.println(rs.getString(1)+" \t"+rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
