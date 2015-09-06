package edu.xmu.hwb.implement;

import com.c5.sdk.J5Response;
import com.c5.sdk.ServiceClient;
import edu.xmu.hwb.dbstructure.GpsTrackMsgData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TreeMap;

/**
 * Created by pmsg863 on 14-10-7.
 */
public class ApiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiClient.class);

    String url1 = "app.mobile.ligong";
    String url2 =  "app.token.123";
    String url3 =  "http://test.hemeiyun.org:8084/service/utf-8/busgps/post";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ServiceClient client ;

    public ApiClient() {
        client = new ServiceClient(url1, url2);

    }

    public ApiClient(String url1,String url2,String url3) throws IOException {
            if(url1!=null) this.url1=url1;
        if(url2!=null) this.url2=url2;
        if(url3!=null) this.url3=url3;
            client = new ServiceClient(url1, url2);

    }

    public void store(Object msg) {

        GpsTrackMsgData track = GpsTrackMsgData.vauleOf(msg);

        TreeMap<String,String> paramsMap = new TreeMap<String, String>();
        paramsMap.put("vehicle_sim", track.getVEHICLE_SIM());
        paramsMap.put("carnumber",  track.getVEHICLE_SIM());
        paramsMap.put("altitude", String.valueOf(track.getALTITUDE()));
        paramsMap.put("gps_time_str", format.format(track.getGPS_TIME()));
        paramsMap.put("longitude", String.valueOf(track.getLONGITUDE()));
        paramsMap.put("latitude",  String.valueOf(track.getLATITUDE()));

        J5Response response = null;
        try {
            response = client.post(url3, paramsMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if( response!=null && response.isSuccess() ){
            //System.out.println("成功");
        }else{
            if(response!=null) LOGGER.error(response.getMessage());
        }
    }

    public static void main(String[] args) {
        GpsTrackMsgData data = new GpsTrackMsgData();
        data.setTest();


        ApiClient apiClient = new ApiClient();
        apiClient.store(data);


    }

}
