import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class ExcelHelper {
	private static XSSFWorkbook workbook = new XSSFWorkbook(); 
	private static final String EXCEL_FILE_PATH = "D:\\Document download stats";

	String getQueryInput()throws Exception{
		List<String> agentList = getAgentList();
		String queryInput = "'";
		for(String agentName : agentList){
			queryInput = queryInput + agentName + "','";
		}
		queryInput = queryInput.substring(0, queryInput.length()-2);
		return queryInput;
	}

	List<String> getAgentList() throws FileNotFoundException, IOException{
		String sheetName = "PFM";
		if(DBConnection.IS_IAV){
			sheetName = "IAV+";
		}
		List<String> agentList = new ArrayList<String>();
		File file  = new File(EXCEL_FILE_PATH + "\\AgentList.xlsx");
		FileInputStream inputStream = new FileInputStream(file);

		Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = workbook.getSheet(sheetName);
        Iterator<Row> iterator = firstSheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
             
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int classCol = 0;
                if(cell.getStringCellValue().equalsIgnoreCase("Agent Name")){
                	classCol = cell.getColumnIndex();
                	continue;
                }
                if(cell.getColumnIndex() == classCol){
                	String curLine = cell.getStringCellValue();
                	curLine = curLine.trim().replaceAll("[\n\r\b\t.]", "");
        			System.out.println("curLine :"+curLine);
        			agentList.add(curLine);
                }
            }
        }
        workbook.close();
        inputStream.close();
		return agentList;
	}

	double[] exportData(ResultSet data,String cobrandID) throws SQLException{
		boolean alertFlag = false;
		XSSFSheet sheet = workbook.createSheet(cobrandID);
		
		CellStyle style = (CellStyle) workbook.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());

		String [] headerArr = new String[]{"CLASS_NAME", "SUM_INFO_ID","TOTAL_REQUEST", "SUCCESS", "PARTIAL","570","AGENT_ERRORS", "SITE_ERRORS", "UAR_ERRORS","SCRIPT_LATENCY"};
		int cellnum = 0;
		Row headerRow = sheet.createRow(0);
			for(String str : headerArr){
				XSSFCell cell = (XSSFCell) headerRow.createCell(cellnum++);
				sheet.autoSizeColumn(cellnum-1);
				cell.setCellValue(str);
				cell.setCellStyle(style);
			}
		//Set<String> keyset = data.;
		int rownum = 1;
		double[] total=new double[7];
		Arrays.fill(total, 0);
		while (data.next())
		{
			Row row = sheet.createRow(rownum++);
			cellnum = 0;

			int cols = data.getMetaData().getColumnCount();
			//Object[] objArr = new Object[cols];
			for(int i=0; i<cols; i++){
				Object obj = data.getObject(i+1);
				XSSFCell cell = (XSSFCell) row.createCell(cellnum++);
				cell.setCellStyle(style);
				if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Double)
					cell.setCellValue(round((Double)obj, 2));
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
				else if(obj instanceof BigDecimal)
					cell.setCellValue(((BigDecimal)obj).doubleValue());
				if(i==0 || i==1 || i==9){
					continue;
				}else if(i==2){
					//System.out.println("obj::"+obj);
					total[i-2]=total[i-2]+((BigDecimal)obj).doubleValue();
					//System.out.println(" request total["+(i-1)+"]:"+total[i-1]);
				}else{
					total[i-2]=(total[i-2]+((BigDecimal)obj).doubleValue());
					//System.out.println("total["+(i-1)+"]:"+total[i-1]);
				}
			}
			//Object [] objArr = data;

			short colorIndex = IndexedColors.LIGHT_BLUE.getIndex();
			/*if(((Integer) objArr[objArr.length-1]).intValue() == 0){
            	colorIndex = IndexedColors.RED.getIndex();
            	alertFlag = true;
            }*/
			style.setFillBackgroundColor(colorIndex);

			/*for (Object obj : objArr)
            {
               XSSFCell cell = (XSSFCell) row.createCell(cellnum++);
               cell.setCellStyle(style);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
               else if(obj instanceof Double)
            	   cell.setCellValue(round((Double)obj, 2));
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }*/
		}
		try
		{
			//Write the workbook in file system
			FileOutputStream out = new FileOutputStream(new File(EXCEL_FILE_PATH + "\\Document Download Stats.xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Data.xlsx written successfully on disk.");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return total;
	}
	
	public static void closeWorkBook() {
		try {
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getFilePath(){
		return EXCEL_FILE_PATH;
	}
	
	public static double round(Double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
	TreeMap<String, Object[]> getTopAgentErrors(TreeMap<String, Object[]> data) {

		final Comparator<Entry<?, Double>> DOUBLE_VALUE_COMPARATOR = new Comparator<Entry<?, Double>>() {
			@Override
			public int compare(Entry<?, Double> o1, Entry<?, Double> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		};

		Map<String, Double> mapToSort = new HashMap<String, Double>();
		for (String key : data.keySet()) {
			Object[] objArr = (Object[]) data.get(key);
			double value = (Double) objArr[3];
			mapToSort.put(key, value);
		}
		Set<Entry<String, Double>> entryOfMap = mapToSort.entrySet();

		List<Entry<?, Double>> entries = new ArrayList<Entry<?, Double>>(entryOfMap);
		Collections.sort(entries, DOUBLE_VALUE_COMPARATOR);

		TreeMap<String, Object[]> mailData = new TreeMap<String, Object[]>();
		if(entries.size() != 0){
			int ubound = (entries.size()>5) ? 5 : entries.size();
			int count = 0; int i = 0;
			while(count < ubound && i < entries.size()){
				Entry<?, Double> entry = entries.get(i++);
				String key = (String) entry.getKey();
				System.out.println("val : "+entry.getValue());
				if(entry.getValue() == 0.0) continue;
				Object[] value = data.get(key);
				mailData.put(key, value);
				count++;
			}
		}
		return mailData;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ExcelHelper ehObj = new ExcelHelper();
		String queryInput = ehObj.getQueryInput();
		System.out.println("queryInput :"+queryInput);
		
	}

}
