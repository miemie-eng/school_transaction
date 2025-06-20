package com.example.web_transaction.controller;

import com.example.web_transaction.entity.Product;
import com.example.web_transaction.entity.User;
import com.example.web_transaction.mapper.ProductMapper;
import com.example.web_transaction.mapper.UserMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin
public class ProductController {
    private final String UPLOAD_DIR = "E:/picture/";
    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserMapper userMapper;
    @RequestMapping("/getall")
    public List<Product> getAll()
    {
        return productMapper.getAll();
    }
    @RequestMapping("/getall2")
    public List<Product> getAll2(int puid)
    {
        return productMapper.getAll2(puid);
    }

    @RequestMapping("/delete")
    public int delete(int pid){

// 先获取商品信息
        Product product = productMapper.getProductById(pid);

        if (product != null && product.getPimage() != null) {
            // 删除图片文件
            Path imagePath = Paths.get(UPLOAD_DIR + product.getPimage());
            try {
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                e.printStackTrace(); // 日志记录即可，不阻断删除
            }
        }
        return productMapper.delete(pid);
    }
    @RequestMapping("/update")
    public int updatePro(@RequestBody Product product){

        return productMapper.updatePro(product);
    }
    @RequestMapping("/addpro")
    public int addPro(@RequestBody Product product){
//        if (product.getPtime() == null) {
//            product.setPtime(new Date()); // 自动设置当前时间
//        }
        product.setPtime(null);
        if (product.getPstate() == null) {
            product.setPstate("未上架");
        }

        if (product.getPimage() == null) {
            product.setPimage("");
        }

        return productMapper.AddPro(product);
    }
    @PostMapping("/onShelf")
    public Map<String, Object> onShelf(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            int pid = Integer.parseInt(request.get("pid").toString());
            Product product = productMapper.getProductById(pid);
            if (product == null) {
                response.put("success", false);
                response.put("message", "商品不存在");
                return response;
            }
//            if ("在售".equals(product.getPstate())) {
//                response.put("success", false);
//                response.put("message", "商品已经在售");
//                return response;
//            }
            // 设置当前日期作为上架时间
            int updateCount = productMapper.updateProductStateAndTime(pid, "在售", new Date());
            if (updateCount > 0) {
                response.put("success", true);
                response.put("message", "商品上架成功");
            } else {
                response.put("success", false);
                response.put("message", "上架失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "上架过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
    @GetMapping("/getTransactionCountLastFourDays")
    public Map<String, Object> getTransactionCountLastFourDays(
            @RequestParam("uid") int uid) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> data = productMapper.getTransactionCountLastFourDays(uid);
            response.put("success", true);
            response.put("data", data);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取交易统计失败: " + e.getMessage());
        }
        return response;
    }
    @RequestMapping("/search")
    public Map<String, Object> searchProducts(
            @RequestParam(value = "day", required = false) String day,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize) {

        int offset = (page - 1) * pageSize;


        List<Product> products = productMapper.searchProducts(
                day, type, name, offset, pageSize
        );


        int total = productMapper.countSearchResults(day, type, name);

        Map<String, Object> result = new HashMap<>();


        result.put("list", products);
        result.put("total", total);
        return result;
    }

    @PostMapping("/buy")
    public Map<String, Object> buyProduct(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            int pid = Integer.parseInt(request.get("pid").toString());
            int uid = Integer.parseInt(request.get("uid").toString());
            Product product = productMapper.getProductById(pid);
            if (product == null) {
                response.put("success", false);
                response.put("message", "商品不存在");
                return response;
            }
            if ("已售出".equals(product.getPstate())) {
                response.put("success", false);
                response.put("message", "该商品已售出");
                return response;
            }
            if (product.getPuid() == uid) {
                response.put("success", false);
                response.put("message", "不能购买自己的商品");
                return response;
            }
            // 重置上架时间为 null
            int updateCount = productMapper.updateProductOwnerAndStateResetTime(pid, uid, "未上架");
            if (updateCount > 0) {
                Product updatedProduct = productMapper.getProductWithOwner(pid);
                response.put("success", true);
                response.put("message", "购买成功");
                response.put("product", updatedProduct);

                productMapper.insertPurchaseHistory(
                        product.getPname(),
                        product.getPuid(),
                        uid,
                        product.getPvalue(),
                        new Date()
                );
            } else {
                response.put("success", false);
                response.put("message", "商品更新失败，可能已被购买");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "购买过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
    // 添加用户名称缓存
    private Map<Integer, String> userNameCache = new ConcurrentHashMap<>();
    private Map<Integer, String> userPhoneCache = new ConcurrentHashMap<>();

    // 在查询方法中使用缓存
    private void enrichProductWithOwnerName(Product product) {
        if (product.getPuid() != 0) {
            String name = userNameCache.computeIfAbsent(product.getPuid(),
                    uid -> userMapper.findNameById(uid));
            product.setOwnerName(name);

        }

    }
    private void enrichProductWithOwnerPhone(Product product) {
        if (product.getPuid() != 0) {
            String phone = userPhoneCache.computeIfAbsent(product.getPuid(),
                    uid -> userMapper.findPhoneById(uid));
            product.setOwnerPhone(phone);

        }

    }

    // 在返回列表前处理
    private void processProductList(List<Product> products) {
        for (Product p : products) {
            enrichProductWithOwnerName(p);
            enrichProductWithOwnerPhone(p);

        }
    }
    // 添加图片上传接口
    @PostMapping("/upload")
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> result = new HashMap<>();
        try {
            // 使用Paths确保跨平台兼容性
            Path uploadPath = Paths.get(UPLOAD_DIR);
            // 创建目录（如果不存在）
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            // 生成唯一文件名
            String fileName = UUID.randomUUID() + "_" +
                    Objects.requireNonNull(file.getOriginalFilename());
            // 解析完整路径
            Path filePath = uploadPath.resolve(fileName);
            // 将文件内容传输到目标位置（创建副本）
            file.transferTo(filePath);
            // 只返回文件名（前端使用/images/{filename}访问）
            result.put("path", fileName);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "上传失败: " + e.getMessage());
            return result;
        }
    }
    // 新增图片访问接口
    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(UPLOAD_DIR + filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/updateState")
    public Map<String, Object> updateProductState(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            int pid = Integer.parseInt(request.get("pid").toString());
            String state = request.get("state").toString();

            // 更新商品状态
            int updateCount = productMapper.updateProductState(pid, state);

            if (updateCount > 0) {
                response.put("success", true);
                response.put("message", "状态更新成功");
            } else {
                response.put("success", false);
                response.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "状态更新过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
    @GetMapping("/myProducts")
    public Map<String, Object> getMyProducts(
            @RequestParam("uid") int uid,
            @RequestParam(value = "day", required = false) String day,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize) {

        int offset = (page - 1) * pageSize;
        List<Product> products = productMapper.getMyProducts(
                uid,
                day,
                type,
                name,
                state,
                offset,
                pageSize
        );

        int total = productMapper.countMyProducts(
                uid,
                day,
                type,
                name,
                state
        );

        Map<String, Object> result = new HashMap<>();
        result.put("list", products);
        result.put("total", total);
        return result;
    }
    @GetMapping("/getUserTransactionHistory")
    public Map<String, Object> getUserTransactionHistory(
            @RequestParam("uid") int uid,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize) {

        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> history = productMapper.getUserTransactionHistory(uid, offset, pageSize);
        int total = productMapper.countUserTransactionHistory(uid);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true); // 添加成功标识
        result.put("data", history);
        result.put("total", total);
        return result;
    }
}

