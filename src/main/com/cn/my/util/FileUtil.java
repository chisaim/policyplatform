package com.cn.my.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * @ClassName: FileUtil
 * @Description: 文件处理工具类
 * @author zengyejun
 * @date 2017年12月4日
 */
public class FileUtil {

	private static final String UPLOAD_FILE_PATH_WINDOWS = "D:\\upload\\";
	private static final String UPLOAD_FILE_PATH_LINUX = "/usr/local/";

	// 将文件写入硬盘
	public static Map<String, String> writeFiles(List<MultipartFile> files) {
		Map<String, String> map = new HashMap<String, String>();
		for (MultipartFile file : files) {
			String file_name = file.getOriginalFilename();
			String new_name = UuidUtil.generateToken() + file_name
					.substring(file_name.lastIndexOf("."), file_name.length());
			if (writeData2File(new_name, file)) {
				map.put(file_name, new_name);
			}
		}
		return map;
	}

	// 写文件
	private static boolean writeData2File(String new_name, MultipartFile file) {
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			in = file.getInputStream();
			String root_path = getRootPath();
			File dir = new File(root_path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File new_file = new File(root_path + new_name);
			// upload文件夹里面创建文件，文件名为new_name
			if (!new_file.exists()) {
				new_file.createNewFile();
			}
			fos = new FileOutputStream(new_file);
			byte[] b = new byte[1024];
			while ((in.read(b)) != -1) {
				fos.write(b);
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				in.close();
				fos.close();
			} catch (IOException e) {
				return false;
			}
		}
	}

	// 判断是否是Excel文件类型
	public static boolean checkIfExcel(List<MultipartFile> files) {
		boolean flag = true;
		for (MultipartFile file : files) {
			String name = file.getOriginalFilename();
			String file_type = name.substring(name.lastIndexOf("."),
					name.length());
			if (".xls".equals(file_type) || ".xlsx".equals(file_type)
					|| ".csv".equals(file_type)) {

			} else {
				flag = flag && false;
			}
		}
		return flag;
	}

