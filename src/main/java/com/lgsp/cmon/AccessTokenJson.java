package com.lgsp.cmon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AccessTokenJson {
	private String consumerKey;
	private String secretKey;
	private String tokenUrl;
	private JsonObject response;

	public AccessTokenJson() {
	}

	public AccessTokenJson(String consumerKey, String scretKey, String url) {
		this.consumerKey = consumerKey;
		this.secretKey = scretKey;
		this.tokenUrl = url;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String url) {
		this.tokenUrl = url;
	}

	public String toString() {
		return response != null ? response.toString() : "";
	}

	public AccessToken generate() {
		AccessToken accessToken = null;
		if (getConsumerKey() != null && getSecretKey() != null) {
			if (getTokenUrl() != null && !getTokenUrl().isEmpty()) {
				String grantType = "client_credentials";
				String keyBase64Encrypted = Base64.getEncoder()
						.encodeToString((getConsumerKey() + ":" + getSecretKey()).getBytes());
				//System.out.println("keyBase64Encrypted: "+keyBase64Encrypted);
				String[] command = { "curl", "-k", "-d", "grant_type=" + grantType, "-H",
						"Authorization: Basic " + keyBase64Encrypted, getTokenUrl() };
				ProcessBuilder process = new ProcessBuilder(command);
				
				Process p;
				try {
					p = process.start();
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					StringBuilder builder = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						builder.append(line);
						builder.append(System.getProperty("line.separator"));
					}
					String result = builder.toString();
					response = new JsonParser().parse(result).getAsJsonObject();
					accessToken = new AccessToken(response);
					p.destroy();
				} catch (IOException e) {
					System.out.print("error");
					e.printStackTrace();
				}
			} else {
				System.out.println("Token url must not be null");
			}
		} else {
			System.out.println("Consumer key and scret key must not be null");
		}
		return accessToken;
	}
}

class AccessToken {
	private String token;
	private long expire;
	private JsonObject json;
	private String scope;
	private String tokenType;
	
	public AccessToken() {}
	
	public AccessToken(JsonObject json) {
		this.json = json;
		if(this.json != null) {
			if(this.json.get("access_token") != null) {
				this.token = this.json.get("access_token").getAsString();
			}
			if(this.json.get("scope") != null) {
				this.scope = this.json.get("scope").getAsString();
			}
			if(this.json.get("expires_in") != null) {
				this.expire = this.json.get("expires_in").getAsLong();
			}
			if(this.json.get("token_type") != null) {
				this.tokenType = this.json.get("token_type").getAsString();
			}
		}
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getExpire() {
		return expire;
	}
	public void setExpire(long expire) {
		this.expire = expire;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
