package github.acodervic.mod.shell.misc;


 
import static github.acodervic.mod.utilFun.hash_strToMd5HexStr;
import static github.acodervic.mod.utilFun.http_getUnsafeOkHttpClient;
import static github.acodervic.mod.utilFun.str;

import cn.hutool.json.JSONObject;
import github.acodervic.mod.Constant;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.net.http.Method;

public class YoudaoApiTran {

	// 王博文
	static String appKey = "5913aa1cbcf81515";
	static String miyao = "zFGTg0DUENZsOVqImVtXl08EcBRUfeUd";
	// 建勇
	// static String appKey = "7fb5a2e6bc496d3a";
	// static String miyao = "PxsdFpoRblwLpD1ymuVQEixol4CqKuBk";
	static String from = "Auto";
	static String to = "zh-CHS";
	static String url = "http://openapi.youdao.com/api";

	public static void main(String[] args) throws Exception {

		System.out.println(toChinses("One false step will make a great difference."));
		;
	}

	public static str toChinses(String str) {
		if (str == null || "".equals(str)) {
			return str("");
		}
		try {
 			String salt = String.valueOf(System.currentTimeMillis());
			String sign = hash_strToMd5HexStr(appKey + str + salt + miyao);
			HMap<String, String> par = new HMap<String, String>();
			par.put("q", new String(str.getBytes(), "UTF-8"));
			par.put("from", from);
			par.put("to", to);
			par.put("appKey", appKey);
			par.put("salt", salt);
			par.put("sign", sign);
				JSONObject da =(JSONObject) http_getUnsafeOkHttpClient(Constant.httpTimeOut).syncReq(url, Method.POST, null, par).doReturn(sucess->{
				return sucess.getBodyJson().get();
			}, failure  ->{
				return null;
			});
 			if (da==null) return null;
			//System.out.println(da.toJSONString());
			if ("0".equals(da.getStr("errorCode"))) {

				//System.out.println(da);
				//System.out.println("beifanyide ++++" + str);
				// String result =
				// da.getJSONArray("web").getJSONObject(0).getJSONArray("value").getString(0);
			 
				String result = da.getStr("translation");
                if (result.startsWith("[")) {
                    result=result.substring(2, result.length()-2);
                }
 				return  str(result);

			}
		} catch (Exception e) {
			e.printStackTrace();

 		}

		return null;
	}

}
