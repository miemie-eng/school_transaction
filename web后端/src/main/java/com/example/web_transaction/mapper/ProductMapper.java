package com.example.web_transaction.mapper;

import com.example.web_transaction.entity.Product;
import org.apache.ibatis.annotations.*;


import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    @Select("SELECT p.*, u.uname as ownerName FROM product p LEFT JOIN user u ON p.puid = u.uid WHERE p.pstate = '在售'")
    List<Product> getAll();

    @Select("SELECT p.*, u.uname as ownerName FROM product p LEFT JOIN user u ON p.puid = u.uid WHERE p.puid=#{puid}")
    List<Product> getAll2(@Param("puid") int puid);
    @Select("SELECT * FROM product WHERE pid = #{pid}")
    Product getProductById(@Param("pid") int pid);

    @Delete("delete from product where pid=#{pid}")
    int delete(int pid);
    // 添加更新商品状态的方法
    @Update("UPDATE product SET pstate = #{state} WHERE pid = #{pid}")
    int updateProductState(@Param("pid") int pid, @Param("state") String state);

    // 更新商品方法 - 添加商品描述字段
    @Update("UPDATE product SET pname=#{pname}, pdescription=#{pdescription}, ptype=#{ptype}, pvalue=#{pvalue}, pimage=#{pimage} WHERE pid=#{pid}")
    int updatePro(Product product);
    @Insert("insert into product values(#{pname},null, #{pdescription}, #{pvalue}, #{ptype}, #{pstate}, #{pimage}, #{ptime}, #{puid})")
    int AddPro(Product product);
    @Select("<script>" +
            "SELECT p.*, u.uname as ownerName,u.uphone as ownerPhone FROM product p " +
            "LEFT JOIN user u ON p.puid = u.uid " +
            "WHERE p.pstate = '在售' " + // 固定为在售状态
            "<if test='day != null and day != \"\"'> " +
            "   AND DATE(p.ptime) = #{day} " +
            "</if> " +
            "<if test='type != null and type != \"\"'> " +
            "   AND p.ptype LIKE CONCAT('%', #{type}, '%') " +
            "</if> " +
            "<if test='name != null and name != \"\"'> " +
            "   AND p.pname LIKE CONCAT('%', #{name}, '%') " +
            "</if> " +
            "LIMIT #{offset}, #{pageSize} " +
            "</script>")
    List<Product> searchProducts(
            @Param("day") String day,
            @Param("type") String type,
            @Param("name") String name,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);


    @Select("<script>" +
            "SELECT p.*, u.uname as ownerName FROM product p " +
            "LEFT JOIN user u ON p.puid = u.uid " +
            "WHERE p.puid = #{uid} " +  // 确保只查询当前用户
            "<if test='day != null and day != \"\"'> " +
            "   AND p.ptime = #{day} " +  // 直接比较日期字段
            "</if> " +
            "<if test='type != null and type != \"\"'> " +
            "   AND p.ptype LIKE CONCAT('%', #{type}, '%') " +
            "</if> " +
            "<if test='name != null and name != \"\"'> " +
            "   AND p.pname LIKE CONCAT('%', #{name}, '%') " +
            "</if> " +
            "<if test='state != null and state != \"\"'> " +
            "   AND p.pstate = #{state} " +
            "</if> " +
            "LIMIT #{offset}, #{pageSize} " +
            "</script>")
    List<Product> getMyProducts(
            @Param("uid") int uid,
            @Param("day") String day,
            @Param("type") String type,
            @Param("name") String name,
            @Param("state") String state,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);


    // 添加上架时更新时间和状态的方法
    @Update("UPDATE product SET pstate = #{state}, ptime = #{ptime} WHERE pid = #{pid}")
    int updateProductStateAndTime(
            @Param("pid") int pid,
            @Param("state") String state,
            @Param("ptime") Date ptime);


    @Select("SELECT p.*, u.uname as ownerName,u.uphone as ownerPhone FROM product p LEFT JOIN user u ON p.puid = u.uid WHERE p.pid = #{pid}")
    Product getProductWithOwner(@Param("pid") int pid);
    @Select("<script>" +
            "SELECT COUNT(*) FROM product " +
            "WHERE pstate = '在售' " + // 固定为在售状态
            "<if test='day != null and day != \"\"'> AND DATE(ptime) = #{day} </if>" +
            "<if test='type != null and type != \"\"'> AND ptype LIKE CONCAT('%', #{type}, '%') </if>" +
            "<if test='name != null and name != \"\"'> AND pname LIKE CONCAT('%', #{name}, '%') </if>" +
            "</script>")
    int countSearchResults(
            @Param("day") String day,
            @Param("type") String type,
            @Param("name") String name);


    @Update("UPDATE product SET puid = #{uid}, pstate = #{state} WHERE pid = #{pid}")
    int updateProductOwnerAndState(
            @Param("pid") int pid,
            @Param("uid") int uid,
            @Param("state") String state);

    @Update("UPDATE product SET puid = #{uid}, pstate = '未上架', ptime = NULL WHERE pid = #{pid}")
    int updateProductOwnerAndStateResetTime(
            @Param("pid") int pid,
            @Param("uid") int uid,
            @Param("state") String state);
    @Select("<script>" +
            "SELECT COUNT(*) FROM product p " +
            "WHERE p.puid = #{uid} " +  // 确保只查询当前用户
            "<if test='day != null and day != \"\"'> AND p.ptime = #{day} </if>" +
            "<if test='type != null and type != \"\"'> AND p.ptype LIKE CONCAT('%', #{type}, '%') </if>" +
            "<if test='name != null and name != \"\"'> AND p.pname LIKE CONCAT('%', #{name}, '%') </if>" +
            "<if test='state != null and state != \"\"'> AND p.pstate = #{state} </if>" +
            "</script>")
    int countMyProducts(
            @Param("uid") int uid,
            @Param("day") String day,
            @Param("type") String type,
            @Param("name") String name,
            @Param("state") String state);

    @Select("SELECT " +
            "ph.pname AS product_name, " + // 直接使用存储的商品名称
            "seller.uname AS seller_name, " +
            "buyer.uname AS buyer_name, " +
            "ph.price AS price, " +
            "ph.purchase_time " +
            "FROM purchase_history ph " +
            "JOIN user seller ON ph.seller_id = seller.uid " +
            "JOIN user buyer ON ph.buyer_id = buyer.uid " +
            "WHERE ph.buyer_id = #{uid} OR ph.seller_id = #{uid} " +
            "ORDER BY ph.purchase_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> getUserTransactionHistory(
            @Param("uid") int uid,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);

    // 计算交易记录总数
    @Select("SELECT COUNT(*) FROM purchase_history " +
            "WHERE buyer_id = #{uid} OR seller_id = #{uid}")
    int countUserTransactionHistory(@Param("uid") int uid);

    @Insert("INSERT INTO purchase_history (pname, seller_id, buyer_id, price, purchase_time) " +
            "VALUES (#{pname}, #{sellerUid}, #{buyerUid}, #{price}, #{purchaseTime})")
    int insertPurchaseHistory(
            @Param("pname") String pname, // 改为接收商品名称
            @Param("sellerUid") int sellerUid,
            @Param("buyerUid") int buyerUid,
            @Param("price") String price,
            @Param("purchaseTime") Date purchaseTime);

    @Select("SELECT " +
            "DATE_FORMAT(ph.purchase_time, '%m-%d') AS date, " +
            "COUNT(*) AS count " +
            "FROM purchase_history ph " +
            "WHERE (ph.buyer_id = #{uid} OR ph.seller_id = #{uid}) " +
            "AND ph.purchase_time >= DATE_SUB(CURDATE(), INTERVAL 3 DAY) " +
            "GROUP BY DATE(ph.purchase_time) " +
            "ORDER BY DATE(ph.purchase_time) ASC")
    List<Map<String, Object>> getTransactionCountLastFourDays(@Param("uid") int uid);
}