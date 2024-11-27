package com.lyf.seexp.utils;

import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImgbbUtils {

    private static final String IMGBB_API_KEY = "e68329ac113e0f593ee7a3bf151d31f7"; // 替换为你的 API Key

    /**
     * 上传图片到 Imgbb 并返回托管 URL
     *
     * @param multipartFile Spring 的 MultipartFile 对象
     * @return 图片托管的 URL
     */
    public static String uploadToImgbb(MultipartFile multipartFile) {
        try {
            // 校验文件是否为空
            if (multipartFile.isEmpty()) {
                System.err.println("上传失败：文件为空");
                return null;
            }

            // 获取文件字节数组并编码为 Base64
            byte[] fileBytes = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);

            // 对 Base64 数据进行 URL 编码
            String encodedBase64Image = URLEncoder.encode(base64Image, StandardCharsets.UTF_8.toString());

            // 构建 API 请求 URL
            String url = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;

            // 构建 POST 请求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("image=" + encodedBase64Image))
                    .build();

            // 发送请求
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 解析 API 响应
            JSONObject jsonResponse = new JSONObject(response.body());
            if (jsonResponse.getBoolean("success")) {
                String imageUrl = jsonResponse.getJSONObject("data").getString("url");
                System.out.println("文件名: " + fileName + " 上传成功，URL: " + imageUrl);
                return imageUrl + "@filename=" + fileName;
            } else {
                System.err.println("上传失败: " + jsonResponse.toString());
                return null;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("请求或响应失败: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("发生未知异常: " + e.getMessage());
            return null;
        }
    }

}
