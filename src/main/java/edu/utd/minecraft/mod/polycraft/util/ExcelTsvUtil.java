package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.*;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class ExcelTsvUtil {
	
	public static void readExcel(String excelInputFile)
	{
		try {
			File inputs=new File(excelInputFile+"\\Polycraft Inputs.xlsx");
			File learning=new File(excelInputFile+"\\Polycraft Learning.xlsx");
			File materials=new File(excelInputFile+"\\Polycraft Materials.xlsx");
			File polymers=new File(excelInputFile+"\\Polycraft Polymers.xlsx");
			File recipes=new File(excelInputFile+"\\Polycraft Recipes.xlsx");
		    
			OPCPackage inputsfs = OPCPackage.open((inputs));
			OPCPackage learningfs = OPCPackage.open((learning));
			OPCPackage materialsfs = OPCPackage.open((materials));
			OPCPackage polymersfs = OPCPackage.open((polymers));
			OPCPackage recipesfs = OPCPackage.open((recipes));
		    
		    XSSFWorkbook inputswb = new XSSFWorkbook(inputsfs);
		    XSSFWorkbook learningwb = new XSSFWorkbook(learningfs);
		    XSSFWorkbook materialswb = new XSSFWorkbook(materialsfs);
		    XSSFWorkbook polymerswb = new XSSFWorkbook(polymersfs);
		    XSSFWorkbook recipeswb = new XSSFWorkbook(recipesfs);
		    
		    int inputsNumSheets=inputswb.getNumberOfSheets();
		    int learningNumSheets=learningwb.getNumberOfSheets();
		    int materialsNumSheets=materialswb.getNumberOfSheets();
		    int polymersNumSheets=polymerswb.getNumberOfSheets();
		    int recipesNumSheets=recipeswb.getNumberOfSheets();
		    
		    XSSFSheet[] inputsSheets = new XSSFSheet[inputsNumSheets];
		    for(int c=0;c<inputsNumSheets;c++)
		    {
		    	inputsSheets[c]=inputswb.getSheetAt(c);
		    }
		    inputswb.close();
		    
		    XSSFSheet[] learningSheets = new XSSFSheet[learningNumSheets];
		    for(int c=0;c<learningNumSheets;c++)
		    {
		    	learningSheets[c]=learningwb.getSheetAt(c);
		    }
		    learningwb.close();
		    
		    XSSFSheet[] materialsSheets = new XSSFSheet[materialsNumSheets];
		    for(int c=0;c<materialsNumSheets;c++)
		    {
		    	materialsSheets[c]=materialswb.getSheetAt(c);
		    }
		    materialswb.close();
		    
		    XSSFSheet[] polymersSheets = new XSSFSheet[polymersNumSheets];
		    for(int c=0;c<polymersNumSheets;c++)
		    {
		    	polymersSheets[c]=polymerswb.getSheetAt(c);
		    }
		    polymerswb.close();
		    
		    XSSFSheet[] recipesSheets = new XSSFSheet[recipesNumSheets];
		    for(int c=0;c<recipesNumSheets;c++)
		    {
		    	recipesSheets[c]=recipeswb.getSheetAt(c);
		    }
		    recipeswb.close();
		    
		    for(int c=0;c<inputsNumSheets;c++)
		    {
		    	String sheetName=inputsSheets[c].getSheetName();
		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = inputsSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = inputsSheets[c].getRow(i);
			        if(row != null) {
			            tmp = inputsSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    XSSFCell[] cells= new XSSFCell[rows*cols] ;
			    for(int r = 0; r < rows; r++) {
			        row = inputsSheets[c].getRow(r);
			        if(row != null) {
			            for(int k = 0; k < cols; k++) {
			                cell = row.getCell((short)k);
			                if(cell != null) {
			                	if(cell.getCellType().equals(CellType.FORMULA))
			                	{
			                		if(cell.getCachedFormulaResultType().equals(CellType.NUMERIC))
			                		{
			                			if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	 //tab
			                }
			            }
			            byte[] strToBytes = ("\n").getBytes();
	                	outputStreamTSV.write(strToBytes);
			        }
			    }
			    
			    outputStreamTSV.close();
		    }
		    
		    for(int c=0;c<learningNumSheets;c++)
		    {
		    	String sheetName=learningSheets[c].getSheetName();
		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = learningSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = learningSheets[c].getRow(i);
			        if(row != null) {
			            tmp = learningSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    XSSFCell[] cells= new XSSFCell[rows*cols] ;
			    for(int r = 0; r < rows; r++) {
			        row = learningSheets[c].getRow(r);
			        if(row != null) {
			            for(int k = 0; k < cols; k++) {
			                cell = row.getCell((short)k);
			                if(cell != null) {
			                	if(cell.getCellType().equals(CellType.FORMULA))
			                	{
			                		if(cell.getCachedFormulaResultType().equals(CellType.NUMERIC))
			                		{
			                			if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	 //tab
			                }
			            }
			            byte[] strToBytes = ("\n").getBytes();
	                	outputStreamTSV.write(strToBytes);
			        }
			    }
			    
			    outputStreamTSV.close();
		    }
		    
		    for(int c=0;c<materialsNumSheets;c++)
		    {
		    	String sheetName=materialsSheets[c].getSheetName();
		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = materialsSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = materialsSheets[c].getRow(i);
			        if(row != null) {
			            tmp = materialsSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    XSSFCell[] cells= new XSSFCell[rows*cols] ;
			    for(int r = 0; r < rows; r++) {
			        row = materialsSheets[c].getRow(r);
			        if(row != null) {
			            for(int k = 0; k < cols; k++) {
			                cell = row.getCell((short)k);
			                if(cell != null) {
			                	if(cell.getCellType().equals(CellType.FORMULA))
			                	{
			                		if(cell.getCachedFormulaResultType().equals(CellType.NUMERIC))
			                		{
			                			if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	 //tab
			                }
			            }
			            byte[] strToBytes = ("\n").getBytes();
	                	outputStreamTSV.write(strToBytes);
			        }
			    }
			    
			    outputStreamTSV.close();
		    }
		    
		    for(int c=0;c<polymersNumSheets;c++)
		    {
		    	String sheetName=polymersSheets[c].getSheetName();
		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = polymersSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = polymersSheets[c].getRow(i);
			        if(row != null) {
			            tmp = polymersSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    XSSFCell[] cells= new XSSFCell[rows*cols] ;
			    for(int r = 0; r < rows; r++) {
			        row = polymersSheets[c].getRow(r);
			        if(row != null) {
			            for(int k = 0; k < cols; k++) {
			                cell = row.getCell((short)k);
			                if(cell != null) {
			                	if(cell.getCellType().equals(CellType.FORMULA))
			                	{
			                		if(cell.getCachedFormulaResultType().equals(CellType.NUMERIC))
			                		{
			                			if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	 //tab
			                }
			            }
			            byte[] strToBytes = ("\n").getBytes();
	                	outputStreamTSV.write(strToBytes);
			        }
			    }
			    
			    outputStreamTSV.close();
		    }
		    
		    for(int c=0;c<recipesNumSheets;c++)
		    {
		    	if(recipesSheets[c].getSheetName().equals("Objects"))
		    	{
		    		c++;
		    	}
		    	String sheetName=recipesSheets[c].getSheetName();
		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = recipesSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = recipesSheets[c].getRow(i);
			        if(row != null) {
			            tmp = recipesSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    XSSFCell[] cells= new XSSFCell[rows*cols] ;
			    for(int r = 0; r < rows; r++) {
			        row = recipesSheets[c].getRow(r);
			        if(row != null) {
			            for(int k = 0; k < cols; k++) {
			                cell = row.getCell((short)k);
			                if(cell != null) {
			                	if(cell.getCellType().equals(CellType.FORMULA))
			                	{
			                		if(cell.getCachedFormulaResultType().equals(CellType.NUMERIC))
			                		{
			                			if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())+"\t").getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())+"\t").getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()+"\t").getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                
			                	

			                	 //tab
			                }
			            }
			            byte[] strToBytes = ("\n").getBytes();
	                	outputStreamTSV.write(strToBytes);
			        }
			    }
			    
			    outputStreamTSV.close();
		    }
		    
		    
		} catch(Exception ioe) {
		    ioe.printStackTrace();
		}
	}

}
