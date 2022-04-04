package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.nio.charset.Charset;
import java.util.logging.Logger;
import cn.hutool.core.net.URLEncoder;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.code.Encode;
import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.data.map.HMap;
import github.acodervic.mod.net.http.HttpClient;
import github.acodervic.mod.net.http.Method;
import github.acodervic.mod.net.http.RepLogic;

/**
 * 图片识别接口
 */
public class OCRUtil {
    static final Logger log = Logger.getLogger(OCRUtil.class.getName());

    public static str baiduOCRFIle(FileRes imageFile, HttpClient httpClient_opt, String token) {
        nullCheck(imageFile, token);
        byte[] imgData = imageFile.readBytes();
        return baiduOCRBytes(imgData, httpClient_opt, token);
    }

    public static str baiduOCRBytes(byte[] imageData, HttpClient httpClient_opt, String token) {
        nullCheck(imageData);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        try {
            String imgStr = Encode.bytesToBase64Str(imageData);
            utilFun.print(ListUtil.toStr(new str(imgStr).getStrList(100), "\n"));
            String imgParam = URLEncoder.createDefault().encode(imgStr, Charset.defaultCharset());
            utilFun.print(ListUtil.toStr(new str(imgParam).getStrList(100), "\n"));
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = token;
            HMap<String, String> post = new HMap<String, String>();
            post.add_("image", imgParam);
            HMap<String, String> header = new HMap<String, String>();
            header.add_("Content-Type", "application/x-www-form-urlencoded");
            RepLogic rep = (httpClient_opt == null ? utilFun.http_getUnsafeOkHttpClient(10000).log() : httpClient_opt)
                    .syncReq(url + "?access_token=" + accessToken, Method.POST, header, post);

            return new str(rep.getHttpRepsoneRequest().getBodyJson().get().getJSONArray("words_result").getJSONObject(0)
                    .getStr("words"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new str("");
    }

    /**
     * 读取百度apiToken
     * 
     * @param apiKey
     * @param SecretKey
     * @return
     */
    public static str getBaiduAPiToken(String apiKey, String SecretKey) {
        String tokenUrl = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + apiKey
                + "&client_secret=" + SecretKey;
        return new str(utilFun.http_getUnsafeOkHttpClient(10000).syncReq(tokenUrl, Method.GET).getHttpRepsoneRequest()
                .getBodyJson().get().getStr("access_token")).trimAllWrap();
    }

    public static void main(String[] args) {
 
 
    }

}
