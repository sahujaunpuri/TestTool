package amtt.epam.com.amtt.processing;


import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import amtt.epam.com.amtt.bo.issue.createmeta.JMetaResponse;


/**
 * Created by Irina Monchenko on 01.04.2015.
 */
public class ProjectsProcessor implements Processor<JMetaResponse, HttpEntity> {

    @Override
    public JMetaResponse process(HttpEntity inputStream){
        String _response = null;
        try {
            _response = EntityUtils.toString(inputStream, HTTP.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Gson.getInstance().fromJson(_response, JMetaResponse.class);
    }
}

