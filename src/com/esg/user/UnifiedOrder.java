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
     * �̻���������Ԥ��������
     * @return
     */
    public static String unifiedOrder(){
//        Map map = WebUtils.getParameterMap();//��ȡǰ̨����
        String appid = Constants.APP_ID;//appid
        System.out.print(appid);//77TEST
        String mch_id = Constants.MCH_ID;//΢��֧���̻���
        String nonce_str =getRandomStringByLength(8);//�����
//        String body = map.get("bodyName").toString();//��Ʒ����
        String body = "77 is so beautiful";//77test
//        String out_trade_no = map.get("outTradeNo").toString();//��Ʒ������
        String out_trade_no = "20180808125346";//77test
//        String product_id = map.get("productId").toString();//��Ʒ���
//        String product_id = "77777777";//77test
//        String total_fee = (int)(Float.valueOf(map.get("totalFee").toString()).floatValue()*100)+"";//�ܽ�� ��
        String total_fee = "100";//77test �ܽ�� ��
//        String time_start =getCurrTime();//������ʼʱ��(��������ʱ��Ǳ���)
        String trade_type = "APP";//���ں�֧��
//        String notify_url = "http://"+"����"+"/"+"��Ŀ��"+"�ص���ַ.do";//�ص�����
        String notify_url = Constants.url;
        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
//        params.put("device_info", "WEB"); //�豸��
        params.put("nonce_str", nonce_str);
        params.put("body", body);//��Ʒ����
        params.put("out_trade_no", out_trade_no);
//        params.put("product_id", product_id);
        params.put("total_fee", total_fee);
//        params.put("time_start", time_start);
        params.put("trade_type", trade_type);
        params.put("notify_url", notify_url); 
        String sign = Constants.APPSECRET;//ǩ��(��ǩ����Ӧʹ��΢���̻�ƽ̨��API֤���е��ܳ�key,���˴�ʹ�õ���΢�Ź��ںŵ��ܳ�APP_SECRET)
        sign = getSign(params);
        System.out.print(sign);//77TEST
        //����xml��
        String xmlParams = parseString2Xml(params,sign);
        
        //�жϷ�����
        byte[] jsonStr = null;
        try {
        	jsonStr = Util.httpPost(Constants.prepayOrderUrl, xmlParams);// ����֧���ӿ�
        	System.out.print(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (jsonStr.indexOf("FAIL") == -1 && jsonStr.trim().length() > 0) {//�ɹ�
//            model.put("result_code", "SUCCESS");
//        } else {//ʧ��
//            try {
//                Map resultMap = new HashMap();
//                resultMap = getMapFromXML(jsonStr);// ��������ֵ
//                if(resultMap!=null&&"SUCCESS".equals(resultMap.get("return_code").toString())){
//                    //ͨ�Žӿڶ�������
//                    model.put("result_code", resultMap.get("result_code"));
//                    model.put("error_code", resultMap.get("err_code"));
//                    model.put("result_msg", resultMap.get("err_code_des"));
//                }else{
//                    //ͨ�Žӿ�һ������
//                    model.put("result_code", "FAIL");
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return "success";
    }
    
    /**
     * ��������XML��
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
     * ��ȡǩ�� md5����(΢��֧��������MD5����)
     * ��ȡ֧��ǩ��
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
        //������Dom�ķ�ʽ�����ذ�������ҪĿ���Ƿ�ֹAPI�����ذ��ֶ�
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is =  new ByteArrayInputStream(xmlString);
        Document document = builder.parse(is);
        //��ȡ��document�����ȫ�����
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
     * ��ȡһ�����ȵ�����ַ���
     * @param length ָ���ַ�������
     * @return һ�����ȵ��ַ���
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
     * ��ȡ��ǰʱ�� yyyyMMddHHmmss
     * @return String
     */ 
    public static String getCurrTime() {
        Date now = new Date();
        SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String s = outFormat.format(now);
        return s;
    }

	

}
