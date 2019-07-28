package sample.request;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpRequestHandler {
	
	@Value("${trust.store}")
	private Resource trustStore;
	
	@Value("${trust.store.password}")
	private String trustStorePassword;
	
	private CloseableHttpClient httpClient;
	
	public void init() throws Exception {
		log.info("<HttpRequestHandler> Prepare to init HttpRequestHandler");
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(trustStore.getInputStream(), trustStorePassword.toCharArray());
		SSLContext sslContext = new SSLContextBuilder()
		        .loadKeyMaterial(keyStore, trustStorePassword.toCharArray())
		        .loadTrustMaterial(keyStore, new TrustSelfSignedStrategy())
		        .build();
		
		SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
		
		log.info("<HttpRequestHandler> Prepare to init done.");
	}
	
	/**
	 * @param url
	 *            Request url
	 */
	public String sendGet(String url) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
		String responsedBody = response.getBody();
		log.debug("sendGet done, requestUrl:<" + url + ">, responsed:<" + responsedBody + ">");
		return responsedBody;
	}
	
	/**
	 * @param url
	 *            Request url
	 * @param clazz
	 *            Responsed Objec class
	 * @return Responsed
	 */
	public <T> T sendGet(String url, Class<T> responsedClazz) {
		RestTemplate restTemplate = new RestTemplate();
		T responsed = restTemplate.getForObject(url, responsedClazz);
		log.debug("sendGet done, requestUrl:<" + url + ">, responsed:<" + responsed + ">");
		return responsed;
	}
	
	/**
	 * @param url
	 *            Request url
	 * @param request
	 *            Request Object
	 * @param clazz
	 *            Responsed Objec class
	 * @return Responsed
	 */
	public <V, T> T sendPost(String url, V requestObject, Class<T> responsedClazz) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<V> httpEntity = new HttpEntity<>(requestObject);
		T responsed = restTemplate.postForObject(url, httpEntity, responsedClazz);
		log.debug("sendPost done, requestUrl:<" + url + ">, request:<" + requestObject + ">, responsed:<" + responsed + ">");
		return responsed;
	}
	
	/**
	 * @param url
	 *            Request url
	 * @param request
	 *            Request Object
	 * @param clazz
	 *            Responsed Objec class
	 * @return Responsed
	 */
	public <V, T> T sendHttpPost(String url, HttpEntity<String> httpEntity, Class<T> responsedClazz) {
		RestTemplate restTemplate = new RestTemplate();
		T responsed = restTemplate.postForObject(url, httpEntity, responsedClazz);
		log.debug("sendPost done, requestUrl:<" + url + ">, request:<" + httpEntity + ">, responsed:<" + responsed + ">");
		return responsed;
	}
	
	public <V, T> String sendmeterPost(String url, V requestObject, Class<T> responsedClazz) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<V> httpEntity = new HttpEntity<>(requestObject);
		String responsed = restTemplate.postForObject(url, httpEntity, String.class);
		log.debug("sendPost done, requestUrl:<" + url + ">, request:<" + requestObject + ">, responsed:<" + responsed + ">");
		return responsed;
	}
	
	public <V, T> String sendmeterMd5Post(String url, String requestContent, Class<T> responsedClazz) throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashInBytes = md.digest(requestContent.getBytes(StandardCharsets.UTF_8));
		
		StringBuilder sb = new StringBuilder();
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));
		}
		
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> httpEntity = new HttpEntity<>(sb.toString());
		String responsed = restTemplate.postForObject(url, httpEntity, String.class);
		log.debug("sendPost done, requestUrl:<" + url + ">, request:<" + requestContent + ">, responsed:<" + responsed + ">");
		return responsed;
	}
	
	public <V, T> String sendmeterHttpsPost(String url, V requestObject, Class<T> responsedClazz) {
		HttpEntity<V> httpEntity = new HttpEntity<>(requestObject);
		RestTemplate restTemplate;
		String responsed = "";
		try {
			restTemplate = restHttpsTemplate();
			responsed = restTemplate.postForObject(url, httpEntity, String.class);
			log.debug("sendmeterHttpsPost done, requestUrl:<" + url + ">, request:<" + requestObject + ">, responsed:<" + responsed + ">");
		}
		catch (Exception e) {
			log.error("Exception raised while sendmeterHttpsPost,  requestUrl:<" + url + ">, request:<" + requestObject + ">, responsed:<" + responsed + ">", e);
		}
		return responsed;
	}
	
	private RestTemplate restHttpsTemplate() throws Exception {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		return new RestTemplate(factory);
	}
	
}
