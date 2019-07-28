package sample.checksum;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class SHA256 {

	public String generateSHA256(String timeStampString) {
		MessageDigest sha = null;
		
		try {
			sha = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("");
		}
		sha.reset();
		sha.update(timeStampString.getBytes());
		String hashCode =byte2Hex(sha.digest());
		return hashCode;
	}
	
	
	private static String byte2Hex(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				// 1得到一位的進行補0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}
}




//public static void main(String[] args) {
//	String timeStampString = "1563786612";
//	MessageDigest sha = null;
//	try {
//		sha = MessageDigest.getInstance("SHA-256");
//	} catch (NoSuchAlgorithmException e) {
//
//	}
//	sha.reset();
//	sha.update(timeStampString.getBytes());
//	String encodeStr =byte2Hex(sha.digest());
//	System.out.println(encodeStr);
//	}