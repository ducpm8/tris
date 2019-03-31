package com.web.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.web.entity.TrisEntity;
import com.web.util.DownloadFile;
import com.web.util.SendEmail;

public class tris {
	
	public static void main(String[] args) throws IOException, ParseException, URISyntaxException, ClassNotFoundException, SQLException {
		
		DownloadFile down = new DownloadFile();
		SendEmail emailSend = new SendEmail();
		
		long start = System.currentTimeMillis();
		
		String fileName = String.valueOf(System.currentTimeMillis());
		
		String downFile = down.downloadFile("https://www.tris.jp/exdata/outzaiko.xls", "C:\\test", fileName+".xls");
		
//		FileInputStream productFile = new FileInputStream(new File("E:\\test\\testfile.xls"));
		FileInputStream productFile = new FileInputStream(new File(downFile));
		
		HSSFWorkbook workbook = new HSSFWorkbook(productFile);
    	
    	HSSFCell cellHS;
    	HSSFPalette palette = workbook.getCustomPalette();
    	TrisEntity product = new TrisEntity();
	    List<TrisEntity>  productList = new ArrayList<TrisEntity>();
    	
		//Get first sheet from the workbook
    	HSSFSheet sheet = workbook.getSheetAt(0);
    	
    	for (int j=1; j <= sheet.getLastRowNum(); j++) {
    		try {
	    		
	    		product = new TrisEntity();
	    		cellHS = sheet.getRow(j).getCell(7);
	    		
	    		if (cellHS == null) {
	    			continue;
	    		}
	    		
	    		String identify = "";
	    		String tmpCellVal = "";
	    		
				HSSFColor color = palette.getColor(cellHS.getCellStyle().getFillForegroundColor());
				short[] triplet = color.getTriplet();
				if (triplet[0] == 255 && triplet[1] == 255 && triplet[2] ==0) {
					//HIT
					if (sheet.getRow(j).getCell(1) != null) {
						tmpCellVal = readCellValue(sheet.getRow(j).getCell(1));
						product.setProductName(tmpCellVal);
						identify = identify + tmpCellVal;
					} else {
						continue;
					}
					
					if (sheet.getRow(j).getCell(2) != null) {
						tmpCellVal = readCellValue(sheet.getRow(j).getCell(2));
						product.setProductName2(tmpCellVal);
						identify = identify + tmpCellVal;
					}
					
					if (sheet.getRow(j).getCell(3) != null) {
						tmpCellVal = readCellValue(sheet.getRow(j).getCell(3));
						product.setProductName3(tmpCellVal);
						identify = identify + tmpCellVal;
					}
					
//					if (sheet.getRow(j).getCell(7) != null)
//						product.setAmount(readCellValue(sheet.getRow(j).getCell(7)));
//					
//					if (sheet.getRow(j).getCell(8) != null)
//						product.setRetailPrice(readCellValue(sheet.getRow(j).getCell(8)));
//					
//					if (sheet.getRow(j).getCell(9) != null)
//						product.setDiscount(readCellValue(sheet.getRow(j).getCell(9)));
					
					if (sheet.getRow(j).getCell(10) != null)
						product.setNetPrice(readCellValue(sheet.getRow(j).getCell(10)));
					
					product.setIdentify(identify);
					
					productList.add(product);
					
				}
    		} catch (Exception loopE) {
    			//System.out.println("Error " + j);
    		}
    	}
    	
    	workbook.close();
    	
    	new File(downFile).delete();
    	
    	//Send email
    	String mailBody = "";
    	
    	if (productList.size() > 0) {
			ArrayList<TrisEntity> notifyList = getFinalProductList(productList);
			
//			for (TrisEntity sub : notifyList) {
//	    		mailBody = mailBody + "商品名 : " + sub.getProductName() + "\r\n";
//	    		mailBody = mailBody + "型式 : " + sub.getProductName2() + "\r\n";
//	    		mailBody = mailBody + "状態・期限・製造年月日等 : " + sub.getProductName3() + "\r\n";
////	    		mailBody = mailBody + "数量 : " + sub.getAmount() + "\r\n";
////	    		mailBody = mailBody + "定価（税別） : " + sub.getRetailPrice() + "\r\n";
////	    		mailBody = mailBody + "掛率 : " + sub.getDiscount() + "\r\n";
//	    		mailBody = mailBody + "卸単価 : " + sub.getNetPrice() + "\r\n";
//	    		mailBody = mailBody + "============================================= \r\n";
//	    	}
//	    	
//	    	if (!mailBody.isEmpty()) {
//	    		emailSend.sendMail(mailBody);
//	    	}
    	}
    	
    	long end = System.currentTimeMillis();
    	
    	
    	long secs = TimeUnit.MILLISECONDS.toSeconds(end - start);
    	
    	System.out.println("Executed time = " + secs);
    	
    	//long seconds = TimeUnit.MILLISECONDS.toSeconds(end - start);
    	
	}
	
