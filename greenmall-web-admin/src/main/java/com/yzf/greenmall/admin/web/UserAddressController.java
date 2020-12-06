package com.yzf.greenmall.admin.web;

import com.yzf.greenmall.entity.UserAddress;
import com.yzf.greenmall.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description:
 * @author:leo_yuzhao
 * @date:2020/12/5
 */
@Controller
@RequestMapping(path = "/address")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 根据地址id查询地址信息
     *
     * @param id
     * @return
     */
    @GetMapping(path = "/queryUserAddress")
    public ResponseEntity<UserAddress> queryUserAddress(@RequestParam(name = "id") Long id) {
        try {
            UserAddress userAddress = userAddressService.findUserAddress(id);
            if (userAddress == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(userAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
