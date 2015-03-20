/**
 * 2014年11月2日
 * ken
 */
package com.myxiaoapp.model;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.myxiaoapp.listener.OnResponseListener;

/**
 * @author ken 后台返回的数据
 */
public class HttpResponseHandler extends AsyncHttpResponseHandler {
	private int statusCode; // HTTP返回的状态码，如200成功，404失败
	private Header[] headers;
	private byte[] responseBody;
	private Throwable error;

	/*
	 * 以下三个为responseBody里面的数据
	 */
	private String errno; // 返回json数据里面的状态码，如20成功，40失败
	private String message;
	private Object data; // JSONObject or JSONArray
 

	private String charSet = "UTF-8";

	private OnResponseListener listener;
	private final String CONSTANTLOG = "HttpResponseHandler:";

	public HttpResponseHandler() {

	}

	public HttpResponseHandler(OnResponseListener l) {
		this.listener = l;
	}

	public void setOnResponseListener(OnResponseListener mListener) {
		this.listener = mListener;
	}

	@Override
	public void onFailure(int statusCode, Header[] headers,
			byte[] responseBody, Throwable error) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.responseBody = responseBody;
		this.error = error;
		listener.onFailure();
	}

	/**
	 * 获取返回数据，reseponseBody有可能是json数据或非json数据(如根据url直接获取图片，只返回非json格式的图片内容)
	 * 
	 * @param statusCode
	 * @param headers
	 * @param responseBody
	 */
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		this.statusCode = statusCode;
		this.headers = headers;
		this.responseBody = responseBody;
		String rec = null;
		try {
			rec = new String(responseBody,"UTF-8");
			this.errno = new JSONObject(rec).getString("errno");  
			this.data =  new JSONObject(rec).get("data");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		listener.onReceive();
		
		/*
		 * try {
		 * 
		 * Log.i("mydebug", CONSTANTLOG+new String(responseBody,charSet));
		 * JSONObject jo = new JSONObject(new String(responseBody,charSet));
		 * Log.i("mydebug", CONSTANTLOG+"JSON解析成功"); analyResponse(jo);
		 * 
		 * if(20 == this.errno){ listener.onReceiveSuccess(); }else {
		 * listener.onReceiveFailure(); } } catch (UnsupportedEncodingException
		 * e) { listener.onReceive(); } catch (JSONException e) {
		 * listener.onReceive(); }
		 */
		
	}

	@Override
	public void onFinish() {
	}

	public int getStatusCode() {
		return statusCode;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public byte[] getResponseBody() {
		return responseBody;
	}

	public Throwable getThrowable() {
		return error;
	}
	public String getErrno(){
		
		return this.errno;
	}
	public Object getData(){
		return data;
	}
	public String getValue(JSONObject jo,String key){
		try {
			return jo.getString(key);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		return null;
	}

}
