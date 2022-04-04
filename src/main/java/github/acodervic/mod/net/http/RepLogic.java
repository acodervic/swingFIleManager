package github.acodervic.mod.net.http;

import java.util.function.Consumer;
import java.util.function.Function;

import github.acodervic.mod.data.str;
import okhttp3.Request;
import okhttp3.Response;

/**
 * http响应逻辑
 * 
 * 
 */
public class RepLogic  {

    HttpRepsoneRepuest repsoneAndRequest;


    
    /**
     * 转换当前响应为st对象,如果返回为""(isEmpty() )则可能代表,响应为null,或者在body().string()转换中出现了错误
     * @return
     */
    public str bodyString() {
        return repsoneAndRequest.getBodyStr();
    }


/**
 * 转换当前响应为st对象,如果返回为""(isEmpty() )则可能代表,响应为null,或者在body().string()转换中出现了错误
 * @param charset_opt 以固定编码读取响应字符串
 * @return
 */
    public str bodyString(String charset_opt) {
        return repsoneAndRequest.getBodyStr(charset_opt);
    }



    /**
     * 读取原始http响应对象,注意绝对不能Body.string()读取数据,因为第一次构建响应的时候就已经自动执行了流被消耗完毕了.
     * 只能使用bodyString().toString()来读取响应内容
     * @return
     */
    public Response getRawOkHttpRepsone() {
        return getHttpRepsoneRequest().rawRepsone();
    }
    /**
     * 注意此函数只对,同步请求操作 如果发送成功,处理结果并返回,如果发送失败则返回Null
    * .onFiled(repsone->{ }); 
     * @param filed_opt
     * @return
     */
    public void onFiled( Consumer<HttpRepsoneRepuest> filed_opt) {
        if (this.getHttpRepsoneRequest().rawRepsone() == null&&filed_opt!=null) {
            filed_opt.accept(this.getHttpRepsoneRequest());
        }
    }

    /**
     * 注意此函数只对,同步请求操作 如果发送成功,处理结果并返回,如果发送失败则返回Null
     * .onFiledReturn(repsone->{ return "success"; });
     * @param filed_opt
     * @return
     */
    public Object onFiledReturn( Function<HttpRepsoneRepuest, Object> filed_opt) {
        if (this.getHttpRepsoneRequest().rawRepsone() == null&&filed_opt!=null) {
            return filed_opt.apply(this.getHttpRepsoneRequest());
        }
        return "执行onFiledReturn失败";
    }

    /**
     * .onSuccess(repsone->{ }); 注意此函数只对,同步请求操作 如果发送成功,处理结果并返回,如果发送失败则返回Null
     * 
     * @param success_opt
     * @return
     */
    public void onSuccess( Consumer<HttpRepsoneRepuest> success_opt) {
        if (this.getHttpRepsoneRequest().rawRepsone() != null&&success_opt!=null) {
            success_opt.accept(this.getHttpRepsoneRequest());
        }
    }

    /**
     * .onSuccessReturn(repsone->{ return "success"; });
     * 
     * 注意此函数只对,同步请求操作 如果发送失败,则处理结果并返回,如果发送失败则返回Null
     * 
     * @param success_opt
     * @return
     */
    public Object onSuccessReturn( Function<HttpRepsoneRepuest, Object> success_opt) {
        if (this.getHttpRepsoneRequest().rawRepsone() != null&&success_opt!=null) {
            return success_opt.apply(this.getHttpRepsoneRequest());
        }
        return "执行onSuccessReturn失败";
    }

    /**
     * .doReturn(repsoneSuccess->{ return "success"; }, repsoneError->{ return
     * "filed"; }); 注意此函数只对,同步请求操作 处理结果并返回
     * 
     * @param success_opt
     * @param filed_opt
     * @return
     */
    public Object doReturn( Function<HttpRepsoneRepuest, Object> success_opt,
             Function<HttpRepsoneRepuest, Object> filed_opt) {
        if (this.getHttpRepsoneRequest() == null) {
            if (filed_opt!=null){
                return  filed_opt.apply(this.getHttpRepsoneRequest());
            }else{
                return null;
            }

        } else {
            if (success_opt!=null) {
                return success_opt.apply(this.getHttpRepsoneRequest());
            }else{
                return null;
            }
        }
    }

    /**
     * .doReturn(repsoneSuccess->{ }, repsoneError->{ }); 注意此函数只对,同步请求操作 处理结果并返回
     * 
     * @param success_opt
     * @param filed_opt
     * @return
     */
    public void doVoid( Consumer<HttpRepsoneRepuest> success_opt,  Consumer<HttpRepsoneRepuest> filed_opt) {
        if (this.getHttpRepsoneRequest().rawRepsone() == null) {
            if (filed_opt!=null){
                filed_opt.accept(this.getHttpRepsoneRequest());
            }
        } else {
            if (success_opt!=null){
                success_opt.accept(this.getHttpRepsoneRequest());
            }
        }
    }

    /**
     * 通过判断响应体是否为null来判断是否超时
     * 
     * @return
     */
    public boolean isTimeOut() {
        return this.repsoneAndRequest.getOkhttpsetOkhttpResponse() == null;
    }

    /**
     * .doReturn(repsoneSuccess->{ }, repsoneError->{ }); 注意此函数只对,同步请求操作 处理结果并返回
     * 
     * @param success_opt
     * @param filed_opt
     * @return
     */
    public void doVoidInNewThread( Consumer<HttpRepsoneRepuest> success_opt,  Consumer<HttpRepsoneRepuest> filed_opt) {
        RepsoneThead repsoneThead = new RepsoneThead();
        repsoneThead.setHttpRepReq(getHttpRepsoneRequest());
        repsoneThead.setSuccess_opt(success_opt);
        repsoneThead.setFiled_opt(filed_opt);
        new Thread(repsoneThead).start();
   }

 

    /**
     * @param getHttpRepsone()
     */
    public RepLogic(Response okhttpRep) {
        this.repsoneAndRequest=new  HttpRepsoneRepuest(okhttpRep);
    }


    /**
     * @param getHttpRepsone()
     */
    public RepLogic(Request okhttpReq) {
        this.repsoneAndRequest=new  HttpRepsoneRepuest(okhttpReq);
    }


    /**
     * @param getHttpRepsone()
     */
    public RepLogic(Response okhttpRep,Request okhttpReq) {
        this.repsoneAndRequest=new  HttpRepsoneRepuest(okhttpReq,okhttpRep);
    }



    

    public HttpRepsoneRepuest getHttpRepsoneRequest() {
        return this.repsoneAndRequest;
    }

    /**
     * 关闭连接
     */
    public void closeConnection() {
         this.repsoneAndRequest.closeConnection();
    }
    /**
     *
     */
    public RepLogic() {
    }
}