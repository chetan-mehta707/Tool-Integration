import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.TreeMap;


public class LocalTesting {
	
	private static HashMap<String, String> cobrandToIDMap=new HashMap<>();
	static{
		cobrandToIDMap.put("PC", "10002812");
		cobrandToIDMap.put("BAC", "10001372");
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		try {
		    // Convert from Unicode to UTF-8
		    String string = "–";
		    byte[] utf8 = string.getBytes("UTF-8");
		    String str = new String(utf8);
		    System.out.println("Str :"+str+":");
		    
		    // Convert from UTF-8 to Unicode
		    string = new String(utf8, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		
		
		
		DBConnection.IS_IAV = true;
		DBConnection.IS_YCOM = false;
		
		if(args.length != 0){
			if(args[0].toLowerCase().contains("y"))
				DBConnection.IS_IAV = true;
			else
				DBConnection.IS_IAV = false;
			
			if(args[1].toLowerCase().contains("y"))
				DBConnection.IS_YCOM = true;
			else
				DBConnection.IS_YCOM = false;
		}
		
		ExcelHelper excelHelper = new ExcelHelper();
		
		//String queryInput = excelHelper.getQueryInput();
		//System.out.println("queryInput :"+queryInput);
		
		DBConnection dbConnection = new DBConnection();
		HashMap<String, double[]> cobrandIDMailDataMap=new HashMap<String, double[]>();
		//String queryIdentifier[]={"stats","users"};
		double[] totalStats=null;
		for(String cobrandID:cobrandToIDMap.keySet()){
				ResultSet data = dbConnection.getResultData(cobrandToIDMap.get(cobrandID), DBConnection.REFRESH_STATS, DBConnection.SITEP_DB);
				if(data!=null){
				totalStats = excelHelper.exportData(data,cobrandID);
				cobrandIDMailDataMap.put(cobrandID, totalStats);		
				}else{
					cobrandIDMailDataMap.put(cobrandID, totalStats);
				}

		}
		//TreeMap<String, Object[]> mailData = excelHelper.getTopAgentErrors(data);
		//TreeMap<String, Object[]> mailData = excelHelper.getTopAgentErrors(data);
		
		/*for(String key : mailData.keySet()){
			System.out.println("Key : "+key+ "Value : "+mailData.get(key));
		}*/
		
		new ProjectsMailer().sendMail(cobrandIDMailDataMap);
		ExcelHelper.closeWorkBook();
	}
}
	
