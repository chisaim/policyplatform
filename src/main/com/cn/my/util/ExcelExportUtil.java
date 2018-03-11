package com.cn.my.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelExportUtil {

	private String[] columTitle;
	private List<?> list;
	private String excelName;
//	private String className;

	public ExcelExportUtil(String[] columTitle, List<?> list, String excelName) {
		super();
		this.columTitle = columTitle;
		this.list = list;
		this.excelName = excelName;
//		this.className = className;
	}

//	public String getClassName() {
//		return "com.bean."+className;
//	}
//
//	public void setClassName(String className) {
//		this.className = className;
//	}

	public String getExcelName() {
		if (excelName == null || excelName == "") {
			Date date = new Date();
			return new SimpleDateFormat("yyMMddhhmmss").format(date).toString();
		}
		return excelName;
	}

	public void setExcelName(String excelName) {
		this.excelName = excelName;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

	public String[] getColumTitle() {
		return columTitle;
	}

	public void setColumTitle(String[] columTitle) {
		this.columTitle = columTitle;
	}

	public static void excelExport(String[] columTitle,	List<?> list, HttpServletResponse resp) {
		//文件名称
		String excelName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()).toString();
			
		ExcelExportUtil exportUtil = new ExcelExportUtil(columTitle, list,
				excelName);
		exportUtil.exportExcel(resp);

	}

	private HSSFWorkbook exportExcel(HttpServletResponse resp) {
		System.out.println("创建表");
		HSSFWorkbook excel = new HSSFWorkbook(); // 创建工作簿
		HSSFSheet sheet = excel.createSheet("sheet");// 创建sheet
		String[] cnTitle = new String[columTitle.length]; // 中文列明名
		String[] enTitle = new String[columTitle.length]; // 英文列名
		for (int i = 0; i < columTitle.length; i++) {
			String[] split = columTitle[i].split(":");
			cnTitle[i] = split[0];
			enTitle[i] = split[0];
		}

		createTableHeader(sheet, cnTitle);
		createTableRow(sheet, enTitle);
		export(sheet, excel, resp);
		return excel;
	}

	// 创建表头
	private void createTableHeader(HSSFSheet sheet, String[] cnTitle) {
		System.out.println("创建表头");

		HSSFRow row = sheet.createRow(0);// 创建表头行对象
		for (int i = 0; i < cnTitle.length; i++) {
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(cnTitle[i]);
		}
	}

	// 填充数据
	private void createTableRow(HSSFSheet sheet, String[] enTitle) {
		System.out.println("填充数据");
		
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			HSSFRow row = sheet.createRow(i + 1);
			for (int j = 0; j < enTitle.length; j++) {
				HSSFCell cell = row.createCell(j);
				//获取cell里的内容
				String cellcontent = getValueByColumName(enTitle[j], obj) == null ? ""
						: getValueByColumName(enTitle[j], obj).toString();
				//填充cell
				cell.setCellValue(cellcontent);
			}
		}
	}

	// 根据对象的成员变量名获得列值
	private Object getValueByColumName(String columName, Object obj) {
		
		Object value = null;
		//获取类的属性
		String[] split = columName.split("\\.");
		if (split.length == 1) {
			value = getValue(split[0], obj);
		} else  {
			Object subObj = getValue(split[0], obj);
			//截取除第一个属性名之后的路径
            String subFieldName = columName
                    .substring(columName.indexOf(".") + 1);
			value = getValueByColumName(subFieldName, subObj);
		}
		return value;
	}
	
	private Object getValue(String key, Object obj) {
		Object value = null;
		Field[] fields = obj.getClass().getDeclaredFields();
		//遍历属性
		for (Field field : fields) {
			if (field.getName().equals(key)) {
				field.setAccessible(true);//类中的成员变量为private,在类外边使用属性值，故必须进行此操作
				try {
					value = field.get(obj);//获取当前对象中当前Field的value
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return value;			
	}

	// 导出excel
	private void export(HSSFSheet sheet, HSSFWorkbook excel,
			HttpServletResponse response) {
		System.out.println("导出excel");
		// 设置response头信息
		response.reset();
		response.setContentType("application/vnd.ms-excel"); // 改成输出excel文件
		try {
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(excelName.getBytes("gb2312"), "ISO-8859-1")
					+ ".xls");
			OutputStream ouputStream = response.getOutputStream();
			excel.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (UnsupportedEncodingException e1) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
