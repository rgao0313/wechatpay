package com.esg.user;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class UnifiedOrder {




    
    public static void main(String[] args) {
    	
        
    }
    
    
    /**
     * 商户发起生成预付单请求
     * @return
     */
    public static String unifiedOrder(){
//        Map map = WebUtils.getParameterMap();//获取前台数据
        String appid = Constants.APP_ID;//appid
        System.out.print(appid);//77TEST
        String mch_id = Constants.MCH_ID;//微信支付商户号
        String nonce_str =getRandomStringByLength(8);//随机码
//        String body = map.get("bodyName").toString();//商品描述
        String body = "77 is so beautiful";//77test
//        String out_trade_no = map.get("outTradeNo").toString();//商品订单号
        String out_trade_no = "20180808125346";//77test
//        String product_id = map.get("productId").toString();//商品编号
//        String product_id = "77777777";//77test
//        String total_fee = (int)(Float.valueOf(map.get("totalFee").toString()).floatValue()*100)+"";//总金额 分
        String total_fee = "100";//77test 总金额 分
//        String time_start =getCurrTime();//交易起始时间(订单生成时间非必须)
        String trade_type = "APP";//公众号支付
//        String notify_url = "http://"+"域名"+"/"+"项目名"+"回调地址.do";//回调函数
        String notify_url = Constants.url;
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
//        params.put("device_info", "WEB"); //设备号
        params.put("nonce_str", nonce_str);
        params.put("body", body);//商品描述
        params.put("out_trade_no", out_trade_no);
//        params.put("product_id", product_id);
        params.put("total_fee", total_fee);
//        params.put("time_start", time_start);
        params.put("trade_type", trade_type);
        params.put("notify_url", notify_url); 
        String sign = Constants.APPSECRET;//签名(该签名本应使用微信商户平台的API证书中的密匙key,但此处使用的是微信公众号的密匙APP_SECRET)
        sign = getSign(params);
        System.out.print(sign);//77TEST
        //参数xml化
        String xmlParams = parseString2Xml(params,sign);
        
        //判断返回码
        byte[] jsonStr = null;
        try {
        	jsonStr = Util.httpPost(Constants.prepayOrderUrl, xmlParams);// 调用支付接口
        	System.out.print(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (jsonStr.indexOf("FAIL") == -1 && jsonStr.trim().length() > 0) {//成功
//            model.put("result_code", "SUCCESS");
//        } else {//失败
//            try {
//                Map resultMap = new HashMap();
//                resultMap = getMapFromXML(jsonStr);// 解析返回值
//                if(resultMap!=null&&"SUCCESS".equals(resultMap.get("return_code").toString())){
//                    //通信接口二级错误
//                    model.put("result_code", resultMap.get("result_code"));
//                    model.put("error_code", resultMap.get("err_code"));
//                    model.put("result_msg", resultMap.get("err_code_des"));
//                }else{
//                    //通信接口一级错误
//                    model.put("result_code", "FAIL");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return "success";
    }
    
    /**
     * 参数进行XML化
     * @param map,sign
     * @return
     */
    public static String parseString2Xml(Map<String, String> map,String sign){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = map.entrySet();
        Iterator iterator = es.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            sb.append("<"+k+">"+v+"</"+k+">");
        }
        sb.append("<sign>"+sign+"</sign>");
        sb.append("</xml>");
        return sb.toString();
    }
    /**
     * 获取签名 md5加密(微信支付必须用MD5加密)
     * 获取支付签名
     * @param params
     * @return
     */
    public static String getSign(SortedMap<String, String> params){
        String sign = null;
        StringBuffer sb = new StringBuffer();
        Set es = params.entrySet();
        Iterator iterator = es.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)&& !"key".equals(k)) {
                sb.append(k+"="+v+"&");
            }
        }
        sb.append("key="+Constants.APPSECRET);
        sign = MD5.getMessageDigest(String.valueOf(sb).getBytes()).toUpperCase();
//        sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
        return sign;
    }
    public static Map<String,Object> getMapFromXML(byte[] xmlString) throws ParserConfigurationException, IOException, SAXException {
        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is =  new ByteArrayInputStream(xmlString);
        Document document = builder.parse(is);
        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Node node;
        Map<String, Object> map = new HashMap<String, Object>();
        int i=0;
        while (i < allNodes.getLength()) {
            node = allNodes.item(i);
            if(node instanceof Element){
                map.put(node.getNodeName(),node.getTextContent());
            }
            i++;
        }
        return map;
    }
    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    /**
     * 获取当前时间 yyyyMMddHHmmss
     * @return String
     */ 
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

	

}
