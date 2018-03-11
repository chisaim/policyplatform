package com.cn.my.util;

//中英文符号转化工具类
public class CharacterConvertUtil {
	//ASCII表中可见字符从!开始，偏移位值为33(Decimal)  
    static final char DBC_CHAR_START = 33; // 半角!    
    
    //ASCII表中可见字符到~结束，偏移位值为126(Decimal)  
    static final char DBC_CHAR_END = 126; // 半角~    
    
    //全角对应于ASCII表的可见字符从！开始，偏移值为65281  
    static final char SBC_CHAR_START = 65281; // 全角！    
    
    //全角对应于ASCII表的可见字符到～结束，偏移值为65374  
    static final char SBC_CHAR_END = 65374; // 全角～    
    
    //ASCII表中除空格外的可见字符与对应的全角字符的相对偏移  
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔    
    
    //全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理  
    static final char SBC_SPACE = 12288; // 全角空格 12288    
    
    // 半角空格的值，在ASCII中为32(Decimal)  
    static final char DBC_SPACE = ' '; // 半角空格    
    
    /**  
     * 半角字符->全角字符转换    
     * 只处理空格，!到˜之间的字符，忽略其他  
     */    
    public static String bj2qj(String src) {    
        if (src == null) {    
            return src;    
        }    
        StringBuilder buf = new StringBuilder(src.length());    
        char[] ca = src.toCharArray();    
        for (int i = 0; i < ca.length; i++) {    
            if (ca[i] == DBC_SPACE) { // 如果是半角空格，直接用全角空格替代    
                buf.append(SBC_SPACE);    
            } else if ((ca[i] >= DBC_CHAR_START) && (ca[i] <= DBC_CHAR_END)) { // 字符是!到~之间的可见字符    
                buf.append((char) (ca[i] + CONVERT_STEP));    
            } else { // 不对空格以及ascii表中其他可见字符之外的字符做任何处理    
                buf.append(ca[i]);    
            }    
        }    
        return buf.toString();    
    }    
    
    /**  
     * 全角字符->半角字符转换    
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他  
     */    
    public static String qj2bj(String src) {    
        if (src == null) {    
            return src;    
        }    
        StringBuilder buf = new StringBuilder(src.length());    
        char[] ca = src.toCharArray();    
        for (int i = 0; i < src.length(); i++) {    
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内    
                buf.append((char) (ca[i] - CONVERT_STEP));    
            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格    
                buf.append(DBC_SPACE);    
            } else { // 不处理全角空格，全角！到全角～区间外的字符    
                buf.append(ca[i]);    
            }    
        }    
        return buf.toString();    
    }
	
	
	/**
	 * @Description: 标点符号互转化  中转英
	 */
	public static String ZHsymbol2ENsymbol(String string){
		String[] ChineseInterpunction = {"“", "”", "‘", "’", "。", "，", "；", "：", "？", "！", "……", "—", "～", "（", "）", "《", "》"};   
		String[] EnglishInterpunction = {"\"", "\"", "'", "'", ".", ",", ";", ":", "?", "!", "…", "-", "~", "(", ")", "<", ">"};   
		for ( int i = 0; i < ChineseInterpunction.length; i++ )
		{
			string = string.replaceAll (ChineseInterpunction[i], EnglishInterpunction[i]);
		}
		return string;
	}
	
	/**
	 * 英文符号替换成中文符号
	 */
	public static String ENsymbol2ZHsymbol(String string){
		String[] ChineseInterpunction = {"“", "”", "‘", "’", "。", "，", "；", "：", "？", "！", "……", "—", "～", "（", "）", "《", "》"};   
		String[] EnglishInterpunction = {"\"", "\"", "'", "'", "[.]", ",", ";", ":", "[?]", "!", "…", "-", "~", "[(]", "[)]", "<", ">"};   
		for ( int i = 0; i < ChineseInterpunction.length; i++ )
		{
			string = string.replaceAll (EnglishInterpunction[i],ChineseInterpunction[i]);
		}
		return string;
	}
	
	public static void main(String[] args) {
		String str = "（策略关键字１２１３Ｓａｋｕｒａ|关键策略）&(1213|1214)&（策略1|策略3）";
		System.err.println(ZHsymbol2ENsymbol(qj2bj(str)));
		System.out.println(ENsymbol2ZHsymbol(str));
	}
}
