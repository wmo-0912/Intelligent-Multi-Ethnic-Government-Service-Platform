package com.example.govservice.mapper;

import com.example.govservice.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据账号查询用户
     */
    @Select("SELECT * FROM User WHERE Account = #{account}")
    @Results(id = "userMap", value = {
            @Result(column = "UserID", property = "userId", id = true),
            @Result(column = "Name", property = "name"),
            @Result(column = "Ethnicity", property = "ethnicity"),
            @Result(column = "PhoneNumber", property = "phoneNumber"),
            @Result(column = "Account", property = "account"),
            @Result(column = "Password", property = "password"),
            @Result(column = "Role", property = "role"),
            @Result(column = "RealNameVerified", property = "realNameVerified"),
            @Result(column = "Frozen", property = "frozen"),
            @Result(column = "CreateTime", property = "createTime"),
            @Result(column = "UpdateTime", property = "updateTime")
    })
    User selectByAccount(@Param("account") String account);
    
    /**
     * 根据用户ID查询用户
     */
    @Select("SELECT * FROM User WHERE UserID = #{userId}")
    @ResultMap("userMap")
    User selectById(@Param("userId") Integer userId);
    
    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM User WHERE PhoneNumber = #{phoneNumber}")
    @ResultMap("userMap")
    User selectByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM User ORDER BY CreateTime DESC")
    @ResultMap("userMap")
    List<User> selectAll();
    
    /**
     * 根据角色查询用户
     */
    @Select("SELECT * FROM User WHERE Role = #{role} ORDER BY CreateTime DESC")
    @ResultMap("userMap")
    List<User> selectByRole(@Param("role") String role);
    
    /**
     * 插入用户
     */
    @Insert("INSERT INTO User (Name, Ethnicity, PhoneNumber, Account, Password, Role, RealNameVerified, Frozen, CreateTime, UpdateTime) " +
            "VALUES (#{name}, #{ethnicity}, #{phoneNumber}, #{account}, #{password}, #{role}, #{realNameVerified}, #{frozen}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "UserID")
    int insert(User user);
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE User SET Name = #{name}, Ethnicity = #{ethnicity}, PhoneNumber = #{phoneNumber}, " +
            "Password = #{password}, Role = #{role}, RealNameVerified = #{realNameVerified}, Frozen = #{frozen}, UpdateTime = NOW() " +
            "WHERE UserID = #{userId}")
    int update(User user);
    
    /**
     * 根据用户ID删除用户
     */
    @Delete("DELETE FROM User WHERE UserID = #{userId}")
    int deleteById(@Param("userId") Integer userId);
    
    /**
     * 检查账号是否存在
     */
    @Select("SELECT COUNT(*) FROM User WHERE Account = #{account}")
    int checkAccountExists(@Param("account") String account);
    
    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM User WHERE PhoneNumber = #{phoneNumber}")
    int checkPhoneNumberExists(@Param("phoneNumber") String phoneNumber);
}