	public static String readCellValue(HSSFCell cell) {
		try {
		    switch (cell.getCellType())
		    {
		    case HSSFCell.CELL_TYPE_BLANK:
		        return "(ブランク)";
		    case HSSFCell.CELL_TYPE_NUMERIC:
		        return String.valueOf(cell.getNumericCellValue());
		    case HSSFCell.CELL_TYPE_STRING:
		        return cell.getStringCellValue();
		    case HSSFCell.CELL_TYPE_FORMULA:
		    	switch(cell.getCachedFormulaResultType()) {
		            case HSSFCell.CELL_TYPE_NUMERIC:
		            	return String.valueOf(cell.getNumericCellValue());
		            case HSSFCell.CELL_TYPE_STRING:
		            	return cell.getRichStringCellValue().toString();
		        }
		    default:
		        return "未定";
		    }
		} catch (Exception e) {
			return "未定";
		}
	}
	
	private static ArrayList<TrisEntity> getFinalProductList(List<TrisEntity> listProduct) throws ClassNotFoundException, SQLException {
		Connection connection = null;
		Statement statement = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<TrisEntity> finalList = new ArrayList<TrisEntity>();
		try {
			
			ArrayList<String> existList = new ArrayList<String>();
			
		    //connection = DriverManager.getConnection(dbUrl, username, password);
		    //connection = DriverManager.getConnection("jdbc:sqlite::resource:tris.s3db");
			connection = DriverManager.getConnection("jdbc:sqlite:C:\\wd\\tris.s3db");
		    
		    statement = connection.createStatement();
		    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		    
		    String sql = "SELECT identify FROM tris";
	        
		    stmt  = connection.createStatement();
		    rs    = stmt.executeQuery(sql);
		    
		    while (rs.next()) {
		    	existList.add(rs.getString("identify"));
		    }
		    
		    for (TrisEntity product : listProduct) {
		    	if (!existList.contains(product.getIdentify())) {
		    		finalList.add(product);
		    	}
		    }
		    
		    if (finalList.size() <= 0) return new ArrayList<TrisEntity>();
		    
		    String mailBody = "";
		    
		    for (TrisEntity sub : finalList) {
	    		mailBody = mailBody + "商品名 : " + sub.getProductName() + "\r\n";
	    		mailBody = mailBody + "型式 : " + sub.getProductName2() + "\r\n";
	    		mailBody = mailBody + "状態・期限・製造年月日等 : " + sub.getProductName3() + "\r\n";
//	    		mailBody = mailBody + "数量 : " + sub.getAmount() + "\r\n";
//	    		mailBody = mailBody + "定価（税別） : " + sub.getRetailPrice() + "\r\n";
//	    		mailBody = mailBody + "掛率 : " + sub.getDiscount() + "\r\n";
	    		mailBody = mailBody + "卸単価 : " + sub.getNetPrice() + "\r\n";
	    		mailBody = mailBody + "============================================= \r\n";
	    	}
	    	
	    	if (!mailBody.isEmpty()) {
	    		SendEmail emailSend = new SendEmail();
	    		emailSend.sendMail(mailBody);
	    	}
		    
		    String result = "";
		    String sum = "";
		    String processDate;
	    	
	    	DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        Date date = new Date();
	        processDate = sdf.format(date);
		    
		    for (TrisEntity product : finalList) {
				result = "'" + product.getIdentify() + "','" + processDate + "'";
				if (!result.isEmpty()) {
				  result = "(" + result + "),";
				  sum = sum + result;
				}
			}
			sum = sum.substring(0, sum.length() -1);
			sum = "INSERT INTO tris VALUES" + sum;
		    
			statement.executeUpdate(sum);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, -7);
			Date dateBefore7DaysD = cal.getTime();
			String dateBefore7DaysS = sdf.format(dateBefore7DaysD);
			
			String houseKeep = "DELETE FROM tris WHERE process_date <" + dateBefore7DaysS;
			statement.executeUpdate(houseKeep);
	    } catch(Exception ex) {
	    	System.out.println("Exception inside getFinalProductList " + ex.getMessage());
	    } finally {
	    	try { rs.close(); } catch (Exception e) { /* ignored */ }
	        try { statement.close(); } catch (Exception e) { /* ignored */ }
	        try { connection.close(); } catch (Exception e) { /* ignored */ }
	    }
		return finalList;
	}
}