	// 读取excel文件，返回实体list,文件已做预处理，表格第一行是列名，接下来是数据行
	@SuppressWarnings("deprecation")
	public static <T> List<T> readExcel(Class<T> clazz, String filename)
			throws IOException {
		String suffix = filename.substring(filename.lastIndexOf("."),
				filename.length());
		filename = getRootPath() + filename;
		InputStream is = new FileInputStream(filename);
		Workbook wb = null;
		if (".xls".equals(suffix)) {
			wb = new HSSFWorkbook(is);
		} else if ("xlsx".equals(suffix)) {
			wb = new XSSFWorkbook(is);
		}
		Sheet sheet = wb.getSheetAt(0);// 只取第一个表格的数据
		List<T> list = new ArrayList<T>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			// 第一列是目录列，其他列是数据列
			Row row = sheet.getRow(i);
			if (0 == i) {
				for (Cell cell : row) {
					if (null != cell.getStringCellValue()
							&& "" != cell.getStringCellValue()) {
						map.put(cell.getStringCellValue(),
								cell.getColumnIndex());
					}
				}
			} else {
				JSONObject json = new JSONObject();
				for (Iterator<String> iterator = map.keySet()
						.iterator(); iterator.hasNext();) {
					String key = iterator.next();
					switch (row.getCell(map.get(key)).getCellType()) {
						case Cell.CELL_TYPE_STRING :
							json.put(key, row.getCell(map.get(key)).toString());
							break;
						case Cell.CELL_TYPE_NUMERIC :
							if (HSSFDateUtil.isCellDateFormatted(
									row.getCell(map.get(key)))) {
								java.util.Date time = row.getCell(map.get(key))
										.getDateCellValue();
								Timestamp timestamp = new Timestamp(
										time.getTime());
								json.put(key, timestamp);
							} else {
								DecimalFormat df = new DecimalFormat("0");
								String value = df
										.format(row.getCell(map.get(key))
												.getNumericCellValue());
								json.put(key, value);
							}
							break;
						default :
							break;
					}
				}
				list.add(json.toJavaObject(clazz));
			}
		}
		return list;
	}

	// 根据操作系统获取根路径
	private static String getRootPath() {
		String root_path = "";
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			root_path = UPLOAD_FILE_PATH_WINDOWS;
		} else {
			root_path = UPLOAD_FILE_PATH_LINUX;
		}
		return root_path;
	}

	// 打印对象的属性值
	public static <T> void printObjectProperties(Class<T> clazz, T t)
			throws Exception {
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			f.setAccessible(true);
		}
		for (Field f : fields) {
			String field = f.toString()
					.substring(f.toString().lastIndexOf(".") + 1);// 取出属性名称
			System.out.println(field + " --> " + f.get(t) + "aa" + f.getType());
		}
	}

	public static List<String> getFilenames(List<MultipartFile> files) {
		List<String> list = new ArrayList<String>();
		for (MultipartFile file : files) {
			String file_name = file.getOriginalFilename();
			list.add(file_name);
		}
		return list;
	}

	// 删除首行
	public static void deleteSheetHeader(String filename) throws Exception {
		String suffix = filename.substring(filename.lastIndexOf("."),
				filename.length());
		filename = getRootPath() + filename;
		InputStream is = new FileInputStream(filename);
		Workbook wb = null;
		if (".xls".equals(suffix)) {
			wb = new HSSFWorkbook(is);
		} else if ("xlsx".equals(suffix)) {
			wb = new XSSFWorkbook(is);
		}
		Sheet sheet = wb.getSheetAt(0);// 只取第一个表格的数据
		Row row = sheet.getRow(0);
		if (null != row) {
			sheet.removeRow(row);
			// sheet.shiftRows(0, 1, -1);//删除第0行到第1行，然后使下方单元格上移
		}
		FileOutputStream os = new FileOutputStream(filename);
		wb.write(os);
		is.close();
		os.close();
	}

	public static int getExcelLastNum(String filename) throws Exception {
		String suffix = filename.substring(filename.lastIndexOf("."),
				filename.length());
		filename = getRootPath() + filename;
		InputStream is = new FileInputStream(filename);
		Workbook wb = null;
		if (".xls".equals(suffix)) {
			wb = new HSSFWorkbook(is);
		} else if ("xlsx".equals(suffix)) {
			wb = new XSSFWorkbook(is);
		}
		Sheet sheet = wb.getSheetAt(0);// 只取第一个表格的数据
		return sheet.getLastRowNum();
	}

	public static void exportUncertainFile(JSONArray jsonArray,
			ServletOutputStream out) {
		try {
			// 第一步，创建一个workbook，对应一个Excel文件
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 第二步，在book中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet hssfSheet = workbook.createSheet("sheet1");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow hssfRow = hssfSheet.createRow(0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
			// 居中样式
			hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
			// 文件流行标题
			HSSFCell hssfCell = null;
			String[] titles = new String[]{"主叫号码", "短信内容", "导入时间", "操作人", "渠道",
					"业务类型"};
			for (int i = 0; i < titles.length; i++) {
				hssfCell = hssfRow.createCell(i);// 列索引从0开始
				hssfCell.setCellValue(titles[i]);// 列名1
				hssfCell.setCellStyle(hssfCellStyle);// 列居中显示
			}
			// 写入数据
			int i = 0;
			for (Object obj : jsonArray) {
				JSONObject json = (JSONObject) obj;
				hssfRow = hssfSheet.createRow(i + 1);
				if (null != json) {
					hssfRow.createCell(0)
							.setCellValue(json.getString("calling_number"));
					hssfRow.createCell(1)
							.setCellValue(json.getString("message_content"));
					hssfRow.createCell(2).setCellValue(
							json.getString("checkout_time").substring(0,
									json.getString("checkout_time")
											.lastIndexOf(".")));
					hssfRow.createCell(3)
							.setCellValue(json.getString("operator"));
					hssfRow.createCell(4)
							.setCellValue(json.getString("sample_origin"));
					hssfRow.createCell(5)
							.setCellValue(json.getString("business_type"));
				}
				i++;
			}
			// 文件输出到客户端浏览器
			workbook.write(out);
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public static byte[] File2byte(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据文件流读取excel文件的表头，返回列名与列号的映射关系
	 */
	public static Map<String, Integer> readExcelHeader(Row row) {
		if (row == null) {
			return null;
		}
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Cell cell : row) {
			if (cell == null) {
				continue;
			}
			if (null != cell.getStringCellValue()
					&& (!"".equals(cell.getStringCellValue()))) {
				map.put(cell.getStringCellValue(), cell.getColumnIndex());
			}
		}
		return map;
	}

	/**
	 * 读取excel文件，将excel文件中的数据转化为实体对象 入参要求：excel文件流中必须包含表头，表头字段严格与实体类属性名对应
	 */
	@SuppressWarnings({"resource", "deprecation"})
	public static <T> List<T> stream2Entity(Class<T> clazz, InputStream in) {
		try {
			Workbook wb = new HSSFWorkbook(in);
			Sheet sheet = wb.getSheetAt(0);// 只取第一个表格的数据
			List<T> list = new ArrayList<>();
			Map<String, Integer> colmap4name2num = readExcelHeader(
					sheet.getRow(0));
			if (colmap4name2num == null) {
				// 没表头，或表头不在第一行不符合入参要求，不读了bye
				return null;
			}
			// 存储字段与数据类型间的映射关系，之后再根据数据类型读数据
			Field[] fields = clazz.getDeclaredFields();
			for (Field f : fields) {
				f.setAccessible(true);
			}
			Map<String, String> objmap4field2type = new HashMap<String, String>();
			for (Field f : fields) {
				String field = f.toString()
						.substring(f.toString().lastIndexOf(".") + 1);// 取出属性名称
				String type = f.getType().toString();
				objmap4field2type.put(field, type
						.substring(type.lastIndexOf(".") + 1, type.length()));
			}
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				JSONObject obj = new JSONObject();
				for (String key : objmap4field2type.keySet()) {
					if (!colmap4name2num.containsKey(key)) {
						// 排除实体类中有而excel中没有的字段
						continue;
					}
					// 判断cell是否为空
					if (row.getCell(colmap4name2num.get(key)) == null) {
						continue;
					}
					switch (objmap4field2type.get(key)) {
						case "Integer" :
						case "int" :
						case "Short" :
						case "Long" :
						case "Double" :
						case "Float" :
						case "Byte" :
							obj.put(key, row.getCell(colmap4name2num.get(key)).getNumericCellValue());
							break;
						case "Date" :
						case "Timestamp":
							obj.put(key, row.getCell(colmap4name2num.get(key)).getDateCellValue());
							break;
						case "Boolean" :
						case "boolean" :
							if(row.getCell(colmap4name2num.get(key)).getCellType()==Cell.CELL_TYPE_NUMERIC){
								boolean flag = row.getCell(colmap4name2num.get(key)).getNumericCellValue()==0?false:true;
								obj.put(key, flag);
								break;
							}
							obj.put(key, row.getCell(colmap4name2num.get(key)).getBooleanCellValue());
							break;
						default :// 默认String
							if(row.getCell(colmap4name2num.get(key)).getCellType()==Cell.CELL_TYPE_NUMERIC){
								String string = (new BigDecimal(row.getCell(colmap4name2num.get(key)).getNumericCellValue())).toString();
								obj.put(key, string);
								break;
							}
							obj.put(key, row.getCell(colmap4name2num.get(key)).getStringCellValue());
							break;
					}
				}
				// 将json数据转为对象
				T t = JsonUtils.json2obj(obj.toString(), clazz);
				list.add(t);
			}
			return list;
		} catch (IOException e) {
			System.err.println("IO错误，读取流失败！ " + e.getMessage());
			return null;
		} catch (Exception e) {
			System.err.println("json对象转化失败!" + e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static void writeJSONData2Out(String[] cn_title,
			String[] en_property, List<Map<String,Object>> maplist, OutputStream out) {
		try {
			// 第一步，创建一个workbook，对应一个Excel文件
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			// 第二步，在book中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet hssfSheet = workbook.createSheet("sheet1");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow hssfRow = hssfSheet.createRow(0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle hssfCellStyle = workbook.createCellStyle();
			// 居中样式
			hssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
			// 文件流行标题
			HSSFCell hssfCell = null;
			for (int i = 0; i < cn_title.length; i++) {
				hssfCell = hssfRow.createCell(i);// 列索引从0开始
				hssfCell.setCellValue(cn_title[i]);// 列名1
				hssfCell.setCellStyle(hssfCellStyle);// 列居中显示
			}
			// 写入数据
			int i = 0;
			for (Map map : maplist) {
				hssfRow = hssfSheet.createRow(i + 1);
				if (null != map) {
					for (int j = 0; j < en_property.length; j++) {
						hssfCell = hssfRow.createCell(j);// 列索引从0开始
						hssfCell.setCellValue(String.valueOf(map.get(en_property[j])));// 列名1
						hssfCell.setCellStyle(hssfCellStyle);// 列居中显示
					}
				}
				i++;
			}
			// 文件输出到客户端浏览器
			workbook.write(out);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

}
