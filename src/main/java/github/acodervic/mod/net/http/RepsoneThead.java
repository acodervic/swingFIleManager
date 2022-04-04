package github.acodervic.mod.net.http;

import java.util.function.Consumer;

public class RepsoneThead implements Runnable {
    HttpRepsoneRepuest httpRepReq;
    Consumer<HttpRepsoneRepuest> success_opt;
    Consumer<HttpRepsoneRepuest> filed_opt;

    /**
     * @return the httpRepReq
     */
    public HttpRepsoneRepuest getHttpRepReq() {
      return httpRepReq;
    }

    /**
     * @return the filed_opt
     */
    public Consumer<HttpRepsoneRepuest> getFiled_opt() {
      return filed_opt;
    }

    /**
     * @return the success_opt
     */
    public Consumer<HttpRepsoneRepuest> getSuccess_opt() {
      return success_opt;
    }

    /**
     * @param filed_opt the filed_opt to set
     */
    public void setFiled_opt(Consumer<HttpRepsoneRepuest> filed_opt) {
      this.filed_opt = filed_opt;
    }

    /**
     * @param httpRepReq the httpRepReq to set
     */
    public void setHttpRepReq(HttpRepsoneRepuest httpRepReq) {
      this.httpRepReq = httpRepReq;
    }

    /**
     * @param success_opt the success_opt to set
     */
    public void setSuccess_opt(Consumer<HttpRepsoneRepuest> success_opt) {
      this.success_opt = success_opt;
    }
    @Override
    public void run() {
      if (this.httpRepReq!=null) {
          if (this.success_opt!=null) {
            this.success_opt.accept(this.httpRepReq);
          }
      }else{
        if (this.filed_opt!=null) {
            this.filed_opt.accept(this.httpRepReq);
          }
      }

    }

    
    
}