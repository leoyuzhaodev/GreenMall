package com.yzf.greenmall.web;

import com.yzf.greenmall.common.Message;
import com.yzf.greenmall.common.jwt.UserInfo;
import com.yzf.greenmall.entity.AdministrativeRegion;
import com.yzf.greenmall.entity.UserAddress;
import com.yzf.greenmall.interceptor.LoginInterceptor;
import com.yzf.greenmall.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:UserAddressController
 * @author:leo_yuzhao
 * @date:2020/12/1
 */
@Controller
@RequestMapping(path = "/address")
public class UserAddressController {
    @Autowired
    private UserAddressService userAddressService;

    /**
     * 更新或新增售后地址
     *
     * @param userAddress
     * @return
     */
    @PostMapping(path = "/update")
    public ResponseEntity<UserAddress> update(@RequestBody UserAddress userAddress) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        try {
            UserAddress message = this.userAddressService.update(loginUser, userAddress);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据用户id查找地址
     *
     * @return
     */
    @GetMapping(path = "/queryUserAddress")
    public ResponseEntity<List<UserAddress>> queryUserAddress() {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            List<UserAddress> list = userAddressService.findUserAddress(loginUser);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 根据地址id查找地址
     *
     * @return
     */
    @GetMapping(path = "/queryUserAddressById")
    public ResponseEntity<UserAddress> queryUserAddressById(@RequestParam(name = "id") Long addressId) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            UserAddress userAddress = userAddressService.findUserAddress(addressId);
            return ResponseEntity.ok(userAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 根据用户id,以及地址id设置默认地址
     *
     * @return
     */
    @GetMapping(path = "/setDefaultAddress/{addressId}")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable(name = "addressId") Long id) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            userAddressService.setDefaultAddress(loginUser, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 根据用户id,以及地址id设置默认地址
     *
     * @return
     */
    @GetMapping(path = "/setUserAddressInvalid/{addressId}")
    public ResponseEntity<Void> setUserAddressInvalid(@PathVariable(name = "addressId") Long id) {
        try {
            UserInfo loginUser = LoginInterceptor.getLoginUser();
            userAddressService.setUserAddressInvalid(loginUser, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
