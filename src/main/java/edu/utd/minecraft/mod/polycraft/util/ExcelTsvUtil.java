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
//		    	String sheetName=inputsSheets[c].getSheetName();
//		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	String sheetName=inputsSheets[c].getSheetName().toLowerCase();
	    		String location = excelInputFile.replace("config", "");
		    	String outputTSV= location+"\\src\\main\\resources\\config\\"+sheetName+".tsv";
		    	
		    	if(sheetName.equals("alloys"))
		    	{
		    		outputTSV = outputTSV.replace("alloys", "alloy");
		    	}
		    	if(sheetName.equals("compounds"))
		    	{
		    		outputTSV = outputTSV.replace("compounds", "compound");
		    	}
		    	if(sheetName.equals("elements"))
		    	{
		    		outputTSV = outputTSV.replace("elements", "element");
		    	}
		    	if(sheetName.equals("items (mc)"))
		    	{
		    		outputTSV = outputTSV.replace("items (mc)", "minecraftitem");
		    	}
		    	if(sheetName.equals("blocks (mc)"))
		    	{
		    		outputTSV = outputTSV.replace("blocks (mc)", "minecraftblock");
		    	}
		    	if(sheetName.equals("minerals"))
		    	{
		    		outputTSV = outputTSV.replace("minerals", "mineral");
		    	}
		    	if(sheetName.equals("polymers"))
		    	{
		    		outputTSV = outputTSV.replace("polymers", "polymer");
		    	}
		    	if(sheetName.equals("polymer objects"))
		    	{
		    		outputTSV = outputTSV.replace("polymer objects", "polymerobject");
		    	}
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = inputsSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    int count = 0;
			    
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = inputsSheets[c].getRow(i);
			        if(row != null) {
			        	 if(row.getPhysicalNumberOfCells()==1)
					        {
					        	if(row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK))
					        	{
					        		if(!sheetName.contentEquals("enums"))
					        			count++;
					        	}
					        }
			            tmp = inputsSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    rows=rows-count;
			    
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
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	 
			                }
			                if(!((k+2)>row.getLastCellNum()))
			                {
			                	byte[] strToBytes = ("\t").getBytes();
	                			outputStreamTSV.write(strToBytes);
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
//		    	String sheetName=learningSheets[c].getSheetName();
//		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	String sheetName=learningSheets[c].getSheetName().toLowerCase();
	    		String location = excelInputFile.replace("config", "");
		    	String outputTSV= location+"\\src\\main\\resources\\config\\"+sheetName+".tsv";
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = learningSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    int count = 0;
			    
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = learningSheets[c].getRow(i);
			        if(row != null) {
			        	 if(row.getPhysicalNumberOfCells()==1)
					        {
					        	if(row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK))
					        	{
					        		count++;
					        	}
					        }
			            tmp = learningSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    rows=rows-count;
			    
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
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                }
			                if(!((k+2)>row.getLastCellNum()))
			                {
			                	byte[] strToBytes = ("\t").getBytes();
	                			outputStreamTSV.write(strToBytes);
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
//		    	String sheetName=materialsSheets[c].getSheetName();
//		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	String sheetName=materialsSheets[c].getSheetName().toLowerCase();
	    		String location = excelInputFile.replace("config", "");
		    	String outputTSV= location+"\\src\\main\\resources\\config\\"+sheetName+".tsv";
		    	if(sheetName.equals("catalysts"))
		    	{
		    		outputTSV = outputTSV.replace("catalysts", "catalyst");
		    	}
		    	if(sheetName.equals("cell culture"))
		    	{
		    		outputTSV = outputTSV.replace("cell culture", "cellculturedish");
		    	}
		    	if(sheetName.equals("compressed blocks"))
		    	{
		    		outputTSV = outputTSV.replace("compressed blocks", "compressedblock");
		    	}
		    	if(sheetName.equals("custom objects"))
		    	{
		    		outputTSV = outputTSV.replace("custom objects", "customobject");
		    	}
		    	if(sheetName.equals("dna sampler"))
		    	{
		    		outputTSV = outputTSV.replace("dna sampler", "dnasampler");
		    	}
		    	if(sheetName.equals("compound vessels"))
		    	{
		    		outputTSV = outputTSV.replace("compound vessels", "compoundvessel");
		    	}
		    	if(sheetName.equals("element vessels"))
		    	{
		    		outputTSV = outputTSV.replace("element vessels", "elementvessel");
		    	}
		    	if(sheetName.equals("internal objects"))
		    	{
		    		outputTSV = outputTSV.replace("internal objects", "internalobject");
		    	}
		    	if(sheetName.equals("nuggets"))
		    	{
		    		outputTSV = outputTSV.replace("nuggets", "nugget");
		    	}
		    	if(sheetName.equals("ores"))
		    	{
		    		outputTSV = outputTSV.replace("ores", "ore");
		    	}
		    	if(sheetName.equals("ingots"))
		    	{
		    		outputTSV = outputTSV.replace("ingots", "ingot");
		    	}
		    	
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = materialsSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
	
			    int count = 0;
			    
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = materialsSheets[c].getRow(i);
			        if(row != null) {
			        	 if(row.getPhysicalNumberOfCells()<=2)
					        {
					        	if(row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK))
					        	{
					        		count++;
					        	}
					        }
			            tmp = materialsSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    rows=rows-count;
			    
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
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                }
			                if(!((k+2)>row.getLastCellNum()))
			                {
			                	byte[] strToBytes = ("\t").getBytes();
	                			outputStreamTSV.write(strToBytes);
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
//		    	String sheetName=polymersSheets[c].getSheetName();
//		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	String sheetName=polymersSheets[c].getSheetName().toLowerCase();
	    		String location = excelInputFile.replace("config", "");
		    	String outputTSV= location+"\\src\\main\\resources\\config\\"+sheetName+".tsv";
		    	if(sheetName.equals("pellets"))
		    	{
		    		outputTSV = outputTSV.replace("pellets", "polymerpellets");
		    	}
		    	if(sheetName.equals("polycraft armor"))
		    	{
		    		outputTSV = outputTSV.replace("polycraft armor", "armor");
		    	}
		    	if(sheetName.equals("gripped synthetic tools"))
		    	{
		    		outputTSV = outputTSV.replace("gripped synthetic tools", "grippedsynthetictool");
		    	}
		    	if(sheetName.equals("gripped tools"))
		    	{
		    		outputTSV = outputTSV.replace("gripped tools", "grippedtool");
		    	}
		    	if(sheetName.equals("masks"))
		    	{
		    		outputTSV = outputTSV.replace("masks", "mask");
		    	}
		    	if(sheetName.equals("molds"))
		    	{
		    		outputTSV = outputTSV.replace("molds", "mold");
		    	}
		    	if(sheetName.equals("molded items"))
		    	{
		    		outputTSV = outputTSV.replace("molded items", "moldeditem");
		    	}
		    	if(sheetName.equals("pogo sticks"))
		    	{
		    		outputTSV = outputTSV.replace("pogo sticks", "pogostick");
		    	}
		    	if(sheetName.equals("polycraft tools"))
		    	{
		    		outputTSV = outputTSV.replace("polycraft tools", "tool");
		    	}
		    	if(sheetName.equals("blocks (poly)"))
		    	{
		    		outputTSV = outputTSV.replace("blocks (poly)", "polymerblock");
		    	}
		    	if(sheetName.equals("bricks"))
		    	{
		    		outputTSV = outputTSV.replace("bricks", "polymerbrick");
		    	}
		    	if(sheetName.equals("stairs (poly)"))
		    	{
		    		outputTSV = outputTSV.replace("stairs (poly)", "polymerstairs");
		    	}
		    	if(sheetName.equals("slabs (poly)"))
		    	{
		    		outputTSV = outputTSV.replace("slabs (poly)", "polymerslab");
		    	}
		    	if(sheetName.equals("walls (poly)"))
		    	{
		    		outputTSV = outputTSV.replace("walls (poly)", "polymerwall");
		    	}
		    	if(sheetName.equals("wafers"))
		    	{
		    		outputTSV = outputTSV.replace("wafers", "waferitem");
		    	}
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = polymersSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
			    int count = 0;
			    
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = polymersSheets[c].getRow(i);
			        if(row != null) {
			        	if(row.getPhysicalNumberOfCells()==1)
				        {
				        	if(row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK))
				        	{
				        		count++;
				        	}
				        }
			            tmp = polymersSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    rows=rows-count;
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
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                }
			                if(!((k+2)>row.getLastCellNum()))
			                {
			                	byte[] strToBytes = ("\t").getBytes();
	                			outputStreamTSV.write(strToBytes);
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
		    	String outputTSV;
		    	String sheetName;
//		    	if(		recipesSheets[c].getSheetName().equals("Objects")		    			)		    		
//		    	{
//		    		c++;
//		    	}
		    	if(		recipesSheets[c].getSheetName().equals("Process") || 
		    			recipesSheets[c].getSheetName().equals("Craft") || 	
		    			recipesSheets[c].getSheetName().equals("Distill") || 
		    			recipesSheets[c].getSheetName().equals("Smelt") || 
		    			recipesSheets[c].getSheetName().equals("Mill") || 
		    			recipesSheets[c].getSheetName().equals("Write") || 
		    			recipesSheets[c].getSheetName().equals("Treat") || 
		    			recipesSheets[c].getSheetName().equals("PolyCraft") || 
		    			recipesSheets[c].getSheetName().equals("Crack") || 
		    			recipesSheets[c].getSheetName().equals("Exchange")
		    			)
		    	{
		    		sheetName=recipesSheets[c].getSheetName().toLowerCase();
		    		String location = excelInputFile.replace("config", "");
			    	if(sheetName.equals("process"))
			    	{
			    		sheetName = sheetName.replace("process", "chemical_processor");
			    	}
			    	if(sheetName.equals("craft"))
			    	{
			    		sheetName = sheetName.replace("craft", "crafting_table");
			    	}
			    	if(sheetName.equals("distill"))
			    	{
			    		sheetName = sheetName.replace("distill", "distillation_column");
			    	}
			    	if(sheetName.equals("smelt"))
			    	{
			    		sheetName = sheetName.replace("smelt", "furnace");
			    	}
			    	if(sheetName.equals("mill"))
			    	{
			    		sheetName = sheetName.replace("mill", "machining_mill");
			    	}
			    	if(sheetName.equals("write"))
			    	{
			    		sheetName = sheetName.replace("write", "mask_writer");
			    	}
			    	if(sheetName.equals("treat"))
			    	{
			    		sheetName = sheetName.replace("treat", "merox_treatment_unit");
			    	}
			    	if(sheetName.equals("polycraft"))
			    	{
			    		sheetName = sheetName.replace("polycraft", "polycrafting_table");
			    	}
			    	if(sheetName.equals("crack"))
			    	{
			    		sheetName = sheetName.replace("crack", "steam_cracker");
			    	}
			    	if(sheetName.equals("exchange"))
			    	{
			    		sheetName = sheetName.replace("e0xchange", "trading_house");
			    	}
			    	outputTSV= location+"\\src\\main\\resources\\recipes\\"+sheetName+".tsv";
		    	}
		    	else
		    	{
		    		sheetName=recipesSheets[c].getSheetName().toLowerCase();
		    		String location = excelInputFile.replace("config", "");
		    		if(sheetName.equals("inventories"))
			    	{
		    			sheetName = sheetName.replace("inventories", "inventory");
			    	}
			    	outputTSV= location+"\\src\\main\\resources\\config\\"+sheetName+".tsv";
		    	}
//		    	String sheetName=recipesSheets[c].getSheetName();
//		    	String outputTSV= excelInputFile+"\\TSVs\\"+sheetName+".tsv";
		    	
		    	
		    	
		    	File outputTSVfile= new File(outputTSV);
		    	FileOutputStream outputStreamTSV = new FileOutputStream(outputTSVfile);
		    	
			    XSSFRow row;
			    XSSFCell cell;
			    
	
			    int rows; // No of rows
			    rows = recipesSheets[c].getPhysicalNumberOfRows();
	
			    int cols = 0; // No of columns
			    int tmp = 0;
			    int count = 0;
			    
			    // This trick ensures that we get the data properly even if it doesn't start from first few rows
			    for(int i = 0; i < 10 || i < rows; i++) {
			        row = recipesSheets[c].getRow(i);
			        if(row != null) {
			        	 if(row.getPhysicalNumberOfCells()==1)
					        {
					        	if(row.getCell(row.getFirstCellNum()).getCellType().equals(CellType.BLANK))
					        	{
					        		count++;
					        	}
					        }
			            tmp = recipesSheets[c].getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }
			    
			    rows=rows-count;
			    
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
				                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
					                		outputStreamTSV.write(strToBytes);
				                		}
				                		else
				                		{
				                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
				                			outputStreamTSV.write(strToBytes);
				                		}
			                		}
			                		else if(cell.getCachedFormulaResultType().equals(CellType.STRING))
			                		{
			                			byte[] strToBytes = (cell.getStringCellValue()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (cell.toString()).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.NUMERIC))
			                	{
			                		if ((cell.getNumericCellValue() == Math.floor(cell.getNumericCellValue())) && !Double.isInfinite(cell.getNumericCellValue())) {
			                			byte[] strToBytes = (String.valueOf(cell.getRawValue())).getBytes();
				                		outputStreamTSV.write(strToBytes);
			                		}
			                		else
			                		{
			                			byte[] strToBytes = (String.valueOf(cell.getNumericCellValue())).getBytes();
			                			outputStreamTSV.write(strToBytes);
			                		}
			                	}
			                	else if(cell.getCellType().equals(CellType.STRING))
			                	{
			                		byte[] strToBytes = (cell.getStringCellValue()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else if(cell.getCellType().equals(CellType.ERROR))
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                	else 
			                	{
			                		byte[] strToBytes = (cell.toString()).getBytes();
			                		outputStreamTSV.write(strToBytes);
			                	}
			                }
			                if(!((k+2)>row.getLastCellNum()))
			                {
			                	byte[] strToBytes = ("\t").getBytes();
	                			outputStreamTSV.write(strToBytes);
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
