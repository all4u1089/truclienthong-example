package com.lgsp.cmon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.wso2.client.api.ApiClient;
import org.wso2.client.api.ApiException;
import org.wso2.client.api.Configuration;
import org.wso2.client.api.TrucLienThong.DefaultApi;
import org.wso2.client.model.TrucLienThong.ChiTietVanBanDen;
import org.wso2.client.model.TrucLienThong.DanhSachDonViLienThong;
import org.wso2.client.model.TrucLienThong.DanhSachVanBanDen;
import org.wso2.client.model.TrucLienThong.DonViLienThong;
import org.wso2.client.model.TrucLienThong.DonViLienThongThemMoi;
import org.wso2.client.model.TrucLienThong.KetQua;
import org.wso2.client.model.TrucLienThong.KetQuaGuiVanBan;
import org.wso2.client.model.TrucLienThong.VanBan;


public class Main {
	@SuppressWarnings("resource")
	public static void main(String[] args)  {
		String adapterAddress = "https://projects.greenglobal.vn:8247";
		String key = "pLVtjDmfnL1vy45fobBPfqQTfCEa"; // (1) Consumer key
		String secret = "Z944I37nNA3OiCDS0jAbM3DiYCsa"; // (2) Consumer secret
	    String url = adapterAddress + "/token"; // (3) URL get token
	    String accessToken = new AccessTokenJson(key, secret, url).generate().getToken();
	    System.out.println("accessToken: " + accessToken);	    

		ApiClient defaultClient = Configuration.getDefaultApiClient();
	    defaultClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        defaultClient.setBasePath(adapterAddress + "/truclienthong/v1.0.0"); // (4) URL Base Path
        defaultClient.setVerifyingSsl(false);
        
        int run = 8;
        
        //1: Lấy danh sách văn bản đến
        //2: Lấy chi tiết văn bản
        //3: Gửi văn bản liên thông
        //4: Cập nhật trạng thái văn bản
        //5: Lấy danh sách đơn vị liên thông (cả nước)
        //6: Lấy danh sách đơn vị liên thông (Quảng Nam)
        //7: Đăng ký đơn vị liên thông
        //8: Hủy đăng ký đơn vị liên thông
        
        DefaultApi apiInstance = new DefaultApi();
        apiInstance.setApiClient(defaultClient);
        
        String secretDonVi = "ffc9f7158d72f32e0a0399ec3c2b359875870715c76993bb3d2ae4f1bdd35bb4"; //Mã định danh đơn vị
        
        switch (run) {
		case 1:
			// Lấy danh sách văn bản đến
	        try {
	        	DanhSachVanBanDen danhSach = apiInstance.vanbansGet(secretDonVi);
	        	if (danhSach != null) {
	        		System.out.println("status: " + danhSach.getStatus());
	        		System.out.println("errorCode: " + danhSach.getErrorCode());
	        		System.out.println("errorDesc: " + danhSach.getErrorDesc());
	        		for (VanBan vanBan : danhSach.getData()) {
	        			System.out.println("----------------------------------");
	        			System.out.println("serviceType: " + vanBan.getServiceType());
	        			System.out.println("messageType: " + vanBan.getMessageType());
	        			System.out.println("statusDesc: " + vanBan.getStatusDesc());
	        			System.out.println("docId: " + vanBan.getDocId());
	        			System.out.println("from: " + vanBan.getFrom());
	        			System.out.println("to: " + vanBan.getTo());
	        			System.out.println("status: " + vanBan.getStatus());
	        			System.out.println("createdTime: " + vanBan.getCreatedTime());
	        		}
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 2:
			// Lấy chi tiết văn bản
	        try {
	        	String docId = "5b8209a7-f5f8-4ada-b766-27c30a2eaf8b";
	        	ChiTietVanBanDen chiTiet = apiInstance.vanbansDocIdGet(docId, secretDonVi);
	        	if (chiTiet != null) {
	        		System.out.println("status: " + chiTiet.getStatus());
	        		System.out.println("errorCode: " + chiTiet.getErrorCode());
	        		System.out.println("errorDesc: " + chiTiet.getErrorDesc());
	        		System.out.println("data: " + chiTiet.getData());
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 3:
			// Gửi văn bản liên thông
	        try {
	        	File file = new File("./resources/edoc_new.edxml");
	        	FileInputStream fileInputStreamReader = null;
				try {
					fileInputStreamReader = new FileInputStream(file);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	            byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
	            String base64Encode = new String(encoded, StandardCharsets.UTF_8);
	            
	        	String loaiVanBan = "edoc";
	        	KetQuaGuiVanBan ketQua = apiInstance.vanbansPost(base64Encode, secretDonVi, loaiVanBan);
	        	if (ketQua != null) {
	        		System.out.println("status: " + ketQua.getStatus());
	        		System.out.println("errorCode: " + ketQua.getErrorCode());
	        		System.out.println("errorDesc: " + ketQua.getErrorDesc());
	        		System.out.println("docId: " + ketQua.getDocId());
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 4:
			// Cập nhật trạng thái văn bản
	        try {
	        	String status = "DONE"; 
	        	//Trạng thái văn bản, gồm 4 giá trị: INITIAL (Khởi tạo), PROCESSING (Đang xử lý), DONE (Hoàn thành), FAIL (Thất bại).
	        	String docId = "5b8209a7-f5f8-4ada-b766-27c30a2eaf8b";
	        	KetQua chiTiet = apiInstance.vanbansDocIdPut(docId, status, secretDonVi);
	        	if (chiTiet != null) {
	        		System.out.println("status: " + chiTiet.getStatus());
	        		System.out.println("errorCode: " + chiTiet.getErrorCode());
	        		System.out.println("errorDesc: " + chiTiet.getErrorDesc());
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 5:
			// Lấy danh sách đơn vị liên thông (cả nước)
	        try {
	        	DanhSachDonViLienThong danhSach = apiInstance.donvilienthongsGet(secretDonVi);
	        	if (danhSach != null) {
	        		System.out.println("status: " + danhSach.getStatus());
	        		System.out.println("errorCode: " + danhSach.getErrorCode());
	        		System.out.println("errorDesc: " + danhSach.getErrorDesc());
	        		for (DonViLienThong donVi : danhSach.getData()) {
	        			System.out.println("----------------------------------");
	        			System.out.println("id: " + donVi.getId());
	        			System.out.println("code: " + donVi.getCode());
	        			System.out.println("name: " + donVi.getName());
	        			System.out.println("pid: " + donVi.getPid());
	        		}
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 6:
			// Lấy danh sách đơn vị liên thông (Quảng Nam)
	        try {
	        	DanhSachDonViLienThong danhSach = apiInstance.donvilienthongsQnGet(secretDonVi);
	        	if (danhSach != null) {
	        		System.out.println("status: " + danhSach.getStatus());
	        		System.out.println("errorCode: " + danhSach.getErrorCode());
	        		System.out.println("errorDesc: " + danhSach.getErrorDesc());
	        		for (DonViLienThong donVi : danhSach.getData()) {
	        			System.out.println("----------------------------------");
	        			System.out.println("id: " + donVi.getId());
	        			System.out.println("code: " + donVi.getCode());
	        			System.out.println("name: " + donVi.getName());
	        			System.out.println("pid: " + donVi.getPid());
	        		}
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 7:
			// Đăng ký đơn vị liên thông
	        try {
	        	DonViLienThongThemMoi data = new DonViLienThongThemMoi();
	        	data.setCode("001.00.10.H47");
	        	data.setEmail("");
	        	data.setFax("");
	        	data.setMobile("");
	        	data.setName("UBND Huyện Duy Xuyen");
	        	data.setPcode("001.00.00.H47");
				KetQua chiTiet = apiInstance.donvilienthongsPost(secretDonVi, data);
	        	if (chiTiet != null) {
	        		System.out.println("status: " + chiTiet.getStatus());
	        		System.out.println("errorCode: " + chiTiet.getErrorCode());
	        		System.out.println("errorDesc: " + chiTiet.getErrorDesc());
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		case 8:
			// Hủy đăng ký đơn vị liên thông
	        try {
	        	String agencyCode = "001.00.10.H47";
				KetQua chiTiet = apiInstance.donvilienthongsPut(secretDonVi, agencyCode);
	        	if (chiTiet != null) {
	        		System.out.println("status: " + chiTiet.getStatus());
	        		System.out.println("errorCode: " + chiTiet.getErrorCode());
	        		System.out.println("errorDesc: " + chiTiet.getErrorDesc());
	        	}
	        	
	        } catch (ApiException e) {
	            e.printStackTrace();
	        }
			break;
		default:
			break;
		}
	}
}
